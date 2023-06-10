package com.example.AntiFraudSystem.payload;

import com.example.AntiFraudSystem.utilities.Status;
import lombok.Data;

@Data
public class TransactionResponse {

    private Status result;

    private String info;

}

