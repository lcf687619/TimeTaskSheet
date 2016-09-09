package com.mission.schedule.bean;

import java.io.Serializable;

public class TagCommandBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private String ctgId;
	private String ctgType;
	private String ctgOrder;
	private String ctgUpdateState;
	private String ctgText;
	private String ctgColor;
	private String ctgDesc;
	private String ctgCount;
	
	private boolean check;
//	private LeftRightDeleteView view;
	
//	public LeftRightDeleteView getView() {
//        return view;
//    }
//    public void setView(LeftRightDeleteView view) {
//        this.view = view;
//    }
	
	public String getCtgId() {
		return ctgId;
	}
	public void setCtgId(String ctgId) {
		this.ctgId = ctgId;
	}
	public String getCtgType() {
		return ctgType;
	}
	public void setCtgType(String ctgType) {
		this.ctgType = ctgType;
	}
	public String getCtgOrder() {
		return ctgOrder;
	}
	public void setCtgOrder(String ctgOrder) {
		this.ctgOrder = ctgOrder;
	}
	public String getCtgUpdateState() {
		return ctgUpdateState;
	}
	public void setCtgUpdateState(String ctgUpdateState) {
		this.ctgUpdateState = ctgUpdateState;
	}
	public String getCtgText() {
		return ctgText;
	}
	public void setCtgText(String ctgText) {
		this.ctgText = ctgText;
	}
	public String getCtgColor() {
		return ctgColor;
	}
	public void setCtgColor(String ctgColor) {
		this.ctgColor = ctgColor;
	}
	public String getCtgDesc() {
		return ctgDesc;
	}
	public void setCtgDesc(String ctgDesc) {
		this.ctgDesc = ctgDesc;
	}
	public String getCtgCount() {
		return ctgCount;
	}
	public void setCtgCount(String ctgCount) {
		this.ctgCount = ctgCount;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
}
