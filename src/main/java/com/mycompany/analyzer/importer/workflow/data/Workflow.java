/**
 *
 */
package com.mycompany.analyzer.importer.workflow.data;

import com.mycompany.analyzer.core.entity.BusinessEntity;
import com.mycompany.analyzer.core.entity.PipelineCandidate;
import com.mycompany.analyzer.importer.employee.data.Employee;
import com.mycompany.analyzer.importer.workflowinstance.data.WorkflowInstance;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author Mina
 *
 */
@Entity(name = "workflows")
public class Workflow implements PipelineCandidate, BusinessEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String code;

    @NotNull
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Employee author;

    @NotNull
    private Long version;

    @OneToMany(mappedBy = "workflow")
    private List<WorkflowInstance> workflowInstances = new ArrayList<>();

    public Workflow(String code, String name, Employee author, Long version) {
        this.code = code;
        this.name = name;
        this.author = author;
        this.version = version;
    }

    public Workflow(String code) {
        setCode(code);
    }

    public Workflow() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Employee getAuthor() {
        return author;
    }

    public void setAuthor(Employee author) {
        this.author = author;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<WorkflowInstance> getWorkflowInstances() {
        return workflowInstances;
    }

    public void setWorkflowInstances(List<WorkflowInstance> workflowInstances) {
        this.workflowInstances = workflowInstances;
    }

    @Override
    public String toString() {
        return "Workflow{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", author='" + author.getEmail() + '\'' +
                ", version=" + version +
                '}';
    }
}
