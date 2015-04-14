package com.yisa.qiqilogin;

public class TBProductProperty {
	private String mNameID;
	private String mName;

	private String mValue;
	private String mValueID;

	public String getNameID() {
		return mNameID;
	}

	public void setNameID(String nameID) {
		mNameID = nameID;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getValue() {
		return mValue;
	}

	public void setValue(String value) {
		mValue = value;
	}

	public String getValueID() {
		return mValueID;
	}

	public void setValueID(String valueID) {
		mValueID = valueID;
	}
	
	@Override
	public String toString(){
		return mName +"(" + mNameID +")" + " : " + mValue+"("+ mValueID +")" ;
	}
}
