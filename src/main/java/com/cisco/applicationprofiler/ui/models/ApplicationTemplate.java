package com.cisco.applicationprofiler.ui.models;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.cisco.applicationprofiler.util.ACISizerConstant;

public class ApplicationTemplate extends Template{

	@NotEmpty
	private String name;
	@Min(1)
	private int noOfInstances;

	private String model; // "No Template or Flat or 2 tier or 3 tier",

	private ApplicationConfiguration configuration ;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the noOfInstances
	 */
	public int getNoOfInstances() {
		return noOfInstances;
	}

	/**
	 * @param noOfInstances the noOfInstances to set
	 */
	public void setNoOfInstances(int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the configuration
	 */
	public ApplicationConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(ApplicationConfiguration configuration) {
		this.configuration = configuration;
	}

	public ApplicationTemplate()
	{
		this.setTemplateType(ACISizerConstant._app);
	}
		
	
}
