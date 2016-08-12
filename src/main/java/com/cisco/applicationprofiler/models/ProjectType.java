/**
 * 
 */
package com.cisco.applicationprofiler.models;

/**
 * @author Mahesh
 *
 */
public enum ProjectType {
	ACI("ACI"), NEXUS9000("Nexus 9000");
	private String value;

	public String getValue() {
		return value;
	}

	private ProjectType(String s) {
		this.value = s;
	}

	public static ProjectType fromString(String text) {
		if (text != null) {
			for (ProjectType projectType : ProjectType.values()) {
				if (text.equalsIgnoreCase(projectType.value)) {
					return projectType;
				}
			}
		}
		return null;
	}
}
