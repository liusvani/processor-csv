package com.processor_csv.infraestructure.quartz.launcher;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import org.springframework.batch.core.JobParameters;

@Component
public class BatchJobLauncher implements org.quartz.Job{

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job userImportJob;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // Parámetros únicos para evitar conflictos de ejecución
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(userImportJob, params);
        } catch (Exception e) {
            // Manejo explícito del error para Quartz
            JobExecutionException jobException = new JobExecutionException("Falló la ejecución del batch", e);
            jobException.setRefireImmediately(false); // se cambia para que haga reintentos
            throw jobException;
        }
    }
}


