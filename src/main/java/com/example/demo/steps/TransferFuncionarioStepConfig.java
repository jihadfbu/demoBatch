package com.example.demo.steps;

import com.example.demo.Pessoa;
import com.example.demo.validator.ValidationProcessorFuncionario;
import com.example.demo.util.Util;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransferFuncionarioStepConfig {


    private String fileOrigin ="files/funcionarios.csv";

    long lastPos = 0;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    Util util;

    @Bean
    public Step transferFuncionarioStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        util.getLastFailed(jobExplorer);
        return new StepBuilder("transferFuncionarioStep",jobRepository)
                .<Pessoa, Pessoa>chunk(5,transactionManager)
                .reader(readerFuncionario())
                .allowStartIfComplete(false)
                .processor(processorFuncionario())
                .faultTolerant().retryLimit(3).retry(Exception.class)
                .writer(writerFuncionario())
                .build();

    }


    @Bean
    ItemReader<Pessoa> readerFuncionario() {
        if(util.getStepName().equals("transferFuncionarioStep")){
            lastPos = util.getLastPos();
        }
        return new FlatFileItemReaderBuilder<Pessoa>()
                .name("readerFuncionario")
                .resource(new FileSystemResource(fileOrigin))
                .comments("--")
                .linesToSkip((int)lastPos)
                .delimited()
                .names("nome", "email", "dataNascimento", "idade", "id")
                .targetType(Pessoa.class)
                .build();
    }

    @Bean
    public FlatFileItemWriter<Pessoa> writerFuncionario() throws Exception {
        boolean append = (lastPos==0)?false:true;
        return new FlatFileItemWriterBuilder<Pessoa>()
                .append(append)
                .name("writerFuncionario")
                .resource(new FileSystemResource("files/funcionarios2.csv"))
                .delimited()
                .delimiter("|")
                .names("nome", "email", "dataNascimento", "idade", "id")
                .build();
    }

    @Bean
    public ItemProcessor<Pessoa, Pessoa> processorFuncionario() {
        return new ValidationProcessorFuncionario();
    }

}
