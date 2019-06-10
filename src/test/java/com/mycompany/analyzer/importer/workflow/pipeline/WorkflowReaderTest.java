package com.mycompany.analyzer.importer.workflow.pipeline;

import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.workflow.data.Workflow;
import com.mycompany.analyzer.ApplicationTestConfigurations;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTestConfigurations.class)
public class WorkflowReaderTest {

    private static final String WORKFLOWS_FILE ="test_data/workflows_unit_test.data";

    @Autowired
    private WorkflowReader workflowReader;

    @Test
    public void shouldReadContractorsAndReportErrors() throws Exception {
        workflowReader.getDelegate().setResource(new ClassPathResource(WORKFLOWS_FILE));
        workflowReader.open(MetaDataInstanceFactory.createStepExecution().getExecutionContext());

        Workflow expectedEntity;

        //validate records
        expectedEntity = new Workflow("1", "name1", new Employee("email1@something.com"), 1L);
        MatcherAssert.assertThat(workflowReader.read().getBusinessEntity(), sameBeanAs(expectedEntity));

        expectedEntity = new Workflow("2", "name2", new Employee("email2@something.com"), 2L);
        MatcherAssert.assertThat(workflowReader.read().getBusinessEntity(), sameBeanAs(expectedEntity));
    }
}
