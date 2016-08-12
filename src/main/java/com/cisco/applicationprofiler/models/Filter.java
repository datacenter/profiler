package com.cisco.applicationprofiler.models;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.cisco.applicationprofiler.domain.FilterEntry;
@XmlRootElement
public class Filter {

	private int id;

	private String name;

	private String description;

	private List<FilterEntry> filterEntry;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<FilterEntry> getFilterEntry() {
		return filterEntry;
	}

	public void setFilterEntry(List<FilterEntry> filterEntry) {
		this.filterEntry = filterEntry;
	}
}
