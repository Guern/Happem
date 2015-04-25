package com.happem.happem;

import java.util.ArrayList;
import java.util.Date;

public class Parent_Pref
{
	private Date data;
	
	private String checkedtype;
	
	private boolean checked;
	private ArrayList<Child_rr> children;
	
	public Date getData(){
		return data;
	}
	
	public void setData(Date string){
		data=string;
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
	
	public ArrayList<Child_rr> getChildren()
	{
		return children;
	}
	
	public void setChildren(ArrayList<Child_rr> arrayList)
	{
		this.children = arrayList;
	}
}
