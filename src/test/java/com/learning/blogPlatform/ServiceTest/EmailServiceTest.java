package com.learning.blogPlatform.ServiceTest;

import com.learning.blogPlatform.entities.NotificationEvent;
import com.learning.blogPlatform.services.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    public void test_EmailSend() {

        NotificationEvent event = new NotificationEvent();
        event.setToEmail("test@gmail.com");
        event.setBody("Body of Mail");
        event.setSubject("Test of Email");

        emailService.sendMail(event);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(javaMailSender).send(captor.capture());

        SimpleMailMessage mail = captor.getValue();

        assertEquals("test@gmail.com", mail.getTo()[0]);
        assertEquals("Test of Email", mail.getSubject());
        assertEquals("Body of Mail", mail.getText());
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    public void test_EmailSend_ExceptionHandled() {

        NotificationEvent event = new NotificationEvent();
        event.setToEmail("test@gmail.com");
        event.setBody("Body of Mail");
        event.setSubject("Test of Email");

        doThrow(new RuntimeException("Mail Error"))
                .when(javaMailSender)
                .send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.sendMail(event));

        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }
}
