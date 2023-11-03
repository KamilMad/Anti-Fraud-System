package com.example.AntiFraudSystem.service;

import com.example.AntiFraudSystem.model.Transaction;
import com.example.AntiFraudSystem.payload.TransactionRequestDto;
import com.example.AntiFraudSystem.payload.TransactionResponse;
import com.example.AntiFraudSystem.repositories.TransactionRepository;
import com.example.AntiFraudSystem.services.TransactionService;
import com.example.AntiFraudSystem.utilities.Status;
import com.example.AntiFraudSystem.utilities.TransactionStatus;
import com.example.AntiFraudSystem.utilities.TransactionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionUtils transactionUtils;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction validTransaction;

    @BeforeEach
    public void init() {
        validTransaction = new Transaction();
        validTransaction.setIp("192.168.1.1");
        validTransaction.setRegion("EAP");
        validTransaction.setNumber("4000008449433403");
        validTransaction.setAmount(300L);
        validTransaction.setDate(LocalDateTime.now());
    }

    @Test
    public void testMakeTransaction_ValidTransaction() {

        TransactionStatus transactionStatus = new TransactionStatus();
        transactionStatus.setStatus(Status.ALLOWED);

        TransactionResponse response = new TransactionResponse();
        response.setResult(Status.ALLOWED);
        response.setInfo("none");

        List<String> reasons = new ArrayList<>();
        transactionStatus.setReasons(reasons);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(validTransaction);

        when(transactionUtils.getStatus(any(TransactionRequestDto.class))).thenReturn(transactionStatus);

        TransactionResponse result = transactionService.makeTransaction(validTransaction);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(response, result);
    }
}

