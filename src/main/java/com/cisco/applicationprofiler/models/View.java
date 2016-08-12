/**
 * 
 */
package com.cisco.applicationprofiler.models;

/**
 * @author Mahesh
 *
 */
public class View {
	public interface Project extends User{}
	public interface User{}
	public interface FilterEntry extends Filter{}
	public interface Filter extends Subject {}
	public interface Subject extends Contract {}
	public interface Contract {}
	public interface Plugin{}
	public interface AuditInfo{}
	public interface Model{}
	public interface Device{}{}
	

}