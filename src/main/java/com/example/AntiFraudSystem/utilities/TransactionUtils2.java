package com.example.AntiFraudSystem.utilities;

import com.example.AntiFraudSystem.model.Transaction;
import com.example.AntiFraudSystem.payload.TransactionRequestDto;
import com.example.AntiFraudSystem.repositories.AddressIpRepository;
import com.example.AntiFraudSystem.repositories.CardRepository;
import com.example.AntiFraudSystem.repositories.TransactionRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransactionUtils2 {

    private final CardRepository cardRepository;
    private final AddressIpRepository addressIpRepository;
    private final TransactionRepository transactionRepository;

    public TransactionUtils2(CardRepository cardRepository, AddressIpRepository addressIpRepository, TransactionRepository transactionRepository) {
        this.cardRepository = cardRepository;
        this.addressIpRepository = addressIpRepository;
        this.transactionRepository = transactionRepository;
    }

    public TransactionStatus getStatus(Transaction transaction) {
        List<String> reasons = new ArrayList<>();
        Set<String> distinctRegions = new HashSet<>();
        Set<String> distinctIPs = new HashSet<>();

        processPastTransactions(transaction, distinctRegions, distinctIPs);

        Status status = calculateStatus(transaction, reasons, distinctRegions, distinctIPs);

        return new TransactionStatus(status, reasons);
    }

    private void processPastTransactions(Transaction transaction, Set<String> distinctRegions, Set<String> distinctIPs) {
        List<Transaction> pastTransactions = transactionRepository.findAllByNumberAndDateBetween(
                transaction.getNumber(),
                transaction.getDate().minusHours(1),
                transaction.getDate()
        );

        for (Transaction pastTransaction : pastTransactions) {
            distinctRegions.add(pastTransaction.getRegion());
            distinctIPs.add(pastTransaction.getIp());
        }
    }

    private Status calculateStatus(Transaction transaction, List<String> reasons, Set<String> distinctRegions, Set<String> distinctIPs) {
        if (isProhibited(transaction, reasons, distinctRegions, distinctIPs)) {
            return Status.PROHIBITED;
        } else if (isManualProcessing(transaction, reasons, distinctRegions, distinctIPs)) {
            return Status.MANUAL_PROCESSING;
        } else {
            return Status.ALLOWED;
        }
    }

    private boolean isProhibited(Transaction transaction, List<String> reasons, Set<String> distinctRegions, Set<String> distinctIPs) {
        boolean prohibited = isIpSuspicious(transaction) || isCardStolen(transaction)
                || isAmountProhibited(transaction) || distinctRegions.size() > 3
                || distinctIPs.size() > 3;

        if (prohibited) {
            addReasonsForProhibited(transaction, reasons, distinctRegions, distinctIPs);
        }

        return prohibited;
    }

    private void addReasonsForProhibited(Transaction transaction, List<String> reasons, Set<String> distinctRegions, Set<String> distinctIPs) {
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

    private boolean isManualProcessing(Transaction transaction, List<String> reasons, Set<String> distinctRegions, Set<String> distinctIPs) {
        boolean manualProcessing = isManualProcessingAmount(transaction) || distinctRegions.size() == 3
                || distinctIPs.size() == 3;

        if (manualProcessing) {
            addReasonsForManualProcessing(transaction, reasons, distinctRegions, distinctIPs);
        }

        return manualProcessing;
    }

    private void addReasonsForManualProcessing(Transaction transaction, List<String> reasons, Set<String> distinctRegions, Set<String> distinctIPs) {
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
}
