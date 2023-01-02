package com.example.demo;

import com.example.demo.util.Util;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
public class BatchConfig {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    JobOperator jobOperator;

    @Autowired
    JobExplorer jobExplorer;

    @Autowired
    Util util;


    @Bean
    public Job job(@Qualifier("transferClienteStep") Step transferClienteStep,
                   @Qualifier("transferFuncionarioStep") Step transferFuncionarioStep,
                   @Qualifier("transferFornecedorStep") Step transferFornecedorStep,
                   @Qualifier("cleanFilesStep") Step cleanFilesStep,
                   JobRepository jobRepository) {
        JobBuilder jobBuilder = new JobBuilder("job",jobRepository);
        util.getLastFailed(jobExplorer);
        return switch (util.getStepName()) {
            case "transferFornecedorStep" -> jobBuilder
                    .start(transferFornecedorStep)
                    .next(transferFuncionarioStep)
                    .next(cleanFilesStep)
                    .incrementer(new RunIdIncrementer())
                    .preventRestart()
                    .build();
            case "transferFuncionarioStep" -> jobBuilder
                    .start(transferFuncionarioStep)
                    .next(cleanFilesStep)
                    .incrementer(new RunIdIncrementer())
                    .preventRestart()
                    .build();
            default -> jobBuilder
                    .start(transferClienteStep)
                    .next(transferFornecedorStep)
                    .next(transferFuncionarioStep)
                    .next(cleanFilesStep)
                    .incrementer(new RunIdIncrementer())
                    .preventRestart()
                    .build();
        };
    }

}
