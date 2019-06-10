package com.mycompany.analyzer.core.decision;

import com.mycompany.analyzer.core.entity.PipelineCandidate;

import org.springframework.batch.support.annotation.Classifier;
import org.springframework.stereotype.Service;

/**
 * Class responsible for delegating write operations to the correct writer based on class name.
 */
@Service
public class PipelineCandidateWriterDecision {
        @Classifier
        public String classify(PipelineCandidate pipelineCandidate) {
            return pipelineCandidate.getClass().getName();
        }
}
