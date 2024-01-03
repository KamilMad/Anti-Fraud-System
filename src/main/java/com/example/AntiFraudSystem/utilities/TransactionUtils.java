package com.example.AntiFraudSystem.utilities;

import com.example.AntiFraudSystem.model.Transaction;
import com.example.AntiFraudSystem.repositories.AddressIpRepository;
import com.example.AntiFraudSystem.repositories.CardRepository;
import com.example.AntiFraudSystem.repositories.TransactionRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TransactionUtils {

    private final CardRepository cardRepository;
    private final AddressIpRepository addressIpRepository;
    private final TransactionRepository transactionRepository;

    public TransactionUtils(CardRepository cardRepository, AddressIpRepository addressIpRepository, TransactionRepository transactionRepository) {
        this.cardRepository = cardRepository;
        this.addressIpRepository = addressIpRepository;
        this.transactionRepository = transactionRepository;
    }
    public TransactionStatus getStatus(Transaction transaction) {
        List<String> reasons = new ArrayList<>();

        List<Transaction> pastTransactions = transactionRepository.findAllByNumberAndDateBetween(
                transaction.getNumber(),
                transaction.getDate().minusHours(1),
                transaction.getDate()
        );

        Set<String> distinctRegions = new HashSet<>();
        Set<String> distinctIPs = new HashSet<>();

        for (Transaction pastTransaction : pastTransactions) {
            distinctRegions.add(pastTransaction.getRegion());
            distinctIPs.add(pastTransaction.getIp());
        }

        Status status = Status.ALLOWED;

        if (isIpSuspicious(transaction) || isCardStolen(transaction)
                || isAmountProhibited(transaction) || distinctRegions.size() > 3
                || distinctIPs.size() > 3) {
            status = Status.PROHIBITED;

            if (isIpSuspicious(transaction)) {
                reasons.add("ip");
            }

            if (isCardStolen(transaction)) {
                reasons.add("card-number");
            }

            if (distinctRegions.size() > 3) {
                reasons.add("region-correlation");
            }

            if (distinctIPs.size() > 3) {
                reasons.add("ip-correlation");
            }

            if (isAmountProhibited(transaction)) {
                reasons.add("amount");
            }
        } else if (isManualProcessingAmount(transaction) || distinctRegions.size() == 3
                || distinctIPs.size() == 3) {
            status = Status.MANUAL_PROCESSING;

            if (isManualProcessingAmount(transaction)) {
                reasons.add("amount");
            }

            if (distinctRegions.size() == 3) {
                reasons.add("region-correlation");
            }

            if (distinctIPs.size() == 3) {
                reasons.add("ip-correlation");
            }
        }

        return new TransactionStatus(status, reasons);
    }

    private boolean isAmountProhibited(Transaction transaction) {
        long amount = transaction.getAmount();
        return amount > 1500;
    }

    private boolean isManualProcessingAmount(Transaction transaction) {
        long amount = transaction.getAmount();
        return amount > 200 && amount <= 1500;
    }

    private boolean isCardStolen(Transaction transaction) {
        return cardRepository.findByNumber(transaction.getNumber()).isPresent();
    }

    private boolean isIpSuspicious(Transaction transaction) {
        return addressIpRepository.findByIp(transaction.getIp()).isPresent();
    }

    public void addReasonsForProhibited(Transaction transaction, List<String> reasons, Set<String> distinctRegions, Set<String> distinctIPs){
        if (isIpSuspicious(transaction)) {
            reasons.add("ip");
        }

        if (isCardStolen(transaction)) {
            reasons.add("card-number");
        }

        if (distinctRegions.size() > 3) {
            reasons.add("region-correlation");
        }

        if (distinctIPs.size() > 3) {
            reasons.add("ip-correlation");
        }

        if (isAmountProhibited(transaction)) {
            reasons.add("amount");
        }
    }
}
