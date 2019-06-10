package com.mycompany.analyzer.importer;

import com.mycompany.analyzer.core.decision.PipelineCandidateWriterDecision;
import com.mycompany.analyzer.core.entity.Persisted;
import com.mycompany.analyzer.core.entity.PipelineCandidate;
import com.mycompany.analyzer.core.pipeline.EntityCollectionItemWriter;
import com.mycompany.analyzer.importer.JobCompletionNotificationListener;
import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.errors.data.BusinessError;
import com.mycompany.analyzer.importer.errors.data.BusinessErrorsCollection;
import com.mycompany.analyzer.importer.errors.data.ParsingError;
import com.mycompany.analyzer.importer.errors.data.ParsingErrorsCollection;
import com.mycompany.analyzer.importer.workflow.data.Workflow;
import com.mycompany.analyzer.importer.workflowinstance.data.WorkflowInstance;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.classify.BackToBackPatternClassifier;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableBatchProcessing
@EnableJpaRepositories
@ComponentScan("com.mycompany.analyzer")
public class ApplicationConfigurations {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private EntityManagerFactory emf;

    @Bean
    public ClassifierCompositeItemWriter<PipelineCandidate> compositeWriter(Classifier<PipelineCandidate, ItemWriter<? super PipelineCandidate>> classifier) {
        ClassifierCompositeItemWriter<PipelineCandidate> writer = new ClassifierCompositeItemWriter<>();
        writer.setClassifier(classifier);
        return writer;
    }

    @Bean
    @SuppressWarnings("unchecked")
    public Classifier<PipelineCandidate, ItemWriter<? super PipelineCandidate>> classifier(
            PipelineCandidateWriterDecision writerDecision,
            Map matcherMap) {
        BackToBackPatternClassifier classifier = new BackToBackPatternClassifier();
        classifier.setRouterDelegate(writerDecision);
        classifier.setMatcherMap(matcherMap);
        return classifier;
    }

    @Bean
    public Map matcherMap(EntityCollectionItemWriter<ParsingError, ParsingErrorsCollection> parsingErrorsCollectionWriter,
                          EntityCollectionItemWriter<BusinessError, BusinessErrorsCollection> businessErrorsCollectionWriter,
                          JpaItemWriter<? extends PipelineCandidate> pipelineCandidateWriter) {
        Map<String, ItemWriter<? extends PipelineCandidate>> matcherMap = new HashMap<>();

        matcherMap.put(ParsingErrorsCollection.class.getName(), parsingErrorsCollectionWriter);
        matcherMap.put(BusinessErrorsCollection.class.getName(), businessErrorsCollectionWriter);

        matcherMap.put(Workflow.class.getName(), pipelineCandidateWriter);
        matcherMap.put(WorkflowInstance.class.getName(), pipelineCandidateWriter);
        matcherMap.put(Employee.class.getName(), pipelineCandidateWriter);

        return matcherMap;
    }

    @Bean
    public JpaItemWriter pipelineCandidateWriter() {
        JpaItemWriter<? extends PipelineCandidate> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @Bean
    public JpaItemWriter entityCollectionDelegateWriter() {
        JpaItemWriter<? extends Persisted> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @Bean
    public EntityCollectionItemWriter<ParsingError, ParsingErrorsCollection> parsingErrorsCollectionWriter(
            JpaItemWriter<ParsingError> entityCollectionDelegateWriter) {
        EntityCollectionItemWriter<ParsingError, ParsingErrorsCollection> itemWriter = new EntityCollectionItemWriter<>();
        itemWriter.setDelegate(entityCollectionDelegateWriter);
        return itemWriter;
    }

    @Bean
    public EntityCollectionItemWriter<BusinessError, BusinessErrorsCollection> businessErrorsCollectionWriter(
            JpaItemWriter<BusinessError> entityCollectionDelegateWriter) {
        EntityCollectionItemWriter<BusinessError, BusinessErrorsCollection> itemWriter = new EntityCollectionItemWriter<>();
        itemWriter.setDelegate(entityCollectionDelegateWriter);
        return itemWriter;
    }

    @Bean
    public Job importWorkflowInformationJob(JobCompletionNotificationListener jobCompletionNotificationListener,
                                            Step importWorkflow,
                                            Step importWorkflowInstance,
                                            Step importEmployees,
                                            Step importContractors) {
        return jobBuilderFactory.get("importWorkflowInformationJob")
                                .incrementer(new RunIdIncrementer())
                                .listener(jobCompletionNotificationListener)
                                .flow(importEmployees)
                                .next(importContractors)
                                .next(importWorkflow)
                                .next(importWorkflowInstance)
                                .end()
                                .build();
    }
}
