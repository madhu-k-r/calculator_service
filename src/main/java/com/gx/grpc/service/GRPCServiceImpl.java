package com.gx.grpc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import tmt.pcrf.grpc.ClientMessage;
import tmt.pcrf.grpc.MessageType;
import tmt.pcrf.grpc.ServerMessage;

@Slf4j
@Component
public class GRPCServiceImpl implements StreamObserver<ClientMessage> {

	//@Autowired
	private GxSessionManager gxSessionManager;
	
	
	public GRPCServiceImpl(GxSessionManager gxSessionManager) {
		super();
		this.gxSessionManager = gxSessionManager;
	}

	private static StreamObserver<ServerMessage> responseObserver;

	public GRPCServiceImpl(GxSessionManager gxSessionManager, StreamObserver<ServerMessage> responseObserver) {
		this.gxSessionManager = gxSessionManager;
		this.responseObserver = responseObserver;
	}

	public GRPCServiceImpl() {

	}

	@Override
	public void onNext(ClientMessage bidirectionalClientRequest) {
		String jsonRequest = bidirectionalClientRequest.getMessage();
		//GxSessionManager gxSessionManager = new GxSessionManager();
		
		gxSessionManager.ProcessNetworkRequest(jsonRequest);
		// sendResponsetoClient(gxmanagerResponse);
		// For Sending Response from server to client - ProcessAppResponse(String
		// RspType, String ResponseData)
	}

	@Override
	public void onError(Throwable t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCompleted() {
		this.responseObserver.onCompleted();
		log.info("Server Response completed");
	}

//	public void sendResponsetoClient(String response) {
//		BidirectionalServerResponse serverResponse = BidirectionalServerResponse.newBuilder()
//				.setBidirectionalresponse(response).build();
//		responseObserver.onNext(serverResponse);
//	}

	public void ProcessAppResponse(String cca) {
		ServerMessage serverResponse = ServerMessage.newBuilder().setMessageType(MessageType.APP_RESPONSE).setStatus(0)
				.setSessionId("dummy").setModuleId(16777238).setMessage(cca).build();
		GRPCServiceImpl.responseObserver.onNext(serverResponse);
	}

	public void ProcessAppResponse(String sessionId, String cca) {
		ServerMessage serverResponse = ServerMessage.newBuilder().setMessageType(MessageType.APP_RESPONSE).setStatus(0)
				.setSessionId(sessionId).setModuleId(16777238).setMessage(cca).build();
		GRPCServiceImpl.responseObserver.onNext(serverResponse);
	}

}
