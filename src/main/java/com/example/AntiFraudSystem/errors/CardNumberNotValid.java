package com.example.AntiFraudSystem.errors;

public class CardNumberNotValid extends RuntimeException{

    public CardNumberNotValid(String message) {
        super(message);
    }
}
