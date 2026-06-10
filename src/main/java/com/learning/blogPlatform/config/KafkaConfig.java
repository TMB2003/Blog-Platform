//package com.learning.blogPlatform.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.TopicBuilder;
//
//@Configuration
//public class KafkaConfig {
//
//    @Bean
//    public NewTopic postNotificationTopic(){
//        return TopicBuilder.name("post-notification")
//                .partitions(1)
//                .replicas(1)
//                .build();
//    }
//
//    @Bean
//    public NewTopic likeNotificationTopic(){
//        return TopicBuilder.name("like-notification")
//                .partitions(1)
//                .replicas(1)
//                .build();
//    }
//}
