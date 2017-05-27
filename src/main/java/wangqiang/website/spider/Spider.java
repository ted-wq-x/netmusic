package wangqiang.website.spider;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wangqiang.website.utils.EncryptTools;
import wangqiang.website.utils.ProxyPool;
import wangqiang.website.utils.SpiderUtils;

import java.util.Map;

/**
 * Created by wangq on 2017/5/27.
 */
public abstract class Spider {

    private static final Logger LOGGER = LoggerFactory.getLogger(Spider.class);

    protected JSONObject spiderMetMusic(String postUrl, HttpHost host, Map<String, String> map) {
        JSONObject jsonObject = SpiderUtils.spiderMetMusic(postUrl, host, map);
        return jsonObject;
    }

    /**
     * 生成请求参数
     *
     * @param url
     * @param paras 添加的额外参数
     * @return
     */
    protected abstract JSONObject generatePara(String url, JSONObject paras);

    /**
     * 解析爬取的接送，获取有效数据
     *
     * @param jsonObject
     * @return
     */
    protected abstract void analysisResult(JSONObject jsonObject);


    /**
     * @param url
     * @param paras
     */
    public final void start(String url, JSONObject paras) {
//        生成请求参数
        JSONObject para = this.generatePara(url, paras);
        Map<String, String> stringStringMap = EncryptTools.encryptedRequest(para);

//        非法ip重试
        for (int i = 0; i < 3; i++) {
            HttpHost host = ProxyPool.getHost();
            if (host == null) {
                return;
            }
            JSONObject jsonObject = spiderMetMusic(url, host, stringStringMap);
            if (jsonObject != null) {
                this.analysisResult(jsonObject);
            }
        }
    }


}
