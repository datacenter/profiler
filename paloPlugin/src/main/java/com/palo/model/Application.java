/**
 * 
 */
package com.palo.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Mahesh
 *
 */
@XmlRootElement(name = "application")
@XmlAccessorType(XmlAccessType.FIELD)
public class Application {
	@XmlElement(name = "entry")
	private List<Entry> entry;

	/**
	 * @return the entry
	 */

    
	public List<Entry> getEntry() {
		return entry;
	}

	/**
	 * @param entry the entry to set
	 */
	public void setEntry(List<Entry> entry) {
		this.entry = entry;
	}
}
