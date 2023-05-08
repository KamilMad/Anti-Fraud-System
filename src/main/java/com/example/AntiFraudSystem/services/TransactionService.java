package com.example.AntiFraudSystem.services;

import com.example.AntiFraudSystem.model.Transaction;
import com.example.AntiFraudSystem.payload.TransactionDto;
import com.example.AntiFraudSystem.utilities.TransactionStatus;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    public TransactionDto validate(Transaction transaction){
        TransactionDto result = new TransactionDto();

        if (transaction.getAmount() <= 200)
            result.setResult(TransactionStatus.ALLOWED);
        else if (transaction.getAmount() <= 1500) {
            result.setResult(TransactionStatus.MANUAL_PROCESSING);
        }
        else
            result.setResult(TransactionStatus.PROHIBITED);

        return result;
    }
}
