package com.github.ka4ok85.cryptocomparestreamconsumer.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
public class MongoConfig {

	@Value("${spring.data.mongodb.host}")
	private String host;
	@Value("${spring.data.mongodb.port}")
	private Integer port;
	@Value("${spring.data.mongodb.username}")
	private String username;
	@Value("${spring.data.mongodb.database}")
	private String database;
	@Value("${spring.data.mongodb.authentication-database}")
	private String authDatabase;
	@Value("${spring.data.mongodb.password}")
	private String password;

	@Bean
	public MongoClient mongo() throws Exception {
		return new MongoClient(Collections.singletonList(new ServerAddress(host, port)), Collections
				.singletonList(MongoCredential.createCredential(username, authDatabase, password.toCharArray())));

	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongo(), database);
	}

}
