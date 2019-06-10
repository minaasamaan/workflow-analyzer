package com.mycompany.analyzer.importer.errors.repository;

import com.mycompany.analyzer.importer.errors.data.BusinessError;

import org.springframework.data.repository.CrudRepository;

public interface BusinessErrorRepository extends CrudRepository<BusinessError, Long> {
}
