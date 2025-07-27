package com.processor_csv.infraestructure.batch.mapper;

import com.processor_csv.infraestructure.batch.listener.UserSkipListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Autowired
    public void injectSkipListener(UserFieldSetMapper mapper, UserSkipListener listener) {
        mapper.setSkipListener(listener);
    }
}

