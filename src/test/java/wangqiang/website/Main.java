package wangqiang.website;

import wangqiang.website.utils.SpiderUtils;

/**
 * Created by wangq on 2017/5/15.
 */
public class Main {
    public static void main(String[] args) {
//        us.codecraft.webmagic.SpiderUtils spider = us.codecraft.webmagic.SpiderUtils.create(new PageProcessor() {
//            @Override
//            public void process(Page page) {
//                List<Object> ips = new ArrayList<>();
//                List<Object> ports = new ArrayList<>();
//                String rawText = page.getRawText();
//
//                String[] split = rawText.split("\r\n");
//                for (String s : split) {
//                    String[] ipPort = s.split(":");
//                    ips.add(ipPort[0]);
//                    ports.add(ipPort[1]);
//                }
//                page.putField("ips", ips);
//                page.putField("ports", ports);
//            }
//
//            @Override
//            public Site getSite() {
//                return Site.me().addHeader("Connection", "keep-alive")
//                        .addHeader("Upgrade-Insecure-Requests", "1")
//                        .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko)")
//                        .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
//                        .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
//                        .addHeader("Cache-Control", "max-age=0");
//            }
//        });
//        spider.addUrl("http://music.163.com/song?id=479408220");
//        spider.addPipeline((resultItems, task) -> {
//            List<Selectable> ips = resultItems.get("ips");
//            List<Selectable> ports = resultItems.get("ports");
//            System.out.println(ips + ":" + ports);
//        }).run();
        SpiderUtils spiderUtils = new SpiderUtils();
        spiderUtils.getUserPlayList();
    }
}
