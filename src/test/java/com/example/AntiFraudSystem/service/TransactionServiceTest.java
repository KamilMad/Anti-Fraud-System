package com.example.AntiFraudSystem.service;

<<<<<<< HEAD
import com.example.AntiFraudSystem.errors.CardNumberNotValid;
=======
>>>>>>> 6343b30de5db4aa06728129a50466150ca972029
import com.example.AntiFraudSystem.model.Transaction;
import com.example.AntiFraudSystem.payload.TransactionRequestDto;
import com.example.AntiFraudSystem.payload.TransactionResponse;
import com.example.AntiFraudSystem.repositories.TransactionRepository;
import com.example.AntiFraudSystem.services.TransactionService;
<<<<<<< HEAD
import com.example.AntiFraudSystem.utilities.LuhnAlgorithm;
=======
>>>>>>> 6343b30de5db4aa06728129a50466150ca972029
import com.example.AntiFraudSystem.utilities.Status;
import com.example.AntiFraudSystem.utilities.TransactionStatus;
import com.example.AntiFraudSystem.utilities.TransactionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
<<<<<<< HEAD
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
=======
>>>>>>> 6343b30de5db4aa06728129a50466150ca972029
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
import static org.junit.jupiter.api.Assertions.*;
=======
>>>>>>> 6343b30de5db4aa06728129a50466150ca972029
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionUtils transactionUtils;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

<<<<<<< HEAD
    @Test
    public void testMakeTransaction_ValidTransaction() {

        Transaction validTransaction = createTransaction("4000008449433403", "EAP");

=======
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

>>>>>>> 6343b30de5db4aa06728129a50466150ca972029
        TransactionStatus transactionStatus = new TransactionStatus();
        transactionStatus.setStatus(Status.ALLOWED);

        TransactionResponse response = new TransactionResponse();
        response.setResult(Status.ALLOWED);
        response.setInfo("none");

        List<String> reasons = new ArrayList<>();
        transactionStatus.setReasons(reasons);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(validTransaction);
<<<<<<< HEAD
=======

>>>>>>> 6343b30de5db4aa06728129a50466150ca972029
        when(transactionUtils.getStatus(any(TransactionRequestDto.class))).thenReturn(transactionStatus);

        TransactionResponse result = transactionService.makeTransaction(validTransaction);

<<<<<<< HEAD
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
=======
        Assertions.assertNotNull(result);
        Assertions.assertEquals(response, result);
>>>>>>> 6343b30de5db4aa06728129a50466150ca972029
    }
}

