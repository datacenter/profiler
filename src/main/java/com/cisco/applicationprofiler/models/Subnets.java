/**
 * 
 */
package com.cisco.applicationprofiler.models;

/**
 * @author Mahesh
 *
 */
public class Subnets {
	private int ipv4;

	/**
	 * @return the ipv4
	 */
	public Subnets()
	{}
	
	public Subnets(int param)
	{
		ipv4 = param;
	}
	public int getIpv4() {
		return ipv4;
	}

	/**
	 * @param ipv4 the ipv4 to set
	 */
	public void setIpv4(int ipv4) {
		this.ipv4 = ipv4;
	}

	

}
