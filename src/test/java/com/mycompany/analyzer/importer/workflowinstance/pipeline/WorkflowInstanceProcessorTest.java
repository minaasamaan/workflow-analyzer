package com.mycompany.analyzer.importer.workflowinstance.pipeline;

import com.mycompany.analyzer.core.entity.PipelineCandidate;
import com.mycompany.analyzer.core.pipeline.AbstractItemProcessor;
import com.mycompany.analyzer.core.pipeline.AbstractItemProcessorTest;
import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.employee.repository.EmployeeRepository;
import com.mycompany.analyzer.importer.errors.data.BusinessErrorsCollection;
import com.mycompany.analyzer.importer.workflow.data.Workflow;
import com.mycompany.analyzer.importer.workflow.repository.WorkflowRepository;
import com.mycompany.analyzer.importer.workflowinstance.data.WorkflowInstance;
import com.mycompany.analyzer.importer.workflowinstance.data.WorkflowInstanceDto;
import com.mycompany.analyzer.importer.workflowinstance.data.WorkflowInstanceStatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
public class WorkflowInstanceProcessorTest extends AbstractItemProcessorTest<WorkflowInstanceDto, WorkflowInstance> {

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkflowInstanceProcessor workflowInstanceProcessor;

    @Override
    protected void enrichEntityWithValidData(WorkflowInstance entity) {
        entity.setCode("someCode");
        Employee employee= getEmployee();
        entity.setAssignee(employee);
        entity.setWorkflow(getWorkflow());
        entity.setStatus(WorkflowInstanceStatus.RUNNING);
        entity.setStep("step");
    }

    @Test
    public void shouldReportIfAssigneeNotFound() throws Exception {
        //given
        WorkflowInstanceDto workflowDto= getValidDto();
        employeeRepository.delete(employeeRepository.findByCode("code"));

        //then
        assertBusinessErrorExists(workflowDto, "email@something.com");
    }

    @Test
    public void shouldReportIfWorkflowNotFound() throws Exception {
        //given
        WorkflowInstanceDto workflowDto= getValidDto();
        workflowRepository.deleteAll();

        //then
        assertBusinessErrorExists(workflowDto, "workflow_code");
    }


    @Override
    protected WorkflowInstance getNewEntity() {
        return new WorkflowInstance();
    }

    @Override
    protected AbstractItemProcessor<WorkflowInstanceDto, WorkflowInstance> getProcessorUnderTest() {
        return workflowInstanceProcessor;
    }

    @Override
    protected WorkflowInstanceDto getNewDto() {
        return new WorkflowInstanceDto();
    }

    private Employee getEmployee() {
        return employeeRepository.save(new Employee("code", "name", "email@something.com", false));
    }

    private Workflow getWorkflow() {
        Employee employee= employeeRepository.save(new Employee("code1", "name1", "email1@something.com", false));
        return workflowRepository.save(new Workflow("workflow_code", "name", employee, 1L));
    }

    private void assertBusinessErrorExists(WorkflowInstanceDto workflowDto, String violationContent) throws Exception {
        //when
        PipelineCandidate candidate = getProcessorUnderTest().process(workflowDto);

        //then
        assertThat(candidate instanceof BusinessErrorsCollection, is(true));

        BusinessErrorsCollection errorsCollection = (BusinessErrorsCollection) candidate;

        assertThat(errorsCollection.getBusinessErrors().size(), is(1));

        assertThat(errorsCollection.getBusinessErrors().get(0).getEntity(), is(WorkflowInstance.class.getSimpleName()));

        assertThat(errorsCollection.getBusinessErrors().get(0).getConstraintViolation().contains(violationContent), is(true));
    }
}
