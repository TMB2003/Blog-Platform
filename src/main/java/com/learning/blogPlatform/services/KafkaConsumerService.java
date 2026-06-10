//package com.learning.blogPlatform.services;
//
//import com.learning.blogPlatform.entities.NotificationEvent;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//public class KafkaConsumerService {
//
//    @Autowired
//    private EmailService emailService;
//
//    @KafkaListener(topics = "post-notification", groupId = "blog-platform")
//    public void handlePostNotification(NotificationEvent event){
//        emailService.sendMail(event);
//    }
//
//    @KafkaListener(topics = "like-notification", groupId = "blog-platform")
//    public void handleLikeNotification(NotificationEvent event){
//        emailService.sendMail(event);
//    }
//
//}
