package com.josefy.nnpda.infrastructure.service.impl;

import com.josefy.nnpda.infrastructure.exceptions.ConflictException;
import com.josefy.nnpda.infrastructure.exceptions.NotFoundException;
import com.josefy.nnpda.infrastructure.exceptions.UnauthorizedException;
import com.josefy.nnpda.infrastructure.repository.IUserRepository;
import com.josefy.nnpda.infrastructure.security.JwtTokenProvider;
import com.josefy.nnpda.infrastructure.service.IUserService;
import com.josefy.nnpda.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        userRepository.deleteByUsername(user.getUsername());
    }

    @Override
    @Transactional
    public User save(User user) {
        if (userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail())) {
            throw new ConflictException("User with provided username or email already exists.");
        }
        return userRepository.save(user);
    }
}
