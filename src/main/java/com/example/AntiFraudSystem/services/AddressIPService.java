package com.example.AntiFraudSystem.services;


import com.example.AntiFraudSystem.errors.AddressIpAlreadyInDataBase;
import com.example.AntiFraudSystem.errors.AddressNotFoundException;
import com.example.AntiFraudSystem.model.AddressIp;
import com.example.AntiFraudSystem.repositories.AddressIpRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AddressIPService {

    private final AddressIpRepository addressIpRepository;

    public AddressIPService(AddressIpRepository addressIpRepository) {
        this.addressIpRepository = addressIpRepository;
    }

    public AddressIp save(AddressIp address){

        validateAddressIpUnique(address.getIp());

        return addressIpRepository.save(address);
    }

    public void delete(String ip){
        validateIpAddressFormat(ip);

       AddressIp address = addressIpRepository.findByIp(ip).orElseThrow(()
                -> new AddressNotFoundException("Address with ip: " + ip + " not found in database"));

        addressIpRepository.delete(address);
    }

    public List<AddressIp> getAll(){
        return addressIpRepository.findAll();
    }

    private void validateAddressIpUnique(String ip) {
        if (addressIpRepository.findByIp(ip).isPresent()) {
            throw new AddressIpAlreadyInDataBase("Address ip: " + ip + " already exists");
        }
    }

    private void validateIpAddressFormat(String ip) {
        Pattern ipAddressPattern = Pattern.compile("^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$");
        Matcher matcher = ipAddressPattern.matcher(ip);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Address ip: " + ip + " has the wrong format");
        }
    }
}



