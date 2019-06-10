package com.mycompany.analyzer.core.repository;

import com.mycompany.analyzer.core.entity.BusinessEntity;
import com.mycompany.analyzer.core.pipeline.AbstractItemProcessor;

import org.springframework.data.repository.CrudRepository;

/**
 * Parent repository for business entities to enable natural-id lookup as a common behavior to specific repository extensions
 * @see AbstractItemProcessor
 * @param <E> persisted business entity
 */
public interface BusinessEntityRepository<E extends BusinessEntity> extends CrudRepository<E, Long> {

    E findByCode(String code);
}
