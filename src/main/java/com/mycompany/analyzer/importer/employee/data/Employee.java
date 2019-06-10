/**
 * 
 */
package com.mycompany.analyzer.importer.employee.data;

import com.mycompany.analyzer.core.entity.BusinessEntity;
import com.mycompany.analyzer.core.entity.PipelineCandidate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author Mina
 *
 */
@Entity(name = "employees")
public class Employee implements PipelineCandidate, BusinessEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String code;

    @NotNull
    @Column(name="full_name")
	private String fullName;

    @NotNull
    @Email
    @Column(unique = true)
    private String email;

	private boolean contractor =false;

    public Employee() {
    }

    public Employee(@NotNull String code,
                    @NotNull String fullName,
                    @NotNull @Email String email, boolean contractor) {
        this.code = code;
        this.fullName = fullName;
        this.email = email;
        this.contractor = contractor;
    }

    public Employee(String email) {
        setEmail(email);
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isContractor() {
		return contractor;
	}

	public Employee setContractor(boolean contractor) {
		this.contractor = contractor;
		return this;
	}

	@Override
	public String toString() {
		return "Employee{" +
				"code='" + code + '\'' +
				", fullName='" + fullName + '\'' +
				", email='" + email + '\'' +
				", contractor=" + contractor +
				'}';
	}
}
