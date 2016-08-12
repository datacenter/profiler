/**
 * CopyRight @MapleLabs
 */
package com.cisco.applicationprofiler.models;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Deepa
 *
 */
@XmlRootElement
public class Bd extends ACISizerModel {
	private int ownershipId;
	private String vrf;
	private Subnets subnets;
	private Set<String> suffix = new LinkedHashSet<String>();
	private int noOfInstances = 1;
	private String type;

	public int getNoOfInstances() {
		return noOfInstances;
	}

	public void setNoOfInstances(int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}

	/**
	 * @return the suffix
	 */
	public Set<String> getSuffix() {
		return suffix;
	}

	/**
	 * @param suffix
	 *            the suffix to set
	 */
	public void setSuffix(Set<String> suffix) {
		this.suffix = suffix;
	}

	public Bd(int name, String displayName) {
		this.name = "" + name;
		this.displayName = displayName;
		setNoOfInstances(1);
	}
	
	public Bd(String name, String displayName) {
		this.name = "" + name;
		this.displayName = displayName;
		setNoOfInstances(1);
	}

	public Bd(String displayName) {
		this.displayName = displayName;
		setNoOfInstances(1);
	}

	public Bd() {
		this.name = "";
		setNoOfInstances(1);
	}

	@Override
	public boolean equals(Object object) {
		boolean result = false;
		if (object == null || object.getClass() != getClass()) {
			result = false;
		} else {
			Bd other = (Bd) object;
			return this.displayName.equals(other.getDisplayName());
		}
		return result;
	}

	@Override
	public int hashCode() {
		return this.getDisplayName().hashCode();
	}

	/**
	 * Note: This creates a shallow copy
	 * 
	 * @param bd
	 */
	public void copyBd(Bd bd) {
		this.vrf = bd.vrf;
		this.subnets = bd.subnets;
		this.suffix = bd.suffix;
		this.uiData = bd.uiData;
		this.type=bd.type;
		uiData.copyUIData(bd.getUiData());
	}

	public void setUiDataNull() {
		if (null == uiData)
			uiData = new UIData();
		uiData.setXNull();
		uiData.setYNull();
	}

	public Subnets getSubnets() {
		return subnets;
	}

	public void setSubnets(Subnets subnets) {
		this.subnets = subnets;
	}

	/**
	 * @return the vrf
	 */
	public String getVrf() {
		return vrf;
	}

	/**
	 * @param vrf
	 *            the vrf to set
	 */
	public void setVrf(String vrf) {
		this.vrf = vrf;
	}

	/**
	 * @return the ownershipId
	 */
	public int getOwnershipId() {
		return ownershipId;
	}

	/**
	 * @param ownershipId the ownershipId to set
	 */
	public void setOwnershipId(int ownershipId) {
		this.ownershipId = ownershipId;
	}
	
	@Override
	public String toString() {
	String temp = ACISizerConstant.NAME +  this.name +this.displayName;
		return temp;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	

}
