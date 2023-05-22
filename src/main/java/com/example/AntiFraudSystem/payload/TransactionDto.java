package com.example.AntiFraudSystem.payload;

import com.example.AntiFraudSystem.utilities.TransactionStatus;
import lombok.Data;

@Data
public class TransactionDto {

    private TransactionStatus result;

    private String info;

}