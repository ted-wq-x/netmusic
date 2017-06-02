package wangqiang.website;

import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.HtmlNode;
import us.codecraft.webmagic.selector.Selectable;
import wangqiang.website.utils.SpiderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangq on 2017/5/15.
 */
public class Main {
    public static void main(String[] args) {
        Spider spider = Spider.create(new PageProcessor() {
            @Override
            public void process(Page page) {
                List<Selectable> nodes =page.getHtml().xpath("//td[@class='ip']").nodes();
                nodes.forEach(index->{
                    HtmlNode selectable = (HtmlNode) index.nodes().get(0);
//                    TODO 待测试
                    Elements allElements = ((Html)selectable).getDocument().getAllElements();

//                    List<Selectable> ip = index.xpath("//text()").nodes();
                    System.out.println(allElements);
                });
                System.out.println(nodes);
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
        spider.addUrl("http://www.goubanjia.com/free/gngn/index1.shtml");
        spider.addPipeline((resultItems, task) -> {
            List<Selectable> ips = resultItems.get("ips");
            List<Selectable> ports = resultItems.get("ports");
            System.out.println(ips + ":" + ports);
        }).run();

//        String str = "他在南方当兵，我留在东北上大学，他说我借他三年青春他还我我一辈子，我坐了将近三十个小时的火车去看他，只希望抱他一下，只希望我们不会分开，只希望三年我没白等\uD83D\uDE14";
//        SpiderUtils spiderUtils = new SpiderUtils();
//        spiderUtils.getUserPlayList();
    }
}
