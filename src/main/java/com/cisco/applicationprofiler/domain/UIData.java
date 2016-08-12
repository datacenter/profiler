package com.cisco.applicationprofiler.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UIData {

	private Float x;
	private Float y;
	public UIData()
	{
		x = (float) 0;
		y = (float) 0;
	}
	
	public UIData(float paramX, float paramY)
	{
		this.x = paramX;
		this.y = paramY;
	}
	public void copyUIData(UIData src)
	{
		x = src.x;
		y = src.y;
	}
	public Float getX() {
		return x;
	}
	public void setX(Float x) {
		this.x = x;
	}
	public Float getY() {
		return y;
	}
	public void setY(Float y) {
		this.y = y;
	}
	
	public void setXNull()
	{
		x = null;
	}
	
	public void setYNull()
	{
		y = null;
	}
}
