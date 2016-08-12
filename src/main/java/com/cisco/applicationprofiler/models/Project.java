/**
 * CopyRight @MapleLabs
 */
package com.cisco.applicationprofiler.models;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.domain.Device;

/**
 * @author Deepa
 *
 */
@XmlRootElement
public class Project implements Comparable<Project> {
	private int id;
	private String name;
	private LogicalRequirement logicalRequirement;
	private LogicalSummary logicalRequirementSummary;
	private String logicalResultSummary;
	private String physicalRequirement;
	private String physicalRequirementSummary;
	private String physicalResultSummary;
	private String customerName;
	private String salesContact;
	private String opportunity;
	private String account;
	private ProjectType type;
	private String createdBy = "";
	private transient Timestamp createdTime;
	private transient Timestamp lastUpdatedTime;
	private String description;
	private int roomId;
	private boolean usePhysical;
	private Device device;
	
	
	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public boolean isUsePhysical() {
		return usePhysical;
	}

	public void setUsePhysical(boolean usePhysical) {
		this.usePhysical = usePhysical;
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

	public LogicalRequirement getLogicalRequirement() {
		return logicalRequirement;
	}

	public void setLogicalRequirement(LogicalRequirement logicalRequirement) {
		this.logicalRequirement = logicalRequirement;
	}

	/**
	 * @return the logicalRequirementSummary
	 */
	public LogicalSummary getLogicalRequirementSummary() {
		return logicalRequirementSummary;
	}

	/**
	 * @param logicalRequirementSummary
	 *            the logicalRequirementSummary to set
	 */
	public void setLogicalRequirementSummary(LogicalSummary logicalRequirementSummary) {
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
	 * @return the physicalRequirement
	 */
	public String getPhysicalRequirement() {
		return physicalRequirement;
	}

	/**
	 * @param physicalRequirement
	 *            the physicalRequirement to set
	 */
	public void setPhysicalRequirement(String physicalRequirement) {
		this.physicalRequirement = physicalRequirement;
	}

	/**
	 * @return the physicalRequirementSummary
	 */
	public String getPhysicalRequirementSummary() {
		return physicalRequirementSummary;
	}

	/**
	 * @param physicalRequirementSummary
	 *            the physicalRequirementSummary to set
	 */
	public void setPhysicalRequirementSummary(String physicalRequirementSummary) {
		this.physicalRequirementSummary = physicalRequirementSummary;
	}

	/**
	 * @return the physicalResultSummary
	 */
	public String getPhysicalResultSummary() {
		return physicalResultSummary;
	}

	/**
	 * @param physicalResultSummary
	 *            the physicalResultSummary to set
	 */
	public void setPhysicalResultSummary(String physicalResultSummary) {
		this.physicalResultSummary = physicalResultSummary;
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
	 * @return the type
	 */
	public String getType() {
		return type.getValue();
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(ProjectType type) {
		this.type = type;
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

	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + "]";
	}

	/**
	 * 
	 * @param o
	 * @return project name in descending order
	 */

	@Override
	public int compareTo(Project o) {
		return o.lastUpdatedTime.compareTo(this.lastUpdatedTime);
	}

}
