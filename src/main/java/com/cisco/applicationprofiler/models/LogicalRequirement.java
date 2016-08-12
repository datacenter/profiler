/**
 * 
 */
package com.cisco.applicationprofiler.models;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mahesh
 *
 */
public class LogicalRequirement {
	private Options options;
	private List<Tenant> tenants;
	private List<Leaf> leafs;
	
	public LogicalRequirement()
	{
		tenants = new ArrayList<Tenant>();
		leafs = new ArrayList<Leaf>();
	}
	

	public List<Leaf> getLeafs() {
		return leafs;
	}
	public void setLeafs(List<Leaf> leafs) {
		this.leafs = leafs;
	}

	public Options getOptions() {
		return options;
	}
	public void setOptions(Options options) {
		this.options = options;
	}
	public List<Tenant> getTenants() {
		return tenants;
	}
	public void setTenants(List<Tenant> tenants) {
		this.tenants = tenants;
	}
	
}
