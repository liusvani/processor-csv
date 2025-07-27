package com.processor_csv.application.service;

import com.processor_csv.domain.model.User;
import com.processor_csv.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserImportUseCase {

    @Autowired
    private UserService userService;

    public User processUser(User user) {
        return userService.validate(user);
    }

    public void saveUsers(List<User> users) {
        userService.saveAll(users);
    }
}
