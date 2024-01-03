package com.example.AntiFraudSystem.services;

import com.example.AntiFraudSystem.errors.CardNumberNotValid;
import com.example.AntiFraudSystem.model.CodeEnum;
import com.example.AntiFraudSystem.model.Transaction;
import com.example.AntiFraudSystem.payload.TransactionRequestDTO;
import com.example.AntiFraudSystem.payload.TransactionResponseDTO;
import com.example.AntiFraudSystem.repositories.TransactionRepository;
import com.example.AntiFraudSystem.utilities.LuhnAlgorithm;
import com.example.AntiFraudSystem.utilities.Status;
import com.example.AntiFraudSystem.utilities.TransactionStatus;
import com.example.AntiFraudSystem.utilities.TransactionUtils;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionUtils transactionUtils;
    private final TransactionRepository transactionRepository;

    TransactionService(TransactionUtils transactionUtils, TransactionRepository transactionRepository) {
        this.transactionUtils = transactionUtils;
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponseDTO makeTransaction(Transaction transaction){
        transactionRepository.save(transaction);

        return validate(transaction);
    }

    private TransactionResponseDTO validate(Transaction transaction) {

        validateCardNumber(transaction.getNumber());
        validateRegion(transaction.getRegion());

        TransactionRequestDTO transactionRequestDto = mapEntityToDto(transaction);
        TransactionStatus transactionStatus = transactionUtils.getStatus(transaction);

        List<String> reasons = transactionStatus.getReasons();
        if (Status.ALLOWED.equals(transactionStatus.getStatus()))
            reasons.add("none");

        String info = processReasons(reasons);

        return buildTransactionResponse(transactionStatus.getStatus(), info);
    }

    private TransactionResponseDTO buildTransactionResponse(Status result, String info) {
        return new TransactionResponseDTO(result, info);
    }

    private void validateCardNumber(String cardNumber) {
        if (!LuhnAlgorithm.isValidCardNumber(cardNumber)) {
            throw new CardNumberNotValid("Card number: " + cardNumber + " not valid");
        }
    }

    private void validateRegion(String region) {
        if (!isRegionValid(CodeEnum.valueOf(region))) {
            throw new IllegalArgumentException("Region not valid");
        }
    }
    private String processReasons(List<String> reasons) {
        reasons.sort(String::compareTo);
        return String.join(", ", reasons);
    }

    private boolean isRegionValid(CodeEnum region){
        return EnumSet.allOf(CodeEnum.class).contains(region);
    }

    public TransactionRequestDTO mapEntityToDto(Transaction transaction){

        return new TransactionRequestDTO(
                transaction.getAmount(),
                transaction.getIp(),
                transaction.getNumber(),
                transaction.getRegion(),
                transaction.getDate());
    }

}
