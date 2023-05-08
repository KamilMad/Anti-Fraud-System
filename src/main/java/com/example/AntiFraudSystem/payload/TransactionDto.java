package com.example.AntiFraudSystem.payload;

import com.example.AntiFraudSystem.utilities.TransactionStatus;

public class TransactionDto {

    private TransactionStatus result;

    public TransactionDto() {
    }

    public TransactionDto(TransactionStatus response) {
        this.result = response;
    }

    public TransactionStatus getResult() {
        return result;
    }

    public void setResult(TransactionStatus result) {
        this.result = result;
    }
}
