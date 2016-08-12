/**
 * 
 */
package com.cisco.applicationprofiler.apic.models;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Mahesh
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Epg {
	@XmlAttribute
	private String name;
	
	@XmlElement(name="fvRsProv")
	private List<ProviderConsumer> provider;
	
	@XmlElement(name="fvRsCons")
	private List<ProviderConsumer> consumer;
	
	@XmlElement(name="fvRsBd")
	private BdEpgMap bdEpgMap;
	

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
	 * @return the provider
	 */
	public List<ProviderConsumer> getProvider() {
		return provider;
	}

	/**
	 * @param provider the provider to set
	 */
	public void setProvider(List<ProviderConsumer> provider) {
		this.provider = provider;
	}

	/**
	 * @return the consumer
	 */
	public List<ProviderConsumer> getConsumer() {
		return consumer;
	}

	/**
	 * @param consumer the consumer to set
	 */
	public void setConsumer(List<ProviderConsumer> consumer) {
		this.consumer = consumer;
	}

	/**
	 * @return the bdEpgMap
	 */
	public BdEpgMap getBdEpgMap() {
		return bdEpgMap;
	}

	/**
	 * @param bdEpgMap the bdEpgMap to set
	 */
	public void setBdEpgMap(BdEpgMap bdEpgMap) {
		this.bdEpgMap = bdEpgMap;
	}
}
