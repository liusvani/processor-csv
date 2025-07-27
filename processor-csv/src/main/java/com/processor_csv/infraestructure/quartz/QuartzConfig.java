package com.processor_csv.infraestructure.quartz;

import com.processor_csv.infraestructure.quartz.launcher.BatchJobLauncher;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(BatchJobLauncher.class)
                .withIdentity("userJob")
                .storeDurably() //permance almacenado en schelduler
                .build();
    }


    @Bean
    public Trigger trigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("userTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?")) // cada minuto
                .build();
    }


}

