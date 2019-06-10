package com.mycompany.analyzer.importer.errors.repository;

import com.mycompany.analyzer.importer.errors.data.ParsingError;

import org.springframework.data.repository.CrudRepository;

public interface ParsingErrorRepository extends CrudRepository<ParsingError, Long> {
}
