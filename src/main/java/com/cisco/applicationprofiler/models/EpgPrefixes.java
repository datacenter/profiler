/**
 * 
 */
package com.cisco.applicationprofiler.models;

/**
 * @author Mahesh
 *
 */
public class EpgPrefixes {

	private int ipv4;

	public EpgPrefixes()
	{}
	
	public EpgPrefixes(int param)
	{
		ipv4 = param;
	}
	/**
	 * @return the ipv4
	 */
	public int getIpv4() {
		return ipv4;
	}

	/**
	 * @param ipv4
	 *            the ipv4 to set
	 */
	public void setIpv4(int ipv4) {
		this.ipv4 = ipv4;
	}

}
