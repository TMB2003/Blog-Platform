package com.learning.blogPlatform.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class NotificationEvent {
    private String toEmail;
    private String subject;
    private String body;
}
