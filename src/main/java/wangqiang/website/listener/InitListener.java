package wangqiang.website.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import wangqiang.website.service.UserRepository;
import wangqiang.website.spider.SongSpider;

import javax.annotation.PostConstruct;


/**
 * Created by wangq on 2017/5/15.
 */
@Component
public class InitListener implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitListener.class);
    @Autowired
    private SpiderMusic spiderMusic;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        LOGGER.info("Enter onApplicationEvent method:init listener");
        spiderMusic.startThisSpider();
        LOGGER.info("Exit onApplicationEvent method.");
    }


    @Autowired
    protected UserRepository userRepository;

    @PostConstruct
    public void init(){
        try {
            SongSpider.uidQueue.put(userRepository.selectMaxId());//初始化数据
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
