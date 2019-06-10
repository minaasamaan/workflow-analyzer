package com.mycompany.analyzer.importer.employee.pipeline;

import com.mycompany.analyzer.core.pipeline.AbstractItemReader;
import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.employee.data.EmployeeDto;

public class EmployeeReader extends AbstractItemReader<EmployeeDto, Employee> {

    private boolean contractors=false;

    @Override
    protected boolean doRead(String attribute,
                             String value,
                             EmployeeDto dto,
                             Employee employee,
                             long lineNumber,
                             String line) {
        switch (attribute) {
            case "employeeId":
            case "contractorName":
                employee.setCode(value);
                return true;
            case "fullName":
                employee.setFullName(value);
                return true;
            case "email":
                employee.setEmail(value);
                return true;
        }
        return false;
    }

    @Override
    protected Employee newEntityInstance() {
        return new Employee().setContractor(contractors);
    }

    @Override
    protected EmployeeDto newDtoInstance() {
        return new EmployeeDto();
    }

    protected void setContractors(boolean contractors) {
        this.contractors = contractors;
    }
}
