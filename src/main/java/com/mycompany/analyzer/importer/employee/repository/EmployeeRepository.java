package com.mycompany.analyzer.importer.employee.repository;

import com.mycompany.analyzer.core.repository.BusinessEntityRepository;
import com.mycompany.analyzer.importer.employee.data.Employee;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends BusinessEntityRepository<Employee> {

    Employee findByEmail(String email);

    @Query("select employee from com.mycompany.analyzer.importer.workflowinstance.data.WorkflowInstance instance inner join instance.assignee employee where instance.status='RUNNING' and employee.contractor=true")
    List<Employee> findAllContractorsAssignedToRunningInstance();
}
