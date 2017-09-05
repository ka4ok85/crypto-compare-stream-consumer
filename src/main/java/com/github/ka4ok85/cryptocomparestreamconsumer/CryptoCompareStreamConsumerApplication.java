package com.github.ka4ok85.cryptocomparestreamconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class CryptoCompareStreamConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoCompareStreamConsumerApplication.class, args);
	}
}
