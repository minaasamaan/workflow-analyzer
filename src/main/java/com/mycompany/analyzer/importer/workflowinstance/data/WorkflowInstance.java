package com.mycompany.analyzer.importer.workflowinstance.data;

import com.mycompany.analyzer.core.entity.BusinessEntity;
import com.mycompany.analyzer.core.entity.PipelineCandidate;
import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.workflow.data.Workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import static javax.persistence.GenerationType.IDENTITY;

@Entity(name = "workflow_instances")
public class WorkflowInstance implements PipelineCandidate, BusinessEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String code;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private Employee assignee;

    @NotNull
    private String step;

    @NotNull
    @Enumerated(EnumType.STRING)
    private WorkflowInstanceStatus status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    public WorkflowInstance() {
    }

    public WorkflowInstance(@NotNull String code,
                            @NotNull Employee assignee,
                            @NotNull String step,
                            @NotNull WorkflowInstanceStatus status,
                            @NotNull Workflow workflow) {
        this.code = code;
        this.assignee = assignee;
        this.step = step;
        this.status = status;
        this.workflow = workflow;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setAssignee(Employee assignee) {
        this.assignee = assignee;
    }

    public Employee getAssignee() {
        return assignee;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public void setStatus(WorkflowInstanceStatus status) {
        this.status = status;
    }

    public WorkflowInstanceStatus getStatus() {
        return status;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    @Override
    public String toString() {
        return "WorkflowInstance{" +
                "code=" + code +
                ", assignee='" + assignee.getEmail() + '\'' +
                ", step='" + step + '\'' +
                ", status=" + status +
                '}';
    }
}
