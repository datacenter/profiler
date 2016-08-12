package com.cisco.applicationprofiler.view;

public class ViewNode implements Comparable<ViewNode> {
	
	private int id;
	private ViewModel dataItem;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ViewModel getDataItem() {
		return dataItem;
	}
	public void setDataItem(ViewModel dataItem) {
		this.dataItem = dataItem;
	}

   @Override
    public int compareTo(ViewNode compareNode) {
	   if(null == compareNode) return 0;
	   if(null == compareNode.getDataItem())return 0;
	   if(null == compareNode.getDataItem().getUiData().getX())return 0;
	   
        Float compareX=((ViewNode)compareNode).getDataItem().getUiData().getX();
        /* For Ascending order*/
        return (int)(this.getDataItem().getUiData().getX()-compareX);
    }
}
