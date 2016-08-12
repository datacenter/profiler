/**
 * 
 */
package com.cisco.applicationprofiler.apic.models;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author Mahesh
 *
 */
@XmlRootElement(name = "fvTenant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Tenant {
	
	@XmlAttribute
	private String name;
	
	@XmlElement(name="vzFilter")
	private List<Filter> filters;
	
	@XmlElement(name="vzBrCP")
	private List<Contract> contracts;
	
	@XmlElement(name="fvCtx")
	private List<Vrf> vrfs;
	
	@XmlElement(name="fvBD")
	private List<Bd> bds;
	
	@XmlElement(name="fvAp")
	private List<App> apps;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the filters
	 */
	public List<Filter> getFilters() {
		return filters;
	}

	/**
	 * @param filters the filters to set
	 */
	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	/**
	 * @return the contracts
	 */
	public List<Contract> getContracts() {
		return contracts;
	}

	/**
	 * @param contracts the contracts to set
	 */
	public void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}

	/**
	 * @return the vrfs
	 */
	public List<Vrf> getVrfs() {
		return vrfs;
	}

	/**
	 * @param vrfs the vrfs to set
	 */
	public void setVrfs(List<Vrf> vrfs) {
		this.vrfs = vrfs;
	}

	/**
	 * @return the bds
	 */
	public List<Bd> getBds() {
		return bds;
	}

	/**
	 * @param bds the bds to set
	 */
	public void setBds(List<Bd> bds) {
		this.bds = bds;
	}

	/**
	 * @return the apps
	 */
	public List<App> getApps() {
		return apps;
	}

	/**
	 * @param apps the apps to set
	 */
	public void setApps(List<App> apps) {
		this.apps = apps;
	}
}
