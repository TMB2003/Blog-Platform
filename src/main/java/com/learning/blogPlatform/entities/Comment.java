package com.learning.blogPlatform.entities;


import com.learning.blogPlatform.enums.TargetType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "comments")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType targetType;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String content;

    private Long likeCount = 0L;
}