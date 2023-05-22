package com.example.AntiFraudSystem.repositories;


import com.example.AntiFraudSystem.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByNumber(String number);
}
