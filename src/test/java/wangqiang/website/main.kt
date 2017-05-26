package wangqiang.website

import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.Spider
import us.codecraft.webmagic.processor.PageProcessor
import us.codecraft.webmagic.selector.Selectable
import java.util.*

/**
 * Created by wangq on 2017/5/22.
 */
fun main(args: Array<String>) {
    val spider = Spider.create(object : PageProcessor {
        override fun getSite(): Site {
            return Site.me().addHeader("Connection", "keep-alive")
                    .addHeader("Upgrade-Insecure-Requests", "1")
                    .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko)")
                    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
                    .addHeader("Cache-Control", "max-age=0")
        }

        override fun process(p0: Page?) {
            val ips = ArrayList <Any>()
            val ports = ArrayList<Any>()
            val split = p0?.rawText?.split("\r\n")
            split?.forEach {
                val ipPort = it.split(":")
                ips.add(ipPort[0])
                ports.add(ipPort[1])
            }
            p0?.putField("ips", ips)
            p0?.putField("ports", ports)
        }
    })
    spider.addUrl("http://music.163.com/song?id=479408220")

    /**
     * addPipeline方法中存放的是pipeline类，该类的一个函数式接口，所以可以使用lambda表达式去创建
     */
    spider.addPipeline { resultItems, task ->
        val ips = resultItems.get<List<Selectable>>("ips")
        val ports = resultItems.get<List<Selectable>>("ports")
        println(ips + ":" + ports)
    }.run()
}




