package com.cisco.applicationprofiler.view;

import java.util.LinkedHashSet;
import java.util.Set;

import com.cisco.applicationprofiler.models.Application;
import com.cisco.applicationprofiler.models.Bd;
import com.cisco.applicationprofiler.models.Contract;
import com.cisco.applicationprofiler.models.Epg;
import com.cisco.applicationprofiler.models.L3out;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.models.Vrf;
import com.cisco.applicationprofiler.util.ACISizerConstant;

public class ViewTenantData extends ViewModel {

	private String tenantType = "user"; // 'user' | 'utility',
	private int count = 1;
	private Set<Vrf> vrfs = new LinkedHashSet<Vrf>();
	private Set<Bd> bds = new LinkedHashSet<Bd>();
	private Set<L3out> l3Outs = new LinkedHashSet<L3out>();
	private Set<Application> apps = new LinkedHashSet<Application>();
	private Set<Epg> epgs = new LinkedHashSet<Epg>();
	private Set<Contract> contracts = new LinkedHashSet<Contract>();

	public ViewTenantData() {

	}

	public ViewTenantData(Tenant src) {
		super.setName(src.getName());
		super.setType(ACISizerConstant._tenant);

		if (null != src.getUiData()) {
			super.getUiData().setX(src.getUiData().getX());
			super.getUiData().setY(src.getUiData().getY());
		}

		count = src.getCount();
		setVrfs(src.getVrfs());
		bds = src.getBds();
		setL3Outs(src.getL3outs());
		setApps(src.getApps());
		setEpgs(src.getEpgs());
		setContracts(src.getContracts());
		tenantType = src.getType();
	}

	public String getTenantType() {
		return tenantType;
	}

	public void setTenantType(String tenantType) {
		this.tenantType = tenantType;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the bds
	 */
	public Set<Bd> getBds() {
		return bds;
	}

	/**
	 * @param bds
	 *            the bds to set
	 */
	public void setBds(Set<Bd> bds) {
		this.bds = bds;
	}

	/**
	 * @return the epgs
	 */
	public Set<Epg> getEpgs() {
		return epgs;
	}

	/**
	 * @param epgs
	 *            the epgs to set
	 */
	public void setEpgs(Set<Epg> epgs) {
		this.epgs = epgs;
	}

	/**
	 * @return the contracts
	 */
	public Set<Contract> getContracts() {
		return contracts;
	}

	/**
	 * @param contracts
	 *            the contracts to set
	 */
	public void setContracts(Set<Contract> contracts) {
		this.contracts = contracts;
	}

	/**
	 * @return the vrfs
	 */
	public Set<Vrf> getVrfs() {
		return vrfs;
	}

	/**
	 * @param vrfs
	 *            the vrfs to set
	 */
	public void setVrfs(Set<Vrf> vrfs) {
		this.vrfs = vrfs;
	}

	/**
	 * @return the apps
	 */
	public Set<Application> getApps() {
		return apps;
	}

	/**
	 * @param apps
	 *            the apps to set
	 */
	public void setApps(Set<Application> apps) {
		this.apps = apps;
	}

	/**
	 * @return the l3Outs
	 */
	public Set<L3out> getL3Outs() {
		return l3Outs;
	}

	/**
	 * @param l3Outs the l3Outs to set
	 */
	public void setL3Outs(Set<L3out> l3Outs) {
		this.l3Outs = l3Outs;
	}
}
