/**
 * 
 */
package com.palo.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Mahesh
 *
 */
@XmlRootElement
public class Entry {
	private String id;
	private String name;
	private Default default1;
	private UseApplications useApplications;
	private String description;
	/**
	 * @return the default1
	 */
	@XmlElement(name="default")
	public Default getDefault1() {
		return default1;
	}
	/**
	 * @param default1 the default1 to set
	 */
	public void setDefault1(Default default1) {
		this.default1 = default1;
	}
	/**
	 * @return the id
	 */
	@XmlAttribute(name="id")
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	@XmlAttribute(name="name")
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
	 * @return the description
	 */
	@XmlElement(name="description")
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the useApplications
	 */
	@XmlElement(name="use-applications")
	public UseApplications getUseApplications() {
		return useApplications;
	}
	/**
	 * @param useApplications the useApplications to set
	 */
	public void setUseApplications(UseApplications useApplications) {
		this.useApplications = useApplications;
	}
}
