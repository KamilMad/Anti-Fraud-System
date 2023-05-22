package com.example.AntiFraudSystem.errors;

public class CardAlreadyInDatabase extends RuntimeException{

    public CardAlreadyInDatabase(String message) {
        super(message);
    }
}
