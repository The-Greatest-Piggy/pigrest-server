package app.pigrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PigrestApplication {

	public static void main(String[] args) {
		SpringApplication.run(PigrestApplication.class, args);
	}

}
