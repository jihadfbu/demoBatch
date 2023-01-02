package com.example.demo.steps;

import com.example.demo.Fornecedor;
import com.example.demo.validator.ValidationProcessorFornecedor;
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
public class TransferFornecedorStepConfig {


    @Autowired
    private JobExplorer jobExplorer;

    private String fileOrigin ="files/fornecedores.csv";

    long lastPos = 0;
    @Autowired
    Util util;

    @Bean
    public Step transferFornecedorStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        util.getLastFailed(jobExplorer);
        return new StepBuilder("transferFornecedorStep",jobRepository)
                    .<Fornecedor, Fornecedor>chunk(5,transactionManager)
                    .reader(readerFornecedor())
                    .processor(processorFornecedor())
                    .faultTolerant().retryLimit(3).retry(Exception.class)
                    .writer(writerFornecedor())
                    .build();

    }


    @Bean
    ItemReader<Fornecedor> readerFornecedor() {
       if(util.getStepName().equals("transferFornecedorStep")){
           lastPos = util.getLastPos();
       }
        return new FlatFileItemReaderBuilder<Fornecedor>()
                .name("reader")
                .resource(new FileSystemResource(fileOrigin))
                .comments("--")
                .linesToSkip((int)lastPos)
                .delimited()
                .names("id","nome", "cnpj")
                .targetType(Fornecedor.class)
                .build();
    }

    @Bean
    public FlatFileItemWriter<Fornecedor> writerFornecedor() throws Exception {
        boolean append = (lastPos==0)?false:true;
        return new FlatFileItemWriterBuilder<Fornecedor>()
                .append(append)
                .name("customerCreditWriter")
                .resource(new FileSystemResource("files/fornecedores2.csv"))
                .delimited()
                .delimiter("|")
                .names( "id","nome", "cnpj")
                .build();
    }

    @Bean
    public ItemProcessor<Fornecedor, Fornecedor> processorFornecedor() {
        return new ValidationProcessorFornecedor();
    }

}
