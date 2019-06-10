package com.mycompany.analyzer.importer.workflowinstance;

import com.google.common.collect.ImmutableMap;

import com.mycompany.analyzer.core.entity.PipelineCandidate;
import com.mycompany.analyzer.core.utils.BatchProcessingUtils;
import com.mycompany.analyzer.core.utils.LineAwareFieldSet;
import com.mycompany.analyzer.importer.workflowinstance.data.WorkflowInstanceDto;
import com.mycompany.analyzer.importer.workflowinstance.pipeline.WorkflowInstanceProcessor;
import com.mycompany.analyzer.importer.workflowinstance.pipeline.WorkflowInstanceReader;

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
public class WorkflowInstanceConfigurations {

    @Value("${workflow.analyzer.input.workflow_instances}")
    private String inputFile;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public WorkflowInstanceReader workflowInstanceReader() {
        return new WorkflowInstanceReader() {{
            setDelegate(workflowInstanceReaderDelegate());
            setResource(inputFile);
        }};
    }

    @Bean
    public FlatFileItemReader<LineAwareFieldSet> workflowInstanceReaderDelegate() {
        return BatchProcessingUtils.newFlatFileItemReader("workflowInstanceReader",
                                                          inputFile,
                                                          tokenizerMap(),
                                                          "WORKFLOW INSTANCES");//TODO change to configuration for all entities
    }

    @Bean
    public WorkflowInstanceProcessor workflowInstanceProcessor() {
        return new WorkflowInstanceProcessor();
    }

    @Bean
    public Step importWorkflowInstance(ClassifierCompositeItemWriter<PipelineCandidate> compositeWriter) {
        return stepBuilderFactory.get("importWorkflowInstance")
                .<WorkflowInstanceDto, PipelineCandidate>chunk(50)//TODO change to configuration for all steps
                                                                  .reader(workflowInstanceReader())
                                                                  .processor(workflowInstanceProcessor())
                                                                  .writer(compositeWriter)
                                                                  .build();
    }

    private Map<String, LineTokenizer> tokenizerMap() {
        return ImmutableMap.<String, LineTokenizer>builder()
                .put("id*", BatchProcessingUtils.dataTokenizer)
                .put("workflowId*", BatchProcessingUtils.dataTokenizer)
                .put("assignee*", BatchProcessingUtils.dataTokenizer)
                .put("step*", BatchProcessingUtils.dataTokenizer)
                .put("status*", BatchProcessingUtils.dataTokenizer)
                .build();
    }
}
