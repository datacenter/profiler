/**
 * 
 */
package com.cisco.applicationprofiler.logical.services;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mahesh
 *
 */
public class LoggingHelperModel {

	public static final String SEPERATOR = ",";
	private long inputGenerationStartTime;
	private long inputGenerationStopTime;
	private long inputGenerationDiffTime;

	private long executionStartTime;
	private long executionStopTime;
	private long executionDiffTime;

	private long resultProcessingStartTime;
	private long resultProcessingStopTime;
	private long resultProcessingDiffTime;
	
	private long totalTime;

	/**
	 * @return the resultProcessingStartTime
	 */
	public long getResultProcessingStartTime() {
		return resultProcessingStartTime;
	}

	/**
	 * @param resultProcessingStartTime
	 *            the resultProcessingStartTime to set
	 */
	public void setResultProcessingStartTime(long resultProcessingStartTime) {
		this.resultProcessingStartTime = resultProcessingStartTime;
	}

	/**
	 * @return the resultProcessingStopTime
	 */
	public long getResultProcessingStopTime() {
		return resultProcessingStopTime;
	}

	/**
	 * @param resultProcessingStopTime
	 *            the resultProcessingStopTime to set
	 */
	public void setResultProcessingStopTime(long resultProcessingStopTime) {
		this.resultProcessingStopTime = resultProcessingStopTime;
	}

	/**
	 * @return the resultProcessingDiffTime
	 */
	public long getResultProcessingDiffTime() {
		return resultProcessingDiffTime;
	}

	/**
	 * @param resultProcessingDiffTime
	 *            the resultProcessingDiffTime to set
	 */
	public void setResultProcessingDiffTime(long resultProcessingDiffTime) {
		this.resultProcessingDiffTime = resultProcessingDiffTime;
	}

	private int tenantCount;
	private int epgCount;
	private int contractCount;

	/**
	 * @return the inputGenerationStartTime
	 */
	public long getInputGenerationStartTime() {
		return inputGenerationStartTime;
	}

	/**
	 * @param inputGenerationStartTime
	 *            the inputGenerationStartTime to set
	 */
	public void setInputGenerationStartTime(long inputGenerationStartTime) {
		this.inputGenerationStartTime = inputGenerationStartTime;
	}

	/**
	 * @return the inputGenerationStopTime
	 */
	public long getInputGenerationStopTime() {
		return inputGenerationStopTime;
	}

	/**
	 * @param inputGenerationStopTime
	 *            the inputGenerationStopTime to set
	 */
	public void setInputGenerationStopTime(long inputGenerationStopTime) {
		this.inputGenerationStopTime = inputGenerationStopTime;
	}

	/**
	 * @return the inputGenerationDiffTime
	 */
	public long getInputGenerationDiffTime() {
		return inputGenerationDiffTime;
	}

	/**
	 * @param inputGenerationDiffTime
	 *            the inputGenerationDiffTime to set
	 */
	public void setInputGenerationDiffTime(long inputGenerationDiffTime) {
		this.inputGenerationDiffTime = inputGenerationDiffTime;
	}

	/**
	 * @return the executionStartTime
	 */
	public long getExecutionStartTime() {
		return executionStartTime;
	}

	/**
	 * @param executionStartTime
	 *            the executionStartTime to set
	 */
	public void setExecutionStartTime(long executionStartTime) {
		this.executionStartTime = executionStartTime;
	}

	/**
	 * @return the executionStopTime
	 */
	public long getExecutionStopTime() {
		return executionStopTime;
	}

	/**
	 * @param executionStopTime
	 *            the executionStopTime to set
	 */
	public void setExecutionStopTime(long executionStopTime) {
		this.executionStopTime = executionStopTime;
	}

	/**
	 * @return the executionDiffTime
	 */
	public long getExecutionDiffTime() {
		return executionDiffTime;
	}

	/**
	 * @param executionDiffTime
	 *            the executionDiffTime to set
	 */
	public void setExecutionDiffTime(long executionDiffTime) {
		this.executionDiffTime = executionDiffTime;
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

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		this.inputGenerationDiffTime = inputGenerationStopTime - inputGenerationStartTime;
		this.executionDiffTime = executionStopTime - executionStartTime;
		this.resultProcessingDiffTime = resultProcessingStopTime - resultProcessingStartTime;
		this.totalTime=resultProcessingStopTime-inputGenerationStartTime;

		return this.contractCount + SEPERATOR + this.epgCount + SEPERATOR + this.tenantCount + SEPERATOR
				+ sdf.format(new Date(inputGenerationStartTime)) + SEPERATOR+sdf.format(new Date(inputGenerationStopTime))
						+ SEPERATOR + inputGenerationDiffTime + SEPERATOR + sdf.format(new Date(executionStartTime))
						+ SEPERATOR + sdf.format(new Date(executionStopTime)) + SEPERATOR + this.executionDiffTime
						+ SEPERATOR + sdf.format(new Date(resultProcessingStartTime)) + SEPERATOR
						+ sdf.format(new Date(resultProcessingStopTime))+SEPERATOR+this.resultProcessingDiffTime+SEPERATOR+totalTime;
	}

}
