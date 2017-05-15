package wangqiang.website;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by wangq on 2017/5/15.
 */
public class Spider {


    public static JSONObject spiderMetMusic(String postUrl) {
        if (postUrl == null) {
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

        RequestConfig.Builder config = RequestConfig.custom().setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setCookieSpec(CookieSpecs.STANDARD);
        post.setConfig(config.build());
/*
        EncryptTools encryptTools = new EncryptTools();
        Map<String, String> stringStringMap = encryptTools.encryptedRequest(jsonObj);
        post.addParameter("params", stringStringMap.get("params"))
                .addParameter("encSecKey", stringStringMap.get("encSecKey"));*/
        post.addParameter("params", "h1m21We/65UBosHAYHVyX0z/w2bqBnvhjHsrLK09Gbivu6LbBr/O9qbq+Q8kfHavJJ/YHz6+RlCw1UnoSsRFml+YmkP7ne9VIIr5vMdhpZ2UoXfWHa84WeCGdzUFuUya4ngIKJyyQyS7N2+madxg8QLNRh8jDazesxTPrzgDvzLKuN4jWbp8ZYBWONE9QZS3hVSxGYTjZxmjv0ARICyRSdxeRdtqPeDfPq9bhc1D5J+lfQn/njUrBQdH/HYiEIT9uuEWPyfRfBmRD8zYSaTZJ5k4Q7Tkp2X9/dGiwmLk/Zs=")
                .addParameter("encSecKey", "045e7c05a92df2112e61178543f91d8235f8c0fb48cc2f370bcd45535c99d68a98be82ea3d82a51ab00aeb4368afd76351e65964fc595980f47f73874d16a8e54571ac9aa38f234a1489544c75ccac4c18e107bcfb83ed82332c2938b67a62da0ebd03dd9b201c4200fd808cd1689956cd7e34629e525f3ce82976f43b024c49");
        HttpResponse execute = null;
        byte[] bytes = new byte[0];
        try {
            execute = build.execute(post.build());
            InputStream content = execute.getEntity().getContent();
            bytes = IOUtils.toByteArray(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (execute != null) {
                EntityUtils.consumeQuietly(execute.getEntity());
            }
        }
        String s = new String(bytes);
        return JSON.parseObject(s);
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
     * @param userId
     */
    public void getUserPlayList(Integer userId) {

    }


}
