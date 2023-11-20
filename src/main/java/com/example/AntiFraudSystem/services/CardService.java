package com.example.AntiFraudSystem.services;


import com.example.AntiFraudSystem.errors.CardAlreadyInDatabase;
import com.example.AntiFraudSystem.errors.CardNotFoundException;
import com.example.AntiFraudSystem.errors.CardNumberNotValid;
import com.example.AntiFraudSystem.model.Card;
import com.example.AntiFraudSystem.repositories.CardRepository;
import com.example.AntiFraudSystem.utilities.LuhnAlgorithm;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;


    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Card saveCard(Card card){

        validateCardNumber(card.getNumber());
        ensureCardNumberIsUnique(card.getNumber());

        Card savedCard = new Card();
        savedCard.setNumber(card.getNumber());

        return cardRepository.save(savedCard);
    }

    public void deleteCard(String number){

    validateCardNumber(number);

        Card card = cardRepository.findByNumber(number).orElseThrow(()
                -> new CardNotFoundException("Card with number: " + number + " not found"));

        cardRepository.delete(card);
    }

    public List<Card> getAll(){
        return cardRepository.findAll();
    }

    private void validateCardNumber(String cardNumber) {
        if (!LuhnAlgorithm.isValidCardNumber(cardNumber)) {
            throw new CardNumberNotValid("Card number: " + cardNumber + " not valid");
        }
    }

    private void ensureCardNumberIsUnique(String cardNumber) {
        if (cardRepository.findByNumber(cardNumber).isPresent()) {
            throw new CardAlreadyInDatabase("Card with number " + cardNumber + " exists in the database");
        }
    }
}
