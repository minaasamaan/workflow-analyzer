package com.mycompany.analyzer.core.utils;

import com.mycompany.analyzer.core.pipeline.AbstractItemProcessor;
import com.mycompany.analyzer.core.entity.BusinessEntity;
import com.mycompany.analyzer.importer.employee.data.Employee;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Central cache for codes->entities and email-> employees. It's useful to detect duplicate records within one import.
 * @see AbstractItemProcessor
 */
public class ProcessingCache {

    private static final Map<Class<? extends BusinessEntity>, Set<String>> entityClassToCode = new HashMap<>();

    private static final Map<String, String> emailToEmployeeCode = new HashMap<>();

    public static void purgeAll() {
        entityClassToCode.clear();
        emailToEmployeeCode.clear();
    }

    /*
    * Business entities cache management
    * */

    public static <E extends BusinessEntity> boolean contains(Class<E> entityClass, String code) {
        return entityClassToCode.containsKey(entityClass) && entityClassToCode.get(entityClass).contains(code);
    }

    public static <E extends BusinessEntity> void put(Class<? extends BusinessEntity> entityClass, E entity) {
        if (!entityClassToCode.containsKey(entityClass)) {
            entityClassToCode.put(entityClass, new HashSet<>());
        }
        entityClassToCode.get(entityClass).add(entity.getCode());
    }

    /*
    * Employees cache management
    * */

    public static boolean containsEmployee(String email) {
        return emailToEmployeeCode.containsKey(email);
    }

    public static void putEmployee(Employee employee) {
        emailToEmployeeCode.put(employee.getEmail(), employee.getCode());
    }
}
