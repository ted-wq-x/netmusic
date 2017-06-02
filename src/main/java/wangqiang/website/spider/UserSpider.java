package wangqiang.website.spider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 获取用户听过的歌曲的爬虫，使用单线程爬取
 * Created by wangq on 2017/5/27.
 */
public class UserSpider extends Spider {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSpider.class);

    public static final String PARAS_UID = "uid";

    /**
     * 缓存等待爬取的歌曲id，容量1000
     */
    public static final BlockingQueue<Integer> songIdQueue = new LinkedBlockingQueue<>(1000);

    /**
     *
     * @param url http://music.163.com/weapi/v1/play/record?csrf_token=
     * @return
     */
    @Override
    protected JSONObject generatePara(String url,JSONObject paras) {

        JSONObject para = new JSONObject();
        para.put("limit", 20);
        para.put("offset", 0);
        para.put("total", true);
        para.put("type",-1 );
        para.put("uid", paras.getInteger(PARAS_UID));

        return para;
    }

    /**
     * 最多获取100首歌
     * @param jsonObject 爬虫返回结果
     */
    @Override
    protected void analysisResult(JSONObject jsonObject) {
        LOGGER.info("Enter user analysisResult method");
        JSONArray weekData = jsonObject.getJSONArray("allData");
        if (weekData == null) {
            return;
        }
        for (int i = 0; i < weekData.size(); i++) {
            Integer songId = weekData.getJSONObject(i).getJSONObject("song").getInteger("id");
            try {
                songIdQueue.put(songId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("Exit user analysisResult method");
    }
}
