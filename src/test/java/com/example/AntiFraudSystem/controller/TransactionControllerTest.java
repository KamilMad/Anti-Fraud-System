package com.example.AntiFraudSystem.controller;

import com.example.AntiFraudSystem.controllers.TransactionController;
import com.example.AntiFraudSystem.model.AddressIp;
import com.example.AntiFraudSystem.model.Card;
import com.example.AntiFraudSystem.model.Transaction;
import com.example.AntiFraudSystem.payload.StatusDTO;
import com.example.AntiFraudSystem.payload.TransactionResponseDTO;
import com.example.AntiFraudSystem.services.AddressIPService;
import com.example.AntiFraudSystem.services.CardService;
import com.example.AntiFraudSystem.services.TransactionService;
import com.example.AntiFraudSystem.utilities.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class TransactionControllerTest {

    @MockBean
    TransactionService transactionService;
    @MockBean
    AddressIPService addressIPService;
    @MockBean
    CardService cardService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("transactionDataProvider")
    void testMakeTransaction(Transaction transaction, TransactionResponseDTO expectedResponse) throws Exception {
        when(transactionService.makeTransaction(transaction)).thenReturn(expectedResponse);

        ResultActions result = mockMvc.perform(post("/api/antifraud/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(transaction))
                        .accept(MediaType.APPLICATION_JSON));


        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.result", CoreMatchers.is(expectedResponse.result().toString())))
                .andExpect(jsonPath("$.info", CoreMatchers.is(expectedResponse.info())));
    }

    @Test
    public void testSaveSuspiciousAddress() throws Exception {
        AddressIp addressIp = new AddressIp();
        addressIp.setId(1L);
        addressIp.setIp("192.168.1.1");

        when(addressIPService.save(addressIp)).thenReturn(addressIp);

        ResultActions result = mockMvc.perform(post("/api/antifraud/suspicious-ip")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(addressIp)));

        result.andExpect(jsonPath("$.ip", CoreMatchers.is(addressIp.getIp())));
    }

    @Test
    public void testDeleteSuspiciousAddress() throws Exception {
        String ip = "192.168.1.1";
        StatusDTO response = new StatusDTO("IP " + ip + " successfully removed!");

        doNothing().when(addressIPService).delete(ip);

        ResultActions result = mockMvc.perform(delete("/api/antifraud/suspicious-ip/192.168.1.1")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(content().json(asJsonString(response)));

    }

    @Test
    public void testGetAllAddresses() throws Exception{
        AddressIp addressIp1 = new AddressIp();
        addressIp1.setId(1L);
        addressIp1.setIp("192.168.1.1");

        AddressIp addressIp2 = new AddressIp();
        addressIp2.setId(2L);
        addressIp2.setIp("192.168.123.123");

        List<AddressIp> addressIpList = new ArrayList<>();
        addressIpList.add(addressIp1);
        addressIpList.add(addressIp2);

        when(addressIPService.getAll()).thenReturn(addressIpList);

        ResultActions result = mockMvc.perform(get("/api/antifraud/suspicious-ip")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(addressIpList.size())));
    }

    @Test
    public void testSaveStolenCard() throws Exception {
        Card card = new Card();
        card.setId(1L);
        card.setNumber("4929156341843324");

        when(cardService.saveCard(card)).thenReturn(card);

        ResultActions result = mockMvc.perform(post("/api/antifraud/stolencard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(card))
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.number", CoreMatchers.is(card.getNumber())));
    }

    @Test
    public void testDeleteStolenCard() throws Exception{
        String number = "4929156341843324";
        StatusDTO response = new StatusDTO("Card " + number + " successfully removed!");

        doNothing().when(cardService).deleteCard(number);

        ResultActions result = mockMvc.perform(delete("/api/antifraud/stolencard/" + number)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(content().json(asJsonString(response)));
    }

    @Test
    public void testGetAllCard() throws Exception {
        Card card1 = new Card();
        card1.setId(1L);
        card1.setNumber("4929156341843324");
        Card card2 = new Card();
        card1.setId(2L);
        card1.setNumber("4556299266351863");

        List<Card> cards = List.of(card1, card2);

        when(cardService.getAll()).thenReturn(cards);

        ResultActions result = mockMvc.perform(get("/api/antifraud/stolencard")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(cards.size())));
    }
    private static Stream<Object[]> transactionDataProvider() {
        return Stream.of(
                // Test data for ALLOWED scenario
                new Object[]{createTransaction("192.168.1.1", "EAP", "valid-Number", 150L),
                        createExpectedResponse(Status.ALLOWED, "none")},

                // Test data for PROHIBITED scenario
                new Object[]{createTransaction("192.168.1.1", "EAP", "stolen_card_number", 1600L),
                        createExpectedResponse(Status.PROHIBITED, "card-number")},

                // Test data for MANUAL_PROCESSING scenario
                new Object[]{createTransaction("192.168.1.1", "EAP", "validCardNumber", 400L),
                        createExpectedResponse(Status.MANUAL_PROCESSING, "amount")}
        );
    }

    private static TransactionResponseDTO createExpectedResponse(Status status, String info) {
        return new TransactionResponseDTO(status, info);
    }
    private static Transaction createTransaction(String ip, String region, String number, Long amount) {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setIp(ip);
        transaction.setRegion(region);
        transaction.setNumber(number);
        transaction.setAmount(amount);
        transaction.setDate(LocalDateTime.now());

        return transaction;
    }

    private String asJsonString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(object);
    }
}
