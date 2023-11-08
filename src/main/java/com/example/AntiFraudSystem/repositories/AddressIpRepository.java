package com.example.AntiFraudSystem.repositories;


import com.example.AntiFraudSystem.model.AddressIp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressIpRepository extends JpaRepository<AddressIp, Long> {
    Optional<AddressIp> findByIp(String ip);


}
