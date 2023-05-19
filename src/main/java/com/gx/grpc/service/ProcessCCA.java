package com.gx.grpc.service;

import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gx.grpc.Gxdata.GxData;
import com.gx.grpc.gxDataStore.GxDataStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import com.gx.grpc.constants.GxSessionConstants;
import com.gx.grpc.constants.GxSessionConstants.*;
// static com.validation.constants.ValidationConstants.*;

@Service
public class ProcessCCA {

	@Value("${ORIGIN_HOST}")
	private String originHost;

	@Value("${ORIGIN_REALM}")
	private String originRealm;

	// ENUM
	// IpCanType._3GPP2.getIpCType();

	// private LocalCache<String, Object> localCache = new
	// LocalCacheImpl<>("redis-dev.yml");

	@Value("${VENDOR_ID}")
	String vendorId;

	@Autowired
	GxDataStore gxDataStore;

	GxData gxData = new GxData();

	@Autowired
	GxSessionManager gxSessionManager; 
	
//	public ProcessCCA(GxSessionManager gxSessionManager) {
//		super();
//		this.gxSessionManager = gxSessionManager;
//	}
//	public ProcessCCA() {
//		super();
//		// TODO Auto-generated constructor stub
//	}

	private static final Logger logger = LoggerFactory.getLogger(ProcessCCA.class.getName());

	public String FormCCAAck(String sessionId) throws JsonMappingException, JsonProcessingException {

		int pendingRequests = 0;

		int requestProcessing = 0;

		// TODO Get the gxData from gxCache if not found then throw exception
		String ccr = null;
		Map<String, Object> validationResponse = new HashMap<String, Object>();
		Map<String, Object> headerResponse = new HashMap<>();
		Map<String, Object> messageResponse = new HashMap<>();

		try {
			gxData = gxDataStore.getGxContextData(sessionId);
			ccr = gxData.getCcr();
		} catch (Exception e) {
			logger.error("SID: " + sessionId + "Unable to fetch ccr from gx data store, ERROR: " + e.getMessage());
			return "";
		}

		
		Map<String, Object> ccrNode = new ObjectMapper().readValue(ccr, HashMap.class);
		

		Map<String, Object> messageNodeResponse = (Map<String, Object>) ccrNode.get("message");
		Map<String, Object> vendorSpecificApplId = (Map<String, Object>) messageNodeResponse
				.get("Vendor-Specific-Application-Id");
		

		Map<String, Object> response = new HashMap<>();

		response.put(GxSessionConstants.SESSION_ID, sessionId);
		response.put(GxSessionConstants.AUTH_APPLICATION_ID,
				vendorSpecificApplId.get(GxSessionConstants.AUTH_APPLICATION_ID));
		response.put(GxSessionConstants.ORIGIN_HOST, originHost);
		response.put(GxSessionConstants.ORIGIN_REALM, originRealm);
		response.put(GxSessionConstants.RESULT_CODE, 2001);
		response.put(GxSessionConstants.CC_REQUEST_TYPE, messageNodeResponse.get(GxSessionConstants.CC_REQUEST_TYPE));
		response.put(GxSessionConstants.CC_Request_Number,
				messageNodeResponse.get(GxSessionConstants.CC_Request_Number));
		response.put(GxSessionConstants.IP_CAN_TYPE, messageNodeResponse.get(GxSessionConstants.IP_CAN_TYPE));

		gxData.setRequestProcessing(0);
		
		Map<String, Object> chargingRuleInstallResponse = new HashMap<>();
		chargingRuleInstallResponse.put(GxSessionConstants.CHARGING_RULE_BASE_NAME,
				gxData.getPolicyData());
				

		response.put(GxSessionConstants.CHARGING_RULE_INSTALL, chargingRuleInstallResponse);

		logger.info("SID: " + GxSessionConstants.SESSION_ID + ", " + GxSessionConstants.AUTH_APPLICATION_ID + ": "
				+ response.get(GxSessionConstants.AUTH_APPLICATION_ID) + ", " + GxSessionConstants.ORIGIN_HOST + ": "
				+ response.get(GxSessionConstants.ORIGIN_HOST) + ", " + GxSessionConstants.ORIGIN_REALM + ": "
				+ response.get(GxSessionConstants.ORIGIN_REALM) + ", " + GxSessionConstants.DEST_HOST + ": "
				+ response.get(GxSessionConstants.DEST_HOST) + ", " + GxSessionConstants.DEST_REALM + ": "
				+ response.get(GxSessionConstants.DEST_REALM) + ", " + GxSessionConstants.RESULT_CODE + ": "
				+ response.get(GxSessionConstants.RESULT_CODE) + ", " + GxSessionConstants.CC_REQUEST_TYPE + ": "
				+ response.get(GxSessionConstants.CC_REQUEST_TYPE) + ", " + GxSessionConstants.CC_Request_Number + ": "
				+ response.get(GxSessionConstants.CC_Request_Number) + ", " + GxSessionConstants.CC_Request_Number
				+ ": " + response.get(GxSessionConstants.CC_Request_Number) + ", "
				+ GxSessionConstants.REQUEST_PROCESSING + ": " + requestProcessing + ", "
				+ GxSessionConstants.PENDING_REQUESTS + ": " + pendingRequests + " ");

		// TODO Update the CACHE here --->
		Map<String, Object> ccaNode = new HashMap<>();
		Map<String, Object> headerNode = (Map<String, Object>) ccrNode.get("header");

		Map<String, Object> headerResponseNode = new HashMap<>();

		headerResponseNode.put(GxSessionConstants.COMMAND_CODE, headerNode.get(GxSessionConstants.COMMAND_CODE));
		headerResponseNode.put(GxSessionConstants.FLAG, 0);
		headerResponseNode.put(GxSessionConstants.APPLICATION_ID, headerNode.get(GxSessionConstants.APPLICATION_ID));
		headerResponseNode.put(GxSessionConstants.HOP_BY_HOP_ID, headerNode.get(GxSessionConstants.HOP_BY_HOP_ID));
		headerResponseNode.put(GxSessionConstants.END_TO_END_ID, headerNode.get(GxSessionConstants.END_TO_END_ID));

		ccaNode.put("header", headerResponseNode);
		ccaNode.put("message", response);

		String ResponseCca = new ObjectMapper().writeValueAsString(ccaNode);
		Log.info("CCA:===> "+ResponseCca);
		
		gxData.setCca(ResponseCca);

		try {
			gxDataStore.updateGxContextData(sessionId,gxData);
		} catch (Exception e) {
			logger.error("SID: " + sessionId + " gxData not updated!");
		}
		logger.info("SID: " + sessionId + ", Gx-Cache updated");

		if (false == gxSessionManager.SendResponse(sessionId, ResponseCca)) {
			logger.info("SID:{} send response to GxSMgr failed", sessionId);
			// In failure need to check on the state to be done - DIVYA - dont delete
			return "STOP_EVT";
		}
		logger.info("SID:{} Response sent to GxSMgr " + sessionId);
		return "SEND_RSP_EVT";
	}

	public String FormInitialCCANack(String sessionId) {
try {
		String ccr = null;
		Map<String, Object> validationResponse = new HashMap<String, Object>();
		Map<String, Object> headerResponse = new HashMap<>();
		Map<String, Object> messageResponse = new HashMap<>();

		try {
			gxData = gxDataStore.getGxContextData(sessionId);
			ccr = gxData.getCcr();
		} catch (Exception e) {
			logger.error("SID: " + sessionId + "Unable to fetch ccr from gx data store, ERROR: " + e.getMessage());
			return null;
		}

		Map<String, Object> response = new ObjectMapper().readValue(ccr, HashMap.class);
		Map<String, Object> headerNode = (Map<String, Object>) response.get("header");

		headerResponse.put(GxSessionConstants.COMMAND_CODE, headerNode.get(GxSessionConstants.COMMAND_CODE));
		headerResponse.put(GxSessionConstants.FLAG, 0);
		headerResponse.put(GxSessionConstants.APPLICATION_ID, headerNode.get(GxSessionConstants.APPLICATION_ID));
		headerResponse.put(GxSessionConstants.HOP_BY_HOP_ID, headerNode.get(GxSessionConstants.HOP_BY_HOP_ID));
		headerResponse.put(GxSessionConstants.END_TO_END_ID, headerNode.get(GxSessionConstants.END_TO_END_ID));

		Map<String, Object> messageNode = (Map<String, Object>) response.get("message");

		messageResponse.put(GxSessionConstants.SESSION_ID, messageNode.get(GxSessionConstants.SESSION_ID));
		messageResponse.put(GxSessionConstants.ORIGIN_HOST, messageNode.get(GxSessionConstants.ORIGIN_HOST));
		messageResponse.put(GxSessionConstants.CC_REQUEST_TYPE, messageNode.get(GxSessionConstants.CC_REQUEST_TYPE));
		messageResponse.put(GxSessionConstants.CC_Request_Number,
				messageNode.get(GxSessionConstants.CC_Request_Number));

		Map<String, Object> vendorSpecificApplicationIdRes = new HashMap<>();
		Map<String, Object> vendorSpecificApplicationIdReq = (Map<String, Object>) messageNode
				.get("Vendor-Specific-Application-Id");

		vendorSpecificApplicationIdRes.put(GxSessionConstants.VENDOR_ID, vendorId);
		vendorSpecificApplicationIdRes.put(GxSessionConstants.AUTH_APPLICATION_ID,
				vendorSpecificApplicationIdReq.get(GxSessionConstants.AUTH_APPLICATION_ID));

		messageResponse.put(GxSessionConstants.VENDOR_SPECIFIC_APPLICATION_ID, vendorSpecificApplicationIdRes);

		// if (errStr != null) {

		// @Value(errStr)
//		String error = null;

		Map<String, Object> experimenatalResult = new HashMap<>();
		experimenatalResult.put("Vendor-Id", vendorId);
		experimenatalResult.put("Experimental-Result-Code", 5140);
		messageResponse.put("Experimental-Result", experimenatalResult);
		messageResponse.put("result_code", 5005);
		// String errors[] = error.split(",");
		// messageResponse.put("result_code", Integer.parseInt(errors[0]));
		// messageResponse.put("experimental-code", Integer.parseInt(errors[1]));
		// }

		validationResponse.put("header", headerResponse);
		validationResponse.put("message", messageResponse);
		
		String responseCCA = new ObjectMapper().writeValueAsString(validationResponse);
		if (false == gxSessionManager.SendResponse(sessionId,responseCCA)) {
			logger.info("SID:{} send response to GxSMgr failed", sessionId);
			// In failure need to check on the state to be done - DIVYA - dont delete
			return "STOP_EVT";
		}
		logger.info("SID:{} Response sent to GxSMgr " + sessionId);
		
	
	}catch(Exception e) {
		logger.info("SID:{} Initial NACK Exception:{}",sessionId, e);
		return "CLEANUP_EVT";
	}
return "CLEANUP_EVT";
}

public String FormCCANack(String sessionId) {

	try{
	String ccr = null;
		Map<String, Object> validationResponse = new HashMap<String, Object>();
		Map<String, Object> headerResponse = new HashMap<>();
		Map<String, Object> messageResponse = new HashMap<>();

		try {
			gxData = gxDataStore.getGxContextData(sessionId);
			ccr = gxData.getCcr();
		} catch (Exception e) {
			logger.error("SID: " + sessionId + "Unable to fetch ccr from gx data store, ERROR: " + e.getMessage());
			return null;
		}

		Map<String, Object> response = new ObjectMapper().readValue(ccr, HashMap.class);
		Map<String, Object> headerNode = (Map<String, Object>) response.get("header");

		headerResponse.put(GxSessionConstants.COMMAND_CODE, headerNode.get(GxSessionConstants.COMMAND_CODE));
		headerResponse.put(GxSessionConstants.FLAG, 0);
		headerResponse.put(GxSessionConstants.APPLICATION_ID, headerNode.get(GxSessionConstants.APPLICATION_ID));
		headerResponse.put(GxSessionConstants.HOP_BY_HOP_ID, headerNode.get(GxSessionConstants.HOP_BY_HOP_ID));
		headerResponse.put(GxSessionConstants.END_TO_END_ID, headerNode.get(GxSessionConstants.END_TO_END_ID));

		Map<String, Object> messageNode = (Map<String, Object>) response.get("message");

		messageResponse.put(GxSessionConstants.SESSION_ID, messageNode.get(GxSessionConstants.SESSION_ID));
		messageResponse.put(GxSessionConstants.ORIGIN_HOST, messageNode.get(GxSessionConstants.ORIGIN_HOST));
		messageResponse.put(GxSessionConstants.CC_REQUEST_TYPE, messageNode.get(GxSessionConstants.CC_REQUEST_TYPE));
		messageResponse.put(GxSessionConstants.CC_Request_Number,
				messageNode.get(GxSessionConstants.CC_Request_Number));

		Map<String, Object> vendorSpecificApplicationIdRes = new HashMap<>();
		Map<String, Object> vendorSpecificApplicationIdReq = (Map<String, Object>) messageNode
				.get("Vendor-Specific-Application-Id");

		vendorSpecificApplicationIdRes.put(GxSessionConstants.VENDOR_ID, vendorId);
		vendorSpecificApplicationIdRes.put(GxSessionConstants.AUTH_APPLICATION_ID,
				vendorSpecificApplicationIdReq.get(GxSessionConstants.AUTH_APPLICATION_ID));

		messageResponse.put(GxSessionConstants.VENDOR_SPECIFIC_APPLICATION_ID, vendorSpecificApplicationIdRes);

		Map<String, Object> experimenatalResult = new HashMap<>();
		experimenatalResult.put("Vendor-Id", vendorId);
		experimenatalResult.put("Experimental-Result-Code", gxData.getResultCode());
		messageResponse.put("Experimental-Result", experimenatalResult);
		messageResponse.put("result_code", gxData.getResultCode());
		

		validationResponse.put("header", headerResponse);
		validationResponse.put("message", messageResponse);
		
		String responseCCA = new ObjectMapper().writeValueAsString(validationResponse);
		if (false == gxSessionManager.SendResponse(sessionId,responseCCA)) {
			logger.info("SID:{} send response to GxSMgr failed", sessionId);
			// In failure need to check on the state to be done - DIVYA - dont delete
			return "STOP_EVT";
		}
		logger.info("SID:{} Response sent to GxSMgr " + sessionId);
		
	
	}catch(Exception e) {
		logger.info("SID:{} Send CCA NACK Exception:{}", sessionId, e);
		return "CLEANUP_EVT";
	}
return "SEND_RSP_EVT";
}
}
