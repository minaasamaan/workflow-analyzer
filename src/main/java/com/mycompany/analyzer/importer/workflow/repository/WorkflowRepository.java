package com.mycompany.analyzer.importer.workflow.repository;

import com.mycompany.analyzer.core.repository.BusinessEntityRepository;
import com.mycompany.analyzer.importer.workflow.data.Workflow;
import com.mycompany.analyzer.importer.workflow.data.WorkflowStatisticsDto;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WorkflowRepository extends BusinessEntityRepository<Workflow> {

    @Query("select new com.mycompany.analyzer.importer.workflow.data.WorkflowStatisticsDto(workflow, count(instance)) from com.mycompany.analyzer.importer.workflow.data.Workflow workflow inner join workflow.workflowInstances instance where instance.status='RUNNING' group by workflow")
    List<WorkflowStatisticsDto> countRunningInstancesPerWorkflow();
}
