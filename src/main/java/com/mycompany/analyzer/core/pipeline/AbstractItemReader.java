package com.mycompany.analyzer.core.pipeline;

import com.google.common.annotations.VisibleForTesting;

import com.mycompany.analyzer.core.dto.ImportedItemDto;
import com.mycompany.analyzer.core.entity.BusinessEntity;
import com.mycompany.analyzer.core.utils.BatchProcessingUtils;
import com.mycompany.analyzer.core.utils.LineAwareFieldSet;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;

import static com.mycompany.analyzer.importer.errors.data.ParsingErrorType.INVALID_FIELD_TYPE;
import static com.mycompany.analyzer.importer.errors.data.ParsingErrorType.INVALID_RECORD_END;
import static com.mycompany.analyzer.importer.errors.data.ParsingErrorType.INVALID_RECORD_START;
import static com.mycompany.analyzer.importer.errors.data.ParsingErrorType.INVALID_TOKEN;
import static com.mycompany.analyzer.importer.errors.data.ParsingErrorType.MISSING_RECORD_END;
import static com.mycompany.analyzer.importer.errors.data.ParsingErrorType.MISSING_RECORD_START;

/**
 * Parent of all readers, which is responsible for reading flat files, construct & inject Dto and business entity based on predefined matching configurations.
 * @see BatchProcessingUtils
 * @param <Dto>
 * @param <E>
 */
public abstract class AbstractItemReader<Dto extends ImportedItemDto<E>, E extends BusinessEntity> extends
                                                                                                   AbstractItemStreamItemReader<Dto> {

    private FlatFileItemReader<LineAwareFieldSet> delegate;
    private String                                resource;
    private boolean                               startRecord;

    protected abstract boolean doRead(String attribute,
                                      String value,
                                      Dto dto,
                                      E entity,
                                      long lineNumber,
                                      String line);

    protected abstract E newEntityInstance();

    protected abstract Dto newDtoInstance();

    protected void setDelegate(FlatFileItemReader<LineAwareFieldSet> delegate) {
        this.delegate = delegate;
    }

    protected void setResource(String resource) {
        this.resource = resource;
    }

    protected Long readLong(String value,
                            Dto dto,
                            long lineNumber,
                            String line) {
        try {
            return Long.parseLong(value);
        }
        catch (NumberFormatException e) {
            dto.addParsingError(resource, INVALID_FIELD_TYPE, lineNumber, line);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dto read() throws Exception {

        final Dto dto = newDtoInstance();
        E entity = startRecord ? newEntityInstance() : null;

        startRecord=false;

        long lineNumber = 0;
        String line = null;

        for (LineAwareFieldSet fieldSet; (fieldSet = this.delegate.read()) != null; ) {

            lineNumber = fieldSet.getLineNumber();
            line = fieldSet.getLine();

            //check record boundary
            String prefix = "";
            if (fieldSet.getFieldCount() > 0) {
                prefix = fieldSet.readString(0);
            }

            switch (prefix) {
                //check record start
                case "start":
                    if (entity != null) {
                        //Invalid 'start'
                        dto.addParsingError(resource, INVALID_RECORD_START, lineNumber, line);
                    }
                    else {
                        //check if parsing issues existed before the record start
                        if (!dto.getParsingErrorsCollection().isEmpty()) {
                            startRecord = true;
                            return dto;
                        }
                        entity = newEntityInstance(); // start new record
                        dto.setStartsAtLine(lineNumber);
                    }
                    break;

                //check record end
                case "end":
                    if (entity == null) {
                        //Invalid 'end'
                        dto.addParsingError(resource, INVALID_RECORD_END, lineNumber, line);
                        break;
                    }
                    else {
                        dto.setBusinessEntity(entity);
                        dto.setEndsAtLine(lineNumber);
                        return dto; // Record must end with footer
                    }
                    //check other fields
                default:

                    if (entity == null) {
                        //missing record start
                        dto.addParsingError(resource, MISSING_RECORD_START, lineNumber, line);
                    }
                    else if (fieldSet.getFieldCount() < 2 || !doRead(fieldSet.readString(0).trim(),
                                                                     fieldSet.readString(1).trim(),
                                                                     dto,
                                                                     entity,
                                                                     lineNumber,
                                                                     line)) {
                        //unknown/invalid token
                        dto.addParsingError(resource, INVALID_TOKEN, lineNumber, line);
                    }
                    break;
            }//end switch
        }//end loop

        if (entity != null) {
            //start exists without end
            dto.addParsingError(resource, MISSING_RECORD_END, lineNumber, line);
            return dto;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        delegate.open(executionContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }

    @VisibleForTesting
    public FlatFileItemReader<LineAwareFieldSet> getDelegate() {
        return delegate;
    }
}
