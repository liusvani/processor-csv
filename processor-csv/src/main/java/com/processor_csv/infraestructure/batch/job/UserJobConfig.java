package com.processor_csv.infraestructure.batch.job;

import com.processor_csv.domain.model.User;
import com.processor_csv.infraestructure.batch.listener.UserSkipListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@EnableBatchProcessing
public class UserJobConfig {


    private final FlatFileItemReader<User> reader;


    private final ItemProcessor<User, User> processor;


    private final ItemWriter<User> writer;

    public UserJobConfig(FlatFileItemReader<User> reader, ItemProcessor<User, User> processor, ItemWriter<User> writer) {
        this.reader = reader;
        this.processor = processor;
        this.writer = writer;
    }

    @Bean
    public UserSkipListener userSkipListener() {
        return new UserSkipListener();
    }
    /*@Autowired
    private SkipListener<User, User> skipListener;
    */
    @Bean
    public Job userImportJob(JobRepository jobRepository, Step userImportStep) {
        return new JobBuilder("userImportJob", jobRepository)
                .start(userImportStep)
                .build();
    }

    @Bean
    public Step userImportStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               ItemReader<User> reader,
                               ItemProcessor<User, User> processor,
                               ItemWriter<User> writer,
                               SkipPolicy FlatFileParseExceptionSkipPolicy,
                               UserSkipListener userSkipListener) {
        return new StepBuilder("userImportStep", jobRepository)
                .<User, User>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipLimit(5)
                .skipPolicy(FlatFileParseExceptionSkipPolicy)
                .skip(Exception.class)
                .listener((SkipListener<User, User>) userSkipListener)
                .listener((StepExecutionListener) userSkipListener)
                .build();
    }

}

