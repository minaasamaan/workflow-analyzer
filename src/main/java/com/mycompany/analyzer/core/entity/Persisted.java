package com.mycompany.analyzer.core.entity;

/**
 * Parent interface for entities that are persisted
 */
public interface Persisted {
    Long getId();
    void setId(Long id);
}
