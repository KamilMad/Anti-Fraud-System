package com.example.AntiFraudSystem.errors;

public class RoleAlreadyAssignedException extends RuntimeException{
    public RoleAlreadyAssignedException(String message) {
        super(message);
    }
}
