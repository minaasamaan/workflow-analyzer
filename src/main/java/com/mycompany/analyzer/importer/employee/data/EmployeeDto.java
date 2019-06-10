package com.mycompany.analyzer.importer.employee.data;

import com.mycompany.analyzer.core.dto.ImportedItemDto;

public class EmployeeDto extends ImportedItemDto<Employee> {

    private Employee employee;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public void setBusinessEntity(Employee employee) {
        setEmployee(employee);
    }

    @Override
    public Employee getBusinessEntity() {
        return getEmployee();
    }
}
