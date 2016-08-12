package com.cisco.applicationprofiler.view;

import java.util.Set;

import com.cisco.applicationprofiler.models.SharedResource;
import com.cisco.applicationprofiler.util.ACISizerConstant;

public class ViewSharedResourceData extends ViewModel  {

	private String vrf;
	private int vrfId;
	private Set<String> consumed_contracts;
	private String displayName;
	private int filters;

	public int getFilters() {
		return filters;
	}

	public void setFilters(int filters) {
		this.filters = filters;
	}



	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public ViewSharedResourceData()
	{}
	
	public ViewSharedResourceData(SharedResource src)
	{
		super.setName(src.getName());
		super.setType(ACISizerConstant._shared_resource);
		//super.setFullyQualifiedName(src.getFullyQualifiedName());
		if(null != src.getUiData())
		{
			super.getUiData().setX(src.getUiData().getX());
			super.getUiData().setY(src.getUiData().getY());
		}

		vrf = src.getVrf();
		consumed_contracts = src.getConsumed_contracts();
		displayName = src.getDisplayName();
	}

	public Set<String> getConsumed_contracts() {
		return consumed_contracts;
	}

	public void setConsumed_contracts(Set<String> consumed_contracts) {
		this.consumed_contracts = consumed_contracts;
	}

	public String getVrf() {
		return vrf;
	}

	public void setVrf(String vrf) {
		this.vrf = vrf;
	}

	public int getVrfId() {
		return vrfId;
	}

	public void setVrfId(int vrfId) {
		this.vrfId = vrfId;
	}
}
