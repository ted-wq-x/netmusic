package wangqiang.website.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wangqiang.website.service.MusicRepository;

/**
 * Created by wangq on 2017/5/26.
 */
@Component
public class SpiderUser {

    @Autowired
    private MusicRepository musicRepository;

    public void startThisSpider(){
        Integer songId = musicRepository.selectMax();


    }


}
