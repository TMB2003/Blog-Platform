package com.learning.blogPlatform.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "likes", uniqueConstraints = @UniqueConstraint(
        columnNames = {"postId", "userName"}
))
@Data
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String postId;

    @Column(nullable = false)
    private String userName;
}
