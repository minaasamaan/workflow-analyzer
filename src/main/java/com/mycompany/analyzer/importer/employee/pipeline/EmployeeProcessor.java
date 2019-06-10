package com.mycompany.analyzer.importer.employee.pipeline;

import com.mycompany.analyzer.core.pipeline.AbstractItemProcessor;
import com.mycompany.analyzer.core.repository.BusinessEntityRepository;
import com.mycompany.analyzer.core.utils.ProcessingCache;
import com.mycompany.analyzer.importer.errors.data.BusinessErrorsCollection;
import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.employee.data.EmployeeDto;
import com.mycompany.analyzer.importer.employee.repository.EmployeeRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class EmployeeProcessor extends AbstractItemProcessor<EmployeeDto, Employee> {

    private static final Logger log = LoggerFactory.getLogger(EmployeeProcessor.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    protected void doProcess(Employee employee, BusinessErrorsCollection businessErrorsCollection) {
        log.info("Converting (" + employee + ")");
        //check if email is duplicate with another record
        if (ProcessingCache.containsEmployee(employee.getEmail())) {
            reportBusinessError(employee.getClass(),
                                String.format("Employee already exists in the same import with email: %s",
                                              employee.getEmail()),
                                businessErrorsCollection);
        }else{
            //update cache
            ProcessingCache.putEmployee(employee);
        }
    }

    @Override
    protected BusinessEntityRepository<Employee> getRepository() {
        return employeeRepository;
    }
}
