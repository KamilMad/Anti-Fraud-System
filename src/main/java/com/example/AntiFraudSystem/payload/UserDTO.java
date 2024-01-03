package com.example.AntiFraudSystem.payload;

public record UserDTO(
        Long id,
        String name,
        String username,
        String role
) {
}
