package com.mycompany.analyzer.core.entity;

import com.mycompany.analyzer.core.repository.BusinessEntityRepository;

/**
 * Parent of imported business entities, to enforce natural-id
 * @see BusinessEntityRepository
 */
public interface BusinessEntity extends Persisted{

    String getCode();
}
