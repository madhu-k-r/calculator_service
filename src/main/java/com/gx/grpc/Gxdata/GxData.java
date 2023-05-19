package com.gx.grpc.Gxdata;

import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateMachine;

import lombok.Data;

enum diameterRequestType {
	UNKNOWN, INITIAL, UPDATE, TERMINATE, EVENT
}

enum gxOriginType {
	UNKNOWN, PGW, TDF
}

@Data
public class GxData {

//	public String ccr;
//	public String cca;
//	public String pgwsm;
//	public int smVersion;
//	public int pendingReuest;
//	public String previousRequestTime;
//	public int resultCode;
//	public int experimentalCode;
//	public String SubscriptionId;
//	public String policyData;

	public String ccr;
	public String cca;
//	  public StateMachine<String, String> machine;
//	public StateMachine<String, String> machine;
	public Integer smVersion;
	public Integer pendingRequest;
	public String OriginationTimestamp;
	public Integer resultCode;
	public Integer experimentalCode;
	public String subscriptionId;
	public String spr_data;
	public Integer spr_result_code;
	public String sy_data;
	public Integer sy_result_code;
	public String policyData;
	public String previousRequestTime;
	public Integer requestProcessing;
	//////////////////////////////////
	public Object Spr_response;
	

	}
