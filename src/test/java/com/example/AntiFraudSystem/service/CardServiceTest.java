package com.example.AntiFraudSystem.service;

import com.example.AntiFraudSystem.errors.CardNumberNotValid;
import com.example.AntiFraudSystem.model.Card;
import com.example.AntiFraudSystem.repositories.CardRepository;
import com.example.AntiFraudSystem.services.CardService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private Card card;

    @BeforeEach
    void init() {
        card = new Card();
        card.setId(1L);
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
    public void testSaveCardThrowsCardNumberNotValid(String cardNumber) {
        card.setNumber(cardNumber);
        CardNumberNotValid exception = assertThrows(CardNumberNotValid.class, () -> cardService.saveCard(card));
        assertEquals("Card number: " + card.getNumber() + " not valid", exception.getMessage());

        verifyNoInteractions(cardRepository);
    }

    @ParameterizedTest
    @CsvSource({
            "4556299266351863",
            "4929711009542853",
            "4532489740373290",
            "4716166368289527",
            "4532586798233932",
            "4929357689540831",
            "4916859772028579",
            "4532898340745936",
            "4916328795912748",
            "4556239655728227",
            "4716455010492126",
            "4532986365924137",
            "4929386698371256",
            "4716851623040281",
            "4929518593486810",
            "4556463056945723",
            "4716072997869510",
            "4929578964132386",
            "4556298367817459",
            "4916850198457642",
    })
    public void testSaveButCardAlreadyInDatabase() {

        card.setNumber("4929156341843324");
        when(cardRepository.findByNumber(card.getNumber()))
                .thenThrow(new UsernameNotFoundException("Card with number: " + card.getNumber()+ " not found"));

        UsernameNotFoundException usernameNotFoundException = assertThrows(UsernameNotFoundException.class,
                () -> cardService.saveCard(card));

        assertEquals("Card with number: " + card.getNumber()+ " not found" ,usernameNotFoundException.getMessage());

        verify(cardRepository, never()).save(card);

    }

    @Test
    public void testSaveCardWhenValidCardNumberAndNotAlreadyInDatabase() {
        card.setNumber("4929156341843324");

        Card newCard = new Card();
        newCard.setNumber(card.getNumber());

        when(cardRepository.findByNumber(card.getNumber())).thenReturn(Optional.empty());
        when(cardRepository.save(newCard)).thenReturn(newCard);

        Card returnedCard = cardService.saveCard(newCard);

        assertEquals(newCard, returnedCard);
        verify(cardRepository, times(1)).findByNumber(newCard.getNumber());
        verify(cardRepository, times(1)).save(newCard);
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
    public void testDeleteCardWhenCardNumberNotValid(String cardNumber) {
        card.setNumber(cardNumber);
        CardNumberNotValid exception = assertThrows(CardNumberNotValid.class,
                () -> cardService.deleteCard(card.getNumber()));

        assertEquals("Card number: " + cardNumber + " not valid", exception.getMessage());
        verify(cardRepository, never()).delete(card);
    }

    @Test
    public void testDeleteCardWhenValidCardNumberButNotExistInDatabase() {
        card.setNumber("4929156341843324");

        when(cardRepository.findByNumber(card.getNumber())).thenThrow(
                new UsernameNotFoundException("Card with number: " + card.getNumber() + " not found"));

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> cardService.deleteCard(card.getNumber()));

        assertEquals("Card with number: " + card.getNumber() + " not found", exception.getMessage());
        verify(cardRepository, never()).delete(card);
    }

    @Test
    public void testDeleteCardWhenValidCardNumberAndExistInDatabase() {
        card.setNumber("4929156341843324");

        when(cardRepository.findByNumber(card.getNumber())).thenReturn(Optional.of(card));
        assertDoesNotThrow(() -> cardService.deleteCard(card.getNumber()));

        verify(cardRepository, times(1)).delete(card);

    }

    @Test
    public void tedtGetAllCardsReturnExpectedList() {

        List<Card> cardList = new ArrayList<>();

        Card card1 = new Card();
        card1.setId(1L);
        card1.setNumber("4556299266351863");

        Card card2 = new Card();
        card2.setId(2L);
        card2.setNumber("4929711009542853");

        Card card3 = new Card();
        card3.setId(3L);
        card3.setNumber("4532489740373290");

        cardList.add(card1);
        cardList.add(card2);
        cardList.add(card3);

        when(cardRepository.findAll()).thenReturn(cardList);

        List<Card> returnedList = cardService.getAll();

        assertEquals(cardList.size(), returnedList.size());
        assertEquals(cardList, returnedList);

        assertTrue(returnedList.contains(card1));
        assertTrue(returnedList.contains(card2));
        assertTrue(returnedList.contains(card3));
    }

    @Test
    public void testGetAllEmptyList() {

        when(cardRepository.findAll()).thenReturn(Collections.emptyList());

        List<Card> cardList = cardService.getAll();

        assertTrue(cardList.isEmpty());
    }


}
