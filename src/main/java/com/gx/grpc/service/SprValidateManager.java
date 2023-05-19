package com.gx.grpc.service;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gx.grpc.Gxdata.GxData;
import com.gx.grpc.gxDataStore.GxDataStore;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tmt.generic.cache.IdNotFoundException;
import tmt.generic.cache.LocalCache;
import tmt.generic.cache.LocalCacheConfigurationsNotInitilizedException;
import tmt.generic.cache.LocalCacheImpl;
import tmt.generic.scriptEngine.JavaScriptEngine;
import tmt.pcrf.sp.sp_session.SpRequestData;
import tmt.pcrf.sp.sp_session.SpResponseData;
import tmt.pcrf.sp.sp_session.SpSessionMgr;
import tmt.pcrf.sp.sp_session.SpSessionMgrImpl;

@Service
public class SprValidateManager {

	Logger logger = LoggerFactory.getLogger(SprValidateManager.class);

	private static SpSessionMgr spSessionMgr;

	@Autowired
	public SprValidateManager(SpSessionMgr spSessionMgr) {
		super();
		SprValidateManager.spSessionMgr = spSessionMgr;
	}

	public SprValidateManager() {

	}

//	GxDataStore gxDataStore = new GxDataStore();
	@Autowired
	private GxDataStore gxDataStore;

//	GxData gxData = new GxData();
//	@Autowired
//	private GxData gxData;

	// private JavaScriptEngine validateObj = new JavaScriptEngine();
//	@Autowired
//	private JavaScriptEngine validateObj;

	public String ValidateSubsProfile(String sessionId) {

		logger.info("SID: " + sessionId + " inside ValidateSubsProfile()");
		final JavaScriptEngine validateObj = new JavaScriptEngine();
		String sprResponse = null;
		GxData gxData = new GxData();
		try {

			gxData = gxDataStore.getGxContextData(sessionId);

			logger.info("SID: " + sessionId + " Getting Data from GxDataCache");

		} catch (Exception e1) {

			logger.error("SID: " + sessionId + " failed to get GxData Exception: " + e1.toString());

			return "SPR_NACK_EVT";
		}

		try {

			sprResponse = (String) gxData.getSpr_response();
			// validation script with path to be read from config from
			// application.properties - DIVYA
			validateObj.initialize("D:\\ValidateEngine"); // ScriptValidation.execute("ValidateSubsProfile.js");

			logger.info("SID: " + sessionId + " initializing Validator");

		} catch (Exception e) {

			logger.error("SID: " + sessionId + " failed to initialize Validator Exception: " + e.toString());

			return "SPR_NACK_EVT";
		}

		String json = null;
		try {
			json = new ObjectMapper().writeValueAsString(gxData.getSpr_response());

			logger.info("SID: " + sessionId + " Converting gxData.getSpr_response() as String");
		} catch (JsonProcessingException e1) {
			logger.error("SID: " + sessionId + " failed to Converting gxData.getSpr_response() as String Exception: "
					+ e1.toString());
			return "SPR_NACK_EVT";
		}

		try {

			Long status = validateObj.executeScript("expiry", json).getStatus();

			logger.info("SID: " + sessionId + " executing Script");

			if (status != 0) {

				logger.info("SID: " + sessionId + " SPR Validation failed status: " + status
						+ "description: status code not equal to zero info: ");

				return "SPR_NACK_EVT";

			} else {

				logger.info("SID: " + sessionId + " SPR validation success status :" + status + " info:");

				return "SPR_ACK_EVT";
			}

		} catch (Exception e) {

			logger.error("SID: " + sessionId + " failed to executeScript Exception: " + e.toString());

			return "SPR_NACK_EVT";
		}

	}

	public String GetSPRCache(String sessionId) {

		logger.info("SID: " + sessionId + "inside GetSPRCache() ");
		GxData gxData = new GxData();
		try {
			gxData = gxDataStore.getGxContextData(sessionId);

			logger.info("TBR----------0------------- SID:" + sessionId);
			logger.info("SID: " + sessionId + "Getting datas from GxDataCache to Form the request Data");

		} catch (Exception e1) {

			logger.error("SID: " + sessionId + " failed to get GxData Exception: " + e1.toString());

			return "SPR_NACK_EVT";
		}

		String diameterJson = gxData.getCcr();
		Map<String, Object> response;
		String subsId;
		Integer subsType;
		Integer reqNo;
		try {
			response = new ObjectMapper().readValue(diameterJson, HashMap.class);

			Map<String, Object> message = (Map<String, Object>) response.get("message");

			Map<String, Object> Subscription = (Map<String, Object>) message.get("Subscription-Id");

			subsId = Subscription.get("Subscription-Id-Data").toString();

			subsType = (Integer) Subscription.get("Subscription-Id-Type");

			reqNo = (Integer) message.get("CCRequestNumber");

		} catch (Exception e) {

			logger.error("SID: " + sessionId + " failed to fetch data from  CCR Exception: " + e.toString());

			return "SPR_NACK_EVT";

		}

		SpRequestData spRequestData = new SpRequestData();
		spRequestData.setUniqueMsgRef(sessionId);
		spRequestData.setSessionsId(sessionId);
		spRequestData.setSubscriptionId(subsId);
		spRequestData.setSubscriptionIdType(subsType);
		spRequestData.setReqNo(reqNo);

		logger.info("TBR----------1------------- SID:" + sessionId);

		SpResponseData spResponseData = spSessionMgr.getSubscriberProfileFromCache(spRequestData);

		logger.info("TBR----------2------------- SID:" + sessionId + " D1:" + spResponseData.getSubscriberProfile());

		if (spResponseData != null) {
			try {

				if (spResponseData.getStatus() == 0) {
//				String spr_data = spResponseData.getSubscriberProfile();
//
//				gxData.setSpr_data(spr_data);

					gxData.setSpr_response(spResponseData);

					gxDataStore.updateGxContextData(sessionId, gxData);

					logger.info("SID:" + sessionId + " gxData updated");
				} else {
					logger.error("SID:" + sessionId + " Response Status is not equal to ZERO");

					return "SPR_NACK_EVT";
				}

			} catch (Exception e1) {

				logger.error("SID: " + sessionId + " failed to update GxData Exception: " + e1.toString());

				return "SPR_NACK_EVT";
			}
		}

		logger.info("TBR----------3------------- SID:" + sessionId);

		logger.info("SID:" + sessionId + " Returning SPR_VALIDATE_EVT from GetSPRCache");
		return "SPR_VALIDATE_EVT";
	}

	public String sendAck(String sessionId) {

		logger.info("SID: " + sessionId + " SPR Validation ACK");

		logger.info("SID:" + sessionId + " Returning SPR_ACK_EVT from sendAck");
		return "SPR_ACK_EVT";
	}

	public String sendNack(String sessionId) {
		GxData gxData = new GxData();
		try {
			gxData = gxDataStore.getGxContextData(sessionId);

			logger.info("SID: " + sessionId + "Getting datas from GxDataCache for get the result code sendNack");

		} catch (Exception e1) {

			logger.error("SID: " + sessionId + " failed to get GxData Exception: " + e1.toString());

			return "SPR_NACK_EVT";
		}
		// Fetch the errorcode in the subsprofile
		// set the result code onto Gx store
		gxData.resultCode = gxData.getResultCode();

		logger.info("SID: " + sessionId + " SPR Validation NACk RC:" + gxData.resultCode);

		logger.info("SID:" + sessionId + " Returning SPR_NACK_EVT from sendNack");
		return "SPR_NACK_EVT";

	}

	public void HandleSPRTimeout() {

	}

}