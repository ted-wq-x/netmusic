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

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangq on 2017/5/16.
 */
@Component
public class ProxyPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyPool.class);
    private static List<HttpHost> ipPool = Collections.synchronizedList(new ArrayList<>());


    public static HttpHost getHost() {
        int size = ipPool.size();
        if (size == 1) {
            return null;
        }
        int i = 1 + (int) (Math.random() * size - 1);
        LOGGER.info("ipPool size={}", size);
        return ipPool.get(i);
    }

    public static void removeIpFromPool(HttpHost httpHost) {
        LOGGER.info("Enter removeIpFromPool method:{}", httpHost);
        ipPool.remove(httpHost);
        LOGGER.info("Exit removeIpFromPool method.");
    }

    /**
     * 每10分钟爬取一次
     * http://www.kuaidaili.com/
     */
    @Scheduled(cron = "* 0/10 * * * *")
    private static void getIpsFrom() {
        LOGGER.info("Enter getIpPool timer.");
        Spider spider = Spider.create(new PageProcessor() {
            @Override
            public void process(Page page) {
                List<String> ips = new ArrayList<>();
                List<String> ports = new ArrayList<>();
                String url = page.getUrl().get();
                if (url.contains("kuaidaili")) {
                    List<Selectable> nodes = page.getHtml().xpath("//td[@data-title='IP']/text()").nodes();
                    List<Selectable> nodes1 = page.getHtml().xpath("//td[@data-title='PORT']/text()").nodes();
                    for (int i = 0; i < nodes.size(); i++) {
                        ips.add(nodes.get(i).get());
                        ports.add(nodes1.get(i).get());
                    }
                } else if (url.contains("xicidaili")) {
                    String rawText = page.getRawText();
                    String[] split = rawText.split("\r\n");
                    for (String s : split) {
                        String[] ipPort = s.split(":");
                        ips.add(ipPort[0]);
                        ports.add(ipPort[1]);
                    }
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

//        添加爬取的地址
        addSpiderUrl(spider);

        spider.addPipeline((resultItems, task) -> {
            List<String> ips = resultItems.get("ips");
            List<String> ports = resultItems.get("ports");
//            ipPool.clear(); TODO 不清除原有的数据，再次添加会出现相同的ip
            for (int i = 0; i < ips.size(); i++) {
                HttpHost host = new HttpHost(ips.get(i), Integer.parseInt(ports.get(i)));
                ipPool.add(host);
            }
        }).thread(4).run();

        LOGGER.info("Exit getIpPool timer.poolSize={}", ipPool.size());
    }

    /**
     * 添加爬虫的地址
     * @param spider
     */
    private static void addSpiderUrl(Spider spider) {
        for (int i = 1; i <= 10; i++) {
//            TODO ip不够用
            spider.addUrl("http://www.kuaidaili.com/proxylist/" + i);
        }
        spider.addUrl("http://api.xicidaili.com/");
    }

    @PostConstruct
    private void initPool() {
        getIpsFrom();
    }
}
