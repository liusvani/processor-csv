package com.processor_csv.infraestructure.batch.processor;

import com.processor_csv.domain.model.User;
import com.processor_csv.domain.service.UserService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserItemProcessor implements ItemProcessor<User, User> {

    @Autowired
    private UserService userService;

    @Override
    public User process(User user) {
        return userService.validate(user);
    }

}

