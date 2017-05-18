package wangqiang.website;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import wangqiang.website.utils.Spider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangq on 2017/5/15.
 */
public class Main {
    public static void main(String[] args) {
        us.codecraft.webmagic.Spider spider = us.codecraft.webmagic.Spider.create(new PageProcessor() {
            @Override
            public void process(Page page) {
                List<Object> ips = new ArrayList<>();
                List<Object> ports = new ArrayList<>();
                String url = page.getUrl().get();
                String rawText = page.getRawText();

                String[] split = rawText.split("\r\n");
                for (String s : split) {
                    String[] ipPort = s.split(":");
                    ips.add(ipPort[0]);
                    ports.add(ipPort[1]);
                }
                page.putField("ips", ips);
                page.putField("ports", ports);
            }

            @Override
            public Site getSite() {
                return Site.me().addHeader("Connection", "keep-alive")
                        .addHeader("Upgrade-Insecure-Requests", "1")
                        .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko)")
                        .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
                        .addHeader("Cache-Control", "max-age=0");
            }
        });

        spider.addUrl("http://api.xicidaili.com/");

        spider.addPipeline((resultItems, task) -> {
            List<Selectable> ips = resultItems.get("ips");
            List<Selectable> ports = resultItems.get("ports");
            System.out.println(ips+":"+ports);
//            ipPool.clear(); TODO 不清除原有的数据，再次添加会出现相同的ip
        }).run();
    }


}
