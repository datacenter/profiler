/**
 * CopyRight @MapleLabs
 */
package com.cisco.applicationprofiler.models;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Deepa
 *
 */
@XmlRootElement
public class Epg extends ACISizerModel implements Comparable<Epg> {

	private String app;
	private String bd;
	private Eps eps;
	@Min(1)
	private int span;
	private int ports;
	private String label = "Regular Leaf";
	private Set<String> suffixes;
	private Set<String> provided_contracts;
	private Set<String> consumed_contracts;
	private boolean enableSharedReource;
	private int sharedResourceId;
	private int filterCount = 0;// for creating contract , this has to be set
								// for the contract
	
	private Subnets subnets;

	public int getFilterCount() {
		return filterCount;
	}

	public void setFilterCount(int filterCount) {
		if (enableSharedReource)
			this.filterCount = filterCount;
	}

	public int getSharedResourceId() {
		return sharedResourceId;
	}

	public void setSharedResourceId(int sharedResourceId) {
		this.sharedResourceId = sharedResourceId;
	}

	public Epg() {
		this.name = "epgDefault";
		this.app = "appDefault";
		this.provided_contracts = new LinkedHashSet<>();
		this.consumed_contracts = new LinkedHashSet<>();
	}

	public Epg(int id, String name, String appName) {
		this();
		this.name = "" + id;
		this.displayName = name;
		this.app = appName;
	}
	
	public Epg(String id, String name, String appName) {
		this();
		this.name = "" + id;
		this.displayName = name;
		this.app = appName;
	}

	/**
	 * Note: This creates a shallow copy
	 * 
	 * @param epg
	 */
	public void copyEpg(Epg epg) {
		this.bd = epg.bd;
		this.eps = epg.eps;
		this.span = epg.span;
		this.ports = epg.ports;
		this.label = epg.label;
		this.suffixes = epg.suffixes;
		this.provided_contracts = epg.provided_contracts;
		this.consumed_contracts = epg.consumed_contracts;
		this.uiData = epg.uiData;
		this.enableSharedReource = epg.enableSharedReource;
		this.sharedResourceId = epg.sharedResourceId;
		this.filterCount = epg.filterCount;
		this.uiData.copyUIData(epg.getUiData());
	}

	@Override
	public boolean equals(Object object) {
		boolean result = false;
		if (object == null || object.getClass() != getClass()) {
			result = false;
		} else {
			Epg other = (Epg) object;
			return (this.displayName.equals(other.getDisplayName()) && this.app.equals(other.app));
		}
		return result;
	}

	@Override
	public int hashCode() {
		return this.getDisplayName().hashCode() + this.app.hashCode();
	}

	public boolean isEnableSharedReource() {
		return enableSharedReource;
	}

	public void setEnableSharedReource(boolean enableSharedReource) {
		this.enableSharedReource = enableSharedReource;
	}

	public void setUiDataNull() {
		if (null == uiData)
			uiData = new UIData();
		uiData.setXNull();
		uiData.setYNull();
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the app
	 */
	public String getApp() {
		return app;
	}

	/**
	 * @return the bd
	 */
	public String getBd() {
		return bd;
	}

	/**
	 * @param bd
	 *            the location to set
	 */
	public void setBd(String bd) {
		this.bd = bd;
	}

	/**
	 * @return the span
	 */
	public int getSpan() {
		return span;
	}

	/**
	 * @param span
	 *            the span to set
	 */
	public void setSpan(int span) {
		this.span = span;
	}

	/**
	 * @return the ports
	 */
	public int getPorts() {
		return ports;
	}

	/**
	 * @param ports
	 *            the ports to set
	 */
	public void setPorts(int ports) {
		this.ports = ports;
	}

	/**
	 * @return the provided_contracts
	 */
	public Set<String> getProvided_contracts() {
		return provided_contracts;
	}

	/**
	 * @param provided_contracts
	 *            the provided_contracts to set
	 */
	public void setProvided_contracts(Set<String> provided_contracts) {
		this.provided_contracts = provided_contracts;
	}

	/**
	 * @return the consumed_contracts
	 */
	public Set<String> getConsumed_contracts() {
		return consumed_contracts;
	}

	/**
	 * @param consumed_contracts
	 *            the consumed_contracts to set
	 */
	public void setConsumed_contracts(Set<String> consumed_contracts) {
		this.consumed_contracts = consumed_contracts;
	}

	/**
	 * @return the eps
	 */
	public Eps getEps() {
		return eps;
	}

	/**
	 * @param eps
	 *            the eps to set
	 */
	public void setEps(Eps eps) {
		this.eps = eps;
	}

	/**
	 * @return the suffixes
	 */
	public Set<String> getSuffixes() {
		return suffixes;
	}

	/**
	 * @param suffixes
	 *            the suffixes to set
	 */
	public void setSuffixes(Set<String> suffixes) {
		this.suffixes = suffixes;
	}

	/**
	 * @return the subnets
	 */
	public Subnets getSubnets() {
		return subnets;
	}

	/**
	 * @param subnets the subnets to set
	 */
	public void setSubnets(Subnets subnets) {
		this.subnets = subnets;
	}

	@Override
	public int compareTo(Epg o) {
		return (int) (this.uiData.getX()-o.getUiData().getX());
	}
	
	@Override
	public String toString() {
	String temp = ACISizerConstant.NAME +  this.name +this.displayName;
		return temp;
	}
	

}
