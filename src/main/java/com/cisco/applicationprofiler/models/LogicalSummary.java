/**
 * CopyRight @MapleLabs
 */
package com.cisco.applicationprofiler.models;



import javax.xml.bind.annotation.XmlRootElement;



/**
 * @author Deepa
 *
 */
@XmlRootElement
public class LogicalSummary {
	
    private int tenantCount;
    private int vrfCount;
    private int bdCount;
    private int l3OutCount;
    private int appCount;
    private int epgCount;
    private int endPoints;
    private int contractCount;
    private int eppCount;
    private int serviceChainCount;
    private int l2OutCount;
    private int connectionDefCount;
    
    private int uniqueId;
    private int sharedResourceCount;
	private int leafCountBorder;
	private int leafCountRegular;
	private int filterCount;

	public int getFilterCount() {
		return filterCount;
	}

	public void setFilterCount(int filterCount) {
		this.filterCount = filterCount;
	}
	
	public int getLeafCountBorder() {
		return leafCountBorder;
	}

	public void setLeafCountBorder(int leafCountBorder) {
		this.leafCountBorder = leafCountBorder;
	}

	public int getLeafCountRegular() {
		return leafCountRegular;
	}

	public void setLeafCountRegular(int leafCountRegular) {
		this.leafCountRegular = leafCountRegular;
	}

	public void incrementSharedResourceCount() {
		setSharedResourceCount(getSharedResourceCount() + 1);
	}

	public void decrementSharedResourceCount() {
		setSharedResourceCount(getSharedResourceCount() - 1);;
	}

	public int generateUniqueId() {
		return ++uniqueId;
	}

	public int generateUniqueId(int setter)
	{
		if(uniqueId == 0)
			uniqueId = setter+100;
		
		return ++uniqueId;
	}
	
	public int getLastUsedUniqueId()
	{
		return uniqueId;
	}
	
	/**
	 * @return the tenantCount
	 */
	public int getTenantCount() {
		return tenantCount;
	}

	/**
	 * @param tenantCount
	 *            the tenantCount to set
	 */
	public void setTenantCount(int tenantCount) {
		this.tenantCount = tenantCount;
	}
	
	/**
	 * @return the vrfCount
	 */
	public int getVrfCount() {
		return vrfCount;
	}

	/**
	 * @param vrfCount
	 *            the vrfCount to set
	 */
	public void setVrfCount(int vrfCount) {
		this.vrfCount = vrfCount;
	}

	
	/**
	 * @return the bdCount
	 */
	public int getBdCount() {
		return bdCount;
	}

	/**
	 * @param bdCount
	 *            the bdCount to set
	 */
	public void setBdCount(int bdCount) {
		this.bdCount = bdCount;
	}

	
	/**
	 * @return the l3OutCount
	 */
	public int getL3OutCount() {
		return l3OutCount;
	}

	/**
	 * @param l3OutCount
	 *            the l3OutCount to set
	 */
	public void setL3OutCount(int l3OutCount) {
		this.l3OutCount = l3OutCount;
	}
	
	/**
	 * @return the appCount
	 */
	public int getAppCount() {
		return appCount;
	}

	/**
	 * @param appCount
	 *            the appCount to set
	 */
	public void setAppCount(int appCount) {
		this.appCount = appCount;
	}
	
	/**
	 * @return the epgCount
	 */
	public int getEpgCount() {
		return epgCount;
	}

	/**
	 * @param epgCount
	 *            the epgCount to set
	 */
	public void setEpgCount(int epgCount) {
		this.epgCount = epgCount;
	}
	
	
	/**
	 * @return the contractCount
	 */
	public int getContractCount() {
		return contractCount;
	}

	/**
	 * @param contractCount
	 *            the contractCount to set
	 */
	public void setContractCount(int contractCount) {
		this.contractCount = contractCount;
	}

	/**
	 * @return the eppCount
	 */
	public int getEppCount() {
		return eppCount;
	}

	/**
	 * @param eppCount
	 *            the eppCount to set
	 */
	public void setEppCount(int eppCount) {
		this.eppCount = contractCount;
	}
	
	/**
	 * @return the serviceChainCount
	 */
	public int getServiceChainCount() {
		return serviceChainCount;
	}

	/**
	 * @param serviceChainCount
	 *            the serviceChainCount to set
	 */
	public void setServiceChainCount(int serviceChainCount) {
		this.serviceChainCount = serviceChainCount;
	}
	
	/**
	 * @return the connectionDefCount
	 */
	public int getConnectionDefCount() {
		return connectionDefCount;
	}

	/**
	 * @param connectionDefCount
	 *            the connectionDefCount to set
	 */
	public void setConnectionDefCount(int connectionDefCount) {
		this.connectionDefCount = connectionDefCount;
	}
	
	/**
	 * @return the l2OutCount
	 */
	public int getL2OutCount() {
		return l2OutCount;
	}

	/**
	 * @param l2OutCount
	 *            the l2OutCount to set
	 */
	public void setL2OutCount(int l2OutCount) {
		this.l2OutCount = l2OutCount;
	}

	/**
	 * @return the sharedResourceCount
	 */
	public int getSharedResourceCount() {
		return sharedResourceCount;
	}

	/**
	 * @param sharedResourceCount the sharedResourceCount to set
	 */
	public void setSharedResourceCount(int sharedResourceCount) {
		this.sharedResourceCount = sharedResourceCount;
	}

	/**
	 * @return the endPoints
	 */
	public int getEndPoints() {
		return endPoints;
	}

	/**
	 * @param endPoints the endPoints to set
	 */
	public void setEndPoints(int endPoints) {
		this.endPoints = endPoints;
	}
	
	
	/**
	 * @return the lastUsedTenantId
	 */
	
}
