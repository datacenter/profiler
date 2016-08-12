/**
 * 
 */
package com.cisco.applicationprofiler.apic.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Mahesh
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Bd {

	@XmlAttribute
	private String name;

	@XmlElement(name = "fvRsCtx")
	private BdVrfMap bdVrfMap;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the bdVrfMap
	 */
	public BdVrfMap getBdVrfMap() {
		return bdVrfMap;
	}

	/**
	 * @param bdVrfMap
	 *            the bdVrfMap to set
	 */
	public void setBdVrfMap(BdVrfMap bdVrfMap) {
		this.bdVrfMap = bdVrfMap;
	}
}
