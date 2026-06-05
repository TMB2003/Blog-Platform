package com.learning.blogPlatform.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "follows")
@Data
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String followerUserName;

    @Column(nullable = false)
    private String followingUserName;
}