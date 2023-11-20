package com.example.AntiFraudSystem.services;

import com.example.AntiFraudSystem.model.Role;
import com.example.AntiFraudSystem.repositories.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getOrCreateRole(String roleName) {
        Role role = roleRepository.findByName(roleName);

        if (role == null) {
            role = createAndSaveRole(roleName);
        }

        return role;
    }

    private Role createAndSaveRole(String roleName) {
        Role newRole = new Role(roleName);
        return roleRepository.save(newRole);
    }
    public boolean roleExist(String name){
        return roleRepository.existsByName(name);
    }
}
