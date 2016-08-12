/**
 * 
 */
package com.cisco.applicationprofiler.ui.models;

import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Mahesh
 *
 */
public class ApplicationUi extends AciSizerModelUi {
	private int noOfInstances;

	/**
	 * @return the noOfInstances
	 */
	public int getNoOfInstances() {
		return noOfInstances;
	}

	/**
	 * @param noOfInstances
	 *            the noOfInstances to set
	 */
	public void setNoOfInstances(int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}

	public ApplicationUi() {
		type = ACISizerConstant._app;
	}

	@Override
	public String toString() {
		return ACISizerConstant.NAME + this.name;
	}
	
	public static ApplicationUi getDefaultApplication(){
		ApplicationUi applicationUi=new ApplicationUi();
		applicationUi.setNoOfInstances(ACISizerConstant.APPLICATION_NO_OF_INSTANCES_DEFAULT);
		return applicationUi;
	}

}
