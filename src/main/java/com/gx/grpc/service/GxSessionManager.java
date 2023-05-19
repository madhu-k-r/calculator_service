package com.gx.grpc.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gx.grpc.Gxdata.GxData;
import com.gx.grpc.constants.lOriginType;
import com.gx.grpc.gxDataStore.GxDataStore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@SuppressWarnings("unchecked")
//@GrpcService
//@Configuration
@Service
public class GxSessionManager {


	//@Autowired
	GxDataStore gxDataStore;
	
	
	

public GxSessionManager(GxDataStore gxDataStore) {
		super();
		this.gxDataStore = gxDataStore;
	}

//	SMHandler smHandler;
	// = new SMHandler();

	private String sessionId;
//	@Value("${tmt.origin-RealmData}")
	private String originRealmFromConfig = "seagull.mydomain.com,seagull.mydomain1.com,seagull.mydomain2.com,seagull.mydomain3.com";

	public void ProcessNetworkRequest(String jsonStringrequest) {
		try {
			lOriginType originType;
			Map<String, Object> map = new Gson().fromJson(jsonStringrequest, new TypeToken<HashMap<String, Object>>() {
			}.getType());
			ObjectMapper oMapper = new ObjectMapper();
			Object headerobject = map.get("header");
			Object msgobject = map.get("message");
			Map<String, Object> mapMsg = oMapper.convertValue(msgobject, Map.class);
			Map<String, Object> mapheader = oMapper.convertValue(headerobject, Map.class);

			String sessionIdfromRequest = (String) mapMsg.get("Session-Id");
			this.sessionId = sessionIdfromRequest;

			String lRcvdOrigRealm = (String) mapMsg.get("Origin-Realm");
			Integer lCcrReqType = doubleToInteger((Double) mapMsg.get("CC-Request-Type"));

			if (originRealmFromConfig.contains(lRcvdOrigRealm)) {
				log.info("SID: " + sessionId + ", Origin-Realm: " + lRcvdOrigRealm + ",TDF request: " + lCcrReqType);
				originType = lOriginType.TDF;
			} else {
				log.info("SID: " + sessionId + ", Origin-Realm: " + lRcvdOrigRealm + ",PGW request: " + lCcrReqType);
				originType = lOriginType.PGW;
			}
			Integer ccr_flag = doubleToInteger((Double) mapheader.get("flag"));
			if (ccr_flag == 1) {
				log.info("SID: " + sessionId + " T-BIT Request");
				HandleTBitRequest(sessionId);

			} else {
				log.info("SID: " + sessionId + "-NORMAL Request");
				if (false == HandleNormalRequest(lCcrReqType, sessionId, jsonStringrequest)) {
					// Form the errorCCA
					// SendResponse
				}
			}
		} catch (Exception ex) {
			log.error("SID " + sessionId + "Exceptions occur while performing NormalRequest");
			ex.printStackTrace();
		} finally {
			// FormError CCA and send - DIVYA
			// sending demo response while performing normal request
			String demoresponse = "This is demo-Response comes from server: ";
			SendResponse(sessionId,demoresponse);

		}

	}

	private boolean HandleNormalRequest(Integer requestType, String SID, String request) {
		// CC-Request-Type - ENUM=>INITIAL =1, UPDATE=2,TERMINATE=3,EVENT=4 - DIVYA
		// use switch instead of if - DIVYA
		////////////////////////////////////////////////////////
		try {
			switch (requestType) {
			case 1:
				return intilization(SID, request);

			case 2:
			case 3:
				String sm_state = "SESS_ESTABLISHED_ST";
				String sm_event="";
				if(requestType == 2)
				{
				    sm_event="UPDATE_EVT";
				}
				else
				{
					sm_event = "TERMINATE_EVT";
				}
				return ProcessCCRRequest(SID, sm_state, sm_event,request);

			// I Added GET Operation
			case 4:
			default:
				log.info("SID:{} Unsupported Request Type {}", SID, requestType);
				break;
			}
			return false;
		} catch (Exception er) {
			log.error("SID " + sessionId + "Exceptions occur while processRequest");
			er.printStackTrace();
			return false;
		}
	}

	private boolean intilization(String SID, String request) {
		try {
			boolean isgxStoreintilize = gxDataStore.InitializeGxDataStore();
			if (isgxStoreintilize) {
				log.info("SID: " + SID + " GxDataStore initilized");
				ObjectMapper oMapper = new ObjectMapper();
				Map<String, Object> map = new Gson().fromJson(request, new TypeToken<HashMap<String, Object>>() {
				}.getType());
				Object msgobject = map.get("message");
				Map<String, Object> mapMsg = oMapper.convertValue(msgobject, Map.class);
				String orientationTimeStamp = (String) mapMsg.get("Origination-Time-Stamp");// check the parameter name
																							// - DIVYA
				Object subscriptionIDMap = mapMsg.get("Subscription-Id");
				Map<String, Object> subscriptionIDData = oMapper.convertValue(subscriptionIDMap, Map.class);
				// System.out.println(subscriptionIDData.get("Subscription-Id-Type"));
				Integer subscriptionID = doubleToInteger((Double) subscriptionIDData.get("Subscription-Id-Type"));

				GxData gxData = new GxData();
				gxData.setCcr(request);
				gxData.setPendingRequest(0);
				gxData.setPreviousRequestTime(orientationTimeStamp);// take this param valueOrigination-Time-Stamp from
																	// request and put in here - DIVYA
				// ADD subscriptionIDData HERE in switch
				String subscribeIdData = (String) mapMsg.get("Subscription-Id-Data");
				switch (subscriptionID) {
				case 0:
					// 0 - MSISDN
					gxData.setSubscriptionId(subscribeIdData);// if subscription-Id-Type=0 take the data from CCR
																// request and polutate it here - DIVYA
					break;
				}
				// Create StateMachine - DIVYA
				StateMachine<String, String> sm = SMHandler.CreateSM(SID);
//				gxData.setMachine(sm);

				// Storing the SM object on to cache - after the serialization -DIVYA
				// gxData.setPgwsm(fsm);
				// gxData.setSmVersion(smVersion);
				boolean isDatainserted = GxDataStore.addGxContextData(SID, gxData);
				if (isDatainserted) {
					// Put the GxData details in the log
					log.info("SID: " + SID + "ccr" + gxData.getCcr() + "cca" + gxData.getCca() + "machine"
//							+ gxData.getMachine() 
							+ "smVersion" + gxData.getSmVersion() + "pendingReuest" + gxData.getPendingRequest()
							+ "previousRequestTime" + gxData.getPreviousRequestTime() + "resultCode"
							+ gxData.getResultCode() + "experimentalCode" + gxData.getExperimentalCode()
							+ "SubscriptionId" + gxData.getSubscriptionId()
							+ " These values are Inserted into GxDataStore");
					
					log.info("SID:{} ---- Current State:{}", sessionId, sm.getState().toString());
					log.info("SID:{} Sending Event INITIAL_EVT", SID);

					sm.getExtendedState().getVariables().put("sid", sessionId);
					sm.sendEvent("INITIAL_EVT");
					return isDatainserted;
				} else {
					log.error("SID: " + SID + "Data Not Inserted into GxDataStore");
					return isDatainserted;
				}

			} else {
				log.error("SID: " + SID + " GxDataStore is Not Initilized");
				return false;
			}
		} catch (Exception qw) {
			log.error("SID: " + SID + " Exceptions occur while performing IntilizationRequest " + qw);
			return false;
		}
	}

	private boolean ProcessCCRRequest(String SID, String smState, String sm_event,String request) {
		try {
			boolean isgxStoreintilize = gxDataStore.InitializeGxDataStore();
			if (isgxStoreintilize) {
				log.info("SID: " + SID + " GxDataStore Successfully initilized");

			} else {
				log.error("SID: " + SID + " GxDataStore is not initilized Successfully for ProcessCCRRequest ");
				return isgxStoreintilize;
			}

			GxData gxData = new GxData();
			gxData = gxDataStore.getGxContextData(SID);
			gxData.setCcr(request);
			if (true == IsPendingRequest(gxData)) {
				// update the gxDS
				boolean isUpdated = gxDataStore.updateGxContextData(SID, gxData);
				return false;
			}

			// update the gxDS
			gxDataStore.updateGxContextData(SID, gxData);
			// CreateSM
			StateMachine<String, String> sm = SMHandler.CreateSM(SID);
			// ResetSM
			sm = SMHandler.ResetSM(smState, sm);
			// Generate Event
			log.info("SID:{} ---- Current State:{}", sessionId, sm.getState().toString());
			log.info("SID:{} Sending Event {}", sessionId, sm_event );

			sm.getExtendedState().getVariables().put("sid", sessionId);
			boolean sm_result = sm.sendEvent(sm_event);
			log.info("TBR ----------------------- Send Event Status:{}", sm_result);
			return true;

		} catch (Exception exr) {
			log.error("SID " + SID + "Exceptions occur while ProcessCCRRequest");
			return false;
		}
	}

	
	public boolean IsPendingRequest(GxData gxData){

        int pendingRequests=0;
        int requestProcessing=0;
        String ccr = null;

        
        if(gxData.getRequestProcessing() > 0)
        {
        	pendingRequests = gxData.getPendingRequest() + 1;
        	gxData.setPendingRequest(pendingRequests);
        }
        else
        {
        	gxData.setRequestProcessing(1);
        }
        
		if (pendingRequests > 0) {
			return true;
		} else {
			return false;
		}
    }
	
	public Integer doubleToInteger(Double doubleValue) throws Exception {
		Integer integerValue = doubleValue.intValue();
		return integerValue;
	}

	public boolean HandleTBitRequest(String SID) {
		try {
			// Fetch the cca from the GxDS
			// If cca is not NULL then SendResponse
			boolean isgxStoreintilize = gxDataStore.InitializeGxDataStore();
			if (isgxStoreintilize) {
				GxData gxdata = GxDataStore.getGxContextData(SID);
				String cca = gxdata.getCca();
				System.out.println("right now cca value is: " + cca);
				///////////////////////////////////////////////
				cca = "This is demo response from cca-HandleTBitRequest: ";
				if (cca != null) {

					// SendResponse(cca);
					////////////////////////////////////////////////////////////
				}
			}
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			log.error("SID " + SID + "Exceptions occur while procesing HandleTBitRequest");
			return false;
		}
	}

	public boolean SendResponse(String cca) {
		try {
			GRPCServiceImpl gRPCServiceImpl = new GRPCServiceImpl();
			gRPCServiceImpl.ProcessAppResponse(cca);
			// grpc -> ProcessAppResponse(RspType - sid, ResponseData-CCA)
//			BidirectionalServerResponse serverResponse = BidirectionalServerResponse.newBuilder()
//					.setBidirectionalresponse(cca).build();
//			responseObserver.onNext(serverResponse);
//			responseObserver.onCompleted()
			return true;
		} catch (Exception ert) {
			log.error("SID " + sessionId + "Exceptions occur while SendingResponse to client");
			ert.printStackTrace();
			return false;
		}
	}

	public boolean SendResponse(String sessionId, String cca) {
		try {
			GRPCServiceImpl gRPCServiceImpl = new GRPCServiceImpl();
			gRPCServiceImpl.ProcessAppResponse(sessionId, cca);
			// grpc -> ProcessAppResponse(RspType - sid, ResponseData-CCA)
//			BidirectionalServerResponse serverResponse = BidirectionalServerResponse.newBuilder()
//					.setBidirectionalresponse(cca).build();
//			responseObserver.onNext(serverResponse);
//			responseObserver.onCompleted()
			return true;
		} catch (Exception ert) {
			log.error("SID " + sessionId + "Exceptions occur while SendingResponse to client");
			ert.printStackTrace();
			return false;
		}
	}
//		public void SendResponse(String sessionID, String cca) {
//			try {
//				//grpc -> ProcessAppResponse(RspType - sid, ResponseData-CCA)
//				BidirectionalServerResponse serverResponse = BidirectionalServerResponse.newBuilder()
//						.setBidirectionalresponse(cca).build();
////				responseObserver.onNext(serverResponse);
////				responseObserver.onCompleted();
//			} catch (Exception ert) {
//				log.error("SID " + sessionId + "Exceptions occur while SendingResponse to client");
//				ert.printStackTrace();
////				responseObserver.onError(ert);
//			}
//	}
//	
	//////////////////////////////////////////////////////////////////////////////////////////////////
}
