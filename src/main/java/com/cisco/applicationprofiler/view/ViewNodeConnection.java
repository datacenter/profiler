package com.cisco.applicationprofiler.view;

public class ViewNodeConnection {
	private int id;
	private ViewConnectionItem dataItem = new ViewConnectionItem();
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ViewConnectionItem getDataItem() {
		return dataItem;
	}
	public void setDataItem(ViewConnectionItem dataItem) {
		this.dataItem = dataItem;
	}
}
