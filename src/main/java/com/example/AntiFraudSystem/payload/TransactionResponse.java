package com.example.AntiFraudSystem.payload;

import com.example.AntiFraudSystem.utilities.Status;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TransactionResponse {

    private Status result;

    private String info;

}

