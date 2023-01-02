package com.example.demo;

import java.io.PrintWriter;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class FileCleanTasklet implements Tasklet, InitializingBean {

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        PrintWriter writer = new PrintWriter(fileName);
        writer.print("");
        return RepeatStatus.FINISHED;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(fileName, "file must be set");
    }
}