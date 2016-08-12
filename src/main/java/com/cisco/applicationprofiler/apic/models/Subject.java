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
public class Subject {

	@XmlAttribute
	private String name;
	
	@XmlElement(name="vzRsSubjFiltAtt")
	private List<SubjectAttributes> subjectAttributes;
	
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
	 * @return the subjectAttributes
	 */
	public List<SubjectAttributes> getSubjectAttributes() {
		return subjectAttributes;
	}

	/**
	 * @param subjectAttributes the subjectAttributes to set
	 */
	public void setSubjectAttributes(List<SubjectAttributes> subjectAttributes) {
		this.subjectAttributes = subjectAttributes;
	}

}
