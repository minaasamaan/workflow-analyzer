package com.mycompany.analyzer.importer;

import com.mycompany.analyzer.core.utils.ProcessingCache;
import com.mycompany.analyzer.importer.employee.repository.EmployeeRepository;
import com.mycompany.analyzer.importer.errors.repository.BusinessErrorRepository;
import com.mycompany.analyzer.importer.errors.repository.ParsingErrorRepository;
import com.mycompany.analyzer.importer.workflow.repository.WorkflowRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final WorkflowRepository workflowRepository;

    private final EmployeeRepository employeeRepository;

    private final BusinessErrorRepository businessErrorRepository;

    private final ParsingErrorRepository parsingErrorRepository;

    public JobCompletionNotificationListener(WorkflowRepository workflowRepository,
                                             EmployeeRepository employeeRepository,
                                             BusinessErrorRepository businessErrorRepository,
                                             ParsingErrorRepository parsingErrorRepository) {
        this.workflowRepository = workflowRepository;
        this.employeeRepository = employeeRepository;
        this.businessErrorRepository = businessErrorRepository;
        this.parsingErrorRepository = parsingErrorRepository;
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {

        //clean cache records
        ProcessingCache.purgeAll();

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info(
                    "\n======================================================= Workflow Analyzer Results ==========================================================\n");


            log.info(
                    "\n======================================================= Parsing Errors ==========================================================\n");

            parsingErrorRepository.findAll().forEach(parsingError ->
                                                             log.info("\n"+parsingError)
            );

            log.info(
                    "\n======================================================= Business violations ==========================================================\n");

            businessErrorRepository.findAll().forEach(businessError ->
                                                              log.info("\n"+ businessError)
            );

            log.info(
                    "\n======================================================= All Workflows with associated instances ==========================================================\n");
            workflowRepository.findAll().forEach(workflow -> {
                log.info("\n----------------------->");
                log.info("\n"+ workflow);
                log.info("\n----------------------->");
                workflow.getWorkflowInstances()
                        .forEach(workflowInstance -> log.info("\n"+ workflowInstance));
            });

            log.info(
                    "\n======================================================= Workflows with running instances count ==========================================================\n");

            workflowRepository.countRunningInstancesPerWorkflow().forEach(workflowStatisticsDto ->
                                                                                  log.info("\n"+ workflowStatisticsDto));

            log.info(
                    "\n======================================================= Contractors assigned to running instances ==========================================================\n");

            employeeRepository.findAllContractorsAssignedToRunningInstance().forEach(contractor ->
                                                                                             log.info("\n"+ contractor));
        }
    }
}
