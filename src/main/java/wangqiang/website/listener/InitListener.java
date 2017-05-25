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

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static wangqiang.website.utils.Spider.spiderMetMusic;

/**
 * Created by wangq on 2017/5/15.
 */
@Component
public class InitListener implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitListener.class);

    @Autowired
    private MusicRepository musicRepository;

    private ExecutorCompletionService<MusicVo> service = null;

    private AtomicInteger atomicInteger = null;

    /**
     * 限制向任务队列提交任务的个数
     */
    private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>(25);
    /**
     * 缓存爬取的数据，达到100个时存入数据库
     */
    private BlockingQueue<MusicVo> blockingQueue = new LinkedBlockingQueue<>();

    @PostConstruct
    private void init() {
//     存放任务执行结果的队列
        BlockingQueue<Future<MusicVo>> completeTask = new LinkedBlockingQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() + 1) * 2);
        service = new ExecutorCompletionService<>(executorService, completeTask);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        LOGGER.info("Enter onApplicationEvent method:init listener");
        atomicInteger = new AtomicInteger(getCurrentNum());
        new Thread(this::main).start();
        new Thread(this::complateTask).start();
        LOGGER.info("Exit onApplicationEvent method.");
    }


    private int getCurrentNum() {
        return musicRepository.selectMax() + 1;
    }

    private void main() {
        LOGGER.info("Enter spider main method");
        while (true) {
            int andIncrement = atomicInteger.getAndIncrement();
            if (andIncrement >= 1000000000) {
                LOGGER.info("num gt 1000000000");
                insertMusic();
                break;
            }
            try {
                queue.put(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LOGGER.info("submit task num={}", andIncrement);
            service.submit(new task(andIncrement));
        }
        LOGGER.info("Exit spider main method");
    }

    /**
     * 执行任务合并
     */
    private void complateTask() {
        LOGGER.info("Enter spider complateTask method");
        while (true) {
            try {
                Future<MusicVo> take = service.take();
//                出现null exception
                MusicVo musicVousicVo = take.get();
                if (musicVousicVo != null) {
                    blockingQueue.add(musicVousicVo);
                    if (blockingQueue.size() == 100) {
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
    }

    private void insertMusic() {
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
            LOGGER.info("Enter task and current num={}", andIncrement);
            queue.take();
            MusicVo musicVo = null;
            JSONObject jsonObject = spiderMetMusic("http://music.163.com/weapi/v1/resource/comments/R_SO_4_" + andIncrement + "?csrf_token=");
            if (jsonObject != null && jsonObject.getInteger("code") != null && jsonObject.getInteger("code") == 200) {
                Integer total = jsonObject.getInteger("total");
                if (total != 0) {
                    musicVo = new MusicVo();
                    musicVo.setCommitTotal(total);
                    musicVo.setId(andIncrement);
                }
            }
            LOGGER.info("Exit task and musicVo={}", musicVo);
            return musicVo;
        }
    }

}
