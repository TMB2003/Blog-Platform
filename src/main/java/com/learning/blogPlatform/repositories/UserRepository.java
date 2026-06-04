package com.learning.blogPlatform.repositories;

import com.learning.blogPlatform.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUserName(String userName);

    void deleteByUserName(String userName);
}