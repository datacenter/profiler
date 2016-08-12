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
public class Default {

	private Port port;
	
	
	private String identByIpProtocol;

	/**
	 * @return the port
	 */
	@XmlElement(name = "port")
	public Port getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(Port port) {
		this.port = port;
	}

	/**
	 * @return the identByIpProtocol
	 */
	@XmlElement(name = "ident-by-ip-protocol")
	public String getIdentByIpProtocol() {
		return identByIpProtocol;
	}

	/**
	 * @param identByIpProtocol the identByIpProtocol to set
	 */
	public void setIdentByIpProtocol(String identByIpProtocol) {
		this.identByIpProtocol = identByIpProtocol;
	}

}
