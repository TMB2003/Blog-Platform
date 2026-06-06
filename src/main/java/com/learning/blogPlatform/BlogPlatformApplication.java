package com.learning.blogPlatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
//@EnableTransactionManagement
public class BlogPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogPlatformApplication.class, args);
	}

//	@Bean
//	public PlatformTransactionManager add(MongoDatabaseFactory dbFactory){
//		return new MongoTransactionManager(dbFactory);
//	}
}
