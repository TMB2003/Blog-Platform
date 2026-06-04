package com.learning.blogPlatform.entities;

import com.learning.blogPlatform.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    @NonNull
    @Indexed(unique = true)
    private String userName;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    private Role role;
}
