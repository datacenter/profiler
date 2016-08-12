/**
 * CopyRight @MapleLabs
 */
package com.cisco.applicationprofiler.models;


import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Deepa
 *
 */
@XmlRootElement
public class Tenant extends ACISizerModel {
	private String type = "user"; // 'user' | 'utility',
	private int count = 1;
	private Set<Vrf> vrfs;
	private Set<Bd> bds;
	private Set<L3out> l3outs ;
	private int totalBdCount;
	private int totalEndPoints;
	private boolean localVrf;
	private boolean localL3out;
	private String l3outComplexity;
	private Timestamp modifiedTime;
	private Timestamp lastPushedTime;
	
	
	
	
	public Timestamp getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Timestamp modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public Set<L3out> getL3outs() {
		return l3outs;
	}

	public void setL3outs(Set<L3out> l3outs) {
		this.l3outs = l3outs;
	}

	private Set<Application> apps;
	private Set<Epg> epgs;

	/**
	 * @return the apps
	 */
	public Set<Application> getApps() {
		return apps;
	}

	/**
	 * @param apps
	 *            the apps to set
	 */
	public void setApps(Set<Application> apps) {
		this.apps = apps;
	}

	private Set<Contract> contracts;
	private int totalEpgCount;
	private Set<SharedResource> sharedResources;
	private int totalContractCount = 0;
	private int totalFilterCount = 0;

	public void copyTenant(Tenant tenant) {
		this.apps = tenant.apps;
		this.bds = tenant.bds;
		this.contracts = tenant.contracts;
		this.count = tenant.count;
		this.epgs = tenant.epgs;
		this.l3outs = tenant.l3outs;
		this.sharedResources = tenant.sharedResources;
		this.totalContractCount = tenant.totalContractCount;
		this.totalEpgCount = tenant.totalEpgCount;
		this.totalFilterCount = tenant.totalFilterCount;
		this.type = tenant.type;
		this.uiData = tenant.uiData;
		this.vrfs = tenant.vrfs;
		this.localVrf=tenant.localVrf;
		this.localL3out=tenant.localL3out;
		this.l3outComplexity=tenant.l3outComplexity;
		this.modifiedTime=tenant.modifiedTime;
	}

	public int getTotalContractCount() {
		return totalContractCount;
	}

	public void setTotalContractCount(int totalContractCount) {
		this.totalContractCount = totalContractCount;
	}

	public int getTotalFilterCount() {
		return totalFilterCount;
	}

	public void setTotalFilterCount(int totalFilterCount) {
		this.totalFilterCount = totalFilterCount;
	}

	public Set<SharedResource> getSharedResources() {
		return sharedResources;
	}

	public void setSharedResources(Set<SharedResource> sharedResources) {
		this.sharedResources = sharedResources;
	}

	public Tenant() {
		this.name = "";
		this.l3outs=new LinkedHashSet<>();
		this.vrfs = new LinkedHashSet<>();
		this.bds = new LinkedHashSet<>();
		this.epgs = new TreeSet<>();
		this.contracts = new TreeSet<>();
		this.apps = new LinkedHashSet<>();
		this.sharedResources = new LinkedHashSet<>();
	}

	public Tenant(int id, String name) {
		this();
		this.name = "" + id;
		this.displayName = name;

	}

	@Override
	public boolean equals(Object object) {
		boolean result = false;
		if (object == null || object.getClass() != getClass()) {
			result = false;
		} else {
			Tenant other = (Tenant) object;
			return this.displayName.equals(other.getDisplayName());
		}
		return result;
	}

	@Override
	public int hashCode() {
		return this.getDisplayName().hashCode();
	}

	public int getTotalEpgCount() {
		return totalEpgCount;
	}

	public void setTotalEpgCount(int totalEpgCount) {
		this.totalEpgCount = totalEpgCount;
	}

	public UIData getUiData() {
		return uiData;
	}

	public void setUiData(UIData uiData) {
		this.uiData = uiData;
	}

	public void setUiDataNull() {
		if (null == uiData)
			uiData = new UIData();
		uiData.setXNull();
		uiData.setYNull();
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
	 * @return the instances
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param instances
	 *            the instances to set
	 */
	public void setCount(int count) {
		this.count = count;
	}
	/**
	 * @return the bds
	 */
	public Set<Bd> getBds() {
		return bds;
	}

	/**
	 * @param bds
	 *            the bds to set
	 */
	public void setBds(Set<Bd> bds) {
		this.bds = bds;
	}

	/**
	 * @return the epgs
	 */
	public Set<Epg> getEpgs() {
		return epgs;
	}

	/**
	 * @param epgs
	 *            the epgs to set
	 */
	public void setEpgs(Set<Epg> epgs) {
		this.epgs = epgs;
	}

	/**
	 * @return the contracts
	 */
	public Set<Contract> getContracts() {
		return contracts;
	}

	/**
	 * @param contracts
	 *            the contracts to set
	 */
	public void setContracts(Set<Contract> contracts) {
		this.contracts = contracts;
	}

	/**
	 * @return the vrfs
	 */
	public Set<Vrf> getVrfs() {
		return vrfs;
	}

	/**
	 * @param vrfs
	 *            the vrfs to set
	 */
	public void setVrfs(Set<Vrf> vrfs) {
		this.vrfs = vrfs;
	}
	
	@Override
	public String toString() {
	String temp = ACISizerConstant.NAME +  this.name +this.displayName;
		return temp;
	}

	/**
	 * @return the totalBdCount
	 */
	public int getTotalBdCount() {
		return totalBdCount;
	}

	/**
	 * @param totalBdCount the totalBdCount to set
	 */
	public void setTotalBdCount(int totalBdCount) {
		this.totalBdCount = totalBdCount;
	}

	/**
	 * @return the totalEndPoints
	 */
	public int getTotalEndPoints() {
		return totalEndPoints;
	}

	/**
	 * @param totalEndPoints the totalEndPoints to set
	 */
	public void setTotalEndPoints(int totalEndPoints) {
		this.totalEndPoints = totalEndPoints;
	}

	/**
	 * @return the localVrf
	 */
	public boolean isLocalVrf() {
		return localVrf;
	}

	/**
	 * @param localVrf the localVrf to set
	 */
	public void setLocalVrf(boolean localVrf) {
		this.localVrf = localVrf;
	}

	/**
	 * @return the localL3out
	 */
	public boolean isLocalL3out() {
		return localL3out;
	}

	/**
	 * @param localL3out the localL3out to set
	 */
	public void setLocalL3out(boolean localL3out) {
		this.localL3out = localL3out;
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
	 * @return the lastPushedTime
	 */
	public Timestamp getLastPushedTime() {
		return lastPushedTime;
	}

	/**
	 * @param lastPushedTime the lastPushedTime to set
	 */
	public void setLastPushedTime(Timestamp lastPushedTime) {
		this.lastPushedTime = lastPushedTime;
	}

}
