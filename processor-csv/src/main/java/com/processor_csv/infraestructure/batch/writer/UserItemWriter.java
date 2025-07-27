package com.processor_csv.infraestructure.batch.writer;

import com.processor_csv.domain.model.User;
import com.processor_csv.domain.service.UserService;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserItemWriter implements ItemWriter<User> {

    @Autowired
    private UserService userService;

    @Override
    public void write(Chunk<? extends User> users) {
        userService.saveAll(users.getItems());
    }
}


