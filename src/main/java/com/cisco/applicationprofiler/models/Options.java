/**
 * 
 */
package com.cisco.applicationprofiler.models;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Mahesh
 *
 */
@XmlRootElement
public class Options {

	private Boolean markdown_output = false;
	private String apic_version = "1.0.4.0";
	private String switch_version = "11.0";
	private String apic_model = "m3";
	private String switch_model = "N9K-C9396PX";

	public Boolean getMarkdown_output() {
		return markdown_output;
	}

	public void setMarkdown_output(Boolean markdown_output) {
		this.markdown_output = markdown_output;
	}

	public String getApic_version() {
		return apic_version;
	}

	public void setApic_version(String apic_version) {
		this.apic_version = apic_version;
	}

	public String getSwitch_version() {
		return switch_version;
	}

	public void setSwitch_version(String switch_version) {
		this.switch_version = switch_version;
	}

	public String getApic_model() {
		return apic_model;
	}

	public void setApic_model(String apic_model) {
		this.apic_model = apic_model;
	}

	public String getSwitch_model() {
		return switch_model;
	}

	public void setSwitch_model(String switch_model) {
		this.switch_model = switch_model;
	}

}
