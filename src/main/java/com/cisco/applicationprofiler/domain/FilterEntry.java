package com.cisco.applicationprofiler.domain;

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
@Table(name = "filter_entry")
@XmlRootElement
public class FilterEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@JsonView(View.FilterEntry.class)
	private int id;

	@Column(name = "name")
	@JsonView(View.FilterEntry.class)
	private String name;

	@Column(name = "etherType")
	@JsonView(View.FilterEntry.class)
	private String etherType;

	@Column(name = "ipProtocol")
	@JsonView(View.FilterEntry.class)
	private String ipProtocol;

	@Column(name = "srcPort")
	@JsonView(View.FilterEntry.class)
	private String srcPort;

	@Column(name = "destPort")
	@JsonView(View.FilterEntry.class)
	private String destPort;

	@ManyToOne
	@JoinColumn(name = "filterId")
	@JsonBackReference
	private Filter filter;
	
	@ManyToOne
	@JoinColumn(name = "auditId")
	@JsonView(View.AuditInfo.class)
	private AuditInfo auditInfo;
	

	public AuditInfo getAuditInfo() {
		return auditInfo;
	}

	public void setAuditInfo(AuditInfo auditInfo) {
		this.auditInfo = auditInfo;
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

	public void setName(String name) {
		this.name = name;
	}

	public String getEtherType() {
		return etherType;
	}

	public void setEtherType(String etherType) {
		this.etherType = etherType;
	}

	public String getIpProtocol() {
		return ipProtocol;
	}

	public void setIpProtocol(String ipProtocol) {
		this.ipProtocol = ipProtocol;
	}

	public String getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}

	public String getDestPort() {
		return destPort;
	}

	public void setDestPort(String destPort) {
		this.destPort = destPort;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

}
