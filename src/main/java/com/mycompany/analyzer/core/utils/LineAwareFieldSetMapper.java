package com.mycompany.analyzer.core.utils;

import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;

public class LineAwareFieldSetMapper extends DefaultLineMapper<LineAwareFieldSet> implements LineMapper<LineAwareFieldSet> {
    @Override
    public LineAwareFieldSet mapLine(String line, int lineNumber) throws Exception {
        return super.mapLine(line, lineNumber).setLineNumber(lineNumber).setLine(line);
    }
}
