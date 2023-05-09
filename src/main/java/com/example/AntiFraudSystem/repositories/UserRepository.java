package com.example.AntiFraudSystem.repositories;


import com.example.AntiFraudSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
