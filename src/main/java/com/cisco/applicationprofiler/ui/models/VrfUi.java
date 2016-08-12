/**
 * 
 */
package com.cisco.applicationprofiler.ui.models;

import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Mahesh
 *
 */
public class VrfUi extends AciSizerModelUi {
	private int ownershipId;
	private String ownershipName;
	private boolean enforced;

	public VrfUi() {
		type = ACISizerConstant._vrf;
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
	 * @return the enforced
	 */
	public boolean isEnforced() {
		return enforced;
	}

	/**
	 * @param enforced
	 *            the enforced to set
	 */
	public void setEnforced(boolean enforced) {
		this.enforced = enforced;
	}

	@Override
	public String toString() {
		return ACISizerConstant.NAME + this.name;
	}
}
