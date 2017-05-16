package wangqiang.website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAsync //利用@EnableAsync注解开启异步任务支持
public class NetmusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetmusicApplication.class, args);
    }

}
