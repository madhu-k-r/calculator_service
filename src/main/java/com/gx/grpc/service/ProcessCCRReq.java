package com.gx.grpc.service;

import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.gx.grpc.Gxdata.GxData;
import com.gx.grpc.gxDataStore.GxDataStore;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.gx.grpc.constants.GxSessionConstants;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

//import static com.validation.constants.ValidationConstants.*;

@Slf4j
@Service
public class ProcessCCRReq {

	@Autowired
	GxSessionManager gxSessionManager;

	@Value("${VENDOR_ID}")
    String vendorId;

	@Autowired
	GxDataStore gxDataStore;
	
	GxData gxData = new GxData();

    private static final Logger logger = LoggerFactory.getLogger(ProcessCCRReq.class.getName());

    public String ProcessInitial(String sessionId) {
    	
    	System.err.println("vendorId: "+vendorId);
		//Fetch the ccr from DS for the sessionId
    	Log.info("SID:{} Rcvd Req INITIAL\n", sessionId);

		String ccr=null;
		
		try{
			gxData = gxDataStore.getGxContextData(sessionId);
			ccr = gxData.getCcr();
		}catch(Exception e){
			logger.error("SID: "+sessionId+"Unable to fetch ccr from gx data store, ERROR: "+e.getMessage());
			return "INITIAL_NACK_EVT";
		}


		Map<String, Object> rsp= null;
    	try {
			rsp = convertToMap(ccr);
		} catch (JsonProcessingException e) {
			logger.info("SID: "+sessionId+", "+e.getMessage());
			return "INITIAL_NACK_EVT";
		}

		String response= null;
		try {
			response = Validate(rsp);
		} catch (JsonProcessingException e) {
			logger.info("SID: "+sessionId+", "+e.getMessage());
			return "INITIAL_NACK_EVT";
		}
		return response;
	}
    
    public Map<String, Object> convertToMap(String ccr) throws JsonProcessingException {

        Map<String, Object> response = new ObjectMapper().readValue(ccr, HashMap.class);
        Map<String, Object> messageNodeResponse = (Map<String, Object>) response.get("message");

        return messageNodeResponse;
    }

    //Method to validate the necessary params
    public String Validate(Map<String, Object> messageNodeResponse) throws JsonProcessingException {

        //boolean errorStatus = false;
        /*Map<String, Object> response = new ObjectMapper().readValue(jsonString, HashMap.class);*/
        Map<String, Object> subscriptionIdNodeResponse = (Map<String, Object>) messageNodeResponse.get("Subscription-Id");

		if (messageNodeResponse.get("Session-Id") == null){
			logger.info("Missing paramter: Session-Id");
			return "INITIAL_NACK_EVT";
		}
		if (messageNodeResponse.get("Origin-Realm") == null){
			logger.info("Missing paramter Origin-Realm for SID: "+ messageNodeResponse.get("Session-Id"));
			return "INITIAL_NACK_EVT";
		}
		if (messageNodeResponse.get("CC-Request-Type") == null){
			logger.info("Missing paramter CC-Request-Type for SID: "+ messageNodeResponse.get("Session-Id"));
			return "INITIAL_NACK_EVT";
		}
		if (messageNodeResponse.get("CC-Request-Number") == null){
			logger.info("Missing paramter CC-Request-Number for SID: "+ messageNodeResponse.get("Session-Id"));
			return "INITIAL_NACK_EVT";
		}
		if (messageNodeResponse.get("Subscription-Id") == null){
			logger.info("Missing paramter Subscription-Id for SID: "+ messageNodeResponse.get("Session-Id"));
			return "INITIAL_NACK_EVT";
		}
		if (messageNodeResponse.get("Framed-IP-Address") == null){
			logger.info("Missing paramter Framed-IP-Address for SID: "+ messageNodeResponse.get("Session-Id"));
			return "INITIAL_NACK_EVT";
		}
		if (subscriptionIdNodeResponse.get("Subscription-Id-Type") == null){
			logger.info("Missing paramter Subscription-Id-Type for SID: "+ messageNodeResponse.get("Session-Id"));
			return "INITIAL_NACK_EVT";
		}
		if (subscriptionIdNodeResponse.get("Subscription-Id-Data") == null){
			logger.info("Missing paramter Subscription-Id-Data for SID: "+ messageNodeResponse.get("Session-Id"));
			return "INITIAL_NACK_EVT";
		}


            //Request type is 1 (INITIAL) then Request number should be 0
            if ((Integer) messageNodeResponse.get("CC-Request-Type") == 1 && (Integer) messageNodeResponse.get("CC-Request-Number") == 0) {
                logger.info("SID: " + messageNodeResponse.get("Session-Id") + ", "
                        + "Origin-Host: " + messageNodeResponse.get("Origin-Host").toString() + ", "
                        + "Origin-Realm: " + messageNodeResponse.get("Origin-Realm").toString() + ", "
                        + "CC-Request-Type: " + messageNodeResponse.get("CC-Request-Type").toString() + ", "
                        + "CC-Request-Number: " + messageNodeResponse.get("CC-Request-Number").toString() + ", "
                        + "Subscription-Id-Type: " + subscriptionIdNodeResponse.get("Subscription-Id-Type").toString() + ", "
                        + "Subscription-Id-Data: " + subscriptionIdNodeResponse.get("Subscription-Id-Data").toString()
                );

            } else {
                logger.error("SID: " + messageNodeResponse.get("Session-Id") + ", "
                        + "Origin-Host: " + messageNodeResponse.get("Origin-Host").toString() + ", "
                        + "Error: CC-Request-Type & CC-Request-Number are invalid"
                );
                return "INITIAL_NACK_EVT";
                //return FormCCANack(response, DIAMETER_ERROR_INITIAL_PARAMETERS);
            }


        return "SPR_GET_EVT";
        //return FormCCANack(response, null);
    }

	public String ProcessUpdate(String sessionId) {

		return "SPR_GET_EVT";
	}

	public String ProcessTermination(String sessionId) {

        int pendingRequests=0;
        int requestProcessing=0;
        String ccr = null;

        //Fetch the ccr from DS for the sessionId
        Log.info("SID:{} Rcvd Req TERMINATE", sessionId);

        try {
        	
        
        try {
            gxData = gxDataStore.getGxContextData(sessionId);
            ccr = gxData.getCcr();
        } catch (Exception e) {
            logger.error("SID: " + sessionId + "Unable to fetch ccr from gx data store, ERROR: " + e.getMessage());
            return "CCA_NACK_EVT";
        }



        Map<String, Object> ccrNodeResponse = new ObjectMapper().readValue(ccr, HashMap.class);
        Map<String, Object> messageNodeResponse = (Map<String, Object>) ccrNodeResponse.get("message");
        Map<String, Object> vendorSpecificApplId = (Map<String, Object>) messageNodeResponse.get("Vendor-Specific-Application-Id");

        Map<String, Object> response = new HashMap<>();

        response.put(GxSessionConstants.SESSION_ID, sessionId);
        response.put(GxSessionConstants.AUTH_APPLICATION_ID, vendorSpecificApplId.get(GxSessionConstants.AUTH_APPLICATION_ID));
        response.put(GxSessionConstants.ORIGIN_HOST, messageNodeResponse.get(GxSessionConstants.ORIGIN_HOST));
        response.put(GxSessionConstants.ORIGIN_REALM, messageNodeResponse.get(GxSessionConstants.ORIGIN_REALM));
        response.put(GxSessionConstants.RESULT_CODE, 2001);
        response.put(GxSessionConstants.CC_REQUEST_TYPE, 3);
        response.put(GxSessionConstants.CC_Request_Number, messageNodeResponse.get(GxSessionConstants.CC_Request_Number));
        response.put(GxSessionConstants.EXPERIMENTAL_RESULT,null);

        logger.info("SID: " + GxSessionConstants.SESSION_ID + ", "
                + GxSessionConstants.AUTH_APPLICATION_ID+": " + response.get(GxSessionConstants.AUTH_APPLICATION_ID) + ", "
                + GxSessionConstants.ORIGIN_HOST + ": "+ response.get(GxSessionConstants.ORIGIN_HOST)+ ", "
                + GxSessionConstants.ORIGIN_REALM + ": "+ response.get(GxSessionConstants.ORIGIN_REALM)+ ", "
                + GxSessionConstants.DEST_HOST + ": "+ response.get(GxSessionConstants.DEST_HOST)+ ", "
                + GxSessionConstants.DEST_REALM + ": " + response.get(GxSessionConstants.DEST_REALM)+ ", "
                + GxSessionConstants.RESULT_CODE + ": " + response.get(GxSessionConstants.RESULT_CODE)+ ", "
                + GxSessionConstants.CC_REQUEST_TYPE + ": " + response.get(GxSessionConstants.CC_REQUEST_TYPE)+ ", "
                + GxSessionConstants.CC_Request_Number + ": " + response.get(GxSessionConstants.CC_Request_Number)+ ", "
                + GxSessionConstants.CC_Request_Number + ": " + response.get(GxSessionConstants.CC_Request_Number)+ ", "
                + GxSessionConstants.REQUEST_PROCESSING + ": " + requestProcessing+ ", "
                + GxSessionConstants.PENDING_REQUESTS + ": "+ pendingRequests+" "
        );


        //TODO Update the CACHE here --->
        Map<String, Object> ccaNode = new HashMap<>();
        Map<String,Object> headerNode= (Map<String, Object>) ccrNodeResponse.get("header");

        Map<String,Object> headerResponseNode = new HashMap<>();

        headerResponseNode.put(GxSessionConstants.COMMAND_CODE,headerNode.get(GxSessionConstants.COMMAND_CODE));
        headerResponseNode.put(GxSessionConstants.FLAG,0);
        headerResponseNode.put(GxSessionConstants.APPLICATION_ID,headerNode.get(GxSessionConstants.APPLICATION_ID));
        headerResponseNode.put(GxSessionConstants.HOP_BY_HOP_ID,headerNode.get(GxSessionConstants.HOP_BY_HOP_ID));
        headerResponseNode.put(GxSessionConstants.END_TO_END_ID,headerNode.get(GxSessionConstants.END_TO_END_ID));

        ccaNode.put("header",headerResponseNode);
        ccaNode.put("message", response);
		String ResponseCCA = new ObjectMapper().writeValueAsString(ccaNode);
        gxData.setCca(ResponseCCA);

        try{
            gxDataStore.updateGxContextData(sessionId, gxData);
        }catch (Exception e){
            logger.error("SID: "+sessionId+ " GxContextData update failed");
        }
        logger.info("SID: " + sessionId + " GxContextData updated");

        //TODO send CCA_RESPONSE to gxSessionManager object
        if(false == gxSessionManager.SendResponse(sessionId,ResponseCCA))
		{
			logger.info("SID:{} send response to GxSMgr failed", sessionId);
			// In failure need to check on the state to be done - DIVYA - dont delete
			return "STOP_EVT";
		}

	}catch(Exception e) {
		logger.info("SID:{} ProcessTerminate Exception:P{}", sessionId);
	}
        logger.info("SID:{} Response sent to GxSMgr " + sessionId);
		return "CLEANUP_EVT";
    }

    

}

