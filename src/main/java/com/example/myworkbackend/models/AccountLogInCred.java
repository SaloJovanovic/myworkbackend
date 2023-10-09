package com.example.mywork.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@Builder
public class AccountLogInCred {
    public boolean success;
    public String id;
    public String token;
}
