package com.mycompany.analyzer.importer.workflow;

import com.google.common.collect.ImmutableMap;

import com.mycompany.analyzer.core.entity.PipelineCandidate;
import com.mycompany.analyzer.core.utils.BatchProcessingUtils;
import com.mycompany.analyzer.core.utils.LineAwareFieldSet;
import com.mycompany.analyzer.importer.workflow.data.WorkflowDto;
import com.mycompany.analyzer.importer.workflow.pipeline.WorkflowProcessor;
import com.mycompany.analyzer.importer.workflow.pipeline.WorkflowReader;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class WorkflowConfigurations {

    @Value("${workflow.analyzer.input.workflows}")
    private String inputFile;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public WorkflowReader workflowReader() {
        return new WorkflowReader(){{
         setDelegate(workflowReaderDelegate());
         setResource(inputFile);
        }};
    }

    @Bean
    public FlatFileItemReader<LineAwareFieldSet> workflowReaderDelegate() {
        return BatchProcessingUtils.newFlatFileItemReader("employeeReader", inputFile, tokenizerMap() , "WORKFLOWS");
    }

    private Map<String, LineTokenizer> tokenizerMap() {
        return ImmutableMap.<String, LineTokenizer>builder()
                .put("id*", BatchProcessingUtils.dataTokenizer)
                .put("name*", BatchProcessingUtils.dataTokenizer)
                .put("author*", BatchProcessingUtils.dataTokenizer)
                .put("version*", BatchProcessingUtils.dataTokenizer)
                .build();
    }

    @Bean
    public WorkflowProcessor workflowProcessor() {
        return new WorkflowProcessor();
    }

    @Bean
    public Step importWorkflow(ClassifierCompositeItemWriter<PipelineCandidate> compositeWriter) {
        return stepBuilderFactory.get("importWorkflow")
                .<WorkflowDto,PipelineCandidate>chunk(50)
                .reader(workflowReader())
                .processor(workflowProcessor())
                .writer(compositeWriter)
                .build();
    }
}
