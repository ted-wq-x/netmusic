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
import java.util.Map;

/**
 * Created by wangq on 2017/5/15.
 */
public class SpiderUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpiderUtils.class);

    public static JSONObject spiderMetMusic(String postUrl, HttpHost host, Map<String, String> map) {
        LOGGER.info("Enter spiderMetMusic url={} ,host={}", postUrl, host);
        if (postUrl == null || host == null) {
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

        post.addParameter("params", map.get("params"))
                .addParameter("encSecKey", map.get("encSecKey"));

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

        if (type.contains("application/json") || type.contains("text/plain")) {
            LOGGER.info("return msg type={}", type);
            JSONObject jsonObject = null;
            try {
                jsonObject = JSON.parseObject(s);
            } catch (Exception e) {
                LOGGER.error("json parse fail:e={}", e.getMessage());
            }
            return jsonObject;
        } else if (type.contains("text/html")) {
            LOGGER.info("ERROR html return:{}", s);
            return new JSONObject();
        }
        return new JSONObject();
    }

    /**
     * 测试使用
     *
     * @param
     * @return
     */
    @Deprecated
    public static void test(String url, Map<String, String> map) {

        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient build = builder.build();
        RequestBuilder post = RequestBuilder.post(url);

        post.addHeader("Referer", "http://music.163.com/")
                .addHeader("Host", "music.163.com")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Safari/537.36")
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Encoding", "gzip, deflate");

        post.addParameter("params", map.get("params"))
                .addParameter("encSecKey", map.get("encSecKey"));

        RequestConfig.Builder config = RequestConfig.custom().setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setCookieSpec(CookieSpecs.STANDARD).setProxy(new HttpHost("127.0.0.1", 1080));
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
        } finally {
            if (execute != null) {
                EntityUtils.consumeQuietly(execute.getEntity());
            }
        }
        String s = new String(bytes);

        System.out.println(JSONObject.parse(s));
    }

    /**
     * 测试使用
     *
     * @param
     * @return
     */
    @Deprecated
    public void getSong() {
        JSONObject para = new JSONObject();
//        参数
        para.put("limit", 20);
        para.put("offset", 0);
        para.put("total", true);
        para.put("rid", "R_SO_4_436487056");

        Map<String, String> stringStringMap = EncryptTools.encryptedRequest(para);

        test("http://music.163.com/weapi/v1/resource/comments/R_SO_4_" + 436487056 + "?csrf_token=", stringStringMap);
    }

    /**
     * 测试使用
     *
     * @param
     * @return
     */
    @Deprecated
    public void getUserPlayList() {
        JSONObject para = new JSONObject();
//        参数
        para.put("limit", 1000);
        para.put("offset", 0);
        para.put("total", true);
        para.put("uid", 424641929);
        para.put("type", -1);
        Map<String, String> stringStringMap = EncryptTools.encryptedRequest(para);

        test("http://music.163.com/weapi/v1/play/record?csrf_token=", stringStringMap);
    }


}
