package com.cisco.applicationprofiler.models;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.util.ACISizerConstant;

@XmlRootElement
public class SharedResource extends ACISizerModel {
	private String vrf;
	private Set<String> consumed_contracts = new LinkedHashSet<String>();

	public SharedResource(int id, String srcName) {
		this.name = "" + id;
		this.displayName = srcName;
	}

	public void copySharedResource(SharedResource src) {
		name = src.name;
		uiData = src.uiData;
		vrf = src.vrf;
		consumed_contracts = src.consumed_contracts;
		displayName = src.displayName;
		this.uiData.copyUIData(src.getUiData());

	}

	public Set<String> getConsumed_contracts() {
		return consumed_contracts;
	}

	public void setConsumed_contracts(Set<String> consumed_contracts) {
		this.consumed_contracts = consumed_contracts;
	}

	@Override
	public boolean equals(Object object) {
		boolean result = false;
		if (object == null || object.getClass() != getClass()) {
			result = false;
		} else {
			SharedResource other = (SharedResource) object;
			return this.name.equals(other.getName());
		}
		return result;
	}

	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}

	public String getVrf() {
		return vrf;
	}

	public void setVrf(String vrf) {
		this.vrf = vrf;
	}

	public UIData getUiData() {
		return uiData;
	}

	public void setUiData(UIData uiData) {
		this.uiData = uiData;
	}

	public void setUiDataNull() {
		if (null == uiData)
			uiData = new UIData();
		uiData.setXNull();
		uiData.setYNull();
	}
	
	@Override
	public String toString() {
	String temp = ACISizerConstant.NAME +  this.name +this.displayName;
		return temp;
	}
	

}
