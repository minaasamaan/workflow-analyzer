package com.mycompany.analyzer.importer.workflow.data;

public class WorkflowStatisticsDto {
    private Workflow workflow;
    private Long     runningInstancesCount;

    public Workflow getWorkflow() {
        return workflow;
    }

    public Long getRunningInstancesCount() {
        return runningInstancesCount;
    }

    public WorkflowStatisticsDto(Workflow workflow, Long runningInstancesCount) {
        this.workflow = workflow;
        this.runningInstancesCount = runningInstancesCount;
    }

    @Override
    public String toString() {
        return "WorkflowStatisticsDto{" +
                "workflow=" + workflow +
                ", runningInstancesCount=" + runningInstancesCount +
                '}';
    }
}
