package com.mycompany.analyzer.importer.employee;

import com.google.common.collect.ImmutableMap;

import com.mycompany.analyzer.core.entity.PipelineCandidate;
import com.mycompany.analyzer.core.utils.BatchProcessingUtils;
import com.mycompany.analyzer.core.utils.LineAwareFieldSet;
import com.mycompany.analyzer.importer.employee.data.EmployeeDto;
import com.mycompany.analyzer.importer.employee.pipeline.EmployeeProcessor;
import com.mycompany.analyzer.importer.employee.pipeline.EmployeeReader;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmployeeConfigurations {

    @Value("${workflow.analyzer.input.employees}")
    private String employeesInputFile;

    @Value("${workflow.analyzer.input.contractors}")
    private String contractorsInputFile;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public EmployeeReader employeeReader() {
        return new EmployeeReader() {{
            setDelegate(employeeReaderDelegate());
            setResource(employeesInputFile);
        }};
    }

    @Bean
    public EmployeeReader contractorReader() {
        return new EmployeeReader() {{
            setDelegate(contractorsReaderDelegate());
            setContractors(true);
            setResource(contractorsInputFile);
        }};
    }

    @Bean
    public FlatFileItemReader<LineAwareFieldSet> employeeReaderDelegate() {
        return BatchProcessingUtils.newFlatFileItemReader("employeeReader",
                                                          employeesInputFile,
                                                          tokenizerMap().put("employeeId*", BatchProcessingUtils.dataTokenizer).build(),
                                                          "EMPLOYEES");
    }

    @Bean
    public FlatFileItemReader<LineAwareFieldSet> contractorsReaderDelegate() {
        return BatchProcessingUtils.newFlatFileItemReader("contractorReader",
                                                          contractorsInputFile,
                                                          tokenizerMap().put("contractorName*", BatchProcessingUtils.dataTokenizer).build(),
                                                          "CONTRACTORS");
    }

    private ImmutableMap.Builder<String, LineTokenizer> tokenizerMap() {
        return ImmutableMap.<String, LineTokenizer>builder()
                .put("fullName*", BatchProcessingUtils.dataTokenizer)
                .put("email*", BatchProcessingUtils.dataTokenizer);
    }

    @Bean
    public EmployeeProcessor employeeProcessor() {
        return new EmployeeProcessor();
    }

    @Bean
    public Step importEmployees(ClassifierCompositeItemWriter<PipelineCandidate> compositeWriter) {
        return importWorkers("importEmployees", employeeReader(), compositeWriter);
    }

    @Bean
    public Step importContractors(ClassifierCompositeItemWriter<PipelineCandidate> compositeWriter) {
        return importWorkers("importContractors", contractorReader(), compositeWriter);
    }

    private Step importWorkers(String name,
                               EmployeeReader employeeReader,
                               ClassifierCompositeItemWriter<PipelineCandidate> compositeWriter) {
        return stepBuilderFactory.get(name)
                .<EmployeeDto, PipelineCandidate>chunk(50)
                .reader(employeeReader)
                .processor(employeeProcessor())
                .writer(compositeWriter)
                .build();
    }
}
