/**
 * 
 */
package com.palo.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Mahesh
 *
 */
@XmlRootElement
public class Port {
	
	private String member;

	/**
	 * @return the member
	 */
	@XmlElement(name = "member")
	public String getMember() {
		return member;
	}

	/**
	 * @param member the member to set
	 */
	public void setMember(String member) {
		this.member = member;
	}
}
