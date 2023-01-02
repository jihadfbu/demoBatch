package com.example.demo.steps;

import com.example.demo.Pessoa;
import com.example.demo.validator.ValidationProcessorCliente;
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
public class TransferClienteStepConfig {


    private String fileOrigin ="files/clientes.csv";

    long lastPos = 0;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    Util util;

    @Bean
    public Step transferClienteStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        util.getLastFailed(jobExplorer);
        return new StepBuilder("transferClienteStep",jobRepository)
                .<Pessoa, Pessoa>chunk(5,transactionManager)
                .reader(readerCliente())
                .allowStartIfComplete(false)
                .processor(processorCliente())
                .faultTolerant().retryLimit(3).retry(Exception.class)
                .writer(writerCliente())
                .build();

    }


    @Bean
    ItemReader<Pessoa> readerCliente() {
        if(util.getStepName().equals("transferClienteStep")){
            lastPos = util.getLastPos();
        }
        return new FlatFileItemReaderBuilder<Pessoa>()
                .name("readerCliente")
                .resource(new FileSystemResource(fileOrigin))
                .comments("--")
                .linesToSkip((int)lastPos)

                .delimited()
                .names("nome", "email", "dataNascimento", "idade", "id")
                .targetType(Pessoa.class)
                .build();
    }

    @Bean
    public FlatFileItemWriter<Pessoa> writerCliente() throws Exception {
        boolean append = (lastPos==0)?false:true;
        return new FlatFileItemWriterBuilder<Pessoa>()
                .append(append)
                .name("writerCliente")
                .resource(new FileSystemResource("files/clientes2.csv"))
                .delimited()
                .delimiter("|")
                .names("nome", "email", "dataNascimento", "idade", "id")
                .build();
    }

    @Bean
    public ItemProcessor<Pessoa, Pessoa> processorCliente() {
        return new ValidationProcessorCliente();
    }

}
