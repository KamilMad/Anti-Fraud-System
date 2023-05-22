package com.example.AntiFraudSystem.services;


import com.example.AntiFraudSystem.errors.CardAlreadyInDatabase;
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

        if (!LuhnAlgorithm.isValidCardNumber(card.getNumber()))
            throw new CardNumberNotValid("Card number: " + card.getNumber() + " not valid");

        if (cardRepository.findByNumber(card.getNumber()).isPresent())
            throw new CardAlreadyInDatabase("Card with number " + card.getNumber() + " exist in db");

        Card newCard = new Card();
        newCard.setNumber(card.getNumber());

        return cardRepository.save(newCard);
    }

    public void deleteCard(String number){

        if (!LuhnAlgorithm.isValidCardNumber(number))
            throw new CardNumberNotValid("Card number: " + number + " not valid");

        Card card = cardRepository.findByNumber(number).orElseThrow(()
                -> new UsernameNotFoundException("Card with number: " + number + " not found"));

        cardRepository.delete(card);
    }

    public List<Card> getAll(){
        return cardRepository.findAll();
    }
}
