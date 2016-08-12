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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.models.View;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "aci_project")
@XmlRootElement
public class ProjectTable {

	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	/*@Id
	@GenericGenerator(name = "seq_id", strategy = "com.cisco.applicationprofiler.util.GenericIdGenerator")
	@GeneratedValue(generator = "seq_id")
	@Column(name = "id")
	private int id;*/
	
	@Column(name = "userid")
	private String userId;
	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Column(name = "name", unique = true)
	@Size(min = 0, max = 255)
	private String name;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "logical_requirement")
	private String logicalRequirement;
	
	@Column(name = "logical_requirement_summary")
	private String logicalRequirementSummary;
	
	@Column(name = "logical_result_summary")
	private String logicalResultSummary;
		
	@Column(name = "customer_name")
	@Size(min = 0, max = 255)
	private String customerName;
	
	@Column(name = "sales_contact")
	@Size(min = 0, max = 255)
	private String salesContact;
	
	@Column(name = "opportunity")
	@Size(min = 0, max = 255)
	private String opportunity;
	
	@Column(name = "account")
	@Size(min = 0, max = 255)
	private String account;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "created_time")
	private Timestamp createdTime;
	
	@Column(name = "last_updated_time")
	private Timestamp lastUpdatedTime;
	
	@Column(name = "description")
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "deviceId")
	@JsonView(View.Device.class)
	private Device device;
	
	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the logicalRequirement
	 */
	public String getLogicalRequirement() {
		return logicalRequirement;
	}

	/**
	 * @param logicalRequirement
	 *            the logicalRequirement to set
	 */
	public void setLogicalRequirement(String logicalRequirement) {
		this.logicalRequirement = logicalRequirement;
	}

	/**
	 * @return the logicalRequirementSummary
	 */
	public String getLogicalRequirementSummary() {
		return logicalRequirementSummary;
	}

	/**
	 * @param logicalRequirementSummary
	 *            the logicalRequirementSummary to set
	 */
	public void setLogicalRequirementSummary(String logicalRequirementSummary) {
		this.logicalRequirementSummary = logicalRequirementSummary;
	}

	/**
	 * @return the logicalResultSummary
	 */
	public String getLogicalResultSummary() {
		return logicalResultSummary;
	}

	/**
	 * @param logicalResultSummary
	 *            the logicalResultSummary to set
	 */
	public void setLogicalResultSummary(String logicalResultSummary) {
		this.logicalResultSummary = logicalResultSummary;
	}

	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * @param customerName
	 *            the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return the salesContact
	 */
	public String getSalesContact() {
		return salesContact;
	}

	/**
	 * @param salesContact
	 *            the salesContact to set
	 */
	public void setSalesContact(String salesContact) {
		this.salesContact = salesContact;
	}

	/**
	 * @return the opportunity
	 */
	public String getOpportunity() {
		return opportunity;
	}

	/**
	 * @param opportunity
	 *            the opportunity to set
	 */
	public void setOpportunity(String opportunity) {
		this.opportunity = opportunity;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account
	 *            the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
	

	/**
	 * Method for shallow copy
	 */
	public void copyProjectTable(ProjectTable table) {
		this.account = table.account;
		this.createdBy = table.createdBy;
		this.customerName = table.customerName;
		this.description = table.description;
		this.logicalRequirement = table.logicalRequirement;
		this.logicalRequirementSummary = table.logicalRequirementSummary;
		this.logicalResultSummary = table.logicalResultSummary;
		this.opportunity = table.opportunity;
		this.salesContact = table.salesContact;
		this.type = table.type;
		this.userId = table.userId;
		this.device = table.device;

	}

}