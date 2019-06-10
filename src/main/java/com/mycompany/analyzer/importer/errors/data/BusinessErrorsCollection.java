package com.mycompany.analyzer.importer.errors.data;

import com.mycompany.analyzer.core.entity.EntityCollection;
import com.mycompany.analyzer.core.entity.PipelineCandidate;

import java.util.ArrayList;
import java.util.List;

public class BusinessErrorsCollection implements PipelineCandidate, EntityCollection<BusinessError> {

    private Long startsAtLine;
    private Long endsAtLine;

    public BusinessErrorsCollection(Long startsAtLine, Long endsAtLine) {
        this.startsAtLine = startsAtLine;
        this.endsAtLine = endsAtLine;
    }

    private List<BusinessError> businessErrors = new ArrayList<>();


    public List<BusinessError> getBusinessErrors() {
        return businessErrors;
    }

    public boolean isEmpty() {
        return this.businessErrors.isEmpty();
    }

    @Override
    public List<BusinessError> getEntities() {
        return getBusinessErrors();
    }

    public Long getStartsAtLine() {
        return startsAtLine;
    }

    public Long getEndsAtLine() {
        return endsAtLine;
    }
}
