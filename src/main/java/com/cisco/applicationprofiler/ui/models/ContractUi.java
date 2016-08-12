/**
 * 
 */
package com.cisco.applicationprofiler.ui.models;

import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;

import com.cisco.applicationprofiler.models.Subject;
import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Mahesh
 *
 */
@XmlRootElement
public class ContractUi extends AciSizerModelUi {
	public static final boolean CONTRACT_PROVIDER_ENFORCED_DEFAULT = true;
	public static final boolean CONTRACT_CONSUMER_ENFORCED_DEFAULT = false;
	public static final int CONTRACT_NO_OF_FILTERS_DEFAULT = 1;
	private int noOfFilters;
	private boolean providerEnforced;
	private boolean consumerEnforced;
	@Min(1)
	private int providerId;
	@Min(1)
	private int consumerId;
	@NotEmpty
	private String providerType;
	@NotEmpty
	private String consumerType;
	private String providerName;
	private String consumerName;
	private int noOfInstances;
	private String configName;
	private List<Subject> subjects = new ArrayList<Subject>();



	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public List<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}

	public int getNoOfInstances() {
		return noOfInstances;
	}

	public void setNoOfInstances(int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}

	public ContractUi() {
		type = ACISizerConstant._contract;
	}

	public ContractUi(int appInstance) {
		type = ACISizerConstant._contract;
		setNoOfInstances(appInstance);
	}

	/**
	 * @return the noOfFilters
	 */
	public int getNoOfFilters() {
		return noOfFilters;
	}

	/**
	 * @param noOfFilters
	 *            the noOfFilters to set
	 */
	public void setNoOfFilters(int noOfFilters) {
		this.noOfFilters = noOfFilters;
	}

	/**
	 * @return the providerEnforced
	 */
	public boolean isProviderEnforced() {
		return providerEnforced;
	}

	/**
	 * @param providerEnforced
	 *            the providerEnforced to set
	 */
	public void setProviderEnforced(boolean providerEnforced) {
		this.providerEnforced = providerEnforced;
	}

	/**
	 * @return the consumerEnforced
	 */
	public boolean isConsumerEnforced() {
		return consumerEnforced;
	}

	/**
	 * @param consumerEnforced
	 *            the consumerEnforced to set
	 */
	public void setConsumerEnforced(boolean consumerEnforced) {
		this.consumerEnforced = consumerEnforced;
	}

	/**
	 * @return the providerId
	 */
	public int getProviderId() {
		return providerId;
	}

	/**
	 * @param providerId
	 *            the providerId to set
	 */
	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	/**
	 * @return the consumerId
	 */
	public int getConsumerId() {
		return consumerId;
	}

	/**
	 * @param consumerId
	 *            the consumerId to set
	 */
	public void setConsumerId(int consumerId) {
		this.consumerId = consumerId;
	}

	/**
	 * @return the providerName
	 */
	public String getProviderName() {
		return providerName;
	}

	/**
	 * @param providerName
	 *            the providerName to set
	 */
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	/**
	 * @return the consumerName
	 */
	public String getConsumerName() {
		return consumerName;
	}

	/**
	 * @param consumerName
	 *            the consumerName to set
	 */
	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}

	/**
	 * @return the providerType
	 */
	public String getProviderType() {
		return providerType;
	}

	/**
	 * @param providerType
	 *            the providerType to set
	 */
	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	/**
	 * @return the consumerType
	 */
	public String getConsumerType() {
		return consumerType;
	}

	/**
	 * @param consumerType
	 *            the consumerType to set
	 */
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}

	@Override
	public String toString() {
		return ACISizerConstant.NAME + this.name;
	}
	
	public static ContractUi getContractDefault(){
		ContractUi contractUi=new ContractUi();
		contractUi.setNoOfFilters(CONTRACT_NO_OF_FILTERS_DEFAULT);
		contractUi.setConsumerEnforced(CONTRACT_CONSUMER_ENFORCED_DEFAULT);
		contractUi.setProviderEnforced(CONTRACT_PROVIDER_ENFORCED_DEFAULT);
		return contractUi;
	}

}
