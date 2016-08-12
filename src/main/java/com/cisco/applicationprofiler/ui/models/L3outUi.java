/**
 * 
 */
package com.cisco.applicationprofiler.ui.models;

import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Mahesh
 *
 */
public class L3outUi extends AciSizerModelUi {
	private int vrfId;
	private String vrfName;
	private int ownershipId;
	private String ownershipName;
	private int epgPrefixes;
	private int span;
	private int subInterfaces;
	private int svis;
	private int count;
	private int subnets;

	public L3outUi() {
		type = ACISizerConstant._l3out;
	}

	/**
	 * @return the vrfId
	 */
	public int getVrfId() {
		return vrfId;
	}

	/**
	 * @param vrfId
	 *            the vrfId to set
	 */
	public void setVrfId(int vrfId) {
		this.vrfId = vrfId;
	}

	/**
	 * @return the vrfName
	 */
	public String getVrfName() {
		return vrfName;
	}

	/**
	 * @param vrfName
	 *            the vrfName to set
	 */
	public void setVrfName(String vrfName) {
		this.vrfName = vrfName;
	}

	/**
	 * @return the ownershipId
	 */
	public int getOwnershipId() {
		return ownershipId;
	}

	/**
	 * @param ownershipId
	 *            the ownershipId to set
	 */
	public void setOwnershipId(int ownershipId) {
		this.ownershipId = ownershipId;
	}

	/**
	 * @return the ownershipName
	 */
	public String getOwnershipName() {
		return ownershipName;
	}

	/**
	 * @param ownershipName
	 *            the ownershipName to set
	 */
	public void setOwnershipName(String ownershipName) {
		this.ownershipName = ownershipName;
	}

	/**
	 * @return the epgPrefixes
	 */
	public int getEpgPrefixes() {
		return epgPrefixes;
	}

	/**
	 * @param epgPrefixes
	 *            the epgPrefixes to set
	 */
	public void setEpgPrefixes(int epgPrefixes) {
		this.epgPrefixes = epgPrefixes;
	}

	/**
	 * @return the span
	 */
	public int getSpan() {
		return span;
	}

	/**
	 * @param span
	 *            the span to set
	 */
	public void setSpan(int span) {
		this.span = span;
	}

	/**
	 * @return the subInterfaces
	 */
	public int getSubInterfaces() {
		return subInterfaces;
	}

	/**
	 * @param subInterfaces
	 *            the subInterfaces to set
	 */
	public void setSubInterfaces(int subInterfaces) {
		this.subInterfaces = subInterfaces;
	}

	/**
	 * @return the svis
	 */
	public int getSvis() {
		return svis;
	}

	/**
	 * @param svis
	 *            the svis to set
	 */
	public void setSvis(int svis) {
		this.svis = svis;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the subnets
	 */
	public int getSubnets() {
		return subnets;
	}

	/**
	 * @param subnets
	 *            the subnets to set
	 */
	public void setSubnets(int subnets) {
		this.subnets = subnets;
	}

	@Override
	public String toString() {
		return ACISizerConstant.NAME + this.name;
	}
	
	public static L3outUi getL3outDefault(){
		L3outUi l3outUi=new L3outUi();
		l3outUi.setSpan(1);
		l3outUi.setSubInterfaces(1);
		l3outUi.setSvis(1);
		l3outUi.setEpgPrefixes(1);
		return l3outUi;
	}

}
