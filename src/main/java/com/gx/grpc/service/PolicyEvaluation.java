package com.gx.grpc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gx.grpc.Gxdata.GxData;
import com.gx.grpc.constants.GxSessionConstants;
import com.gx.grpc.gxDataStore.GxDataStore;

import lombok.extern.slf4j.Slf4j;
import tmt.generic.cache.LocalCacheImpl;

@Slf4j
@Service
public class PolicyEvaluation {

	//@Autowired
	GxDataStore gxDataStore ;
	
	public PolicyEvaluation(GxDataStore gxDataStore) {
		super();
		this.gxDataStore = gxDataStore;
	}

	@Value("${tmt.GxData.policydata}")
	private String policydata;

	public String policyEvaluation(String SID) {
		try {
			boolean isgxDataStoreintilize = gxDataStore.InitializeGxDataStore();
			if (isgxDataStoreintilize) {
				log.info("SID: " + SID + " GxDataStore Successfully initilized for policyEvaluation Request");
				GxData gxData = GxDataStore.getGxContextData(SID);
				gxData.setPolicyData(policydata);
				if (GxDataStore.updateGxContextData(SID, gxData)) {
					log.info("SID: " + SID + " Policy: " + policydata + " GxDataStore Updated");
					return GxSessionConstants.POLICY_ACK_EVT;
				}
				log.error("SID: " + SID + " Policy: " + policydata + " GxDataStore NotUpdated");
				return GxSessionConstants.POLICY_NACK_EVT;
			} else {
				log.error("SID: " + SID + "GxDataStore is not initilized ");
				return GxSessionConstants.POLICY_NACK_EVT;
			}
		} catch (Exception qw) {
			log.error("SID: " + SID + " Exception: " + qw);
			return GxSessionConstants.POLICY_NACK_EVT;
		}
	}

}
