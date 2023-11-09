package org.example.services;

import org.example.dto.request.UserRegistrationRequest;
import org.example.entities.User;

import java.util.List;

public interface UserService {
    User create(UserRegistrationRequest user);
    User getById(Long id);
    User getByUsername(String username);
    List<User> getAll();
    User update(User user);
    void delete(Long id);
}
