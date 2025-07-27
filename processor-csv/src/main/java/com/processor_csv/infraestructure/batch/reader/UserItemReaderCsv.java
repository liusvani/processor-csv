package com.processor_csv.infraestructure.batch.reader;

import com.processor_csv.config.UserBatchProperties;
import com.processor_csv.domain.model.User;
import com.processor_csv.infraestructure.batch.mapper.UserFieldSetMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;


@Component
public class UserItemReaderCsv {

    @Bean
    public FlatFileItemReader<User> userItemReader(UserBatchProperties properties, UserFieldSetMapper fieldSetMapper) {
        FlatFileItemReader<User> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(properties.getFilePath()));
        System.out.println("ruta del archivo: " + properties.getFilePath());

        reader.setStrict(true);
        reader.setLinesToSkip(1); // saltar encabezado

        DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("firstname", "lastname", "age", "email", "occupation");
        defaultLineMapper.setLineTokenizer(tokenizer);

        // 👇 Usa el bean inyectado
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper((line, lineNumber) -> {
            if (line.trim().isEmpty()) return null;
            return defaultLineMapper.mapLine(line, lineNumber);
        });

        return reader;
    }

}

