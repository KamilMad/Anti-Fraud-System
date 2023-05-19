package com.example.AntiFraudSystem.payload;

import lombok.Data;

@Data
public class UserAccessRequest {

    private String username;

    private String operation;
}
