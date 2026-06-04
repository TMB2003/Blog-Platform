package com.learning.blogPlatform.entities;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    @NonNull
    private String userId;
    private String imageUrl;
    private String caption;
    private String likeCount;
    @CreatedDate
    private LocalDateTime createdAt;
}
