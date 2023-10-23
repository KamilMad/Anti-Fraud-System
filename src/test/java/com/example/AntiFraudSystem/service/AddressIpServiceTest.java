package com.example.AntiFraudSystem.service;

import com.example.AntiFraudSystem.errors.AddressIpAlreadyInDataBase;
import com.example.AntiFraudSystem.errors.AddressNotFoundException;
import com.example.AntiFraudSystem.model.AddressIp;
import com.example.AntiFraudSystem.repositories.AddressIpRepository;
import com.example.AntiFraudSystem.services.AddressIPService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressIpServiceTest {

    @Mock
    private AddressIpRepository addressIpRepository;

    @InjectMocks
    private AddressIPService addressIPService;

    private AddressIp addressIp;

    @BeforeEach
    void init() {
        addressIp = new AddressIp();
        addressIp.setId(1L);
    }

    @ParameterizedTest
    @CsvSource({
            "192.168.1.1",
            "192.168.0.1",
            "10.0.0.1",
            "172.16.0.1",
            "203.0.113.0",
            "100.64.0.1",
            "198.51.100.42",
            "0.0.0.0",
            "255.255.255.255",

    })
    public void saveAddressWhenAddressDoesNotExistTest(String ip) {
        addressIp.setIp(ip);
        when(addressIpRepository.findByIp(ip))
                .thenReturn(Optional.empty());

        when(addressIpRepository.save(addressIp))
                .thenReturn(addressIp);

        AddressIp returnedAddressIp = addressIPService.save(addressIp);
        Assertions.assertNotNull(addressIp);
        assertEquals(addressIp, returnedAddressIp);
    }


    @Test
    public void saveAddressWhenAddressExistTest() {

        when(addressIpRepository.findByIp(addressIp.getIp()))
                .thenThrow(new AddressIpAlreadyInDataBase("Address ip: " + addressIp.getIp() + " already exists"));

        verifyNoMoreInteractions(addressIpRepository);

        assertThrows(AddressIpAlreadyInDataBase.class, () -> addressIPService.save(addressIp));

    }

    @Test
    public void deleteAddress() {
        String ip = "192.168.1.1";
        addressIp.setIp(ip);

        when(addressIpRepository.findByIp(ip)).thenReturn(Optional.of(addressIp));
        Assertions.assertDoesNotThrow(() -> addressIPService.delete(ip));

        verify(addressIpRepository).delete(addressIp);
    }


    @ParameterizedTest
    @CsvSource({
            "280.0.0.0",
            "-192.-168.-1.256",
            "192.168.01.1",
            "192.168.001.1",
            "192.168.1.01",
            "192.168.1.1.1",
            "192.168.1",
            "192.168.1.1.1.1",
            "invalid-input",
            "-255,-255,-255,-255",

    })
    public void testDeleteInvalidIpAddressFormat(String invalidIpAddress) {
        // Assert that the method throws IllegalArgumentException with the correct error message
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> addressIPService.delete(invalidIpAddress));
        assertEquals("Address ip: " + invalidIpAddress + " has wrong format", exception.getMessage());

        // Verify that findByIp was not called
        verify(addressIpRepository, never()).findByIp(invalidIpAddress);

        // Verify that delete was not called
        addressIp.setIp(invalidIpAddress);
        verify(addressIpRepository, never()).delete(addressIp);
    }

   @Test
    public void testDeleteAddressNotFoundInDatabase() {
        String ip = "192.168.1.1";
        //Assert that method throws AddressNotFoundException with the correct error message
       when(addressIpRepository.findByIp(ip))
                .thenThrow(new AddressNotFoundException("Address with ip: " + ip + " not found in database"));

        AddressNotFoundException exception = assertThrows(AddressNotFoundException.class, () -> addressIPService.delete(ip));

        assertEquals("Address with ip: " + ip + " not found in database", exception.getMessage());
        verifyNoMoreInteractions(addressIpRepository);
    }

    @Test
    public void testGetAllReturnsExpectedList() {
        List<AddressIp> expectedAddressIpList = new ArrayList<>();

        AddressIp addressIp1 = new AddressIp();
        addressIp1.setId(1L);
        addressIp1.setIp("192.168.1.1");

        AddressIp addressIp2 = new AddressIp();
        addressIp2.setId(2L);
        addressIp2.setIp("192.168.0.1");

        AddressIp addressIp3 = new AddressIp();
        addressIp2.setId(3L);
        addressIp2.setIp("10.0.0.1");

        expectedAddressIpList.add(addressIp1);
        expectedAddressIpList.add(addressIp2);
        expectedAddressIpList.add(addressIp3);

        when(addressIpRepository.findAll()).thenReturn(expectedAddressIpList);

        List<AddressIp> returnedList = addressIPService.getAll();

        assertEquals(expectedAddressIpList.size(), returnedList.size());
        assertEquals(expectedAddressIpList, returnedList);

    }
}
