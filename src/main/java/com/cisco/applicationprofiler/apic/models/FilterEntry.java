/**
 * 
 */
package com.cisco.applicationprofiler.apic.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author Mahesh
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FilterEntry {

	@XmlAttribute
	private String name;

	@XmlAttribute(name = "sFromPort")
	private String srcFromPort;

	@XmlAttribute(name = "sToPort")
	private String srcToPort;

	@XmlAttribute(name = "dFromPort")
	private String destFromPort;

	@XmlAttribute(name = "dToPort")
	private String destToPort;

	@XmlAttribute(name = "prot")
	private String protocol;

	@XmlAttribute(name = "etherT")
	private String etherType;

	private FilterEntry(String name, String srcFromPort, String srcToPort, String destFromPort, String destToPort,
			String protocol, String etherType) {
		this.name = name;
		this.srcFromPort = srcFromPort;
		this.srcToPort = srcToPort;
		this.destFromPort = destFromPort;
		this.destToPort = destToPort;
		this.protocol = protocol;
		this.etherType = etherType;
	}

	public static class FilterEntryBuilder {
		private String name;
		private String srcFromPort;
		private String srcToPort;
		private String destFromPort;
		private String destToPort;
		private String protocol;
		private String etherType;

		public FilterEntryBuilder(String name) {
			this.name = name;
		}

		public FilterEntryBuilder srcFromPort(String srcFromPort) {
			this.srcFromPort = srcFromPort;
			return this;
		}

		public FilterEntryBuilder srcToPort(String srcToPort) {
			this.srcToPort = srcToPort;
			return this;
		}

		public FilterEntryBuilder destFromPort(String destFromPort) {
			this.destFromPort = destFromPort;
			return this;
		}

		public FilterEntryBuilder destToPort(String destToPort) {
			this.destToPort = destToPort;
			return this;
		}

		public FilterEntryBuilder protocol(String protocol) {
			this.protocol = protocol;
			return this;
		}

		public FilterEntryBuilder etherType(String etherType) {
			this.etherType = etherType;
			return this;
		}

		public FilterEntry createFilterEntry() {
			return new FilterEntry(name, srcFromPort, srcToPort, destFromPort, destToPort, protocol, etherType);
		}

	}

}
