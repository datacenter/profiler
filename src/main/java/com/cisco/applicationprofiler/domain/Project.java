package com.cisco.applicationprofiler.domain;

import java.sql.Timestamp;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.models.View;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "project")
@XmlRootElement
public class Project extends ProfilerDomain {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@JsonView(View.Project.class)
	private int id;

	@Column(name = "name")
	@JsonView(View.Project.class)
	private String name;

	@Column(name = "description")
	@JsonView(View.Project.class)
	private String description;

	@ManyToOne
	@JoinColumn(name = "userId")
	@JsonView(View.Project.class)
	private User user;

	@Column(name = "created_time")
	@JsonView(View.Project.class)
	private Timestamp createdTime;

	@Column(name = "last_updated_time")
	@JsonView(View.Project.class)
	private Timestamp lastUpdatedTime;

	@Column(name = "logical_requirement_summary")
	@JsonView(View.Project.class)
	private String logicalSummary;

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public Timestamp getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(Timestamp lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	public LogicalSummary getLogicalSummary() {
		LogicalSummary temp = gson.fromJson(logicalSummary, LogicalSummary.class);
		return temp;
	}

	public void setLogicalSummary(LogicalSummary logicalSummary) {
		this.logicalSummary = gson.toJson(logicalSummary);
	}

}
