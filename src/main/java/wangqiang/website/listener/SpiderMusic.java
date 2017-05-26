package wangqiang.website.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wangqiang.website.modal.CommentsVo;
import wangqiang.website.modal.MusicVo;
import wangqiang.website.modal.UserVo;
import wangqiang.website.service.CommentsRepository;
import wangqiang.website.service.MusicRepository;
import wangqiang.website.service.UserRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static wangqiang.website.utils.Spider.spiderMetMusic;

/**
 * Created by wangq on 2017/5/26.
 */
@Component
public class SpiderMusic {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitListener.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private MusicRepository musicRepository;

    private ExecutorCompletionService<JSONObject> service = null;


    /**
     * 限制向任务队列提交任务的个数
     */
    private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>(25);
    /**
     * 缓存爬取的数据，达到100个时存入数据库
     */
    private BlockingQueue<UserVo> blockingQueueUser = new LinkedBlockingQueue<>();
    private BlockingQueue<MusicVo> blockingQueueMusic = new LinkedBlockingQueue<>();
    private BlockingQueue<CommentsVo> blockingQueueComments = new LinkedBlockingQueue<>();

    @PostConstruct
    private void init() {
//     存放任务执行结果的队列
        BlockingQueue<Future<JSONObject>> completeTask = new LinkedBlockingQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() + 1) * 2);
        service = new ExecutorCompletionService<>(executorService, completeTask);
    }

    //    @Override
    public void startThisSpider() {
        LOGGER.info("Enter startThisSpider method。");
        new Thread(this::main).start();
        new Thread(this::complateTask).start();
        LOGGER.info("Exit startThisSpider method.");
    }

    @Deprecated
    private int getCurrentNum() {
        return musicRepository.selectMax() + 1;
    }

    /**
     * 根据歌曲获取用户
     */
    private void main() {
        LOGGER.info("Enter spider main method");
//        while (true) {
//            int andIncrement = atomicInteger.getAndIncrement();
//            if (andIncrement >= 1000000000) {
//                LOGGER.info("num gt 1000000000");
//                insertMusic();
//                break;
//            }
//            try {
//                queue.put(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            LOGGER.info("submit task num={}", andIncrement);
//            service.submit(new InitListener.task(andIncrement));
//        }
        LOGGER.info("Exit spider main method");
    }

    /**
     * 执行任务合并
     */
    private void complateTask() {
        LOGGER.info("Enter spider complateTask method");
        while (true) {
            try {
                Future<JSONObject> take = service.take();
//                出现null exception
                JSONObject musicVousicVo = take.get();
                calculate(musicVousicVo);
                if (blockingQueueMusic.size() >= 100) {
                    insertMusic();
                    LOGGER.info("music queue is full 100 and  insert into databases");
                }
                if (blockingQueueComments.size() >= 100) {
                    insertComments();
                    LOGGER.info("comments queue is full 100 and  insert into databases");
                }
                if (blockingQueueUser.size() >= 100) {
                    insertUser();
                    LOGGER.info("user queue is full 100 and  insert into databases");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertMusic() {
        LOGGER.info("Enter insertMusic method");
        List<MusicVo> temp = new ArrayList<>();
        blockingQueueMusic.drainTo(temp);
        musicRepository.save(temp);
        LOGGER.info("Exit insertMusic method");
    }

    private void insertComments() {
        LOGGER.info("Enter insertComments method");
        List<CommentsVo> temp = new ArrayList<>();
        blockingQueueComments.drainTo(temp);
        commentsRepository.save(temp);
        LOGGER.info("Exit insertComments method");
    }


    private void insertUser() {
        LOGGER.info("Enter insertUser method");
        List<UserVo> temp = new ArrayList<>();
        blockingQueueUser.drainTo(temp);
        userRepository.save(temp);
        LOGGER.info("Exit insertUser method");
    }


    private void calculate(JSONObject jsonObject) {
        JSONArray comments = jsonObject.getJSONArray("comments");
        JSONArray hotComments = jsonObject.getJSONArray("hotComments");
        Integer songId = jsonObject.getInteger("songId");

        List<CommentsVo> commentsVos = new ArrayList<>();
        List<UserVo> userVos = new ArrayList<>();

        getCommentsAndUser(comments, songId, commentsVos, userVos);
        getCommentsAndUser(hotComments, songId, commentsVos, userVos);

        Integer total = jsonObject.getInteger("total");
        MusicVo musicVo = new MusicVo();
        musicVo.setCommitTotal(total);
        musicVo.setId(songId);
        blockingQueueMusic.add(musicVo);
        blockingQueueUser.addAll(userVos);
        blockingQueueComments.addAll(commentsVos);
    }

    private void getCommentsAndUser(JSONArray comments, Integer songId, List<CommentsVo> commentsVos, List<UserVo> userVos) {
        for (int i = 0; i < comments.size(); i++) {
            JSONObject comment = comments.getJSONObject(i);

            JSONObject user = comment.getJSONObject("user");
            Integer userId = user.getInteger("userId");
            String avatarUrl = user.getString("avatarUrl");
            String nickname = user.getString("nickname");

            UserVo userVo = new UserVo();
            userVo.setAvatarUrl(avatarUrl);
            userVo.setId(userId);
            userVo.setNickname(nickname);
            userVos.add(userVo);

            CommentsVo commentsVo = new CommentsVo();
            commentsVo.setId(comment.getInteger("commentId"));
            commentsVo.setTime(comment.getInteger("time"));
            commentsVo.setContent(comment.getString("content"));
            commentsVo.setSongId(songId);
            commentsVo.setUserId(userId);
            commentsVos.add(commentsVo);
        }
    }

    class task implements Callable<JSONObject> {

        private int songId;

        public task(int andIncrement) {
            this.songId = andIncrement;
        }

        @Override
        public JSONObject call() throws Exception {
            LOGGER.info("Enter task and current num={}", songId);
            JSONObject jsonObject = spiderMetMusic("http://music.163.com/weapi/v1/resource/comments/R_SO_4_" + songId + "?csrf_token=");
            if (jsonObject != null && jsonObject.getInteger("code") != null && jsonObject.getInteger("code") == 200) {
                LOGGER.info("Exit task and musicVo=isNotNull");
                return jsonObject;
            }
            LOGGER.info("Exit task and musicVo=null");
            return null;
        }
    }
}
