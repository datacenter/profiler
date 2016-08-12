/**
 * CopyRight @MapleLabs
 */
package com.cisco.applicationprofiler.models;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Deepa
 *
 */
@XmlRootElement
public class Application extends ACISizerModel {
	private int instances = 1;
	private int totalEpgCount;
	private int totalContractCount;
	private int totalFilterCount;
	private int totalEpsCount;
	
	private String model = com.cisco.applicationprofiler.util.ACISizerConstant.TEMPLATE_APP_MODEL_NO_TEMPLATE; // "No Template or Flat or 2 tier or 3 tier",
	private com.cisco.applicationprofiler.ui.models.ApplicationConfiguration configuration ;
	
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public com.cisco.applicationprofiler.ui.models.ApplicationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(com.cisco.applicationprofiler.ui.models.ApplicationConfiguration configuration) {
		this.configuration = configuration;
	}

	public int getTotalFilterCount() {
		return totalFilterCount;
	}

	public void setTotalFilterCount(int totalFilterCount) {
		this.totalFilterCount = totalFilterCount;
	}

	public int getTotalContractCount() {
		return totalContractCount;
	}

	public void setTotalContractCount(int totalContractCount) {
		this.totalContractCount = totalContractCount;
	}
	public void copyApplication(Application src) {
		uiData = src.getUiData();
		totalContractCount=src.getTotalContractCount();
		totalEpgCount=src.getTotalEpgCount();
		totalFilterCount=src.getTotalFilterCount();
		totalEpsCount=src.getTotalEpsCount();
		configuration = src.getConfiguration();
		model = src.getModel();
	}


	public int getTotalEpgCount() {
		return totalEpgCount;
	}

	public void setTotalEpgCount(int totalEpgCount) {
		this.totalEpgCount = totalEpgCount;
	}
	private Application() {
	}

	public Application(int id, String name) {
		this.name = "" + id;
		this.displayName = name;
	}

	@Override
	public boolean equals(Object object) {
		boolean result = false;
		if (object == null || object.getClass() != getClass()) {
			result = false;
		} else {
			Application other = (Application) object;
			return this.displayName.equals(other.getDisplayName());
		}
		return result;
	}

	@Override
	public int hashCode() {
		return this.getDisplayName().hashCode();
	}

	

	public void setUiDataNull() {
		if (null == uiData)
			uiData = new UIData();
		uiData.setXNull();
		uiData.setYNull();
	}

	/**
	 * @return the instances
	 */
	public int getInstances() {
		return instances;
	}

	/**
	 * @param instances
	 *            the instances to set
	 */
	public void setInstances(int instances) {
		this.instances = instances;
	}

	/**
	 * @return the totalEpsCount
	 */
	public int getTotalEpsCount() {
		return totalEpsCount;
	}

	/**
	 * @param totalEpsCount the totalEpsCount to set
	 */
	public void setTotalEpsCount(int totalEpsCount) {
		this.totalEpsCount = totalEpsCount;
	}

}
