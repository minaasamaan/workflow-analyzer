package com.mycompany.analyzer.importer.errors.data;

import com.mycompany.analyzer.core.entity.EntityCollection;
import com.mycompany.analyzer.core.entity.PipelineCandidate;

import java.util.ArrayList;
import java.util.List;

public class ParsingErrorsCollection implements PipelineCandidate, EntityCollection<ParsingError> {

    private List<ParsingError> parsingErrors = new ArrayList<>();

    public List<ParsingError> getParsingErrors() {
        return parsingErrors;
    }

    public boolean isEmpty(){
        return this.parsingErrors.isEmpty();
    }

    @Override
    public List<ParsingError> getEntities() {
        return getParsingErrors();
    }
}
