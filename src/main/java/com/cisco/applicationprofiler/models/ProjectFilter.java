/**
 * CopyRight @MapleLabs
 */
package com.cisco.applicationprofiler.models;

import java.util.ArrayList;


/**
 * @author Deepa
 *
 */

public class ProjectFilter {
	
	@SuppressWarnings("unused")
	private ArrayList<Integer> byProjectIds;
	
	@SuppressWarnings("unused")
	private ArrayList<String> byNames;
	
	@SuppressWarnings("unused")
	private String byNameContains;
	
	@SuppressWarnings("unused")
	private ArrayList<String> byCustomerNames;
	
	@SuppressWarnings("unused")
	private String byCustomerNameContains;
	
	
	@SuppressWarnings("unused")
	private String bySalesContactContains;
	
	@SuppressWarnings("unused")
	private ArrayList<String> bySalesContacts;
	
	@SuppressWarnings("unused")
	private String	byOpportunityContains;
	
	@SuppressWarnings("unused")
	private ArrayList<String> byOpportunities;
	
	@SuppressWarnings("unused")
	private String	byAccountContains;
	
	@SuppressWarnings("unused")
	private ArrayList<String> byAccounts;
	
	/**
	 * @param byProjectIds
	 *            the byProjectIds to set
	 */
	public void setByProjectsIds(ArrayList<Integer> byProjectIds) {
		this.byProjectIds = byProjectIds;
	}


	
	/**
	 * @param byNameContains
	 *            the byNameContains to set
	 */
	public void setName(String byNameContains) {
		this.byNameContains = byNameContains;
	}
	
	/**
	 * @param byNames
	 *            the byNames to set
	 */
	public void setByNames(ArrayList<String> byNames) {
		this.byNames = byNames;
	}
	
	/**
	 * @param byCustomerNameContains
	 *            the byCustomerNameContains to set
	 */
	public void setCustomerName(String byCustomerNameContains) {
		this.byCustomerNameContains = byCustomerNameContains;
	}
	
	/**
	 * @param byCustomerNames
	 *            the byCustomerNames to set
	 */
	public void setByCustomerNames(ArrayList<String> byCustomerNames) {
		this.byCustomerNames = byCustomerNames;
	}
	
	
	/**
	 * @param byOpportunityContains
	 *            the byOpportunityContains to set
	 */
	public void setOpportunity(String byOpportunityContains) {
		this.byOpportunityContains = byOpportunityContains;
	}
	
	/**
	 * @param byOpportunities
	 *            the byOpportunities to set
	 */
	public void setByOpportunitys(ArrayList<String> byOpportunities) {
		this.byOpportunities = byOpportunities;
	}

	/**
	 * @param bySalesContactContains
	 *            the bySalesContactContains to set
	 */
	public void setSalesContact(String bySalesContactContains) {
		this.bySalesContactContains = bySalesContactContains;
	}
	
	/**
	 * @param bySalesContacts
	 *            the bySalesContacts to set
	 */
	public void setBySalesContacts(ArrayList<String> bySalesContacts) {
		this.bySalesContacts = bySalesContacts;
	}

	
	/**
	 * @param byAccountContains
	 *            the byAccountContains to set
	 */
	public void setAccount(String byAccountContains) {
		this.byAccountContains = byAccountContains;
	}
	
	/**
	 * @param byAccounts
	 *            the byAccounts to set
	 */
	public void setByAccounts(ArrayList<String> byAccounts) {
		this.byAccounts = byAccounts;
	}
}
