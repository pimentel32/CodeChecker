package com.checkerWeb.checker.model;

public class UserSelection {
	
	private int percent;
	private String language;
	
	public UserSelection() {
		this.percent = 0;
		this.language = "";
	}
	
	public UserSelection(int percent, String language) {
		this.percent = percent;
		this.language = language;
	}
	
	public int getPercent() {
		return percent;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setPercent(int percent) {
		this.percent = percent;		  
	}
}
