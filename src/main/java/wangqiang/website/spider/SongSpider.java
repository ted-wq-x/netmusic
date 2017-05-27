package wangqiang.website.spider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wangqiang.website.modal.CommentsVo;
import wangqiang.website.modal.MusicVo;
import wangqiang.website.modal.UserVo;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by wangq on 2017/5/27.
 */
public class SongSpider extends Spider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SongSpider.class);

    public static final String PARAS_SONGID = "songId";

    /**
     * 缓存爬取的数据，达到100个时存入数据库
     */
    public static final BlockingQueue<UserVo> blockingQueueUser = new LinkedBlockingQueue<>();
    public static final BlockingQueue<Integer> uidQueue = new LinkedBlockingQueue<>();
    public static final BlockingQueue<MusicVo> blockingQueueMusic = new LinkedBlockingQueue<>();
    public static final BlockingQueue<CommentsVo> blockingQueueComments = new LinkedBlockingQueue<>();

    private ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    @Override
    protected JSONObject generatePara(String url, JSONObject paras) {
        Integer songId = paras.getInteger(PARAS_SONGID);
        JSONObject para = new JSONObject();
        para.put("limit", 20);
        para.put("offset", 0);
        para.put("total", true);
        para.put("rid", "R_SO_4_" + songId);
        threadLocal.set(songId);//线程绑定该参数
        return para;
    }

    @Override
    protected void analysisResult(JSONObject jsonObject) {
        calculate(jsonObject);
    }


    private void calculate(JSONObject jsonObject) {
        JSONArray comments = jsonObject.getJSONArray("comments");
        JSONArray hotComments = jsonObject.getJSONArray("hotComments");

        Integer songId = threadLocal.get();//爬取结果中没有这个字段，是手动添加的

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

            uidQueue.add(userId);//存放带爬取的uid
        }
    }

}
