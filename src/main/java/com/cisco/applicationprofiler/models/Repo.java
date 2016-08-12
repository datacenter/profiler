package com.cisco.applicationprofiler.models;

import java.util.Date;

public class Repo {

	private int id;
	private String sourceDevice;
	private String objectType;
	private String internalObjectName;
	private String userObjectName;
	private String objectVersion;
	private Date importedOn;
	private Date auditedOn;
	private String auditedStatus;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSourceDevice() {
		return sourceDevice;
	}

	public void setSourceDevice(String sourceDevice) {
		this.sourceDevice = sourceDevice;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getInternalObjectName() {
		return internalObjectName;
	}

	public void setInternalObjectName(String internalObjectName) {
		this.internalObjectName = internalObjectName;
	}

	public String getUserObjectName() {
		return userObjectName;
	}

	public void setUserObjectName(String userObjectName) {
		this.userObjectName = userObjectName;
	}

	public String getObjectVersion() {
		return objectVersion;
	}

	public void setObjectVersion(String objectVersion) {
		this.objectVersion = objectVersion;
	}

	public Date getImportedOn() {
		return importedOn;
	}

	public void setImportedOn(Date importedOn) {
		this.importedOn = importedOn;
	}

	public Date getAuditedOn() {
		return auditedOn;
	}

	public void setAuditedOn(Date auditedOn) {
		this.auditedOn = auditedOn;
	}

	public String getAuditedStatus() {
		return auditedStatus;
	}

	public void setAuditedStatus(String auditedStatus) {
		this.auditedStatus = auditedStatus;
	}

}
