package com.example.AntiFraudSystem.service;

import com.example.AntiFraudSystem.model.Role;
import com.example.AntiFraudSystem.repositories.RoleRepository;
import com.example.AntiFraudSystem.services.RoleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceClass {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role1;

    @BeforeEach
    public void init(){
        role1 = new Role();
        role1.setName("ADMINISTRATOR");
    }

    @Test
    public void testGetOrCreateWhenRoleDoesNotExist() {

        when(roleRepository.findByName(anyString())).thenReturn(null);

        when(roleRepository.save(role1)).thenReturn(role1);

        Role result= roleService.getOrCreateRole("ADMINISTRATOR");

        assertEquals(role1, result);
    }

    @Test
    public void testGetOrCreateWhenRoleExist() {
        when(roleRepository.findByName("ADMINISTRATOR")).thenReturn(role1);
        Role result = roleService.getOrCreateRole("ADMINISTRATOR");

        verifyNoMoreInteractions(roleRepository);
        assertEquals(role1, result);
    }

    @Test
    public void testRoleExistWhenRoleDoesNotExist() {
        when(roleRepository.existsByName(anyString())).thenReturn(false);

        boolean result = roleService.roleExist(anyString());

        assertFalse(result);
    }

    @Test
    public void testRoleExistWhenRoleExist() {
        when(roleRepository.existsByName(anyString())).thenReturn(true);

        boolean result = roleService.roleExist(anyString());

        assertTrue(result);
    }
}
