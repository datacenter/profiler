package com.cisco.applicationprofiler.ui.models;

public class ApplicationConfiguration {
	
     private String  epgComplexity; // "Small or Medium or Large",
     private String subnetPolicy;  // "Default or Unique",
     private String contractComplexity; // "Low or Medium or High",
     private boolean l3outEnabled; // true,
     private boolean sharedServiceEnabled; // true,
     private String l3outComplexity; // " Low or Medium or High ",
     private String bdPolicy; // "Default or Unique"
	/**
	 * @return the epgComplexity
	 */
	public String getEpgComplexity() {
		return epgComplexity;
	}
	/**
	 * @param epgComplexity the epgComplexity to set
	 */
	public void setEpgComplexity(String epgComplexity) {
		this.epgComplexity = epgComplexity;
	}
	/**
	 * @return the subnetPolicy
	 */
	public String getSubnetPolicy() {
		return subnetPolicy;
	}
	/**
	 * @param subnetPolicy the subnetPolicy to set
	 */
	public void setSubnetPolicy(String subnetPolicy) {
		this.subnetPolicy = subnetPolicy;
	}
	/**
	 * @return the contractComplexity
	 */
	public String getContractComplexity() {
		return contractComplexity;
	}
	/**
	 * @param contractComplexity the contractComplexity to set
	 */
	public void setContractComplexity(String contractComplexity) {
		this.contractComplexity = contractComplexity;
	}
	/**
	 * @return the l3outEnabled
	 */
	public boolean isL3outEnabled() {
		return l3outEnabled;
	}
	/**
	 * @param l3outEnabled the l3outEnabled to set
	 */
	public void setL3outEnabled(boolean l3outEnabled) {
		this.l3outEnabled = l3outEnabled;
	}
	/**
	 * @return the sharedServiceEnabled
	 */
	public boolean isSharedServiceEnabled() {
		return sharedServiceEnabled;
	}
	/**
	 * @param sharedServiceEnabled the sharedServiceEnabled to set
	 */
	public void setSharedServiceEnabled(boolean sharedServiceEnabled) {
		this.sharedServiceEnabled = sharedServiceEnabled;
	}
	/**
	 * @return the l3outComplexity
	 */
	public String getL3outComplexity() {
		return l3outComplexity;
	}
	/**
	 * @param l3outComplexity the l3outComplexity to set
	 */
	public void setL3outComplexity(String l3outComplexity) {
		this.l3outComplexity = l3outComplexity;
	}
	/**
	 * @return the bdPolicy
	 */
	public String getBdPolicy() {
		return bdPolicy;
	}
	/**
	 * @param bdPolicy the bdPolicy to set
	 */
	public void setBdPolicy(String bdPolicy) {
		this.bdPolicy = bdPolicy;
	}
     
     
	}
