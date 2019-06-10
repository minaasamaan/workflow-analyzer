package com.mycompany.analyzer.importer.workflowinstance.data;

public enum WorkflowInstanceStatus {
    NEW,
    RUNNING,
    DONE,
    PAUSED;

    public static boolean contains(String test) {
        for (WorkflowInstanceStatus wis : WorkflowInstanceStatus.values()) {
            if (wis.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
