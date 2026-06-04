package com.learning.blogPlatform.repositories;

import com.learning.blogPlatform.entities.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
}
