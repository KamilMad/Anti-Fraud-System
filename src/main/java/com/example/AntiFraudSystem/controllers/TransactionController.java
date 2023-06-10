package com.example.AntiFraudSystem.controllers;

import com.example.AntiFraudSystem.model.AddressIp;
import com.example.AntiFraudSystem.model.Card;
import com.example.AntiFraudSystem.model.Transaction;
import com.example.AntiFraudSystem.payload.StatusDto;
import com.example.AntiFraudSystem.payload.TransactionResponse;
import com.example.AntiFraudSystem.services.AddressIPService;
import com.example.AntiFraudSystem.services.CardService;
import com.example.AntiFraudSystem.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;


@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {

    private final TransactionService transactionService;
    private final AddressIPService addressIPService;
    private final CardService cardService;

    @Autowired
    public TransactionController(TransactionService transactionService, AddressIPService addressIPService, CardService cardService) {
        this.transactionService = transactionService;
        this.addressIPService = addressIPService;
        this.cardService = cardService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<TransactionResponse> makeTransaction(@Valid @RequestBody Transaction transaction){
        return new ResponseEntity<>(transactionService.makeTransaction(transaction), HttpStatus.OK);
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<AddressIp> saveSuspiciousAddress(@Valid @RequestBody AddressIp addressIp){

        AddressIp address = addressIPService.save(addressIp);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(address);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<StatusDto> deleteSuspiciousAddress(@PathVariable String ip){
        addressIPService.delete(ip);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new StatusDto("IP " + ip + " successfully removed!"));
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<List<AddressIp>> getAllAddresses(){
        List<AddressIp> addresses = addressIPService.getAll();

        addresses.sort(Comparator.comparing(AddressIp::getId));
        return ResponseEntity.status(HttpStatus.OK).body(addresses);
    }

    @PostMapping("/stolencard")
    public ResponseEntity<Card> saveStolenCard(@Valid @RequestBody Card card){
        Card savedCard = cardService.saveCard(card);

        return ResponseEntity.status(HttpStatus.OK).body(savedCard);
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<StatusDto> deleteStolenCard(@PathVariable String number){
        cardService.deleteCard(number);

        return ResponseEntity.status(HttpStatus.OK). body(new StatusDto("Card " +number + " successfully removed!"));
    }

    @GetMapping("/stolencard")
    public ResponseEntity<List<Card>> getAllCard(){

        List<Card> cards = cardService.getAll();

        return ResponseEntity.status(HttpStatus.OK). body(cards);
    }
}
