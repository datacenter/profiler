/**
 * CopyRight @MapleLabs
 */
package com.cisco.applicationprofiler.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Deepa
 *
 */
@XmlRootElement
public class Vrf extends ACISizerModel {
	private String status = "enforced"; // 'enforced' | 'unenforced'
	private List<String> suffix = new ArrayList<>();
	private String type;

	public void copyVrf(Vrf vrf) {
		this.status = vrf.status;
		this.suffix = vrf.suffix;
		this.uiData = vrf.uiData;
		this.type=vrf.type;
		uiData.copyUIData(vrf.getUiData());
	}

	public Vrf() {
	}

	public Vrf(int id, String diaplayName) {
		this.displayName = diaplayName;
		this.name = "" + id;
	}

	@Override
	public boolean equals(Object object) {
		boolean result = false;
		if (object == null || object.getClass() != getClass()) {
			result = false;
		} else {
			Vrf other = (Vrf) object;
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

	public List<String> getSuffix() {
		return suffix;
	}

	public void setSuffix(List<String> suffix) {
		this.suffix = suffix;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the location to set
	 */
	public void setStatus(String status) {
		this.status = status;
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
