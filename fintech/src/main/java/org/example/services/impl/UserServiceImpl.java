package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.UserRegistrationRequest;
import org.example.entities.User;
import org.example.entities.enums.Role;
import org.example.repositories.UserRepository;
import org.example.services.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User create(UserRegistrationRequest user) {
        User u = new User();
        u.setUsername(user.getUsername());
        u.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        u.setRole(Role.ROLE_USER);

        return userRepository.save(u);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new NoSuchElementException("No value present");
        }

        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        User user = getById(id);

        userRepository.delete(user);
    }
}
