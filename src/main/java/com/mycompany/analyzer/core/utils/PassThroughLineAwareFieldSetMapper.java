package com.mycompany.analyzer.core.utils;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class PassThroughLineAwareFieldSetMapper implements FieldSetMapper<LineAwareFieldSet> {

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.batch.item.file.FieldSetMapper#mapLine(org.springframework
	 * .batch.io.file.FieldSet)
	 */
    @Override
    public LineAwareFieldSet mapFieldSet(FieldSet fieldSet) throws BindException {
        return fieldSet instanceof LineAwareFieldSet? (LineAwareFieldSet) fieldSet :null;
    }
}
