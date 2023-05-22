package com.example.AntiFraudSystem.utilities;

import com.example.AntiFraudSystem.payload.TransactionRequestDto;
import com.example.AntiFraudSystem.repositories.AddressIpRepository;
import com.example.AntiFraudSystem.repositories.CardRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionUtils {

    private final CardRepository cardRepository;
    private final AddressIpRepository addressIpRepository;

    public TransactionUtils(CardRepository cardRepository, AddressIpRepository addressIpRepository) {
        this.cardRepository = cardRepository;
        this.addressIpRepository = addressIpRepository;
    }

    public boolean isAllowedStatus(TransactionRequestDto transaction) {
        long amount = transaction.getAmount();
        return amount <= 200 && !isCardStolen(transaction) && !isIpSuspicious(transaction);
    }

    public boolean isManualProcessingStatus(TransactionRequestDto transaction) {
        long amount = transaction.getAmount();
        return amount > 200 && amount <= 1500 && !isCardStolen(transaction) && !isIpSuspicious(transaction);
    }

    public boolean isProhibitedStatus(TransactionRequestDto transaction, List<String> reasons) {
        boolean result = false;

        if (transaction.getAmount() > 1500) {
            reasons.add("amount");
            result = true;
        }

        if (isCardStolen(transaction)) {
            reasons.add("card-number");
            result = true;
        }

        if (isIpSuspicious(transaction)) {
            reasons.add("ip");
            result = true;
        }

        return result;
    }

    private boolean isCardStolen(TransactionRequestDto transaction) {
        return cardRepository.findByNumber(transaction.getNumber()).isPresent();
    }

    private boolean isIpSuspicious(TransactionRequestDto transaction) {
        return addressIpRepository.findByIp(transaction.getIp()).isPresent();
    }
}
