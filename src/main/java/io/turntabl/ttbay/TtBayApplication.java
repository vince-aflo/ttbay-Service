package io.turntabl.ttbay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TtBayApplication {

	public static void main(String[] args) {
		SpringApplication.run(TtBayApplication.class, args);
	}

}
