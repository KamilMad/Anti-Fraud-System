package com.example.AntiFraudSystem.repositories;


import com.example.AntiFraudSystem.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
    boolean existsByName(String name);
}
