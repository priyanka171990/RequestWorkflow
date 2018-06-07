package com.transfer;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * <p>Processing class stores all the data related to a request type. 
 * 
 */
//Data Transfer Layer
public class ProcessingDTO {

	String requestType;
	String request;
	String ruleName;
	int reqId;
	int stage;
	Boolean valid;

	HashMap<Integer,ArrayList<String>> approverList; 

	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public int getReqId() {
		return reqId;
	}
	public void setReqId(int reqId) {
		this.reqId = reqId;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public HashMap<Integer, ArrayList<String>> getApproverList() {
		return approverList;
	}
	public void setApproverList(HashMap<Integer, ArrayList<String>> approverList) {
		this.approverList = approverList;
	}

	public Boolean isValid(){
		return valid;
	}

	public void setValid(Boolean valid){
		this.valid =valid;
	}

}
