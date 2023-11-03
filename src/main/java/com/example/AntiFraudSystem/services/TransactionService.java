package com.example.AntiFraudSystem.services;

import com.example.AntiFraudSystem.errors.CardNumberNotValid;
import com.example.AntiFraudSystem.model.CodeEnum;
import com.example.AntiFraudSystem.model.Transaction;
import com.example.AntiFraudSystem.payload.TransactionRequestDto;
import com.example.AntiFraudSystem.payload.TransactionResponse;
import com.example.AntiFraudSystem.repositories.TransactionRepository;
import com.example.AntiFraudSystem.utilities.LuhnAlgorithm;
import com.example.AntiFraudSystem.utilities.TransactionStatus;
import com.example.AntiFraudSystem.utilities.TransactionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionUtils transactionUtils;
    private final TransactionRepository transactionRepository;

    TransactionService(TransactionUtils transactionUtils, TransactionRepository transactionRepository) {
        this.transactionUtils = transactionUtils;
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponse makeTransaction(Transaction transaction){
        transactionRepository.save(transaction);

        return validate(transaction);
    }

    public TransactionResponse validate(Transaction transaction2) {

        if (!LuhnAlgorithm.isValidCardNumber(transaction2.getNumber())) {
            throw new CardNumberNotValid("Card number: " + transaction2.getNumber() + " not valid");
        }

        if (!isRegionValid(CodeEnum.valueOf(transaction2.getRegion()))){
            throw new IllegalArgumentException("Region not Valid");
        }

        TransactionResponse result = new TransactionResponse();
        TransactionRequestDto transaction = mapEntityToDto(transaction2);

        TransactionStatus transactionStatus = transactionUtils.getStatus(transaction);

        result.setResult(transactionStatus.getStatus());

        List<String> reasons = transactionStatus.getReasons();

        if (result.getResult().toString().equals("ALLOWED"))
            reasons.add("none");

        reasons.sort(String::compareTo);
        String info = String.join(", ", reasons);

        result.setInfo(info);
        return result;
    }


    private boolean isRegionValid(CodeEnum region){
        for (CodeEnum r : CodeEnum.values()){
            if (region.equals(r))
                return true;
        }
        return false;
    }

    public TransactionRequestDto mapEntityToDto(Transaction transaction){

        TransactionRequestDto dto = new TransactionRequestDto();

        dto.setAmount(transaction.getAmount());
        dto.setDate(transaction.getDate());
        dto.setNumber(transaction.getNumber());
        dto.setIp(transaction.getIp());
        dto.setRegion(transaction.getRegion().toString());

        return dto;
    }

}
