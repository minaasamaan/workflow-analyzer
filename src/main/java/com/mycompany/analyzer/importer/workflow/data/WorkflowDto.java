package com.mycompany.analyzer.importer.workflow.data;

import com.mycompany.analyzer.core.dto.ImportedItemDto;

public class WorkflowDto extends ImportedItemDto<Workflow> {

    private Workflow workflow;

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    @Override
    public void setBusinessEntity(Workflow workflow) {
        setWorkflow(workflow);
    }

    @Override
    public Workflow getBusinessEntity() {
        return getWorkflow();
    }
}
