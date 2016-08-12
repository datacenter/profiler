/**
 * 
 */
package com.cisco.applicationprofiler.ui.models;

import org.hibernate.validator.constraints.NotEmpty;

import com.cisco.applicationprofiler.models.UIData;

/**
 * @author Mahesh
 *
 */
public abstract class AciSizerModelUi implements Comparable<AciSizerModelUi> {
	protected int id;
	@NotEmpty
	protected String name;
	protected String fullName;
	protected String type;
	/**
	 * @return the pKey
	 */
	public int getpKey() {
		return id;
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

	protected UIData uiData;
	
	@Override
    public int compareTo(AciSizerModelUi compareNode) {
	   if(null == compareNode) return 0;
	   if(null == compareNode.getUiData().getX())return 0;
	   
        Float compareX=((AciSizerModelUi)compareNode).getUiData().getX();
        /* For Ascending order*/
        return (int)(this.getUiData().getX()-compareX);
    }

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName
	 *            the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the uiData
	 */
	public UIData getUiData() {
		return uiData;
	}

	/**
	 * @param uiData
	 *            the uiData to set
	 */
	public void setUiData(UIData uiData) {
		this.uiData = uiData;
	}

}
