package com.mycompany.analyzer;

import com.mycompany.analyzer.importer.ApplicationConfigurations;
import com.mycompany.analyzer.importer.employee.EmployeeConfigurations;
import com.mycompany.analyzer.importer.workflow.WorkflowConfigurations;
import com.mycompany.analyzer.importer.workflowinstance.WorkflowInstanceConfigurations;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import({
                EmployeeConfigurations.class,
                WorkflowConfigurations.class,
                WorkflowInstanceConfigurations.class,
                ApplicationConfigurations.class})
public class ApplicationTestConfigurations {
    @Autowired
    private Job importWorkflowInformationJob;

    @Bean
    JobLauncherTestUtils jobLauncherTestUtils()
            throws NoSuchJobException {
        JobLauncherTestUtils jobLauncherTestUtils =
                new JobLauncherTestUtils();
        jobLauncherTestUtils.setJob(importWorkflowInformationJob);

        return jobLauncherTestUtils;
    }
}
