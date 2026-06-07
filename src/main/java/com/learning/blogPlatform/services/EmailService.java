package com.learning.blogPlatform.services;

import com.learning.blogPlatform.entities.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(NotificationEvent event){
        try{
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(event.getToEmail());
            mail.setSubject(event.getSubject());
            mail.setText(event.getBody());
            javaMailSender.send(mail);
        } catch (Exception e) {
            log.error("Exception while sending mail: ", e);
        }
    }
}
