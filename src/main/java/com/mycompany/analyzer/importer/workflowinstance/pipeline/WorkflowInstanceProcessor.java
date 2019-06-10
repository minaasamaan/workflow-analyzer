package com.mycompany.analyzer.importer.workflowinstance.pipeline;

import com.mycompany.analyzer.core.pipeline.AbstractItemProcessor;
import com.mycompany.analyzer.core.repository.BusinessEntityRepository;
import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.employee.repository.EmployeeRepository;
import com.mycompany.analyzer.importer.errors.data.BusinessErrorsCollection;
import com.mycompany.analyzer.importer.workflow.data.Workflow;
import com.mycompany.analyzer.importer.workflow.repository.WorkflowRepository;
import com.mycompany.analyzer.importer.workflowinstance.data.WorkflowInstance;
import com.mycompany.analyzer.importer.workflowinstance.data.WorkflowInstanceDto;
import com.mycompany.analyzer.importer.workflowinstance.repository.WorkflowInstanceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class WorkflowInstanceProcessor extends AbstractItemProcessor<WorkflowInstanceDto, WorkflowInstance> {

    private static final Logger log = LoggerFactory.getLogger(WorkflowInstanceProcessor.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;

    @Override
    public void doProcess(final WorkflowInstance workflowInstance,
                          BusinessErrorsCollection businessErrorsCollection) {

        log.info("Converting (" + workflowInstance + ")");

        Employee employee = employeeRepository.findByEmail(workflowInstance.getAssignee().getEmail());

        if (employee == null) {
            reportBusinessError(workflowInstance.getClass(),
                                String.format("No employee exists with this assignee email: %s", workflowInstance.getAssignee().getEmail()),
                                businessErrorsCollection);
        }
        workflowInstance.setAssignee(employee);

        Workflow workflow = workflowRepository.findByCode(workflowInstance.getWorkflow().getCode());
        if (workflow == null) {
            reportBusinessError(workflowInstance.getClass(),
                                String.format("No workflow exists with given code: %s", workflowInstance.getWorkflow().getCode()),
                                businessErrorsCollection);
        }
        workflowInstance.setWorkflow(workflow);
    }

    @Override
    protected BusinessEntityRepository<WorkflowInstance> getRepository() {
        return workflowInstanceRepository;
    }
}
