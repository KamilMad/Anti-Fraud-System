package com.example.AntiFraudSystem.errors;

public class AddressIpAlreadyInDataBase extends RuntimeException{

    public AddressIpAlreadyInDataBase(String message) {
        super(message);
    }
}
