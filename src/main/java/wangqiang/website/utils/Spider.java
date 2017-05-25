package wangqiang.website.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangq on 2017/5/15.
 */
public class Spider {

    private static final Logger LOGGER = LoggerFactory.getLogger(Spider.class);

    public static JSONObject spiderMetMusic(String url) {
        JSONObject jsonObject=null;
//        非法ip重试
        for (int i = 0; i < 3; i++) {
            HttpHost host = ProxyPool.getHost();
            if (host == null) {
                return jsonObject;
            }
            jsonObject = spiderMetMusic(url, host);
            if (jsonObject != null) {
                return jsonObject;
            }
        }
        return jsonObject;
    }

    public static JSONObject spiderMetMusic(String postUrl,HttpHost host) {
        LOGGER.info("Enter spiderMetMusic url={} ,host={}",postUrl,host);
        if (postUrl == null|| host==null) {
            return null;
        }
        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient build = builder.build();
        RequestBuilder post = RequestBuilder.post(postUrl);

        post.addHeader("Referer", "http://music.163.com/")
                .addHeader("Host", "music.163.com")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Safari/537.36")
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Encoding", "gzip, deflate");

        post.addParameter("params", "/ppnB8jFMOE50oPqknmtoHQvx2WvXt5eok5obFF6MF1UCyAS6RiMrmzud7B60km4PHVAislHLtfy7LRAQknZJDCzHOE9LZAM83EP2pni8kd/QbtucTc9el7o0TqPr2GHDdS538g+c30wGAZXK/AMvdJNm5M2xFk+b2dEJeFOjH/IBRzn0gOApCawycIRGQmN95MQ9lxmWUcMQrO5pwIdM3Ox5O9wDvHy+8Q3Nb3pnQw=")
                .addParameter("encSecKey", "5bf9c0fcad84cec2089be52f3500f6a38681fa16e7ae5969f6c3af9fd08afd215b698a424fbc7af8acbd7df35cdd198659129b3c1a9bf9730901591406d413e654d49bdc717a774d5adf43b36e90dc25bed6c631e45ed242690c56932f44125ddc40fe0b5fcb5efcaa211ce666740b1e8de72416d2277f4e00f3c6a4a58e1194");

        RequestConfig.Builder config = RequestConfig.custom().setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setCookieSpec(CookieSpecs.STANDARD).setProxy(host);
        post.setConfig(config.build());

        HttpUriRequest build1 = post.build();
        HttpResponse execute = null;
        byte[] bytes = new byte[0];
        try {
            execute = build.execute(build1);
            InputStream content = execute.getEntity().getContent();
            bytes = IOUtils.toByteArray(content);
        } catch (IOException e) {
            LOGGER.info("Catch IOException and remove ip from ipPools");
            ProxyPool.removeIpFromPool(host);
            return null;
        } finally {
            if (execute != null) {
                EntityUtils.consumeQuietly(execute.getEntity());
            }
        }
        String type = execute.getEntity().getContentType().getValue();
        String s = new String(bytes);

        if (type.contains("application/json")||type.contains("text/plain")) {
            LOGGER.info("return msg type={}",type);
            JSONObject jsonObject = null;
            try {
                jsonObject = JSON.parseObject(s);
            } catch (Exception e) {
               LOGGER.error("json parse fail:e={}",e.getMessage());
            }
            return jsonObject;
        } else if(type.contains("text/html")) {
            LOGGER.info("ERROR html return:{}",s);
            return new JSONObject();
        }
        return new JSONObject();
    }

    /**
     * 获取歌单列表<br>
     *
     * @return 歌曲id
     */
    ///////////////////////////////////////////////////////////////////////////
    //
    //   {
    //     "offset": offset,
    //      "limit": limit,
    //      "uid": uid
    //   }
    ///////////////////////////////////////////////////////////////////////////
//    public static  Set<Integer> getPlayList(Integer listId){
//        JSONObject jsonObjecta = new JSONObject();
//        jsonObjecta.put("limit", 100);
//        jsonObjecta.put("offset", 0);
//        jsonObjecta.put("uid", listId);
//        JSONObject jsonObject = spiderMetMusic("http://music.163.com/api/playlist/detail?id="+listId,jsonObjecta);
//        Set<Integer> list = new HashSet<>();
//        if (jsonObject.getInteger("code")==200) {
//            JSONObject result = jsonObject.getJSONObject("result");
//            JSONArray tracks = result.getJSONArray("tracks");
//            tracks.forEach(index->{
//                JSONObject object = (JSONObject) index;
//                list.add(object.getInteger("id"));
//            });
//        }
//        return list;
//    }


    /**
     * 获取歌曲信息<br>
     * {"id": id}
     *
     * @param songId
     * @return
     */
    public JSONObject getSong(Integer songId) {
        if (songId == null) {
            return null;
        }
        JSONObject jsonObject = spiderMetMusic("http://music.163.com/weapi/v1/resource/comments/R_SO_4_" + songId + "?csrf_token=");
        return jsonObject;
    }

    /**
     * 获取用户歌单
     *
     * http://music.163.com/weapi/v1/play/record?csrf_token=
     *
     * @param userId
     */
    public void getUserPlayList(Integer userId) {

    }


}
