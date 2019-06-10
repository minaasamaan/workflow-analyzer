package com.mycompany.analyzer.core.entity;

import com.mycompany.analyzer.core.pipeline.EntityCollectionItemWriter;

import java.util.List;

/**
 * Represents a collection of entities that should be persisted/written altogether.
 * @see EntityCollectionItemWriter
 * @param <E> Entity to be persisted
 */
public interface EntityCollection<E extends Persisted> {

    List<E> getEntities();
}
