package com.example.AntiFraudSystem.utilities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class TransactionStatus {

    private Status status;
    private List<String> reasons;
    public TransactionStatus(Status status) {
        this.status = status;
    }
}
