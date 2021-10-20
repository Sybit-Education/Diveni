package de.htwg.aume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class AumeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AumeApplication.class, args);
	}

}
