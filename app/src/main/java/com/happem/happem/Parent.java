package com.happem.happem;

import java.util.ArrayList;

public class Parent
{
	private String id;
	private String posizione;
	private String azienda;
	
	private String checkedtype;
	
	private boolean checked;
	private ArrayList<Child> children;
	
	public String getId(){
		return id;
	}
	
	public void setId(String i){
		id=i;
	}
	
	public String getPosizione()
	{
		return posizione;
	}
	
	public void setPosizione(String name)
	{
		this.posizione = name;
	}
	public String getAzienda()
	{
		return azienda;
	}
	
	public void setAzienza(String text1)
	{
		this.azienda = text1;
	}
	
	
	public String getCheckedType()
	{
		return checkedtype;
	}
	
	public void setCheckedType(String checkedtype)
	{
		this.checkedtype = checkedtype;
	}
	
	
	public boolean isChecked()
	{
		return checked;
	}
	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}
	
	public ArrayList<Child> getChildren()
	{
		return children;
	}
	
	public void setChildren(ArrayList<Child> children)
	{
		this.children = children;
	}
}
