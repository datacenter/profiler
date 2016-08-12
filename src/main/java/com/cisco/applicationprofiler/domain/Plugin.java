package com.cisco.applicationprofiler.domain;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.models.View;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "plugin")
@XmlRootElement
public class Plugin {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@JsonView(View.Plugin.class)
	private int id;

	@Column(name = "name")
	@JsonView(View.Plugin.class)
	private String name;

	@Column(name = "version")
	@JsonView(View.Plugin.class)
	private String version;
	
	@Column(name = "description")
	@JsonView(View.Plugin.class)
	private String description;
	
	
	@Column(name="created_time")
	@JsonView(View.Plugin.class)
	private Timestamp created_time;
	
	@Column(name="last_updated_time")
	@JsonView(View.Plugin.class)
	private Timestamp last_updated_time;
	
	@Column(name = "path")
	@JsonView(View.Plugin.class)
	private String path;
	
	@OneToMany(mappedBy = "plugin",fetch = FetchType.LAZY)
	@JsonView(View.Model.class)
	private List<Model> model;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Timestamp created_time) {
		this.created_time = created_time;
	}

	public Timestamp getLast_updated_time() {
		return last_updated_time;
	}

	public void setLast_updated_time(Timestamp last_updated_time) {
		this.last_updated_time = last_updated_time;
	}
	
	

}
