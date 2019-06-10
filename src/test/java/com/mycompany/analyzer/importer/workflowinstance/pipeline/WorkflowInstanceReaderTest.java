package com.mycompany.analyzer.importer.workflowinstance.pipeline;

import com.mycompany.analyzer.ApplicationTestConfigurations;
import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.workflow.data.Workflow;
import com.mycompany.analyzer.importer.workflowinstance.data.WorkflowInstance;
import com.mycompany.analyzer.importer.workflowinstance.data.WorkflowInstanceStatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTestConfigurations.class)
public class WorkflowInstanceReaderTest {

    private static final String WORKFLOW_INSTANCE_FILE = "test_data/workflowInstances_unit_test.data";

    @Autowired
    private WorkflowInstanceReader workflowInstanceReader;

    @Test
    public void shouldReadContractorsAndReportErrors() throws Exception {
        workflowInstanceReader.getDelegate().setResource(new ClassPathResource(WORKFLOW_INSTANCE_FILE));
        workflowInstanceReader.open(MetaDataInstanceFactory.createStepExecution().getExecutionContext());

        WorkflowInstance expectedEntity;

        //validate records
        expectedEntity = new WorkflowInstance("111",
                                              new Employee("email1@something.com"),
                                              "step 1",
                                              WorkflowInstanceStatus.RUNNING,
                                              new Workflow("1"));
        assertThat(workflowInstanceReader.read().getBusinessEntity(), sameBeanAs(expectedEntity));

        expectedEntity = new WorkflowInstance("112",
                                              new Employee("email2@something.com"),
                                              "step 2",
                                              WorkflowInstanceStatus.PAUSED,
                                              new Workflow("2"));
        assertThat(workflowInstanceReader.read().getBusinessEntity(), sameBeanAs(expectedEntity));
    }
}
