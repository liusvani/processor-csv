package com.processor_csv;

import com.processor_csv.config.UserBatchProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(UserBatchProperties.class)
@SpringBootApplication
public class ProcessorCsvApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessorCsvApplication.class, args);
	}

}
