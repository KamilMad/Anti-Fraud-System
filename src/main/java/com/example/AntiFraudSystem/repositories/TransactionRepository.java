package com.example.AntiFraudSystem.repositories;

import com.example.AntiFraudSystem.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    @Query("SELECT COUNT(DISTINCT t.region) " +
            "FROM Transaction t " +
            "WHERE t.number = ?1 " +
            "AND t.region <> ?2 " +
            "AND t.date BETWEEN ?3 AND ?4")
    long countDistinctRegions(String cardNumber, String currentRegion, LocalDateTime from, LocalDateTime to);



    @Query(value = "SELECT COUNT(DISTINCT t.ip) " +
            "FROM Transaction t " +
            "WHERE t.number = ?1 " +
            "AND t.ip <> ?2 " +
            "AND t.date BETWEEN ?3 AND ?4", nativeQuery = true)
    long countDistinctIpAddresses(String cardNumber, String currentIpAddress, LocalDateTime from, LocalDateTime to);

    List<Transaction> findAllByNumberAndDateBetween(String number, LocalDateTime minusHours, LocalDateTime date);

}
