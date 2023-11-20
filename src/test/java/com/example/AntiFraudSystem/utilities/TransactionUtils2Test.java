package com.example.AntiFraudSystem.utilities;

import com.example.AntiFraudSystem.model.AddressIp;
import com.example.AntiFraudSystem.model.Card;
import com.example.AntiFraudSystem.model.Transaction;
import com.example.AntiFraudSystem.payload.TransactionRequestDto;
import com.example.AntiFraudSystem.repositories.AddressIpRepository;
import com.example.AntiFraudSystem.repositories.CardRepository;
import com.example.AntiFraudSystem.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TransactionUtils2Test {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private AddressIpRepository addressIpRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private TransactionUtils2 transactionUtils2;

    private TransactionRequestDto transactionRequestDto;
    @BeforeEach
    public void init() {
        transactionRequestDto = new TransactionRequestDto();
        transactionRequestDto.setAmount(100L);
        transactionRequestDto.setIp("192.168.1.1");
        transactionRequestDto.setRegion("EAP");
        transactionRequestDto.setNumber("4000008449433403");
        transactionRequestDto.setDate(LocalDateTime.now());
    }

    @ParameterizedTest
    @CsvSource({"0", "100","199", "200",
     "201", "1499", "1500",
            "1501", "2000"
    })
    public void testGetStatus_Amount(Long amount) {

        transactionRequestDto.setAmount(amount);

        when(transactionRepository.findAllByNumberAndDateBetween(
                transactionRequestDto.getNumber(),
                transactionRequestDto.getDate().minusHours(1),
                transactionRequestDto.getDate()))
                .thenReturn(Collections.emptyList());

        TransactionStatus result = transactionUtils2.getStatus(transactionRequestDto);

        if (amount <= 200){
            assertEquals(Status.ALLOWED, result.getStatus());
            assertEquals(0, result.getReasons().size());
        } else if (amount <= 1500) {
            assertEquals(Status.MANUAL_PROCESSING, result.getStatus());
            assertEquals(1, result.getReasons().size());
        }else {
            assertEquals(Status.PROHIBITED, result.getStatus());
            assertEquals(1, result.getReasons().size());
        }
    }

    @Test
    public void testGetStatus_MANUAL_PROCESSING_IpCorrelation() {
        Transaction transaction2 = createTransaction(1L, 100L, "192.168.1.2", "EAP", "4000008449433403", LocalDateTime.now().minusMinutes(10));
        Transaction transaction3 = createTransaction(2L, 100L, "192.168.1.3", "EAP", "4000008449433403", LocalDateTime.now().minusMinutes(20));
        Transaction transaction4 = createTransaction(3L, 100L, "192.168.1.4", "EAP", "4000008449433403", LocalDateTime.now().minusMinutes(30));

        when(transactionRepository.findAllByNumberAndDateBetween(transactionRequestDto.getNumber(),
                transactionRequestDto.getDate().minusHours(1),
                transactionRequestDto.getDate()))
                .thenReturn(List.of(transaction2, transaction3, transaction4));

        TransactionStatus result = transactionUtils2.getStatus(transactionRequestDto);

        assertEquals(Status.MANUAL_PROCESSING, result.getStatus());
        assertEquals(1, result.getReasons().size());
        assertEquals("ip-correlation", result.getReasons().get(0));

    }

    @Test
    public void testGetStatus_MANUAL_PROCESSING_Region_Correlation() {
        Transaction transaction2 = createTransaction(1L, 100L, "192.168.1.1", "ECA", "4000008449433403", LocalDateTime.now().minusMinutes(10));
        Transaction transaction3 = createTransaction(2L, 100L, "192.168.1.1", "HIC", "4000008449433403", LocalDateTime.now().minusMinutes(20));
        Transaction transaction4 = createTransaction(3L, 100L, "192.168.1.1", "LAC", "4000008449433403", LocalDateTime.now().minusMinutes(30));

        when(transactionRepository.findAllByNumberAndDateBetween(transactionRequestDto.getNumber(),
                transactionRequestDto.getDate().minusHours(1),
                transactionRequestDto.getDate()))
                .thenReturn(List.of(transaction2, transaction3, transaction4));

        TransactionStatus result = transactionUtils2.getStatus(transactionRequestDto);

        assertEquals(Status.MANUAL_PROCESSING, result.getStatus());
        assertEquals(1, result.getReasons().size());
        assertEquals("region-correlation", result.getReasons().get(0));
    }

    @Test
    public void testGetStatus_PROHIBITED_Ip_Correlation() {
        Transaction transaction2 = createTransaction(1L, 100L, "192.168.1.2", "ECA", "4000008449433403", LocalDateTime.now().minusMinutes(10));
        Transaction transaction3 = createTransaction(2L, 100L, "192.168.1.3", "ECA", "4000008449433403", LocalDateTime.now().minusMinutes(20));
        Transaction transaction4 = createTransaction(3L, 100L, "192.168.1.4", "ECA", "4000008449433403", LocalDateTime.now().minusMinutes(30));
        Transaction transaction5 = createTransaction(4L, 100L, "192.168.1.5", "ECA", "4000008449433403", LocalDateTime.now().minusMinutes(35));

        when(transactionRepository.findAllByNumberAndDateBetween(transactionRequestDto.getNumber(),
                transactionRequestDto.getDate().minusHours(1),
                transactionRequestDto.getDate()))
                .thenReturn(List.of(transaction2, transaction3, transaction4, transaction5));

        TransactionStatus result = transactionUtils2.getStatus(transactionRequestDto);

        assertEquals(Status.PROHIBITED, result.getStatus());
        assertEquals(1, result.getReasons().size());
        assertEquals("ip-correlation", result.getReasons().get(0));
    }

    @Test
    public void testGetStatus_PROHIBITED_Region_Correlation() {
        Transaction transaction2 = createTransaction(1L, 100L, "192.168.1.1", "ECA", "4000008449433403", LocalDateTime.now().minusMinutes(10));
        Transaction transaction3 = createTransaction(2L, 100L, "192.168.1.1", "HIC", "4000008449433403", LocalDateTime.now().minusMinutes(20));
        Transaction transaction4 = createTransaction(3L, 100L, "192.168.1.1", "EAP", "4000008449433403", LocalDateTime.now().minusMinutes(30));
        Transaction transaction5 = createTransaction(4L, 100L, "192.168.1.1", "LAC", "4000008449433403", LocalDateTime.now().minusMinutes(35));

        when(transactionRepository.findAllByNumberAndDateBetween(transactionRequestDto.getNumber(),
                transactionRequestDto.getDate().minusHours(1),
                transactionRequestDto.getDate()))
                .thenReturn(List.of(transaction2, transaction3, transaction4, transaction5));

        TransactionStatus result = transactionUtils2.getStatus(transactionRequestDto);

        assertEquals(Status.PROHIBITED, result.getStatus());
        assertEquals(1, result.getReasons().size());
        assertEquals("region-correlation", result.getReasons().get(0));
    }

    @Test
    public void testGetStatus_PROHIBITED_cardStolen() {

        when(cardRepository.findByNumber(transactionRequestDto.getNumber())).thenReturn(Optional.of(new Card()));

        TransactionStatus result = transactionUtils2.getStatus(transactionRequestDto);

        assertEquals(Status.PROHIBITED, result.getStatus());
        assertEquals(1, result.getReasons().size());
        assertEquals("card-number", result.getReasons().get(0));
    }

    @Test
    public void testGetStatus_PROHIBITED_suspiciousIp() {

        when(addressIpRepository.findByIp(transactionRequestDto.getIp())).thenReturn(Optional.of(new AddressIp()));

        TransactionStatus result = transactionUtils2.getStatus(transactionRequestDto);

        assertEquals(Status.PROHIBITED, result.getStatus());
        assertEquals(1, result.getReasons().size());
        assertEquals("ip", result.getReasons().get(0));
    }
    private Transaction createTransaction(Long id, Long amount, String ip,
                                          String region, String number, LocalDateTime date) {

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(amount);
        transaction.setNumber(number);
        transaction.setIp(ip);
        transaction.setRegion(region);
        transaction.setDate(date);

        return transaction;
    }
}
