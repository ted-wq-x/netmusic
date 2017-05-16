package wangqiang.website.listener;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import wangqiang.website.modal.MusicVo;
import wangqiang.website.service.MusicRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static wangqiang.website.Spider.spiderMetMusic;

/**
 * Created by wangq on 2017/5/15.
 */
@Component
public class InitListener  implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitListener.class);

    @Autowired
    private MusicRepository musicRepository;

    private ExecutorService service = new ScheduledThreadPoolExecutor((Runtime.getRuntime().availableProcessors() + 1)*2);

    private AtomicInteger atomicInteger = null;

    /**
     * 缓存爬取的数据，达到100个时存入数据库
     */
    private BlockingQueue<MusicVo> blockingQueue = new LinkedBlockingQueue<>();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        LOGGER.info("Enter onApplicationEvent method:init listener");
        atomicInteger = new AtomicInteger(getCurrentNum());
        new Thread(this::main).start();
        LOGGER.info("Exit onApplicationEvent method.");
    }


    private int getCurrentNum(){
        return musicRepository.selectMax()+1;
    }

    private void main(){
        LOGGER.info("Enter spider main method");
        while (true) {
            int andIncrement = atomicInteger.getAndIncrement();
            if (andIncrement >= 1000000000) {
                LOGGER.info("num gt 1000000000");
                insertMusic();
                break;
            }
            Future submit = service.submit(new task(andIncrement));
            try {
                MusicVo musicVousicVo = (MusicVo) submit.get();
                if (musicVousicVo != null) {
                    blockingQueue.add(musicVousicVo);
                    if(blockingQueue.size()==100){
                        insertMusic();
                        LOGGER.info("queue is full 100 and  insert into databases");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("Exit spider main method");
    }

    private void insertMusic(){
        LOGGER.info("Enter insertMusic method");
        List<MusicVo> temp = new ArrayList<>();
        blockingQueue.drainTo(temp);
        musicRepository.save(temp);
        LOGGER.info("Exit insertMusic method");
    }


     class task implements Callable<MusicVo> {

         private int andIncrement;

         public task(int andIncrement) {
             this.andIncrement = andIncrement;
         }

         @Override
        public MusicVo call() throws Exception {
            LOGGER.info("Enter task and current num={}",andIncrement);
            MusicVo musicVo=null;
            int again=0;
            JSONObject jsonObject = spiderMetMusic("http://music.163.com/weapi/v1/resource/comments/R_SO_4_" + andIncrement + "?csrf_token=");
             if (jsonObject != null && jsonObject.getInteger("code") == 200) {
                 Integer total = jsonObject.getInteger("total");
                 if (total != 0) {
                     musicVo = new MusicVo();
                     musicVo.setCommitTotal(total);
                     musicVo.setId(andIncrement);
                 }
             } else {
//                 again++;
//                 TODO ip失效，重试
             }
            LOGGER.info("Exit task and musicVo={}",musicVo);
            return musicVo;
        }
    }

}
