package com.ecommerce.service.user.user_service.service;

import com.ecommerce.service.user.user_service.entity.User;
import com.ecommerce.service.user.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    public boolean addUser(User user) {
        if(user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode password
            userRepository.save(user);
        }
        return false;
    }
}
