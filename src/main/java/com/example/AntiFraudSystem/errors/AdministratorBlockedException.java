package com.example.AntiFraudSystem.errors;

public class AdministratorBlockedException extends RuntimeException{
    public AdministratorBlockedException(String message) {
        super(message);
    }
}
