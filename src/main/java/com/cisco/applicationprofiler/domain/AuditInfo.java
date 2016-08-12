package com.cisco.applicationprofiler.domain;
import java.sql.Timestamp;
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
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "audit_info")
@XmlRootElement
public class AuditInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@JsonView(View.AuditInfo.class)
	private int id;
	
	
	@ManyToOne
	@JoinColumn(name = "source_device")
	@JsonView(View.AuditInfo.class)
	private Device sourceDevice;

	@Column(name = "importedOn")
	@JsonView(View.AuditInfo.class)
	private Timestamp importedOn;
	
	@Column(name="auditedOn")
	@JsonView(View.AuditInfo.class)
	private Timestamp auditedOn;
	
	@Column(name="auditStatus")
	@JsonView(View.AuditInfo.class)
	private String auditStatus;
	
	
	@OneToMany(mappedBy = "auditInfo", fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<Filter> filters;
	
	@OneToMany(mappedBy = "auditInfo",fetch = FetchType.LAZY)
	@JsonManagedReference
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

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
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
