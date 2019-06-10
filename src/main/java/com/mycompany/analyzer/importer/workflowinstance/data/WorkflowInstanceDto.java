package com.mycompany.analyzer.importer.workflowinstance.data;

import com.mycompany.analyzer.core.dto.ImportedItemDto;

public class WorkflowInstanceDto extends ImportedItemDto<WorkflowInstance> {

    private WorkflowInstance workflowInstance;


    public WorkflowInstance getWorkflowInstance() {
        return workflowInstance;
    }

    public void setWorkflowInstance(WorkflowInstance workflowInstance) {
        this.workflowInstance = workflowInstance;
    }

    @Override
    public void setBusinessEntity(WorkflowInstance workflowInstance) {
        setWorkflowInstance(workflowInstance);
    }

    @Override
    public WorkflowInstance getBusinessEntity() {
        return getWorkflowInstance();
    }
}
