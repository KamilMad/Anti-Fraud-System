package com.example.AntiFraudSystem.utilities;

import com.example.AntiFraudSystem.model.Transaction;
import com.example.AntiFraudSystem.payload.TransactionRequestDto;
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
    public TransactionStatus getStatus(TransactionRequestDto transaction) {
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

    private boolean isAmountProhibited(TransactionRequestDto transaction) {
        long amount = transaction.getAmount();
        return amount > 1500;
    }

    private boolean isManualProcessingAmount(TransactionRequestDto transaction) {
        long amount = transaction.getAmount();
        return amount > 200 && amount <= 1500;
    }

    private boolean isCardStolen(TransactionRequestDto transaction) {
        return cardRepository.findByNumber(transaction.getNumber()).isPresent();
    }

    private boolean isIpSuspicious(TransactionRequestDto transaction) {
        return addressIpRepository.findByIp(transaction.getIp()).isPresent();
    }

    private boolean isRegionCorrelatedWithTwo(TransactionRequestDto transaction) {
        long count = transactionRepository.countDistinctRegions(transaction.getNumber(),
                transaction.getRegion(), transaction.getDate().minusHours(1), transaction.getDate());
        return count == 2;
    }

    private boolean isIpCorrelatedWithTwo(TransactionRequestDto transaction) {
        long count = transactionRepository.countDistinctIpAddresses(transaction.getNumber(),
                transaction.getRegion(), transaction.getDate().minusHours(1), transaction.getDate());
        return count == 2;
    }

    private boolean isRegionCorrelatedWithMoreThanTwo(TransactionRequestDto transaction) {
        long count = transactionRepository.countDistinctRegions(transaction.getNumber(),
                transaction.getRegion(), transaction.getDate().minusHours(1), transaction.getDate());
        return count >= 3;
    }

    private boolean isIpCorrelatedWithMoreThanTwo(TransactionRequestDto transaction) {
        long count = transactionRepository.countDistinctIpAddresses(transaction.getNumber(),
                transaction.getRegion(), transaction.getDate().minusHours(1), transaction.getDate());
        return count >= 3;
    }
}
