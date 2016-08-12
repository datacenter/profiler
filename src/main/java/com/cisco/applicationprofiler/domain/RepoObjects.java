package com.cisco.applicationprofiler.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "repo_objects")
@XmlRootElement
public class RepoObjects {
	@Column(name = "id")
	private int id;
	
	@Column (name = "deviceId")
	private int deviceId;
	
	@Column(name = "source_device")
	private String sourceDevice;
	
	@Column(name = "type")
	private String ObjectType;
	
	@Id
	@Column(name = "name")
	private String objectName;
	
	@Column(name = "importedon")
	private String importedOn;
	
	@Column(name = "auditedon")
	private String auditedon;
	
	@Column(name = "auditstatus")
	private String auditstatus;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getSourceDevice() {
		return sourceDevice;
	}

	public void setSourceDevice(String sourceDevice) {
		this.sourceDevice = sourceDevice;
	}

	public String getObjectType() {
		return ObjectType;
	}

	public void setObjectType(String objectType) {
		ObjectType = objectType;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getImportedOn() {
		return importedOn;
	}

	public void setImportedOn(String importedOn) {
		this.importedOn = importedOn;
	}

	public String getAuditedon() {
		return auditedon;
	}

	public void setAuditedon(String auditedon) {
		this.auditedon = auditedon;
	}

	public String getAuditstatus() {
		return auditstatus;
	}

	public void setAuditstatus(String auditstatus) {
		this.auditstatus = auditstatus;
	}

	
	
	
}