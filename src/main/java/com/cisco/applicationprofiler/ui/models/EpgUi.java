/**
 * 
 */
package com.cisco.applicationprofiler.ui.models;

import javax.validation.constraints.Min;

import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Mahesh
 *
 */
public class EpgUi extends AciSizerModelUi {
	
	private int noOfEndPoints;
	private int span;
	private int epgSubnets;
	@Min(1)
	private int bdId;
	private String bdName;
	private boolean sharedServicesEnabled;
	private int sharedServiceId;
	private String sharedServiceName;
	private int noOfsharedServiceFilter;
	private int ports;
	private int appId;
	private int noOfInstances;

	public int getNoOfInstances() {
		return noOfInstances;
	}

	public void setNoOfInstances(int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}

	public EpgUi() {
		type = ACISizerConstant._epg;
	}

	public EpgUi(int appInstance) {
		type = ACISizerConstant._epg;
		setNoOfInstances(appInstance);
	}
	
	public static EpgUi getEpgDefaults(){
		EpgUi epgUi=new EpgUi();
		epgUi.setEpgSubnets(ACISizerConstant.EPG_SUBNET_DEFAULT);
		epgUi.setNoOfEndPoints(ACISizerConstant.EPG_NO_OF_ENDPOINTS_DEFAULT);
		epgUi.setSpan(ACISizerConstant.EPG_SPAN_DEFAULT);
		return epgUi;
	}

	/**
	 * @return the noOfEndPoints, number of VMs in the DC
	 */
	public int getNoOfEndPoints() {
		return noOfEndPoints;
	}

	/**
	 * @param noOfEndPoints
	 *            the noOfEndPoints to set, number of VMs in the DC
	 */
	public void setNoOfEndPoints(int noOfEndPoints) {
		this.noOfEndPoints = noOfEndPoints;
	}

	/**
	 * @return the span, number of instances residing in different leafs
	 */
	public int getSpan() {
		return span;
	}

	/**
	 * @param span
	 *            the span to set, number of instances residing in different
	 *            leafs
	 */
	public void setSpan(int span) {
		this.span = span;
	}

	/**
	 * @return the epgSubnets
	 */
	public int getEpgSubnets() {
		return epgSubnets;
	}

	/**
	 * @param epgSubnets
	 *            the epgSubnets to set
	 */
	public void setEpgSubnets(int epgSubnets) {
		this.epgSubnets = epgSubnets;
	}

	/**
	 * @return the bdId
	 */
	public int getBdId() {
		return bdId;
	}

	/**
	 * @param bdId
	 *            the bdId to set
	 */
	public void setBdId(int bdId) {
		this.bdId = bdId;
	}

	/**
	 * @return the bdName
	 */
	public String getBdName() {
		return bdName;
	}

	/**
	 * @param bdName
	 *            the bdName to set
	 */
	public void setBdName(String bdName) {
		this.bdName = bdName;
	}

	/**
	 * @return the sharedServicesEnabled
	 */
	public boolean isSharedServicesEnabled() {
		return sharedServicesEnabled;
	}

	/**
	 * @param sharedServicesEnabled
	 *            the sharedServicesEnabled to set
	 */
	public void setSharedServicesEnabled(boolean sharedServicesEnabled) {
		this.sharedServicesEnabled = sharedServicesEnabled;
	}

	/**
	 * @return the noOfsharedServiceFilter
	 */
	public int getNoOfsharedServiceFilter() {
		return noOfsharedServiceFilter;
	}

	/**
	 * @param noOfsharedServiceFilter
	 *            the noOfsharedServiceFilter to set
	 */
	public void setNoOfsharedServiceFilter(int noOfsharedServiceFilter) {
		this.noOfsharedServiceFilter = noOfsharedServiceFilter;
	}

	/**
	 * @return the ports
	 */
	public int getPorts() {
		return ports;
	}

	/**
	 * @param ports
	 *            the ports to set
	 */
	public void setPorts(int ports) {
		this.ports = ports;
	}

	/**
	 * @return the sharedServiceId
	 */
	public int getSharedServiceId() {
		return sharedServiceId;
	}

	/**
	 * @param sharedServiceId
	 *            the sharedServiceId to set
	 */
	public void setSharedServiceId(int sharedServiceId) {
		this.sharedServiceId = sharedServiceId;
	}

	/**
	 * @return the sharedServiceName
	 */
	public String getSharedServiceName() {
		return sharedServiceName;
	}

	/**
	 * @param sharedServiceName
	 *            the sharedServiceName to set
	 */
	public void setSharedServiceName(String sharedServiceName) {
		this.sharedServiceName = sharedServiceName;
	}

	/**
	 * @return the appId
	 */
	public int getAppId() {
		return appId;
	}

	/**
	 * @param appId
	 *            the appId to set
	 */
	public void setAppId(int appId) {
		this.appId = appId;
	}

	@Override
	public String toString() {
		return ACISizerConstant.NAME + this.name;
	}
}
