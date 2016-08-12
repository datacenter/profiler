/**
 * 
 */
package com.cisco.applicationprofiler.apic.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Mahesh
 *
 */
@XmlRootElement(name = "aaaUser")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApicUser {
	
	@XmlAttribute
	private String name;
	
	@XmlAttribute
	private String pwd;

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
	 * @return the pwd
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * @param pwd
	 *            the pwd to set
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}
