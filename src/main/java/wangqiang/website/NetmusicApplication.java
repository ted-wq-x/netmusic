package wangqiang.website;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableAsync //利用@EnableAsync注解开启异步任务支持
public class NetmusicApplication implements AsyncConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(NetmusicApplication.class, args);
	}

	@Override
	public Executor getAsyncExecutor() {
		ExecutorService taskExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);
		return taskExecutor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return null;
	}
}
