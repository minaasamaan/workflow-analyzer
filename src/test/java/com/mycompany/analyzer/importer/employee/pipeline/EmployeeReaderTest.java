package com.mycompany.analyzer.importer.employee.pipeline;

import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.employee.data.EmployeeDto;
import com.mycompany.analyzer.importer.errors.data.ParsingErrorType;
import com.mycompany.analyzer.ApplicationTestConfigurations;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTestConfigurations.class)
public class EmployeeReaderTest {

    private static final String EMPLOYEES_FILE="test_data/employees_unit_test.data";
    private static final String CONTRACTORS_FILE="test_data/contractors_unit_test.data";

    @Autowired
    @Qualifier("employeeReader")
    private EmployeeReader employeeReader;

    @Autowired
    @Qualifier("contractorReader")
    private EmployeeReader contractorReader;

    @Test
    public void shouldReadEmployeesAndReportErrors() throws Exception {

        employeeReader.getDelegate().setResource(new ClassPathResource(EMPLOYEES_FILE));
        employeeReader.open(MetaDataInstanceFactory.createStepExecution().getExecutionContext());

        Employee expectedEntity;
        EmployeeDto employeeDto;

        //valid records
        expectedEntity = new Employee("0000", "John Doe", "john.doe@company.local", false);
        MatcherAssert.assertThat(employeeReader.read().getBusinessEntity(), samePropertyValuesAs(expectedEntity));

        expectedEntity = new Employee("0199", "Liam Mills", "liam.mills@company.local", false);
        MatcherAssert.assertThat(employeeReader.read().getBusinessEntity(), samePropertyValuesAs(expectedEntity));

        //Unknown fields
        employeeDto= employeeReader.read();
        MatcherAssert.assertThat(employeeDto.getParsingErrorsCollection().getParsingErrors().size(), is(4));

        final long[] lineNum = {13}; //start of defected lines
        employeeDto.getParsingErrorsCollection().getParsingErrors().forEach(parsingError -> {
            MatcherAssert.assertThat(parsingError.getError(), Matchers.is(ParsingErrorType.INVALID_TOKEN));
            MatcherAssert.assertThat(parsingError.getLineNumber(), is(lineNum[0]));
            lineNum[0]++;
        });

        //Identify corrupted record boundaries
        employeeDto= employeeReader.read();
        MatcherAssert.assertThat(employeeDto.getParsingErrorsCollection().getParsingErrors().size(), is(7));

        employeeDto= employeeReader.read();
        MatcherAssert.assertThat(employeeDto.getParsingErrorsCollection().getParsingErrors().size(), is(2));

        //Process valid data normally after failures
        employeeDto= employeeReader.read();
        expectedEntity = new Employee("0198", "Helga Doster", "h.doster@company.local", false);
        assertThat(employeeDto.getBusinessEntity(), samePropertyValuesAs(expectedEntity));
        MatcherAssert.assertThat(employeeDto.getParsingErrorsCollection().isEmpty(), is(true));
    }

    @Test
    public void shouldReadContractors() throws Exception {
        contractorReader.getDelegate().setResource(new ClassPathResource(CONTRACTORS_FILE));
        contractorReader.open(MetaDataInstanceFactory.createStepExecution().getExecutionContext());

        Employee expectedEntity;

        //valid records
        expectedEntity = new Employee("con24", "Conny Contractor", "conny.contractor@example.com", true);
        MatcherAssert.assertThat(contractorReader.read().getBusinessEntity(), samePropertyValuesAs(expectedEntity));

        expectedEntity = new Employee("con99", "Erica External", "erica.external@example.com", true);
        MatcherAssert.assertThat(contractorReader.read().getBusinessEntity(), samePropertyValuesAs(expectedEntity));
    }
}
