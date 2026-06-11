package com.learning.blogPlatform.services;

import com.learning.blogPlatform.entities.NotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    @Autowired
    private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void sendPostNotification(NotificationEvent event){
        kafkaTemplate.send("post-notification", event);
    }

    public void sendLikeNotification(NotificationEvent event){
        kafkaTemplate.send("like-notification", event);
    }
}
