package com.vatichub.obd2.bean;


public class PIDTypePair {
	
	private String pid;
	private String type;
	
	
	public PIDTypePair(String pid, String type) {
		this.pid=pid;
		this.type=type;
	}
	
	public String getPid() {
		return pid;
	}
	
	public String getType() {
		return type;
	}
	
}