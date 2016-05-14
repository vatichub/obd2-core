package com.vatichub.obd2.util;

import com.vatichub.obd2.OBD2CoreConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class OBD2CoreUtils {

	public static JSONObject createTimeValuePair(long time,double value){
		JSONObject obj=new JSONObject();		
		try {
			obj.put(OBD2CoreConstants.TIME, time);
			obj.put(OBD2CoreConstants.VALUE, value);
			return obj;
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static double getSpeedData(JSONObject data){		
		
		double speed=-1;
		try {
			speed = data.getJSONObject(OBD2CoreConstants.OBDII_REAL_TIME_DATA).getJSONObject(OBD2CoreConstants.OBDII_SPEED).getDouble(OBD2CoreConstants.VALUE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return speed;
	}
	
	public static long getTimestamp(JSONObject data){		
		
		long timestamp = 0;
		try {
			timestamp = data.getJSONObject(OBD2CoreConstants.OBDII_REAL_TIME_DATA).getJSONObject(OBD2CoreConstants.TIMESTAMP).getLong(OBD2CoreConstants.VALUE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timestamp;
	}
	
	public static double getFuelRateData(JSONObject data){		
		
		double fuelrate=-1;
		try {
			fuelrate = data.getJSONObject(OBD2CoreConstants.OBDII_REAL_TIME_DATA).getJSONObject(OBD2CoreConstants.OBDII_FUEL_RATE).getDouble(OBD2CoreConstants.VALUE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fuelrate;
	}
	
}
