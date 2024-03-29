package com.example.AntiFraudSystem.service;

import com.example.AntiFraudSystem.errors.CardNumberNotValid;
import com.example.AntiFraudSystem.model.Transaction;
import com.example.AntiFraudSystem.payload.TransactionResponseDTO;
import com.example.AntiFraudSystem.repositories.TransactionRepository;
import com.example.AntiFraudSystem.services.TransactionService;
import com.example.AntiFraudSystem.utilities.Status;
import com.example.AntiFraudSystem.utilities.TransactionStatus;
import com.example.AntiFraudSystem.utilities.TransactionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

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

        TransactionResponseDTO response = new TransactionResponseDTO(Status.ALLOWED, "none");

        List<String> reasons = new ArrayList<>();
        transactionStatus.setReasons(reasons);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(validTransaction);
        when(transactionUtils.getStatus(any(Transaction.class))).thenReturn(transactionStatus);

        TransactionResponseDTO result = transactionService.makeTransaction(validTransaction);
        assertNotNull(result);
        assertEquals(response, result);

    }

    @ParameterizedTest
    @CsvSource({
            "4532015119473676",
            "4916801319873128",
            "4485567033403440",
            "4716820244404383",
            "4929356649972975",
            "4716453007472149",
            "4532789087199638",
            "4716981858918023",
            "4556348620464042",
            "4532530832551445",
            "4716844319619620",
            "4929499477200296",
            "4716024676106956",
            "4532877283332939",
            "4916935220559393",
            "4716539849822880",
            "4556242273404081",
            "4532811948480197",
    })
    public void testMakeTransaction_InvalidCardNumber(String cardNumber) {
        Transaction invalidCardTransaction= createTransaction(cardNumber, "EAP");

        when(transactionRepository.save(any(Transaction.class))).thenReturn(invalidCardTransaction);

        CardNumberNotValid cardNumberNotValid = assertThrows(CardNumberNotValid.class, () -> transactionService.makeTransaction(invalidCardTransaction));
        assertEquals("Card number: " + invalidCardTransaction.getNumber() + " not valid" ,cardNumberNotValid.getMessage());

    }

    @Test
    public void testMakeTransaction_InvalidRegion() {
        Transaction invalidRegionTransaction = createTransaction("4000008449433403", "PL");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(invalidRegionTransaction);

        IllegalArgumentException regionNotValidException = assertThrows(IllegalArgumentException.class,
                () -> transactionService.makeTransaction(invalidRegionTransaction));

        assertEquals("No enum constant com.example.AntiFraudSystem.model.CodeEnum." + invalidRegionTransaction.getRegion(), regionNotValidException.getMessage());
    }

    private Transaction createTransaction(String cardNumber, String region) {
        Transaction transaction = new Transaction();
        transaction.setIp("192.168.1.1");
        transaction.setRegion(region);
        transaction.setNumber(cardNumber);
        transaction.setAmount(300L);
        transaction.setDate(LocalDateTime.now());
        return transaction;
    }
}

