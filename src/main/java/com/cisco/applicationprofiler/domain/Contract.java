package com.cisco.applicationprofiler.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.models.View;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "contract")
@XmlRootElement
public class Contract {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@JsonView(View.Contract.class)
	private int id;

	@Column(name = "name")
	@JsonView(View.Contract.class)
	private String name;

	@Column(name = "consumerId")
	@JsonView(View.Contract.class)
	private int consumerId;

	@Column(name = "producerId")
	@JsonView(View.Contract.class)
	private int producerId;
	
	@OneToMany(mappedBy = "contract")
	@JsonView(View.Subject.class)
	private List<Subject> subjects;
	
	@ManyToOne
	@JoinColumn(name = "auditId")
	@JsonView(View.Contract.class)
	private AuditInfo auditInfo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(int consumerId) {
		this.consumerId = consumerId;
	}

	public int getProducerId() {
		return producerId;
	}

	public void setProducerId(int producerId) {
		this.producerId = producerId;
	}

	public List<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}

	public AuditInfo getAuditInfo() {
		return auditInfo;
	}

	public void setAuditInfo(AuditInfo auditInfo) {
		this.auditInfo = auditInfo;
	}

	
}
