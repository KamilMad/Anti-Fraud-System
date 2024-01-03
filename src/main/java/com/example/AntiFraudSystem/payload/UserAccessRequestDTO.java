package com.example.AntiFraudSystem.payload;

public record UserAccessRequestDTO(
        String username,
        String operation) { }
