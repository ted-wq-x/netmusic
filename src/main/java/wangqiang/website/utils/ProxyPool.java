package wangqiang.website.utils;

import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by wangq on 2017/5/16.
 */
@Component
public class ProxyPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyPool.class);
    private static List<HttpHost> ipPool = new CopyOnWriteArrayList<>();


    public static HttpHost getHost(){
        int size = ipPool.size();
        if (size == 0) {
            getIpsFrom();
        }
        int i = 1+(int) (Math.random() * (size-1));
        return ipPool.get(i);
    }

    public static void removeIpFromPool(HttpHost httpHost){
        LOGGER.info("Enter removeIpFromPool method:{}",httpHost);
        ipPool.remove(httpHost);
        LOGGER.info("Exit removeIpFromPool method.");
    }

    /**
     * 每10分钟爬取一次
     * http://www.kuaidaili.com/
     */
    @Scheduled(cron="* 0/10 * * * *")
    private static void getIpsFrom(){
        LOGGER.info("Enter getIpPool timer.");
        Spider spider = Spider.create(new PageProcessor() {
            @Override
            public void process(Page page) {
                List<Selectable> ips=new ArrayList<>();
                List<Selectable> ports=new ArrayList<>();
                String url = page.getUrl().get();
                if (url.contains("kuaidaili")) {
                     ips.addAll(page.getHtml().xpath("//td[@data-title='IP']/text()").nodes());
                     ports.addAll( page.getHtml().xpath("//td[@data-title='PORT']/text()").nodes());
                }
                page.putField("ips",ips);
                page.putField("ports",ports);
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

        for (int i = 1; i <= 10; i++) {
            spider.addUrl("http://www.kuaidaili.com/proxylist/" + i);
        }

        spider.addPipeline((resultItems, task) -> {
            List<Selectable> ips = resultItems.get("ips");
            List<Selectable> ports = resultItems.get("ports");
            for (int i = 0; i < ips.size(); i++) {
                HttpHost host = new HttpHost(ips.get(i).get(), Integer.parseInt(ports.get(i).get()));
                ipPool.add(host);
            }
        }).run();

        LOGGER.info("Exit getIpPool timer.poolSize={}",ipPool.size());
    }
}
