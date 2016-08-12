/**
 * CopyRight @MapleLabs
 */
package com.cisco.applicationprofiler.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import com.cisco.applicationprofiler.util.ACISizerConstant;

@XmlRootElement
public class Contract extends ACISizerModel implements Comparable<Contract> {

	private String configName;

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	private List<Subject> subjects = new ArrayList<Subject>();

	public List<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}

	private String providerId;

	/**
	 * @return the providerId
	 */
	public String getProviderId() {
		return providerId;
	}

	/**
	 * @param providerId
	 *            the providerId to set
	 */
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	private String consumerId;

	/**
	 * @return the consumerId
	 */
	public String getConsumerId() {
		return consumerId;
	}

	/**
	 * @param consumerId
	 *            the consumerId to set
	 */
	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	private String providerType;
	private String consumerType;
	private int count = 1;
	private String appName;

	public Contract() {
	}

	public Contract(int id, String name, String appName) {

		this.name = "" + id;
		this.displayName = name;
		this.appName = appName;
	}

	public Contract(String id, String name, String appName) {

		this.name = "" + id;
		this.displayName = name;
		this.appName = appName;
	}

	@Override
	public boolean equals(Object object) {
		boolean result = false;
		if (object == null || object.getClass() != getClass()) {
			result = false;
		} else {
			Contract other = (Contract) object;
			return (this.displayName.equals(other.getDisplayName()) && this.appName.equals(other.appName));
		}
		return result;
	}

	@Override
	public int hashCode() {
		return this.getDisplayName().hashCode() + this.appName.hashCode();
	}

	public void copyContract(Contract contract) {
		this.subjects = contract.subjects;
		this.consumerEnforced = contract.consumerEnforced;
		this.consumerId = contract.consumerId;
		this.consumerType = contract.consumerType;
		this.prefixes = contract.prefixes;
		this.providerEnforced = contract.providerEnforced;
		this.providerId = contract.providerId;
		this.providerType = contract.providerType;
		this.suffixes = contract.suffixes;
		this.uiData = contract.uiData;
		this.unique_filters = contract.unique_filters;
		this.uiData.copyUIData(contract.getUiData());
	}

	public void setUiDataNull() {
		if (null == uiData)
			uiData = new UIData();
		uiData.setXNull();
		uiData.setYNull();
	}

	/**
	 * @return the prefixes
	 */
	public Set<String> getPrefixes() {
		return prefixes;
	}

	/**
	 * @param prefixes
	 *            the prefixes to set
	 */
	public void setPrefixes(Set<String> prefixes) {
		this.prefixes = prefixes;
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

	private Set<String> prefixes;
	private Set<String> suffixes;
	private int unique_filters;
	private boolean providerEnforced;
	private boolean consumerEnforced;

	public String getProviderType() {
		return providerType;
	}

	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public String getConsumerType() {
		return consumerType;
	}

	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}

	public boolean getProviderEnforced() {
		return providerEnforced;
	}

	public void setProviderEnforced(boolean providerEnforced) {
		this.providerEnforced = providerEnforced;
	}

	public boolean getConsumerEnforced() {
		return consumerEnforced;
	}

	public void setConsumerEnforced(boolean consumerEnforced) {
		this.consumerEnforced = consumerEnforced;
	}

	/**
	 * @return the unique_filters
	 */
	public int getUnique_filters() {
		return unique_filters;
	}

	/**
	 * @param unique_filters
	 *            the unique_filters to set
	 */
	public void setUnique_filters(int unique_filters) {
		this.unique_filters = unique_filters;
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
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}

	@Override
	public int compareTo(Contract o) {
		return (int) (this.uiData.getX() - o.getUiData().getX());
	}

	@Override
	public String toString() {
		String temp = ACISizerConstant.NAME + this.name + this.displayName;
		return temp;
	}

}
