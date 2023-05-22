package com.example.AntiFraudSystem.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Component;


@Component
@Data
public class Transaction {

    @Min(value = 1)
    @NotNull
    private Long amount;

}
