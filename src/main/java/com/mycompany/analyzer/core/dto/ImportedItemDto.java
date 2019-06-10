package com.mycompany.analyzer.core.dto;

import com.mycompany.analyzer.core.entity.BusinessEntity;
import com.mycompany.analyzer.importer.errors.data.ParsingError;
import com.mycompany.analyzer.importer.errors.data.ParsingErrorType;
import com.mycompany.analyzer.importer.errors.data.ParsingErrorsCollection;

/**
 * Parent of all imported entities, typically filled with read operations data.
 * @param <E> Associated business entity
 */
public abstract class ImportedItemDto<E extends BusinessEntity> {

    private Long startsAtLine;

    private Long endsAtLine;

    private ParsingErrorsCollection parsingErrorsCollection = new ParsingErrorsCollection();

    public abstract void setBusinessEntity(E businessEntity);

    public abstract E getBusinessEntity();

    public ParsingErrorsCollection getParsingErrorsCollection() {
        return parsingErrorsCollection;
    }

    public void addParsingError(String resource, final ParsingErrorType error,
                                long lineNumber,
                                String line) {
        parsingErrorsCollection.getParsingErrors().add(new ParsingError(resource, error, lineNumber, line));
    }

    public Long getStartsAtLine() {
        return startsAtLine;
    }

    public void setStartsAtLine(Long startsAtLine) {
        this.startsAtLine = startsAtLine;
    }

    public Long getEndsAtLine() {
        return endsAtLine;
    }

    public void setEndsAtLine(Long endsAtLine) {
        this.endsAtLine = endsAtLine;
    }
}
