package com.mycompany.analyzer.importer.workflowinstance.pipeline;

import com.mycompany.analyzer.core.pipeline.AbstractItemReader;
import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.workflow.data.Workflow;
import com.mycompany.analyzer.importer.workflowinstance.data.WorkflowInstance;
import com.mycompany.analyzer.importer.workflowinstance.data.WorkflowInstanceDto;
import com.mycompany.analyzer.importer.workflowinstance.data.WorkflowInstanceStatus;

public class WorkflowInstanceReader extends AbstractItemReader<WorkflowInstanceDto, WorkflowInstance> {

    @Override
    protected boolean doRead(String attribute,
                             String value,
                             WorkflowInstanceDto dto,
                             WorkflowInstance workflowInstance,
                             long lineNumber,
                             String line) {
        switch (attribute) {
            case "id":
                workflowInstance.setCode(value);
                return true;
            case "workflowId":
                workflowInstance.setWorkflow(new Workflow(value));
                return true;
            case "assignee":
                workflowInstance.setAssignee(new Employee(value));
                return true;
            case "step":
                workflowInstance.setStep(value);
                return true;
            case "status":
                if (WorkflowInstanceStatus.contains(value)) {
                    workflowInstance.setStatus(WorkflowInstanceStatus.valueOf(value));
                    return true;
                }
                return false;
        }
        return false;
    }

    @Override
    protected WorkflowInstance newEntityInstance() {
        return new WorkflowInstance();
    }

    @Override
    protected WorkflowInstanceDto newDtoInstance() {
        return new WorkflowInstanceDto();
    }
}
