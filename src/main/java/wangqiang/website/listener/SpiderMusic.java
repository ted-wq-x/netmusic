package wangqiang.website.listener;

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
import wangqiang.website.spider.SongSpider;
import wangqiang.website.spider.Spider;
import wangqiang.website.spider.UserSpider;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


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

    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);


    public void startThisSpider() {
        LOGGER.info("Enter startThisSpider method。");
        new Thread(this::userSpiderThread).start();
        new Thread(this::songSpiderThread).start();
        new Thread(this::complateTask).start();//执行数据入库的线程
        LOGGER.info("Exit startThisSpider method.");
    }


    /**
     * song数据的爬取，使用的是线程池
     */
    private void songSpiderThread(){
        while (true) {
            Spider spider = new SongSpider();
            try {
                JSONObject jsonObject = new JSONObject();
                Integer songId = UserSpider.songIdQueue.take();
                LOGGER.info("song spider add songId={}",songId);
                jsonObject.put(SongSpider.PARAS_SONGID,songId );
                executorService.submit(() -> spider.start("http://music.163.com/weapi/v1/resource/comments/R_SO_4_" + songId + "?csrf_token=",jsonObject));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * user数据的爬取，单线程
     */
    private void userSpiderThread() {
        LOGGER.info("Enter userSpiderThread method");
        Spider userSpider = new UserSpider();
        while (true) {
            JSONObject paras = new JSONObject();
            try {
                Integer uid = SongSpider.uidQueue.take();
                LOGGER.info("user spider add uid={}",uid);
                paras.put(UserSpider.PARAS_UID, uid);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            userSpider.start("http://music.163.com/weapi/v1/play/record?csrf_token=",paras);
        }
        LOGGER.info("Exit userSpiderThread method");
    }

    /**
     * TODO 或者使用线程通讯，当爬取数据之后，检查队列大小，进行线程唤醒
     * 执行任务合并，完成数据的入库操作
     */
    private void complateTask() {
        LOGGER.info("Enter spider complateTask method");
        while (true) {
            if (SongSpider.blockingQueueComments.size() >= 100) {
                LOGGER.info("do spider complateTask method and insert commentsVo");
                insertComments();
            }
            if (SongSpider.blockingQueueUser.size() >= 100) {
                LOGGER.info("do spider complateTask method and insert userVo");
                insertUser();
            }
            if (SongSpider.blockingQueueMusic.size() >= 100) {
                LOGGER.info("do spider complateTask method and insert musicVo");
                insertMusic();
            }
            try {
                Thread.sleep(5000);//每五秒检查一次队列
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 经过测试，当主键相同时不会插入
     */
    private void insertMusic() {
        LOGGER.info("Enter insertMusic method");
        List<MusicVo> temp = new ArrayList<>();
        SongSpider.blockingQueueMusic.drainTo(temp);
        musicRepository.save(temp);
        LOGGER.info("Exit insertMusic method");
    }

    private void insertComments() {
        LOGGER.info("Enter insertComments method");
        List<CommentsVo> temp = new ArrayList<>();
        SongSpider.blockingQueueComments.drainTo(temp);
        commentsRepository.save(temp);
        LOGGER.info("Exit insertComments method");
    }


    private void insertUser() {
        LOGGER.info("Enter insertUser method");
        List<UserVo> temp = new ArrayList<>();
        SongSpider.blockingQueueUser.drainTo(temp);
        userRepository.save(temp);
        LOGGER.info("Exit insertUser method");
    }


}
