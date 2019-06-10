package com.mycompany.analyzer.importer.workflow.pipeline;

import com.mycompany.analyzer.core.entity.PipelineCandidate;
import com.mycompany.analyzer.core.pipeline.AbstractItemProcessor;
import com.mycompany.analyzer.core.pipeline.AbstractItemProcessorTest;
import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.employee.repository.EmployeeRepository;
import com.mycompany.analyzer.importer.errors.data.BusinessErrorsCollection;
import com.mycompany.analyzer.importer.workflow.data.Workflow;
import com.mycompany.analyzer.importer.workflow.data.WorkflowDto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
public class WorkflowProcessorTest extends AbstractItemProcessorTest<WorkflowDto, Workflow> {

    @Autowired
    private WorkflowProcessor workflowProcessor;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    protected void enrichEntityWithValidData(Workflow entity) {
        entity.setCode("someCode");
        entity.setName("name");
        entity.setVersion(1L);
        entity.setAuthor(getAuthor());
    }

    @Test
    public void shouldReportIfAuthorNotFound() throws Exception {
        //given
        WorkflowDto workflowDto= getValidDto();
        employeeRepository.deleteAll();

        PipelineCandidate candidate = getProcessorUnderTest().process(workflowDto);

        //then
        assertThat(candidate instanceof BusinessErrorsCollection, is(true));

        BusinessErrorsCollection errorsCollection = (BusinessErrorsCollection) candidate;

        assertThat(errorsCollection.getBusinessErrors().size(), is(1));

        assertThat(errorsCollection.getBusinessErrors().get(0).getEntity(), is(Workflow.class.getSimpleName()));

        assertThat(errorsCollection.getBusinessErrors().get(0).getConstraintViolation().contains("email@something.com"), is(true));
    }

    @Override
    protected Workflow getNewEntity() {
        return new Workflow();
    }

    @Override
    protected AbstractItemProcessor<WorkflowDto, Workflow> getProcessorUnderTest() {
        return workflowProcessor;
    }

    @Override
    protected WorkflowDto getNewDto() {
        return new WorkflowDto();
    }

    private Employee getAuthor() {
        return employeeRepository.save(new Employee("code", "name", "email@something.com", false));
    }
}
