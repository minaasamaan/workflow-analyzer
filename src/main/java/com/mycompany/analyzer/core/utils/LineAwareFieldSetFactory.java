package com.mycompany.analyzer.core.utils;

import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FieldSetFactory;

public class LineAwareFieldSetFactory implements FieldSetFactory {
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldSet create(String[] values, String[] names) {
        return new LineAwareFieldSet(values, names);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldSet create(String[] values) {
        return new LineAwareFieldSet(values);
    }
}
