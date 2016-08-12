package com.cisco.applicationprofiler.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.models.View;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "filter")
@XmlRootElement
public class Filter {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@JsonView(View.Filter.class)
	private int id;

	@Column(name = "name")
	@JsonView(View.Filter.class)
	private String name;
	
	@Column (name= "description")
	@JsonView(View.Filter.class)
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "subjectId")
	@JsonBackReference
	private Subject subject;
	
	@OneToMany(mappedBy = "filter",fetch = FetchType.LAZY)
	@JsonView(View.FilterEntry.class)
	private List<FilterEntry> filterEntry;
	
	@ManyToOne
	@JoinColumn(name = "auditId")
	@JsonBackReference
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public List<FilterEntry> getFilterEntry() {
		return filterEntry;
	}

	public void setFilterEntry(List<FilterEntry> filterEntry) {
		this.filterEntry = filterEntry;
	}

	public AuditInfo getAuditInfo() {
		return auditInfo;
	}

	public void setAuditInfo(AuditInfo auditInfo) {
		this.auditInfo = auditInfo;
	}
}
