package com.example.AntiFraudSystem.errors;

public class AddressNotFoundException extends RuntimeException{

    public AddressNotFoundException(String message) {
        super(message);
    }
}
