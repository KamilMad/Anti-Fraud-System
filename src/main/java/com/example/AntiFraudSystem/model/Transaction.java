package com.example.AntiFraudSystem.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;


@Component

public class Transaction {

    @Min(value = 1)
    @NotNull
    private Long amount;

    public Transaction(){}

    public Transaction(Long amount) {
        this.amount = amount;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
