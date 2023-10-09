package com.example.mywork.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@Builder
public class Account {
    @Id
    private String id;
    private String name;
    private String username;
    private String password;
    private String role;
}
