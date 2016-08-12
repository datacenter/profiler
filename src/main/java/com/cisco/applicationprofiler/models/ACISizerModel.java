package com.cisco.applicationprofiler.models;

import org.hibernate.validator.constraints.NotEmpty;

public abstract class ACISizerModel {

	protected String name;
	@NotEmpty
	protected String displayName;
	protected UIData uiData =  new UIData();

	/**
	 * @return the uiData
	 */
	public UIData getUiData() {
		return uiData;
	}

	/**
	 * @param uiData the uiData to set
	 */
	public void setUiData(UIData uiData) {
		this.uiData = uiData;
	}

	/**
	 * @return the name
	 */
	
	
	public String getName() {
		return name;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Method deprecated will remove once UI changes all instances of id to name
	 * 
	 * @return
	 */
	@Deprecated
	public int getId() {
		return Integer.parseInt(this.name);
	}

}
