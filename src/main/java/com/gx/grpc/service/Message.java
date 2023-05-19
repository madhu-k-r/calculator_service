package com.gx.grpc.service;

import lombok.Data;

@Data
public class Message {

	MessageType messageType;
	long moduleId;
	int status;
	String uniqueRefId;
	String messageKey;
	String sessionId;
	String message;

	enum MessageType {
		NW_REQUEST, NW_RESPONSE, APP_REQUEST, APP_RESPONSE, OTHERS
	}
}
