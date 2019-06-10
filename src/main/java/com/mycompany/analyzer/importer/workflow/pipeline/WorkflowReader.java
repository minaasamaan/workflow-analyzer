package com.mycompany.analyzer.importer.workflow.pipeline;

import com.mycompany.analyzer.core.pipeline.AbstractItemReader;
import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.workflow.data.Workflow;
import com.mycompany.analyzer.importer.workflow.data.WorkflowDto;

public class WorkflowReader extends AbstractItemReader<WorkflowDto, Workflow> {

    @Override
    protected boolean doRead(String attribute,
                             String value,
                             WorkflowDto dto,
                             Workflow workflow,
                             long lineNumber,
                             String line) {
        switch (attribute) {
            case "id":
                workflow.setCode(value);
                return true;
            case "name":
                workflow.setName(value);
                return true;
            case "author":
                workflow.setAuthor(new Employee(value));
                return true;
            case "version":
                workflow.setVersion(readLong(value, dto, lineNumber, line));
                return true;
        }
        return false;
    }

    @Override
    protected Workflow newEntityInstance() {
        return new Workflow();
    }

    @Override
    protected WorkflowDto newDtoInstance() {
        return new WorkflowDto();
    }
}
