/**
 * 
 */
package com.cisco.applicationprofiler.ui.models;

import java.util.Set;

import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Mahesh
 *
 */
public class SharedResourceUi extends AciSizerModelUi {
	private String vrf;
	private Set<String> consumedContracts;

	public SharedResourceUi() {
		type = ACISizerConstant._shared_resource;
	}

	/**
	 * @return the consumedContracts
	 */
	public Set<String> getConsumedContracts() {
		return consumedContracts;
	}

	/**
	 * @param consumedContracts
	 *            the consumedContracts to set
	 */
	public void setConsumedContracts(Set<String> consumedContracts) {
		this.consumedContracts = consumedContracts;
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

	@Override
	public String toString() {
		return ACISizerConstant.NAME + this.name;
	}

}
