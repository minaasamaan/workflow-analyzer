package com.mycompany.analyzer.core.pipeline;

import com.mycompany.analyzer.core.dto.ImportedItemDto;
import com.mycompany.analyzer.core.entity.BusinessEntity;
import com.mycompany.analyzer.core.entity.PipelineCandidate;
import com.mycompany.analyzer.core.utils.ProcessingCache;
import com.mycompany.analyzer.importer.employee.repository.EmployeeRepository;
import com.mycompany.analyzer.importer.errors.data.BusinessErrorsCollection;
import com.mycompany.analyzer.importer.errors.data.ParsingError;
import com.mycompany.analyzer.importer.errors.data.ParsingErrorType;
import com.mycompany.analyzer.importer.errors.data.ParsingErrorsCollection;
import com.mycompany.analyzer.importer.workflow.repository.WorkflowRepository;
import com.mycompany.analyzer.importer.workflowinstance.repository.WorkflowInstanceRepository;
import com.mycompany.analyzer.ApplicationTestConfigurations;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

@SpringBootTest(classes = ApplicationTestConfigurations.class)
public abstract class AbstractItemProcessorTest<Dto extends ImportedItemDto<E>, E extends BusinessEntity> {

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Before
    public void cleanUp(){
        workflowInstanceRepository.deleteAll();
        workflowRepository.deleteAll();
        employeeRepository.deleteAll();
        ProcessingCache.purgeAll();
    }

    @Test
    public void shouldReturnParsingErrorsAndNotProcessEntity() throws Exception {
        //given
        String resource = "resource", line = "someLine";
        ParsingErrorType errorType = ParsingErrorType.INVALID_TOKEN;
        long lineNum = 1;

        ParsingError expectedParsingError = new ParsingError(resource, errorType, lineNum, line);

        Dto employeeDtoWithParsingErrors = getNewDto();
        employeeDtoWithParsingErrors.addParsingError(resource, errorType, lineNum, line);

        //when
        PipelineCandidate candidate = getProcessorUnderTest().process(employeeDtoWithParsingErrors);

        //then
        assertThat(candidate instanceof ParsingErrorsCollection, is(true));

        ParsingErrorsCollection errorsCollection = (ParsingErrorsCollection) candidate;

        assertThat(errorsCollection.getParsingErrors().size(), is(1));

        assertThat(errorsCollection.getParsingErrors().stream().findFirst().get(),
                   samePropertyValuesAs(expectedParsingError));
    }

    @Test
    public void shouldReturnValidationErrorsAsBusinessErrors() throws Exception {
        //given
        Dto emptyDto = getNewDto();

        emptyDto.setBusinessEntity(getNewEntity());

        //when
        PipelineCandidate candidate = getProcessorUnderTest().process(emptyDto);

        //then
        assertThat(candidate instanceof BusinessErrorsCollection, is(true));

        BusinessErrorsCollection errorsCollection = (BusinessErrorsCollection) candidate;

        assertThat(errorsCollection.getBusinessErrors().isEmpty(), is(false));
    }

    @Test
    public void shouldReturnBusinessErrorWhenHavingDuplicateCode() throws Exception {
        //given
        Dto emptyDto = getNewDto();

        E entity = getNewEntity();

        enrichEntityWithValidData(entity);

        emptyDto.setBusinessEntity(entity);

        //when
        PipelineCandidate candidate = getProcessorUnderTest().process(emptyDto);

        //then
        assertThat(candidate.getClass().equals(entity.getClass()), is(true));

        entity = (E) candidate;

        //and when
        candidate = getProcessorUnderTest().process(emptyDto);

        //then
        assertThat(candidate instanceof BusinessErrorsCollection, is(true));

        BusinessErrorsCollection errorsCollection = (BusinessErrorsCollection) candidate;

        assertThat(errorsCollection.getBusinessErrors().size(), is(1));

        assertThat(errorsCollection.getBusinessErrors().get(0).getEntity(), is(entity.getClass().getSimpleName()));

        assertThat(errorsCollection.getBusinessErrors().get(0).getConstraintViolation().contains(entity.getCode()), is(true));
    }

    protected abstract void enrichEntityWithValidData(E entity);

    protected abstract E getNewEntity();

    protected abstract AbstractItemProcessor<Dto, E> getProcessorUnderTest();

    protected abstract Dto getNewDto();

    protected Dto getValidDto() {
        Dto dto = getNewDto();

        E entity = getNewEntity();

        enrichEntityWithValidData(entity);

        dto.setBusinessEntity(entity);

        return dto;
    }
}
