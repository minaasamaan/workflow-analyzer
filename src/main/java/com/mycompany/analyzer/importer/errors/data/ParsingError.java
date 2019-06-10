package com.mycompany.analyzer.importer.errors.data;

import com.mycompany.analyzer.core.entity.Persisted;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import static javax.persistence.GenerationType.IDENTITY;

@Entity(name = "parsing_errors")
public class ParsingError implements Persisted {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    private String resource;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ParsingErrorType error;

    @NotNull
    @Column(name = "line_number")
    private Long             lineNumber;

    @NotNull
    private String           line;

    public ParsingError(){}

    public ParsingError(String resource, ParsingErrorType error, long lineNumber, String line) {
        this.resource=resource;
        this.error = error;
        this.lineNumber = lineNumber;
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLineNumber() {
        return lineNumber;
    }

    public String getLine() {
        return line;
    }

    public ParsingErrorType getError() {
        return error;
    }

    public String getResource() {
        return resource;
    }

    @Override
    public String toString() {
        return "ParsingError{" +
                " Resource='" + resource + '\'' +
                ", error=" + error +
                ", lineNumber=" + lineNumber +
                ", line='" + line + '\'' +
                '}';
    }
}
