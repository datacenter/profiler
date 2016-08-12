/**
 * CopyRight @MapleLabs
 */
package com.cisco.applicationprofiler.models;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Deepa
 *
 */
@XmlRootElement
public class ProjectInfo {
	private int id;
	private String name;
	private String customerName;
	private String salesContact;
	private String	opportunity;
	private String	account;
	


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
	

}

