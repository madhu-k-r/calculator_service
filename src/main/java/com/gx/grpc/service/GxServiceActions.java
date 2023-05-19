package com.gx.grpc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GxServiceActions {

	//@Autowired
	//ProcessCCRReq processCCRReq;

	@Autowired
	ProcessCCA processCCA;

	@Autowired
	ProcessCCRReq processCCRReq;
	
	@Autowired
	HandleRadiusConn handleRadiusConn;
	
	@Autowired
	SprValidateManager sprValidateManager;
	
	@Autowired
	SyValidateManager syValidateManager;
	
	@Autowired
	RouteRequest routeRequest;
	
	@Autowired
	PolicyEvaluation policyEvaluation;

	// @SuppressWarnings("deprecation")
	public String ProcessInitial(StateContext<String, String> ctx) {
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("------------- ProcessInitial SID:{}", sessionid);
		String response = processCCRReq.ProcessInitial(sessionid);
		log.info("SID: " + sessionid + "," + "Response ProcessInitialReq: " + response);
		StateMachine<String, String> sm = ctx.getStateMachine();
		boolean status = sm.sendEvent(response);
		log.info("EVENT status:{}", status);

		return "SPR_GET_EVT";
	}

	// @SuppressWarnings("deprecation")
	public String GetSPRCache(StateContext<String, String> ctx) {
//		SprValidateManager sprValidateManager = new SprValidateManager();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- GetSPRCache SID:{}", sessionid);
		//////////////////////////////////////////////////////////////////
		String response = sprValidateManager.GetSPRCache(sessionid);
		log.info("SID:{} GetSPRCache Response:{}", sessionid, response);
//////////////////////////////////////////////////////////////////
		StateMachine<String, String> sm = ctx.getStateMachine();
		boolean status = sm.sendEvent("SPR_VALIDATE_EVT");
		log.info("EVENT status:{}", status);

		return "SPR_VALIDATE_EVT";
	}

	public String ValidateSubsProfile(StateContext<String, String> ctx) {
		//SprValidateManager sprValidateManager = new SprValidateManager();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- ValidateSubsProfile SID:{}", sessionid);
//////////////////////////////////////////////////////////////////
		String response = sprValidateManager.ValidateSubsProfile(sessionid);
		log.info("SID:{} ValidateSubsProfile Response:{}",sessionid, response);
//////////////////////////////////////////////////////////////////
		StateMachine<String, String> sm = ctx.getStateMachine();
		boolean status = sm.sendEvent("SPR_ACK_EVT");
		//boolean status = sm.sendEvent(response);
		log.info("EVENT status:{}", status);
		return "SPR_ACK_EVT";
	}

	public String SendSPRNack(StateContext<String, String> ctx) {
		//SprValidateManager sprValidateManager = new SprValidateManager();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- SendSPRNack SID:{}", sessionid);

		String response = sprValidateManager.sendNack(sessionid);
		log.info("SID:{} SendSPRNack Response:{}",sessionid, response);
		
		StateMachine<String, String> sm = ctx.getStateMachine();
		//boolean status = sm.sendEvent("SPR_ACK_EVT");
		boolean status = sm.sendEvent(response);
		log.info("EVENT status:{}", status);
		return "SPR_ACK_EVT";
	}

	public String GetSyCache(StateContext<String, String> ctx) {
		//SyValidateManager syValidateManager = new SyValidateManager();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- GetSyCache SID:{}", sessionid);

		//String response = syValidateManager.GetSyCache(sessionid);
		//log.info("SID:{} GetSyCache Response:{}",sessionid, response);

		StateMachine<String, String> sm = ctx.getStateMachine();
		boolean status = sm.sendEvent("SY_VALIDATE_EVT");
		//boolean status = sm.sendEvent(response);
		log.info("EVENT status:{}", status);
		return "SY_VALIDATE_EVT";
	}

	public String ValidateSyRsp(StateContext<String, String> ctx) {
		//SyValidateManager syValidateManager = new SyValidateManager();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- ValidateSyRsp SID:{}", sessionid);

		//String response = syValidateManager.ValidateProfileCounters(sessionid);
		//log.info("SID:{} ValidateSyRsp Response:{}",sessionid, response);

		StateMachine<String, String> sm = ctx.getStateMachine();
		boolean status = sm.sendEvent("SY_ACK_EVT");
		//boolean status = sm.sendEvent(response);
		log.info("EVENT status:{}", status);
		return "SY_ACK_EVT";
	}

	public String SendSyNack(StateContext<String, String> ctx) {
		//SyValidateManager syValidateManager = new SyValidateManager();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- SendSyNack SID:{}", sessionid);
		String response = syValidateManager.sendNack(sessionid);
		StateMachine<String, String> sm = ctx.getStateMachine();
		log.info("SID:{} SendSyNack Response:{}",sessionid, response);

		boolean status = sm.sendEvent(response);
		//boolean status = sm.sendEvent("SY_ACK_EVT");
		log.info("EVENT status:{}", status);
		return "SY_ACK_EVT";
	}

	public String RouteRequest(StateContext<String, String> ctx) {
		//RouteRequest routeRequest = new RouteRequest();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- RouteRequest SID:{}", sessionid);

		String response = routeRequest.IsTDFEnabled();
		log.info("SID:{} RouteRequest Response:{}", sessionid, response);

		StateMachine<String, String> sm = ctx.getStateMachine();
		boolean status = sm.sendEvent(response);
		//boolean status = sm.sendEvent("INVK_RAD_EVT");
		log.info("EVENT status:{}", status);
		return "INVK_RAD_EVT";
	}

	public String InvokeRadReq(StateContext<String, String> ctx) {
		//HandleRadiusConn handleRadiusConn = new HandleRadiusConn();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- InvokeRadReq SID:{}", sessionid);
		String response = handleRadiusConn.InvokeRadReq(sessionid);
		log.info("SID:{} RouteRequest Response:{}", sessionid, response);
		
		StateMachine<String, String> sm = ctx.getStateMachine();
		log.info("TBR ================== SM OBJ:"+sm);
		boolean status = sm.sendEvent(response);
		//boolean status = sm.sendEvent("RAD_RSP_EVT");
		log.info("EVENT status:{}", status);
		return "RAD_RSP_EVT";
	}

	public String EvalPolicyandExecute(StateContext<String, String> ctx) {
		//PolicyEvaluation policyEvaluation = new PolicyEvaluation();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- EvalPolicyandExecute SID:{}", sessionid);
		String response = policyEvaluation.policyEvaluation(sessionid);
		log.info("SID:{} RouteRequest Response:{}", sessionid, response);

		StateMachine<String, String> sm = ctx.getStateMachine();
		boolean status = sm.sendEvent(response);
		//boolean status = sm.sendEvent("POLICY_ACK_EVT");
		log.info("EVENT status:{}", status);
		return "POLICY_ACK_EVT";
	}

	public String ProcessRadResponse(StateContext<String, String> ctx) {
		//HandleRadiusConn handleRadiusConn = new HandleRadiusConn();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- ProcessRadResponse SID:{}", sessionid);
///////////////////
		String response = handleRadiusConn.ProcessRadResponse(sessionid);
		log.info("SID:{} ProcessRadResponse Response:{}", sessionid, response);
////////////////////////////
		StateMachine<String, String> sm = ctx.getStateMachine();
		//boolean status = sm.sendEvent(response);
	    boolean status = sm.sendEvent("RAD_ACK_EVT");
		log.info("EVENT status:{}", status);
		return "RAD_ACK_EVT";
	}

	public String SendRadNack(StateContext<String, String> ctx) {
		//HandleRadiusConn handleRadiusConn = new HandleRadiusConn();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- SendRadNack SID:{}", sessionid);

		String response = handleRadiusConn.SendRadNack(sessionid);
		log.info("SID:{} SendRadNack Response:{}", sessionid, response);

		StateMachine<String, String> sm = ctx.getStateMachine();
		boolean status = sm.sendEvent(response);
		//boolean status = sm.sendEvent("RAD_ACK_EVT");
		log.info("EVENT status:{}", status);
		return "RAD_ACK_EVT";
	}

	public String SendRadAck(StateContext<String, String> ctx) {
		//HandleRadiusConn handleRadiusConn = new HandleRadiusConn();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- SendRadAck SID:{}", sessionid);

		String response = handleRadiusConn.SendRadAck(sessionid);
		log.info("SID:{} RouteRequest Response:{}", sessionid, response);
		StateMachine<String, String> sm = ctx.getStateMachine();
		boolean status = sm.sendEvent(response);
		//boolean status = sm.sendEvent("POLICY_EVAL_EVT");
		log.info("EVENT status:{}", status);
		return "POLICY_EVAL_EVT";
	}

	public String FormCCANack(StateContext<String, String> ctx) {
		
		//ProcessCCA processCCA;// = new ProcessCCA();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- FormCCANack SID:{}", sessionid);

        String response = processCCA.FormCCANack(sessionid);
		log.info("SID:{} FormCCANack Response:{}", sessionid, response);
		StateMachine<String, String> sm = ctx.getStateMachine();
		boolean status = sm.sendEvent(response);
		//boolean status = sm.sendEvent("POLICY_ACK_EVT");
		log.info("EVENT status:{}", status);
		return "POLICY_ACK_EVT";
	}
	
	public String SendInitialNack(StateContext<String, String> ctx) {
		//ProcessCCA processCCA = new ProcessCCA();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- FormCCANack SID:{}", sessionid);

        String response = processCCA.FormInitialCCANack(sessionid);
		log.info("SID:{} SendInitialNack Response:{}", sessionid, response);
		StateMachine<String, String> sm = ctx.getStateMachine();
		boolean status = sm.sendEvent(response);
		//boolean status = sm.sendEvent("POLICY_ACK_EVT");
		log.info("EVENT status:{}", status);
		return "POLICY_ACK_EVT";
	}

//later removes throws
	public String FormCCAandSend(StateContext<String, String> ctx)
			throws JsonMappingException, JsonProcessingException {
		//ProcessCCA processCCA = new ProcessCCA();
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- FormCCAandSend SID:{}", sessionid);

		String response = processCCA.FormCCAAck(sessionid);
		log.info("SID:{} RouteRequest Response:{}", sessionid, response);

		StateMachine<String, String> sm = ctx.getStateMachine();
		boolean status = sm.sendEvent(response);
		//boolean status = sm.sendEvent("SPR_ACK_EVT");
		log.info("EVENT status:{}", status);
		return "";
	}

	public void Await(StateContext<String, String> ctx) {
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("------------- Await SID:{}", sessionid);
	}

	public String ProcessUpdate(StateContext<String, String> ctx) {
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("------------- ProcessUpdate SID:{}", sessionid);
		String response = processCCRReq.ProcessUpdate(sessionid);
		StateMachine<String, String> sm = ctx.getStateMachine();
		log.info("SID:{} Generate event:{}",sessionid,"SPR_GET_EVT");
		boolean status = sm.sendEvent("SPR_GET_EVT");
		log.info("EVENT status:{}", status);
		return "SPR_GET_EVT";
	}

	public String ProcessTerminate(StateContext<String, String> ctx) {
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- ProcessTerminate SID:{}", sessionid);

		String response = processCCRReq.ProcessTermination(sessionid);
		StateMachine<String, String> sm = ctx.getStateMachine();
		boolean status = sm.sendEvent("STOP_EVT");
		log.info("EVENT status:{}", status);
		return "STOP_EVT";
	}

	public void CleanUp(StateContext<String, String> ctx) {
		String sessionid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
		log.info("TBR ------------- CleanUp SID:{}", sessionid);
		
		//CleanUp
	}

	public static void defaultMethod(String unwantedMthodName) {
		log.error("Unknown method " + unwantedMthodName + " found!");
	}
}
