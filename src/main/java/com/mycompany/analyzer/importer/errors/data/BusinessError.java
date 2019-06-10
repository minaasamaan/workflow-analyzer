package com.mycompany.analyzer.importer.errors.data;

import com.mycompany.analyzer.core.entity.Persisted;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Entity(name = "business_errors")
public class BusinessError implements Persisted {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String entity;

    @Column(name = "record_start_line")
    private Long recordStartLine;

    public BusinessError(String entity, Long recordStartLine, Long recordEndLine, String constraintViolation) {
        this.entity = entity;
        this.recordStartLine = recordStartLine;
        this.recordEndLine = recordEndLine;
        this.constraintViolation = constraintViolation;
    }

    @Column(name = "record_end_line")
    private Long recordEndLine;

    @Column(name = "constraint_violation")
    private String constraintViolation;

    public BusinessError(){}

    public Long getId() {
        return id;
    }

    public String getEntity() {
        return entity;
    }

    public String getConstraintViolation() {
        return constraintViolation;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setConstraintViolation(String constraintViolation) {
        this.constraintViolation = constraintViolation;
    }

    @Override
    public String toString() {
        return "BusinessError{" +
                "entity='" + entity + '\'' +
                ", recordStartLine=" + recordStartLine +
                ", recordEndLine=" + recordEndLine +
                ", constraintViolation='" + constraintViolation + '\'' +
                '}';
    }
}
