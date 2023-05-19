package com.gx.grpc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

//import static com.validation.constants.ValidationConstants.INVK_RADIUS_EVT;
//import static com.validation.constants.ValidationConstants.POLICY_EVAL_EVT;

@Slf4j
@Service
public class RouteRequest {

    @Value("${tmt.gx.gxTdfRadiusEnabled}")
    boolean gxTdfRadius;
    



    public String IsTDFEnabled() {

    	log.info("GX_TDF_RADIUS_ENABLED : "+ gxTdfRadius);
        if (gxTdfRadius)
            return "INVK_RAD_EVT";
        else
            return "POLICY_EVAL_EVT";
    }
}

