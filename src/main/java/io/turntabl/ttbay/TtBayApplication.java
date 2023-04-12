package io.turntabl.ttbay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class TtBayApplication {

    public static void main(String[] args) {
        SpringApplication.run(TtBayApplication.class, args);
    }

//	@Bean
//	public Executor taskExecutor() {
//		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//		executor.setCorePoolSize(3);
//		executor.setMaxPoolSize(3);
//		executor.setQueueCapacity(500);
//		executor.setThreadNamePrefix("GmailService-");
//		executor.initialize();
//		return executor;
//	}

}
