package com.gx.grpc.service;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gx.grpc.Gxdata.GxData;
import com.gx.grpc.gxDataStore.GxDataStore;

import lombok.extern.slf4j.Slf4j;

//import com.example.demo.model.ReqModel;

import tmt.pcrf.spr.radius.RadiusInterface;
import tmt.pcrf.spr.radius.RadiusInterface.RadiusCommand;
import tmt.pcrf.spr.radius.RadiusResponse;

//@Component
@Configuration
//@ConfigurationProperties(prefix="gx")
@Slf4j
public class HandleRadiusConn {
	
	@Value("${EC_RADIUS_NACK}")
	private Integer ec_radius_nack;

	/**
	 * @Value("${templateDataPath}") private String templateDataPath;
	 * 
	 * @Value("${templateDataFile}") private String templateDataFile;
	 * 
	 * @Value("${inputDataFile}") private String inputDataFile;
	 * 
	 * @Value("${ipAddress}") private String ipAddress;
	 * 
	 * @Value("${sharedSecret}") private String sharedSecret;
	 * 
	 * @Value("${port}") private Integer port;
	 */
	private String templateDataPath = "C:/Users/sathvikb/Desktop/GXSESSIONIMPBACKUP/GXGRPC25/spring-grpc/calculator-service/src/main/resources";

	private String templateDataFile = "Templete.json";

	private String inputDataFile = "C:/Users/sathvikb/Desktop/GXSESSIONIMPBACKUP/GXGRPC25/spring-grpc/calculator-service/src/main/resources/input.json";

	private String ipAddress = "10.0.2.80";

	private String sharedSecret = "secret1";

	private Integer port = 1812;

//	log log = logFactory.getlog(HandleRadiusConn.class);

	GxData gxData = new GxData();

	@Autowired
	GxDataStore gxDataStore;

	//@Autowired
	RadiusInterface radiusInterface = new RadiusInterface();
	
	public Map<String, Object> convertToMap(String ccr)  {
		Map<String, Object> messageNodeResponse;
try {
        Map<String, Object> response = new ObjectMapper().readValue(ccr, HashMap.class);
        messageNodeResponse = (Map<String, Object>) response.get("message");
}catch(Exception ex) {
	log.error("Map Convert exception:{}", ex);
	return null;
}

        return messageNodeResponse;
    }


	public String InvokeRadReq(String sessionId) {

		try {
		try {

			// initilizing radius req using radius obj
//			if (true == radiusInterface.initialize(templateDataPath, templateDataFile, ipAddress, port, "user",
//					sharedSecret)) {
//				log.info("SID: " + sessionId + " RADIUS connection initialization successful");
//
//			}

		} catch (Exception e1) {
			log.error("SID: " + sessionId + " RADIUS connection failed " + e1.toString());
			return "RAD_NACK_EVT";
		}

		// using cache object getting gxData data for sessionId
		try {
			gxData = gxDataStore.getGxContextData(sessionId);
			log.info("SID: " + sessionId + " Getting Data from GxDataCache");
		} catch (Exception e) {
			log.error("SID: " + sessionId + " failed to get GxData Exception: " + e.toString());
			return "RAD_NACK_EVT";
		}

		// getting CCR
		String diameterJson = gxData.getCcr();

//		String diameterJson = "{\r\n" + "    \"header\": {\r\n" + "        \"commandCode\": 272,\r\n"
//				+ "        \"flag\": 8,\r\n" + "        \"application-id\": 0,\r\n"
//				+ "        \"hop-by-hop-id\": \"<string>\",\r\n" + "        \"end-to-end-id\": \"<string>\"\r\n"
//				+ "    },\r\n" + "    \"message\": {\r\n"
//				+ "        \"SessionId\": \"seagull.mydomain.com;1096298391;1\",\r\n"
//				+ "        \"Origin-Host\": \"seagull.mydomain.com\",\r\n"
//				+ "        \"Origin-Realm\": \"seagull.mydomain.com\",\r\n"
//				+ "        \"Vendor-Specific-Application-Id\": {\r\n" + "            \"Vendor-Id\": 10415,\r\n"
//				+ "            \"Auth-Application-Id\": 4\r\n" + "        },\r\n"
//				+ "        \"CC-Request-Type\": 0,\r\n" + "        \"CC-Request-Number\": 0,\r\n"
//				+ "        \"Subscription-Id\": {\r\n" + "            \"Subscription-Id-Type\": 0,\r\n"
//				+ "            \"Subscription-Id-Data\": \"0844098440\"\r\n" + "        }\r\n" + "    }\r\n" + "}";

		// passing ccr to SendRadiusRequest
		RadiusResponse radiusResponse;
		try {
			radiusResponse = radiusInterface.SendRadiusRequest(sessionId, diameterJson, "logPrefix");
			log.info("SID: " + sessionId + " passing ccr to SendRadiusRequest");
			log.info(" passing ccr to SendRadiusRequest");
		} catch (Exception e) {
			log.error(
					"SID: " + sessionId + " failed to passing ccr to SendRadiusRequest Exception: " + e.toString());
			log.info(" failed to passing ccr to SendRadiusRequest Exception: " + e.toString());
			return "RAD_NACK_EVT";
		}
		
		Map<String, Object> messageNodeResponse = null;
		
		String ccr = gxData.getCcr();
		messageNodeResponse = convertToMap(ccr);
		Integer ccr_req_type = (Integer)messageNodeResponse.get("CC-Request-Type");

		RadiusCommand radiusCommand = radiusResponse.getReqType();

		String command = radiusCommand.toString();
		// switch case
		switch (command) {

		case "ACCOUNT_ACCEPT":
			log.info("SID: " + sessionId + " Request Type " + command);
			return "RAD_ACK_EVT";

		case "ACCOUNT_REJECT":
			log.info("SID: " + sessionId + " Request Type " + command);

			gxData.setResultCode(ec_radius_nack);
			try {
				GxDataStore.updateGxContextData(sessionId, gxData);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(ccr_req_type == 1)
			{
				return "INVK_CCA_EVT";
			}
			return "RAD_NACK_EVT";

		default:
			log.error("SID: " + sessionId + command + " Invalid Request Type");
			if(ccr_req_type == 1)
			{
				return "INVK_CCA_EVT";
			}
			return "RAD_NACK_EVT";
		}
		} catch (Exception e) {
		log.info("SID:{} HandleRadius Exception:{}",sessionId, e);
		}

		return "RAD_ACK_EVT";
	}

	public String SendRadAck(String sessionId) {

		log.info("SID: " + sessionId + " RADIUS ACK");
		return "POLICY_EVAL_EVT";

	}

	public String SendRadNack(String sessionId) {

		log.info("SID: " + sessionId + "  RADIUS NACK");
		return "INVK_CCA_EVT";

	}

	public String ProcessRadResponse(String sessionId) {

		return "RAD_ACK_EVT";
	}
}
