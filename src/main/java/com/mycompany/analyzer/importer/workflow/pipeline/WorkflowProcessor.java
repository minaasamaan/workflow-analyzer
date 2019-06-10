package com.mycompany.analyzer.importer.workflow.pipeline;

import com.mycompany.analyzer.core.pipeline.AbstractItemProcessor;
import com.mycompany.analyzer.core.repository.BusinessEntityRepository;
import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.employee.repository.EmployeeRepository;
import com.mycompany.analyzer.importer.errors.data.BusinessErrorsCollection;
import com.mycompany.analyzer.importer.workflow.data.WorkflowDto;
import com.mycompany.analyzer.importer.workflow.data.Workflow;
import com.mycompany.analyzer.importer.workflow.repository.WorkflowRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class WorkflowProcessor extends AbstractItemProcessor<WorkflowDto, Workflow> {

    private static final Logger log = LoggerFactory.getLogger(WorkflowProcessor.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Override
    public void doProcess(final Workflow workflow,
                          BusinessErrorsCollection businessErrorsCollection) {
        log.info("Converting (" + workflow + ")");

        Employee employee = employeeRepository.findByEmail(workflow.getAuthor().getEmail());

        if (employee == null) {
            reportBusinessError(workflow.getClass(), String.format("No employee exists with email %s", workflow.getAuthor().getEmail()), businessErrorsCollection);
        }
        workflow.setAuthor(employee);
    }

    @Override
    protected BusinessEntityRepository<Workflow> getRepository() {
        return workflowRepository;
    }
}
