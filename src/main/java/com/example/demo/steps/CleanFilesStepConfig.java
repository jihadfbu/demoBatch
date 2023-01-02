package com.example.demo.steps;

import com.example.demo.FileCleanTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CleanFilesStepConfig {

    private String fileOrigin ="files/clientes.csv";

    @Bean
    public Step cleanFilesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        FileCleanTasklet task = new FileCleanTasklet();
        task.setFileName(fileOrigin);
        return new StepBuilder("cleanFilesStep",jobRepository)
                .tasklet(task,transactionManager)
                .build();
    }
}
