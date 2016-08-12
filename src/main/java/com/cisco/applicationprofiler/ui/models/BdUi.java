/**
 * 
 */
package com.cisco.applicationprofiler.ui.models;

import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Mahesh
 *
 */
public class BdUi extends AciSizerModelUi {
	private int ownershipId;
	private String ownershipName;
	private int vrfId;
	private String vrfName;
	private int bdSubnets;
	private int noOfInstances;

	public int getNoOfInstances() {
		return noOfInstances;
	}

	public void setNoOfInstances(int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}

	public BdUi() {
		type = ACISizerConstant._bd;
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
	 * @return the bdSubnets
	 */
	public int getBdSubnets() {
		return bdSubnets;
	}

	/**
	 * @param bdSubnets
	 *            the bdSubnets to set
	 */
	public void setBdSubnets(int bdSubnets) {
		this.bdSubnets = bdSubnets;
	}

	@Override
	public String toString() {
		return ACISizerConstant.NAME + this.name;
	}
	
	public static BdUi getDefaultBd(){
		BdUi bdUi=new BdUi();
		bdUi.setBdSubnets(ACISizerConstant.BD_SUBNETS_DEFAULT);
		return bdUi;
	}

}
