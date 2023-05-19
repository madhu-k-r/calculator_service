package com.gx.grpc.constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GxSessionConstants {
	
	public static final String errstr = "DIAMETER_UNKNOWN_SESSION_ID";
	public static final Integer INITIAL = 1;
	public static final Integer UPDATE = 2;
	public static final Integer TERMINATE = 3;
	public static final Integer GET = 4;
	public static final String POLICY_ACK_EVT = "POLICY_ACK_EVT";
	public static final String POLICY_NACK_EVT = "POLICY_NACK_EVT";
	
	public static final String COMMAND_CODE = "commandCode";

    public static final String FLAG = "flag";

    public static final String APPLICATION_ID = "application-id";
    public static final String HOP_BY_HOP_ID = "hop-by-hop-id";
    public static final String END_TO_END_ID = "end-to-end-id";

    public static final String SESSION_ID="Session-Id";

    public static final String ORIGIN_HOST="Origin-Host";

    public static final String DEST_HOST="Destination-Host";

    public static final String ORIGIN_REALM="Origin-Realm";

    public static final String DEST_REALM="Destination-Realm";

    public static final String VENDOR_ID="Vendor-Id";
    public static final String CC_REQUEST_TYPE="CC-Request-Type";
    public static final String CC_Request_Number="CC-Request-Number";

    public static final String AUTH_APPLICATION_ID="Auth-Application-Id";

    public static final String IP_CAN_TYPE="IP-CAN-Type";

    public static final String CHARGING_RULE_BASE_NAME="Charging-Rule-Base-Name";

    public static final String CHARGING_RULE_INSTALL="Charging-Rule-Install";


    public static final String VENDOR_SPECIFIC_APPLICATION_ID="Vendor-Specific-Application-Id";

    public static final String DIAMETER_ERROR_INITIAL_PARAMETERS  = "DIAMETER_ERROR_INITIAL_PARAMETERS";

    public static final String RESULT_CODE="Result-Code";

    public static final String INVK_RADIUS_EVT="INVK_RADIUS_EVT";

    public static final String POLICY_EVAL_EVT="POLICY_EVAL_EVT";

    public static final String EXPERIMENTAL_RESULT="Experimental-Result";

    public static final String REQUEST_PROCESSING="Request-Processing";

    public static final String PENDING_REQUESTS="Pending-Requests";
}


/**
public static void main(String[] args){
    System.out.println(Constants.YES.getResponse());
    System.out.println(Constants.NO.getResponse());
    
    System.out.println(getDateTime());
}

enum Constants{
    YES("Y"), NO("N");
    private String value;

    public String getResponse() {
        return value;
    }
    Constants(String value){
        this.value = value;
    }
}
private static String getDateTime() {
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    return dateFormat.format(date);
}
*/