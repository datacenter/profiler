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
public class App {
	
	@XmlAttribute
	private String name;

	@XmlElement(name = "fvAEPg")
	private List<Epg> epgs;

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
	 * @return the epgs
	 */
	public List<Epg> getEpgs() {
		return epgs;
	}

	/**
	 * @param epgs
	 *            the epgs to set
	 */
	public void setEpgs(List<Epg> epgs) {
		this.epgs = epgs;
	}
}
