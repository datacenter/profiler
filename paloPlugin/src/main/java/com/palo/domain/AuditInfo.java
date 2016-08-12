package com.palo.domain;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "audit_info")
public class AuditInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "source_device")
	private Device sourceDevice;
	
	@Column(name = "importedOn")
	private Timestamp importedOn;
	
	@Column(name="auditedOn")
	private Timestamp auditedOn;
	
	@Column(name="auditStatus")
	private String auditedStatus;
	
	
	@OneToMany(mappedBy = "auditInfo", cascade = { CascadeType.ALL })
	private List<Filter> filters;
	
	@OneToMany(mappedBy = "auditInfo", cascade = { CascadeType.ALL })
	private List<Contract> contracts;
	

	public Device getSourceDevice() {
		return sourceDevice;
	}

	public void setSourceDevice(Device sourceDevice) {
		this.sourceDevice = sourceDevice;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getImportedOn() {
		return importedOn;
	}

	public void setImportedOn(Timestamp importedOn) {
		this.importedOn = importedOn;
	}

	public Timestamp getAuditedOn() {
		return auditedOn;
	}

	public void setAuditedOn(Timestamp auditedOn) {
		this.auditedOn = auditedOn;
	}

	public String getAuditedStatus() {
		return auditedStatus;
	}

	public void setAuditedStatus(String auditedStatus) {
		this.auditedStatus = auditedStatus;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public List<Contract> getContracts() {
		return contracts;
	}

	public void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}
	


}
