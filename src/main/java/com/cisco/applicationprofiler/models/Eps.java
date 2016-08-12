/**
 * 
 */
package com.cisco.applicationprofiler.models;

/**
 * @author Mahesh
 *
 */
public class Eps {
	private int mac;

	public Eps()
	{}
	public Eps(int endPoint)
	{
		mac = endPoint;
	}
	/**
	 * @return the mac
	 */
	public int getMac() {
		return mac;
	}

	/**
	 * @param mac
	 *            the mac to set
	 */
	public void setMac(int mac) {
		this.mac = mac;
	}
}
