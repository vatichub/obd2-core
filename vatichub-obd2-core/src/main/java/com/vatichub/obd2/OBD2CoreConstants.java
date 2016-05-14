package com.vatichub.obd2;

import java.util.HashMap;

public class OBD2CoreConstants {
	public static final String APPTAG = "OBD2Core";
	public static final String BTMSGTAG = "OBD2Core-BTMSG";
	
	public static final String CONF_ALLPIDS = "all_pids";
	public static final String CONF_LABEL = "label";
	public static final String CONF_MODE = "mode";
	public static final String CONF_PID = "pid";
	public static final String CONF_BYTES = "response_bytes";
	public static final String CONF_EXPRESSION = "expression";
	public static final String CONF_UNIT = "unit";
	public static final String CONF_MIN = "min";
	public static final String CONF_MAX = "max";
	
	public static final String CONF_SIDDHI = "siddhi";
	public static final String CONF_SIDDHI_STREAM_DEF = "stream_definitions";
	public static final String CONF_SIDDHI_QUERIES = "queries";
	
	public static final String CONF_LOGGING = "logging";
	public static final String CONF_LOGGING_PARAMETERS = "parameters";
	public static final String CONF_LOGGING_PARAMETERS_GPSLONG = "gps_long";
	public static final String CONF_LOGGING_PARAMETERS_GPSLAD  = "gps_lad";
	public static final String CONF_LOGGING_PARAMETERS_GPSALT  = "gps_alt";

	
	public static final String OBDII_REAL_TIME_DATA="obd2_real_time_data";
	public static final String OBDII_REAL_TIME_DATA_LABEL="OBD-II Real Time Data";
	
	public static final String DISPLAY_GRAPH="display_graph";
	public static final String DISPLAY_DIAL_1="display_dial";	
	public static final String DISPLAY_DIGITAL="display_digital";	
	
	public static final String OBDII_SPEED="obd2_speed";	
	public static final String OBDII_SPEED_LABEL="OBD-II Speed(km/h)";	
	
	public static final String OBDII_RPM="obd2_rpm";	
	public static final String OBDII_RPM_LABEL="RPM";
	
	public static final String OBDII_FUEL_RATE="obd2_fuelrate";
	public static final String OBDII_FUEL_RATE_LABEL="Fuel Rate(l/h)";	
	
	public static final String TIME="time";	
	public static final String VALUE="value";		

	public static final String SIDDHI_TIMESTAMP = "timestamp";
	public static final String TIMESTAMP = "timestamp";
	
	//settings - keys
	public static final String LOGGING_APP_PATH = "KampanaAppLog";
	public static final String APP_USED_COUNT = "app_number_of_times_open";
	public static final String LAST_CONNECTED_BT_ADDR = "last_bt_addr";
	
	//obd2
	public static final String OBD2_UPDATE_DELAY = "obd2_update_delay";
	
	//http
	public static final String SEND_TO_SERVER = "send_to_server";

    //logging
    public static final String LOGGING_PARAMERTERS = "logging_parameters";
	
	//----end settings-keys
	
	
	
	// -- default values and settings	
	//ui
	public static final int MAX_LINES_PER_GRAPH=5;
	public static final int GRAPH_HEIGHT_BIG =600;
	public static final int GRAPH_HEIGHT_MEDIUM =400;
	
	public static final double GRAPH_WIDTH_HEIGHT_FACTOR_ADJUST = 0.2;
	public static final double GRAPH_WIDTH_HEIGHT_FACTOR_MEDIUM =0.7;  //height is 'this' times width
	public static final double GRAPH_WIDTH_HEIGHT_FACTOR_BIG =0.9;
	
	public static final double GAUGE_WIDTH_HEIGHT_FACTOR_MEDIUM = 0.5;
	public static final double GAUGE_WIDTH_HEIGHT_FACTOR_ADJUST = -0.1;
	
	public static final int DISPLAY_LABEL_HEIGHT_ADJUST = 120;
	
	//logging
	public static final int LOGGING_QUEUE_MAX = 2048;
	public static final String LOGGING_ENABLE_DEFAULT = "true";	
	
	//siddhi
	public static final String[] SIDDHI_DATA_TYPES= {"float","int","double","long"};
	
	//obd2
	public static final int MIN_UPDATE_TIMEOUT=100;
	public static final int SUPPORTED_PIDS_RESPONSE_LENGTH = 8;
	public static final int OBD2_UPDATE_DELAY_DEFAULT = 1000;
	public static final int MAX_PIDS_SUPPORTED = 161;
	
	//bluetooth
	public static final int MAX_BT_CONNECT_ATTEMPTS_DEFAULT = 5;
	public static final boolean BT_AUTOCONNECT_DEFAULT = true;
	public static final boolean BT_AUTO_ENABLE_DEFAULT = true;
	
	//http
	public static final boolean SEND_TO_SERVER_DEFAULT = false;
	
	//feature
	public static final String FEATURES_DEFAULT = "Reckless Driving Detection,O2 Sensor Failure Detection,MAF Sensor Failure Detection,Driving Anomaly Detection,High Fuel Consumption Alert,High Coolant Temperature Alert,Trip Summary,Real Time Speed Monitoring";
	public static final String FEATURES_ALL = "Reckless Driving Detection,O2 Sensor Failure Detection,MAF Sensor Failure Detection,Driving Anomaly Detection,High Fuel Consumption Alert,High Coolant Temperature Alert,Trip Summary,Real Time Speed Monitoring";
	
	//feature parameter defaults in the order name:description:default_value
	public static final HashMap<String, String> DEFAULT_PARAMS = new HashMap<String,String>();
	static {
		DEFAULT_PARAMS.put("High Coolant Temperature Alert", "threshold:104");
		DEFAULT_PARAMS.put("High Fuel Consumption Alert", "threshold:10");
	}
	//public static final String COOLANT_TEMP_DEFAULT_PARAMS = "threshold:Generate alert when above this value:104";
	//public static final String FUEL_CONSUMPTION_DEFAULT_PARAMS = "threshold:Generate alert when above this value:104";
	
	//notification
	public static final long[] NOTIFICATION_VIBRATE_PATTERN_DEFAULT = new long[] { 100, 500, 100, 200, 1000, 500, 100, 200, 1000};
	public static final int NOTIFICATION_LIGHT_COLOR_DEFAULT = 0xffff0000;
	public static final String ENABLE_SYSTEM_NOTIFICATIONS_DEFAULT = "true";;

	//displays
	//public static final String[] SINGLE_DISPLAYS_DEFAULT = {"Line_Graph:obd2_fuelrate"};
	//public static final String[] DUAL_DISPLAYS_DEFAULT = {"Line_Graph:obd2_speed Line_Graph:obd2_engine_rpm"};
	public static final String DISPLAYS_DEFAULT = "Dial_Display:obd2_speed Dial_Display:obd2_engine_rpm,Dial_Display:obd2_MAF Dial_Display:obd2_engine_temp Dial_Display:obd2_o2sensorV_B1S2,Line_Graph:obd2_speed Line_Graph:obd2_engine_rpm,Line_Graph:obd2_engine_temp,Line_Graph:obd2_MAF Line_Graph:obd2_engine_temp Line_Graph:obd2_o2sensorV_B1S2,Line_Graph:obd2_fuelrate";
	
	//demo http listner
	public static final int HTTP_LISTNER_PORT = 8900;
	
	//contexts
	public static final String CONTEXT_AUTHENTICATE = "/apis/authenticate";
	public static final String CONTEXT_VIN_PUBLISH = "/apis/vinPublish";
	public static final String CONTEXT_RECKLESS_DRIVING = "/recklessDrivingPublish";
	public static final String CONTEXT_DRIVING_ANOMALY = "/drivingAnomalyPublish";
	public static final String CONTEXT_REALTIME_SPEED = "/realTimeSpeedPublish";
	public static final String CONTEXT_MAFSENSOR = "/mafSensorPublish";
	public static final String CONTEXT_O2SENSOR = "/o2SensorPublish";
	
	
	//-- end default values
	
	public static boolean validateRootDataType(String datatype){
		if (datatype.equals(OBDII_REAL_TIME_DATA)){
			return true;
		}else return false;
	}
	
	public static boolean validateOBD2DataType(String datatype){
		return true;
//		if (datatype.equals(OBDII_SPEED) || datatype.equals(OBDII_RPM) || datatype.equals(OBDII_FUEL_RATE)){
//			return true;
//		}else return false;
	}
	
}
