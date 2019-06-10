package com.mycompany.analyzer.importer.employee.pipeline;

import com.mycompany.analyzer.core.entity.PipelineCandidate;
import com.mycompany.analyzer.core.pipeline.AbstractItemProcessor;
import com.mycompany.analyzer.core.pipeline.AbstractItemProcessorTest;
import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.employee.data.EmployeeDto;
import com.mycompany.analyzer.importer.errors.data.BusinessErrorsCollection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
public class EmployeeProcessorTest extends AbstractItemProcessorTest<EmployeeDto, Employee> {

    @Autowired
    private EmployeeProcessor employeeProcessor;

    @Override
    protected void enrichEntityWithValidData(Employee entity) {
        entity.setCode("someCode");
        entity.setEmail("email@something.com");
        entity.setFullName("name");
    }

    @Test
    public void shouldReportDuplicateEmployeeEmail() throws Exception {
        //given
        EmployeeDto emptyDto = getValidDto();

        //when
        PipelineCandidate candidate = getProcessorUnderTest().process(emptyDto);

        //then
        assertThat(candidate.getClass().equals(Employee.class), is(true));

        Employee employee = (Employee) candidate;

        //and when
        EmployeeDto otherDto = getValidDto();
        otherDto.getEmployee().setCode("otherCode"); //so different codes, but same email
        candidate = getProcessorUnderTest().process(otherDto);

        //then
        assertThat(candidate instanceof BusinessErrorsCollection, is(true));

        BusinessErrorsCollection errorsCollection = (BusinessErrorsCollection) candidate;

        assertThat(errorsCollection.getBusinessErrors().size(), is(1));

        assertThat(errorsCollection.getBusinessErrors().get(0).getEntity(), is(employee.getClass().getSimpleName()));

        assertThat(errorsCollection.getBusinessErrors().get(0).getConstraintViolation().contains(employee.getEmail()), is(true));
    }

    @Override
    protected Employee getNewEntity() {
        return new Employee();
    }

    @Override
    protected AbstractItemProcessor<EmployeeDto, Employee> getProcessorUnderTest() {
        return employeeProcessor;
    }

    @Override
    protected EmployeeDto getNewDto() {
        return new EmployeeDto();
    }
}
