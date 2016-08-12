package com.cisco.applicationprofiler.models;

import java.util.ArrayList;
import java.util.List;

public class Leaf {
	
	private String name;
	private int count;
	private List<String> labels;
	private String model;
	
	public Leaf()
	{
		labels = new ArrayList<String>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<String> getLabels() {
		return labels;
	}
	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}

}
