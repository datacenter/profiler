package com.cisco.applicationprofiler.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.models.View;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "device")
@XmlRootElement

public class Device {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@JsonView(View.Device.class)
	private int id;

	@Column(name = "name")
	@JsonView(View.Device.class)
	private String name;
	
	@Column(name = "type")
	@JsonView(View.Device.class)
	private String type;

	@Column(name = "ip_address")
	@JsonView(View.Device.class)
	private String ipAddress;

	@Column(name = "username")
	@JsonView(View.Device.class)
	private String username;

	@Column(name = "password")
	@JsonView(View.Device.class)
	private String password;
	
	@Column(name = "importedOn")
	@JsonView(View.Device.class)
	private Timestamp importedOn;
	
	@Column (name = "importedStatus")
	@JsonView(View.Device.class)
	private int importedStatus;
	
	@ManyToOne
	@JoinColumn(name = "model_id")
	@JsonView(View.Model.class)
	private Model modelDetails;

	/**
	 * @return the modelDetails
	 */
	public Model getModelDetails() {
		return modelDetails;
	}

	/**
	 * @param modelDetails the modelDetails to set
	 */
	public void setModelDetails(Model modelDetails) {
		this.modelDetails = modelDetails;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timestamp getImportedOn() {
		return importedOn;
	}

	public void setImportedOn(Timestamp importedOn) {
		this.importedOn = importedOn;
	}

	public int getImportedStatus() {
		return importedStatus;
	}

	public void setImportedStatus(int importedStatus) {
		this.importedStatus = importedStatus;
	}

	
	

}
