package com.gx.grpc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

//import com.gx.grpc.generatedcode.BidirectionalClientRequest;
//import com.gx.grpc.generatedcode.BidirectionalServerResponse;
//import com.gx.grpc.generatedcode.GxSessionServiceGrpc.GxSessionServiceImplBase;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import tmt.pcrf.grpc.ClientMessage;
import tmt.pcrf.grpc.ServerMessage;
import tmt.pcrf.grpc.TmtGrpcServiceGrpc.TmtGrpcServiceImplBase;

@SuppressWarnings("unchecked")
@GrpcService
@Configuration
public class GRPCService extends TmtGrpcServiceImplBase {

	@Autowired
	GxSessionManager gxSessionManager;

	/**
	 * private static final Integer INITIAL = 1; private static final Integer UPDATE
	 * = 2; private static final Integer TERMINATE = 3; private static final Integer
	 * GET = 4;
	 */
//	@Override
//	public StreamObserver<ClientMessage> ProcessGrpcMessage(
//			StreamObserver<ServerMessage> responseObserver) {
//		return  new GRPCServiceImpl(gxSessionManager, responseObserver);
//	}

	@Override
	public StreamObserver<ClientMessage> processGrpcMessage(StreamObserver<ServerMessage> responseObserver) {
		return new GRPCServiceImpl(gxSessionManager, responseObserver);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @Override public void onNext(BidirectionalClientRequest
	 *           bidirectionalClientRequest) { String jsonRequest =
	 *           bidirectionalClientRequest.getBidirectionalrequest();
	 *           GxSessionManager GxSessionManager = new GxSessionManager(); String
	 *           gxmanagerResponse =
	 *           GxSessionManager.ProcessNetworkRequest(jsonRequest);
	 *           sendResponsetoClient(gxmanagerResponse);
	 * 
	 *           }
	 * 
	 * @Override public void onError(Throwable t) {
	 * 
	 * 
	 *           }
	 * 
	 * @Override public void onCompleted() { this.responseObserver.onCompleted();
	 *           log.info("Server Response completed"); }
	 * 
	 * 
	 *           public void sendResponsetoClient(String response) {
	 *           BidirectionalServerResponse serverResponse =
	 *           BidirectionalServerResponse.newBuilder()
	 *           .setBidirectionalresponse(response).build();
	 *           responseObserver.onNext(serverResponse); }
	 */
}

/**
 * public void processingRequest(ClientRequest clientRequest) { try { String
 * jsonStringrequest = clientRequest.getRequest(); lOriginType originType;
 * Map<String, Object> map = new Gson().fromJson(jsonStringrequest, new
 * TypeToken<HashMap<String, Object>>() { }.getType()); ObjectMapper oMapper =
 * new ObjectMapper(); Object headerobject = map.get("header"); Object msgobject
 * = map.get("message"); Map<String, Object> mapMsg =
 * oMapper.convertValue(msgobject, Map.class); Map<String, Object> mapheader =
 * oMapper.convertValue(headerobject, Map.class); String sessionIdfromRequest =
 * (String) mapMsg.get("Session-Id"); this.sessionId = sessionIdfromRequest;
 * String lRcvdOrigRealm = (String) mapMsg.get("Origin-Realm"); Integer
 * lCcrReqType = doubleToInteger((Double) mapMsg.get("CC-Request-Type")); if
 * (originRealmFromConfig.contains(lRcvdOrigRealm)) { log.info("SID: " +
 * sessionId + ", Origin-Realm: " + lRcvdOrigRealm + ",TDF request: " +
 * lCcrReqType); originType = lOriginType.TDF; } else { log.info("SID: " +
 * sessionId + ", Origin-Realm: " + lRcvdOrigRealm + ",PGW request: " +
 * lCcrReqType); originType = lOriginType.PGW; } Integer ccr_flag =
 * doubleToInteger((Double) mapheader.get("flag")); if (ccr_flag == 1) {
 * log.info("SID: " + sessionId + "-T-BIT Request"); if
 * (HandleTBitRequest(sessionId)) { // // // } } else { log.info("SID: " +
 * sessionId + "-NORMAL Request"); HandleNormalRequest(lCcrReqType, sessionId,
 * jsonStringrequest); } } catch (Exception ex) { log.error("SID " + sessionId +
 * "Exceptions occur while performing NormalRequest"); ex.printStackTrace();
 * responseObserver.onError(ex); } finally { // sending demo response while
 * performing normal request String demoresponse = "This is demo-Response in
 * processRequest"; ServerResponse serverResponse =
 * ServerResponse.newBuilder().setResponse(demoresponse).build();
 * responseObserver.onNext(serverResponse); responseObserver.onCompleted(); }
 * 
 * 
 * }
 * 
 * private boolean HandleNormalRequest(Integer requestType, String SID, String
 * request) { // CC-Request-Type - ENUM=>INITIAL =1,
 * UPDATE=2,TERMINATE=3,EVENT=4 - DIVYA // use switch instead of if - DIVYA
 * //////////////////////////////////////////////////////// try { switch
 * (requestType) { // case GxConstants.GxRequestType.INITIAL: case 1:
 * intilization(SID, request); break; case 2: break; case 3: break; // I Added
 * GET Operation case 4: ProcessCCRRequest(SID); break; } return false; } catch
 * (Exception er) { log.error("SID " + sessionId + "Exceptions occur while
 * processRequest"); er.printStackTrace(); return false; } }
 * 
 * private boolean intilization(String SID, String request) { try { boolean
 * isgxStoreintilize = gxDataStore.InitializeGxDataStore(); if
 * (isgxStoreintilize) { log.info("SID: " + SID + " GxDataStore initilized");
 * ObjectMapper oMapper = new ObjectMapper(); Map<String, Object> map = new
 * Gson().fromJson(request, new TypeToken<HashMap<String, Object>>() {
 * }.getType()); Object msgobject = map.get("message"); Map<String, Object>
 * mapMsg = oMapper.convertValue(msgobject, Map.class); String
 * orientationTimeStamp = (String) mapMsg.get("Origination-Time-Stamp");// check
 * the parameter name // - DIVYA Object subscriptionIDMap =
 * mapMsg.get("Subscription-Id"); Map<String, Object> subscriptionIDData =
 * oMapper.convertValue(subscriptionIDMap, Map.class);
 * System.out.println(subscriptionIDData.get("Subscription-Id-Type")); Integer
 * subscriptionID = doubleToInteger((Double)
 * subscriptionIDData.get("Subscription-Id-Type"));
 * 
 * GxData gxData = new GxData(); gxData.setCcr(request);
 * gxData.setPendingReuest(0);
 * gxData.setPreviousRequestTime(orientationTimeStamp);// take this param
 * valueOrigination-Time-Stamp from // request and put in here - DIVYA // ADD
 * subscriptionIDData HERE in switch String subscribeIdData = (String)
 * mapMsg.get("Subscription-Id-Data"); switch (subscriptionID) { case 0: // 0 -
 * MSISDN gxData.setSubscriptionId(subscribeIdData);// if subscription-Id-Type=0
 * take the data from CCR // request and polutate it here - DIVYA break; }
 * 
 * // Create StateMachine - DIVYA // Storing the SM object on to cache - after
 * the serialization -DIVYA // gxData.setPgwsm(fsm); //
 * gxData.setSmVersion(smVersion);
 * 
 * boolean isDatainserted = GxDataStore.addGxContextData(SID, gxData); if
 * (isDatainserted) { // Put the GxData details in the log // log.info("SID: " +
 * SID + "ccr" + gxData.getCcr() + "cca" + gxData.getCca() + "pgwsm" // +
 * gxData.getPgwsm() + "smVersion" + gxData.getSmVersion() + "pendingReuest" //
 * + gxData.getPendingReuest() + "previousRequestTime" +
 * gxData.getPreviousRequestTime() // + "resultCode" + gxData.getResultCode() +
 * "experimentalCode" + gxData.getExperimentalCode() // + "SubscriptionId" +
 * gxData.getSubscriptionId() // + " These values are Inserted into
 * GxDataStore"); return isDatainserted; } log.error("SID: " + SID + "Data Not
 * Inserted into GxDataStore"); return isDatainserted; } else { log.error("SID:
 * " + SID + " GxDataStore is Not Initilized"); return false; } } catch
 * (Exception qw) { log.error("SID: " + SID + " Exceptions occur while
 * performing IntilizationRequest " + qw); return false; } }
 * 
 * private boolean ProcessCCRRequest(String SID) { try { boolean
 * isgxStoreintilize = gxDataStore.InitializeGxDataStore(); if
 * (isgxStoreintilize) { log.info("SID: " + SID + " GxDataStore Successfully
 * initilized"); if (GxDataStore.getGxContextData(SID) != null) { log.info("SID
 * " + SID + "is found in GxDataStore "); return true; } log.warn("SID: " + SID
 * + "was not found in GxDataStore "); // ForCCANack(SID,
 * GxSessionConstants.errstr); return false; } else { log.error("SID: " + SID +
 * " GxDataStore is not initilized Successfully for ProcessCCRRequest "); return
 * isgxStoreintilize; } } catch (Exception exr) { log.error("SID " + SID +
 * "Exceptions occur while ProcessCCRRequest"); return false; } }
 * 
 * public Integer doubleToInteger(Double doubleValue) throws Exception { Integer
 * integerValue = doubleValue.intValue(); return integerValue; }
 * 
 * public boolean HandleTBitRequest(String SID) { try { // Fetch the cca from
 * the GxDS // If cca is not NULL then SendResponse boolean isgxStoreintilize =
 * gxDataStore.InitializeGxDataStore(); if (isgxStoreintilize) { GxData gxdata =
 * GxDataStore.getGxContextData(SID); String cca = gxdata.getCca();
 * System.out.println("right now cca value is: " + cca);
 * /////////////////////////////////////////////// cca = "This is demo
 * response"; if (cca != null) { SendResponse(cca); } } return true; } catch
 * (Exception exception) { exception.printStackTrace(); log.error("SID " + SID +
 * "Exceptions occur while procesing HandleTBitRequest"); return false; } }
 * 
 * public void SendResponse(String cca) { try { ServerResponse serverResponse =
 * ServerResponse.newBuilder().setResponse(cca).build();
 * responseObserver.onNext(serverResponse); responseObserver.onCompleted(); }
 * catch (Exception ert) { log.error("SID " + sessionId + "Exceptions occur
 * while SendingResponse to client"); ert.printStackTrace();
 * responseObserver.onError(ert); } }
 */
