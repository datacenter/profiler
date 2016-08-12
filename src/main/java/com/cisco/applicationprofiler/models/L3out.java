/**
 * CopyRight @MapleLabs
 */
package com.cisco.applicationprofiler.models;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Deepa
 *
 */
@XmlRootElement
public class L3out extends ACISizerModel {
	private String vrf;
	private int count=1;
	private int span = 2;
	private int subifs = 4;
	private int svis;
	private String type;
	private int ownershipId;

	private Set<String> consumed_contracts = new LinkedHashSet<String>();

	private Subnets subnets;
	private EpgPrefixes epg_prefixes;
	private String protocol;
	private String label = "Border Leaf";
	private WanLpmRoutes wan_lpm_routes;

	public L3out() {
	}

	public L3out(int uniqueId, String name) {
		this.name = "" + uniqueId;
		this.displayName = name;
	}

	@Override
	public boolean equals(Object object) {
		boolean result = false;
		if (object == null || object.getClass() != getClass()) {
			result = false;
		} else {
			L3out other = (L3out) object;
			return this.displayName.equals(other.getDisplayName());
		}
		return result;
	}

	@Override
	public int hashCode() {
		return this.getDisplayName().hashCode();
	}

	public void copyL3out(L3out src) {
		vrf = src.vrf;
		count = src.count;
		span = src.span;
		subifs = src.subifs;
		uiData = src.uiData;
		consumed_contracts = src.consumed_contracts;
		subnets = src.subnets;
		epg_prefixes = src.epg_prefixes;
		protocol = src.protocol;
		label = src.label;
		wan_lpm_routes = src.wan_lpm_routes;
		type=src.type;
		this.uiData.copyUIData(src.getUiData());
	}

	/**
	 * @return the subifs
	 */
	public int getSubifs() {
		return subifs;
	}

	/**
	 * @param subifs
	 *            the subifs to set
	 */
	public void setSubifs(int subifs) {
		this.subifs = subifs;
	}

	/**
	 * @return the subnets
	 */
	public Subnets getSubnets() {
		return subnets;
	}

	/**
	 * @param subnets
	 *            the subnets to set
	 */
	public void setSubnets(Subnets subnets) {
		this.subnets = subnets;
	}

	/**
	 * @return the epg_prefixes
	 */
	public EpgPrefixes getEpg_prefixes() {
		return epg_prefixes;
	}

	/**
	 * @param epg_prefixes
	 *            the epg_prefixes to set
	 */
	public void setEpg_prefixes(EpgPrefixes epg_prefixes) {
		this.epg_prefixes = epg_prefixes;
	}

	/**
	 * @return the wan_lpm_routes
	 */
	public WanLpmRoutes getWan_lpm_routes() {
		return wan_lpm_routes;
	}

	/**
	 * @param wan_lpm_routes
	 *            the wan_lpm_routes to set
	 */
	public void setWan_lpm_routes(WanLpmRoutes wan_lpm_routes) {
		this.wan_lpm_routes = wan_lpm_routes;
	}

	/**
	 * @return the vrf
	 */
	public String getVrf() {
		return vrf;
	}

	/**
	 * @param vrf
	 *            the vrf to set
	 */
	public void setVrf(String vrf) {
		this.vrf = vrf;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
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
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol
	 *            the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
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
	 * @return the svis
	 */
	public int getSvis() {
		return svis;
	}

	/**
	 * @param svis
	 *            the svis to set
	 */
	public void setSvis(int svis) {
		this.svis = svis;
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

	/**
	 * @return the ownershipId
	 */
	public int getOwnershipId() {
		return ownershipId;
	}

	/**
	 * @param ownershipId the ownershipId to set
	 */
	public void setOwnershipId(int ownershipId) {
		this.ownershipId = ownershipId;
	}

}
