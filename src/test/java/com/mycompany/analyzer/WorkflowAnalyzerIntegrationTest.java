package com.mycompany.analyzer;

import com.mycompany.analyzer.core.utils.ProcessingCache;
import com.mycompany.analyzer.importer.employee.repository.EmployeeRepository;
import com.mycompany.analyzer.importer.errors.repository.BusinessErrorRepository;
import com.mycompany.analyzer.importer.errors.repository.ParsingErrorRepository;
import com.mycompany.analyzer.importer.workflow.data.WorkflowStatisticsDto;
import com.mycompany.analyzer.importer.workflow.repository.WorkflowRepository;
import com.mycompany.analyzer.importer.workflowinstance.repository.WorkflowInstanceRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTestConfigurations.class)
@DirtiesContext(classMode= BEFORE_CLASS)
public class WorkflowAnalyzerIntegrationTest {

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private BusinessErrorRepository businessErrorRepository;

    @Autowired
    private ParsingErrorRepository parsingErrorRepository;

    @Before
    public void cleanUp(){
        workflowInstanceRepository.deleteAll();
        workflowRepository.deleteAll();
        employeeRepository.deleteAll();
        ProcessingCache.purgeAll();
    }

    @Test
    public void verifyAnalysisResults() throws Exception {
        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        //then
        assertThat("Check job execution status", jobExecution.getExitStatus(), is(ExitStatus.COMPLETED));

        //inconsistent entries
        assertThat("Check parsing errors", parsingErrorRepository.count(), is(4L));

        assertThat("Check business errors", businessErrorRepository.count(), is(3L));

        //workflows statistics
        assertThat("Check workflows", workflowRepository.count(), is(3L));

        assertThat("Check workflow instances", workflowInstanceRepository.count(), is(15L));

        assertThat("Check employees", employeeRepository.count(), is(9L));

        assertThat("Check active instances", workflowRepository.countRunningInstancesPerWorkflow().stream().mapToLong(
                WorkflowStatisticsDto::getRunningInstancesCount).sum(), is(6L));

        assertThat("Check contractors assigned to running instances",
                   employeeRepository.findAllContractorsAssignedToRunningInstance().size(),
                   is(2));
    }
}
