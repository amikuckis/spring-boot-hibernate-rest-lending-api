package io.fourfinanceit.app.service;

import io.fourfinanceit.app.model.domain.User;
import io.fourfinanceit.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findByNationalIdentityNumber(String nationalIdentityNumber) {
        return userRepository.findByNationalIdentityNumber(nationalIdentityNumber);
    }

}
