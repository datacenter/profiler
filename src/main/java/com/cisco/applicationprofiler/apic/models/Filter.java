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
public class Filter {
	@XmlAttribute
	private String name;
	
	@XmlElement(name="vzEntry")
	private List<FilterEntry> filterEntry;

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
	 * @return the filterEntry
	 */
	public List<FilterEntry> getFilterEntry() {
		return filterEntry;
	}

	/**
	 * @param filterEntry the filterEntry to set
	 */
	public void setFilterEntry(List<FilterEntry> filterEntry) {
		this.filterEntry = filterEntry;
	}

	
}
