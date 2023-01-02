package com.example.demo.util;

import lombok.Getter;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Configuration
public class Util {

    private String stepName = "";

    private long lastPos = 0;

    public boolean getLastFailed(JobExplorer jobExplorer){
        List<JobExecution> jobsExecution = jobExplorer.getJobExecutions(jobExplorer.getLastJobInstance("job"));
        if(jobsExecution.size()>0){
            if(jobsExecution.get(0).getStatus().equals(BatchStatus.FAILED)){
                Collection<StepExecution> stepExecutionList = jobsExecution.get(0).getStepExecutions();
                for(StepExecution step: stepExecutionList){
                        if (step.getStatus().equals(BatchStatus.FAILED)) {
                            this.stepName = step.getStepName();
                            this.lastPos = step.getWriteCount();
                            return true;
                    }
                }
            }
        }
        return false;
    }
}
