package com.mycompany.analyzer.core.utils;

import org.springframework.batch.item.file.transform.DefaultFieldSet;

public class LineAwareFieldSet extends DefaultFieldSet {

    private long lineNumber;

    private String line;

    public LineAwareFieldSet(String[] tokens) {
        super(tokens);
    }

    public LineAwareFieldSet(String[] tokens, String[] names) {
        super(tokens, names);
    }

    public long getLineNumber() {
        return lineNumber;
    }

    LineAwareFieldSet setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    public String getLine() {
        return line;
    }

    public LineAwareFieldSet setLine(String line) {
        this.line = line;
        return this;
    }
}
