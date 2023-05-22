package com.example.AntiFraudSystem.services;

import com.example.AntiFraudSystem.errors.CardNumberNotValid;
import com.example.AntiFraudSystem.model.Transaction;
import com.example.AntiFraudSystem.payload.TransactionDto;
import com.example.AntiFraudSystem.payload.TransactionRequestDto;
import com.example.AntiFraudSystem.utilities.LuhnAlgorithm;
import com.example.AntiFraudSystem.utilities.TransactionStatus;
import com.example.AntiFraudSystem.utilities.TransactionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionUtils transactionUtils;

    public TransactionService(TransactionUtils transactionUtils) {
        this.transactionUtils = transactionUtils;
    }

    public TransactionDto validate(TransactionRequestDto transaction) {
        if (!LuhnAlgorithm.isValidCardNumber(transaction.getNumber())) {
            throw new CardNumberNotValid("Card number: " + transaction.getNumber() + " not valid");
        }

        TransactionDto result = new TransactionDto();
        List<String> reasons = new ArrayList<>();

        if (transactionUtils.isAllowedStatus(transaction)) {
            result.setResult(TransactionStatus.ALLOWED);
        } else if (transactionUtils.isManualProcessingStatus(transaction)) {
            reasons.add("amount");
            result.setResult(TransactionStatus.MANUAL_PROCESSING);
        } else if (transactionUtils.isProhibitedStatus(transaction, reasons)) {
            result.setResult(TransactionStatus.PROHIBITED);
        }

        reasons.sort(String::compareTo);
        String info = String.join(", ", reasons);

        result.setInfo(info);
        return result;
    }
}
