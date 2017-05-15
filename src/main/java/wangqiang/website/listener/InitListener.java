package wangqiang.website.listener;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import wangqiang.website.modal.MusicVo;
import wangqiang.website.service.MusicRepository;

import java.util.HashMap;
import java.util.Map;

import static wangqiang.website.Spider.spiderMetMusic;

/**
 * Created by wangq on 2017/5/15.
 */
@Component
public class InitListener  implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitListener.class);

    @Autowired
    private MusicRepository musicRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        LOGGER.debug("Enter onApplicationEvent method:init listener");
        doIt();
        LOGGER.debug("Exit onApplicationEvent method.");
    }


    @Async
    public void doIt(){
        for (int i = 1; i < 1000000000; i++) {
            JSONObject jsonObject = spiderMetMusic("http://music.163.com/weapi/v1/resource/comments/R_SO_4_" + i + "?csrf_token=");
            if (jsonObject.getInteger("code") == 200) {
                Integer total = jsonObject.getInteger("total");
                if (total != 0) {
                    MusicVo musicVo = new MusicVo();
                    musicVo.setCommitTotal(total);
                    musicVo.setId(i);
                    musicRepository.save(musicVo);
                    LOGGER.info("do something and num={} and this num insert into databases",i);
                }
            }
            LOGGER.info("do something and num={}",i);
        }
    }
}
