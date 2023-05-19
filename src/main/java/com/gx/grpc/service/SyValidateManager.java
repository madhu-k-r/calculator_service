package com.gx.grpc.service;

import com.gx.grpc.service.SyValidateManager;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gx.grpc.Gxdata.GxData;
import com.gx.grpc.gxDataStore.GxDataStore;
import tmt.generic.scriptEngine.JavaScriptEngine;

@Slf4j
@Service
public class SyValidateManager {

	@Autowired
	GxDataStore gxDataStore;// = new GxDataStore();
	GxData gxData = new GxData();
	// @Autowired
	// private SpSessionMgr spSessionMgr;

	public String ValidateProfileCounters(String sessionId) {
		JavaScriptEngine validateObj = new JavaScriptEngine();
		/*
		 * try { // localCache.intilize("abcd"); //Cache Name to be made configurable -
		 * DIVYA gxData = gxDataStore.getGxContextData(sessionId); log.info("SID: " +
		 * sessionId + " subscription-Id: " + gxData.getSubcriptionId +
		 * " Getting Data from gxDataStore"); } catch (Exception e1) {
		 * 
		 * log.error("SID: failed to get gxData Exception: " + e1.toString()); return
		 * "SY_NACK_EVT"; }
		 * 
		 * try {
		 * 
		 * validateObj.initialize("D:\\ValidationEngine");
		 * //ScriptValidation.execute("ValidateSubsProfile.js"); log.info("SID: " +
		 * sessionId + " subscription-Id: " + gxData.getSubcriptionId +
		 * " initializing Validator");
		 * 
		 * }catch (Exception e) {
		 * 
		 * log.error("SID: " + sessionId + " failed to initialize Validator Exception: "
		 * + e.toString());
		 * 
		 * return "SY_NACK_EVT"; }
		 * 
		 * try {
		 * 
		 * Long status = validateObj.executeScript("syvalidate", gxData).getStatus());
		 * 
		 * log.info("SID: " + sessionId + " subscription-Id: " + gxData.getSubcriptionId
		 * + " executing Script");
		 * 
		 * if (status != 0) {
		 * 
		 * log.info("SID: " + sessionId + " SPR Validation failed status: " + status +
		 * "description: status code not equal to zero info: ");
		 * 
		 * return "SY_NACK_EVT";
		 * 
		 * } else {
		 * 
		 * log.info("SID: " + sessionId + " SPR validation success status :" + status +
		 * " info:");
		 * 
		 * return "SY_ACK_EVT"; }
		 * 
		 * }catch (Exception e) {
		 * 
		 * log.error("SID: " + sessionId + " failed to executeScript Exception: " +
		 * e.toString());
		 * 
		 * return "SY_NACK_EVT"; }
		 */

		return "SY_ACK_EVT";
	}

	public String GetSyCache(String sessionId) {

		// gxData gxData;
		/*
		 * try { log.info("SID: " + sessionId + " Initilizing localCache"); //
		 * localCache.intilize("abcd"); //Cache Name to be made configurable - DIVYA
		 * gxData = gxDataStore.getGxContextData(sessionId); log.info("SID: " +
		 * sessionId + " subscription-Id: " + gxData.getSubcriptionId +
		 * "Getting datas from gxDataStore to Form the request Data");
		 * 
		 * } catch (Exception e1) {
		 * 
		 * log.error("SID: " + sessionId + " failed to get gxData Exception: " +
		 * e1.toString()); return "SY_NACK_EVT"; }
		 * 
		 * // Form the request Data SyRequestData syRequestData = new SyRequestData();
		 * syRequestData.setUniqueMsgRef(sessionId);
		 * syRequestData.setSessionId(sessionId);
		 * syRequestData.setSubscriptionId(gxData.getSubscriptionId);
		 * syRequestData.setSubscriptionIdType(gxData.getSubscriptionIdType);
		 * syRequestData.setExpiryTime(gxData.getExpiryTime);
		 * syRequestData.setReqNo(gxData.getReqNo);
		 * 
		 * SyResponseData syResponseData = syCachelib.getSprData(syRequestData);
		 * 
		 * if (syResponseData != null) { try { log.info("SID: " + sessionId +
		 * " Initilizing localCache"); // gxData.sprData // copy the json data from the
		 * sprresponse gxData = syResponseData; gxDataStore.updateData(sessionId,
		 * gxData); log.info("SID:" + sessionId + " subscription-Id: " +
		 * gxData.getSubcriptionId + " gxData updated"); } catch (Exception e1) {
		 * log.error("SID: " + sessionId + "failed to update gxData Exception: " +
		 * e1.toString()); return "SY_NACK_EVT"; } }
		 */
		return "SY_VALIDATE_EVT";
	}

	public String sendAck(String sessionId) {

		log.info("SID: " + sessionId + " SY Validation ACK");
		return "SY_ACK_EVT";
	}

	public String sendNack(String sessionId) {
		// gxData gxData;
		try {
			gxData = gxDataStore.getGxContextData(sessionId);
			log.info("SID: " + sessionId + " subscription-Id: " + "fetchfrom DS"
					+ "Getting datas from gxDataStore for get the result code sendNack");

		} catch (Exception e1) {
			log.error("SID: " + sessionId + " failed to get gxData Exception: " + e1.toString());
			return "SY_NACK_EVT";
		}
		// Fetch the errorcode in the subsprofile
		// set the result code onto Gx store
		// gxData.resultCode = gxData.getSYResultCode();
		log.info("SID: " + sessionId + " SY Validation NACk RC:" + gxData.resultCode);
		return "SY_NACK_EVT";

	}

	public void HandleSyTimeout() {

	}

}
