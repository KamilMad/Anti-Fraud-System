package com.example.AntiFraudSystem.security;

import com.example.AntiFraudSystem.model.User;
import com.example.AntiFraudSystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JDBCUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public JDBCUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username.toLowerCase()).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + username));

        return new CustomUserDetails(user);
    }
}
