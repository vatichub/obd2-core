package com.vatichub.obd2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;

import com.vatichub.obd2.fileio.AppFileLogger;
import com.vatichub.obd2.fileio.FileIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


public class OBD2CoreConfiguration {
	
	public static final String OBD2CORE_PREFS_NAME = "OBD2Core-pref-file";
	public static final String OBD2CORE_PREFS_DEFAULT_VALUE = "OBD2Core-pref-file-default-value";
	
	private static OBD2CoreConfiguration instance;

	private int OBD2updatespeed=500;

	private JSONObject allConfig;
	private JSONObject pidConfig;
	private JSONObject pidLabelToKey;
	
	private ArrayList<String> allPIDsList;
	private ArrayList<String> supportedPIDsList;
	
	private ArrayList<String> queryPIDsList;
    private ArrayList<String> requestedPIDsList;
    /*
	private HashSet<String> dashboardPIDsSet;
	private HashSet<String> loggingPIDsSet;
	private HashSet<String> featurePIDsSet;	*/
	
	private SharedPreferences settings;
	private long startTime;
	
	private HashMap<String, String> settingsMap;
	private ArrayList<String> logList;
	private ArrayList<String> loggingParamsList;

	private int appopencount;
	private Context context;
	private Handler mHandler;
	private Handler notificationActivityHandler;
	private boolean exitting;
	private Point displaySize;
    private ArrayList<FileIO> openFileList;

	private boolean[] supportedPIDsBool;
	
	private HashSet<Integer> activeNotificationIDs;

    private AppFileLogger appFileLogger;

	//developer settings
	private static final String DEBUG_MODE = "debug_mode";

	private OBD2CoreConfiguration(){
		startTime = System.currentTimeMillis();
		settingsMap = new HashMap<String, String>();
		supportedPIDsBool = new boolean[OBD2CoreConstants.MAX_PIDS_SUPPORTED];
		
		activeNotificationIDs=new HashSet<Integer>();

        /*
		dashboardPIDsSet = new HashSet<String>();
		loggingPIDsSet = new HashSet<String>();
		featurePIDsSet = new HashSet<String>();*/
		
		logList = new ArrayList<String>();
        openFileList = new ArrayList<FileIO>();
	}

    public void init(Context context){

        this.context = context;

        settings = context.getSharedPreferences(OBD2CORE_PREFS_NAME, 0);
        AssetManager assetManager = context.getAssets();
        InputStream input;
        try {
			/*Feed pidconfig according to the locale*/
            String country = context.getResources().getConfiguration().locale.getCountry();
            if(country.equals("RU")){
                input = assetManager.open("pidconfig-ru.json");
            }
            else{
                input = assetManager.open("pidconfig.json");
            }

            int size = input.available();
            byte[] buffer = new byte[size];
            //noinspection ResultOfMethodCallIgnored
            input.read(buffer);
            input.close();

            // byte buffer into a string
            String text = new String(buffer);

            JSONObject allconf = new JSONObject(text);

            JSONObject pidLabelToKey =new JSONObject();
            JSONObject pidconf = allconf.getJSONObject(OBD2CoreConstants.CONF_ALLPIDS);


            Iterator<String> itr = pidconf.keys();
            ArrayList<String> allPIDList = new ArrayList<String>();
            Log.i(OBD2CoreConstants.APPTAG, "Identified keys of pidconfig file:");
            while (itr.hasNext()) {
                String element = itr.next();
                allPIDList.add(element);
                Log.i(OBD2CoreConstants.APPTAG, element);
                pidLabelToKey.accumulate(pidconf.getJSONObject(element).getString(OBD2CoreConstants.CONF_LABEL), element);
            }

            Collections.sort(allPIDList, Collator.getInstance());

            setAllConfig(allconf);
            setPidConfig(pidconf);
            setAllPIDsList(allPIDList);
            setPidLabelToKey(pidLabelToKey);

            Log.d(OBD2CoreConstants.APPTAG, "Label to pid mapping: " + pidLabelToKey.toString());


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	public static OBD2CoreConfiguration getInstance(){
		if (instance == null){
			instance=new OBD2CoreConfiguration();
		}
		return instance;
	}

	public static void removeInstance(){ 
		instance = null;
	}

	public int getOBD2UpdateSpeed() {
		return OBD2updatespeed;
	}

	public void setOBD2UpdateSpeed(int OBD2updatespeed) {
		this.OBD2updatespeed = OBD2updatespeed;
		addSetting(OBD2CoreConstants.OBD2_UPDATE_DELAY, OBD2updatespeed +"");
	}

	public long getStartTime() {
		return startTime;
	}
	
	public void setPidConfig(JSONObject pidconfig) {
		this.pidConfig = pidconfig;
	}
	
	public void setAllConfig(JSONObject allconfig) {
		this.allConfig = allconfig;
	}
	
	public void setPidLabelToKey(JSONObject obj) {
		this.pidLabelToKey = obj;
	}
	
	public JSONObject getPidConfig() {
		return pidConfig;
	}
	
	public JSONObject getAllConfig() {
		return allConfig;
	}
	
	public JSONObject getPidLabelToKey() {
		return pidLabelToKey;
	}
	
	public void setAllPIDsList(ArrayList<String> pidList) {
		this.allPIDsList = pidList;
	}
	
	public ArrayList<String> getAllPIDsList() {
		return allPIDsList;
	}

	public void addSetting(String key, String value){
		settingsMap.put(key, value);
		
		SharedPreferences.Editor editor = settings.edit();
	    editor.putString(key, value);		
	    editor.apply();
	}
	
	public void addSetting(String key, boolean value){

		SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean(key, value);		
	    editor.apply();
	}
	
	public String getSetting(String key){
		
		String value = settingsMap.get(key);
		
		if(value == null){
			value = settings.getString(key, OBD2CORE_PREFS_DEFAULT_VALUE);
		}
		
		if (value.equals(OBD2CORE_PREFS_DEFAULT_VALUE)){
			value=null;
		}else{
			settingsMap.put(key, value);
		}
		
		return value;
		
	}
	
	public String getAndAddIfNotAvailable(String key,String value){
		String persvalue = settings.getString(key, OBD2CORE_PREFS_DEFAULT_VALUE);
		if (persvalue.equals(OBD2CORE_PREFS_DEFAULT_VALUE)){
			addSetting(key, value);
			settingsMap.put(key, value);
			return value;
		}else{
			settingsMap.put(key, persvalue);
			return persvalue;
		}
	}
	
	public String getSetting(String key, String defaultValue){
		
		String value = getSetting(key);
		if(value==null) value = defaultValue;
		return value;
		
	}

	public ArrayList<String> getSupportedPIDsList() {
		return supportedPIDsList;
	}
	
	public void setSupportedPIDsList(ArrayList<String> supportedPIDsList) {
		this.supportedPIDsList = supportedPIDsList;
		updateQueryPIDsList();
	}
	
	public ArrayList<String> getLogList() {
		if(logList == null)
			logList=new ArrayList<String>();
		return logList;
	}
	
	public void setLogList(ArrayList<String> logList) {
		this.logList = logList;
	}

    /*
	public List<String> getLoggingParametersList() {
		if(loggingParamsList==null){
			
			try {
				JSONArray loggingPIDsDefault = allConfig.getJSONObject(OBD2CoreConstants.CONF_LOGGING).getJSONArray(OBD2CoreConstants.CONF_LOGGING_PARAMETERS);
				JSONObject loggingParaJSONObjDefault = new JSONObject();
				loggingParaJSONObjDefault.put(OBD2CoreConstants.CONF_LOGGING_PARAMETERS, loggingPIDsDefault);
				
				String setting = getSetting(OBD2CoreConstants.LOGGING_PARAMERTERS, loggingParaJSONObjDefault.toString());
				
				JSONArray loggingPIDs = new JSONObject(setting).getJSONArray(OBD2CoreConstants.CONF_LOGGING_PARAMETERS);
				ArrayList<String> loggingParamsList = new ArrayList<String>();
				for(int i=0;i<loggingPIDs.length();i++){
					loggingParamsList.add(loggingPIDs.getString(i));
				}		
				this.loggingParamsList=loggingParamsList;
				
				loggingPIDsSet.clear();
				loggingPIDsSet.addAll(loggingParamsList);
				updateQueryPIDsList();
				
			} catch (JSONException e) {
				sendMessageToUI("Error in retrieving logging parameter configuration..");
				e.printStackTrace();
			}
			
		}		
		return loggingParamsList;
	}*/

    /*
	public void setLoggingParametersList(ArrayList<String> paraList) {
		loggingParamsList = paraList;
		
		loggingPIDsSet.clear();
		loggingPIDsSet.addAll(loggingParamsList);
		updateQueryPIDsList();
		
		JSONObject loggingParaJSONObj = new JSONObject();		
		JSONArray loggingParasJSONArray = new JSONArray();
		
		for (String value : paraList) {
			loggingParasJSONArray.put(value);
		}		
		try {
			loggingParaJSONObj.put(OBD2CoreConstants.CONF_LOGGING_PARAMETERS, loggingParasJSONArray);
			String setting = loggingParaJSONObj.toString();
			addSetting(OBD2CoreConstants.LOGGING_PARAMERTERS, setting );
		
		} catch (JSONException e) {
			sendMessageToUI("Error in saving logging parameter configuration..");
			e.printStackTrace();
		}
	}*/

	public void setAppFileLogger(AppFileLogger appFileLogger) {
		this.appFileLogger = appFileLogger;
	}
	
	public AppFileLogger getAppFileLogger() {
		return appFileLogger;
	}
	/*
	public void setDataLogger(DataLogger logger) {
		this.datalogger = logger;
	}
	
	public DataLogger getDataLogger() {
		return datalogger;
	}
	
	public String getLoggingPath() {
		return loggingPath;
	}*/
	
	public int getAppOpenCount() {
		return appopencount;
	}
	
	public Context getContext() {
		return context;
	}
	
	public Handler getHandler() {
		return mHandler;
	}


	public void sendMessageToUI(String message){
        //todo
    /*    Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_SHOW_MESSAGE);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.MESSAGE_MESSAGE, message);
        msg.setData(bundle);
        mHandler.sendMessage(msg); */
	}

	public boolean isExitting() {
		return exitting;
	}
	
	public void setExitting(boolean exitting) {
		this.exitting = exitting;
	}


	public void addOpenedFile(FileIO file){
		openFileList.add(file);
	}
	
	public ArrayList<FileIO> getOpenFileList() {
		return openFileList;
	}

    /*
	public void addBulkTransmitter(BulkTransmitter transmitter){
		bulktransmitters.add(transmitter);
	}
	
	public List<BulkTransmitter> getBulkTransmitters() {
		return bulktransmitters;
	}
	
	public void setMockECU(MockECU mecu) {
		this.mockecu = mecu;
	}
	
	public MockECU getMockECU() {
		return mockecu;
	}
	
	public ArrayList<SensorNotificationObject> getSensorNotificationList(){
		return snlist;
	}

	public void sendToastToUI(String message) {
        Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST, message);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
	}*/
	
	public boolean isPIDLoggingEnabled(String pid){
		return loggingParamsList.contains(pid);
	}
	
	public void setSupportedPIDsBool(boolean[] values){
		this.supportedPIDsBool = values;
	}
	
	public boolean isPIDSupported(String pid){
		
		try {
			JSONObject pidObj = pidConfig.getJSONObject(pid);
			String mode = pidObj.getString(OBD2CoreConstants.CONF_MODE);
			String pidHex = pidObj.getString(OBD2CoreConstants.CONF_PID);
			if(mode.equals("01")){
				int pidDec = Integer.parseInt(pidHex,16);
				return supportedPIDsBool[pidDec];
			}else{
				return false;
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

    /*
	public void addNotification(SensorNotificationObject notificationObj){
		snlist.add(0,notificationObj);
		Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_ADD_NOTIFICATION);
        mHandler.sendMessage(msg);
        
        try{
	        if(notificationActivityHandler!=null){
	        	msg = notificationActivityHandler.obtainMessage(NotificationsActivity.MESSAGE_UPDATE_NOTIFICATION_LIST);
	        	notificationActivityHandler.sendMessage(msg);
	        }
        }catch(NullPointerException e){        	
        }
        
        boolean systemNotificationsEnabled = Boolean.valueOf(getSetting(SettingsActivity.ENABLE_SYSTEM_NOTIFICATIONS, Constants.ENABLE_SYSTEM_NOTIFICATIONS_DEFAULT));
        if(systemNotificationsEnabled){
        	sendSystemNotification(notificationObj);
        }
	}
	
	public void setAllNotificationsOld(){
		for (SensorNotificationObject sobj : snlist) {
			sobj.setNewNotification(false);
		}
	}


	public void setNotificationActivityHandler(Handler mHandler) {
		this.notificationActivityHandler = mHandler;
	}

	public void clearAllNotifications(){
		snlist.clear();
	}

	public void sendSystemNotification(SensorNotificationObject notificationObj){
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(notificationObj.getSmallImageIcon())
				.setContentTitle(notificationObj.getTitle())
				.setContentText(notificationObj.getDescription())
				.setAutoCancel(true)
				.setVibrate(Constants.NOTIFICATION_VIBRATE_PATTERN_DEFAULT);
				
		// Creates an explicit intent for an Activity
		Intent resultIntent = new Intent(context, NotificationsActivity.class);
		resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(NotificationsActivity.class);

		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		
	    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
	            resultIntent, 0);
		
		mBuilder.setContentIntent(pendingIntent);		

		// mId allows to update the notification later on.
		int mId = notificationObj.getType().hashCode();
		Notification OSNotification = mBuilder.build();	
		OSNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
		OSNotification.ledARGB = Constants.NOTIFICATION_LIGHT_COLOR_DEFAULT;
		OSNotification.ledOnMS = 300;
		OSNotification.ledOffMS = 1000;
		
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(mId, OSNotification);
		
		activeNotificationIDs.add(mId);
	}*/
	
	public HashSet<Integer> getActiveNotificationIDs() {
		return activeNotificationIDs;
	}
	
	public ArrayList<String> getQueryPIDsList() {
		return queryPIDsList;
	}

    public void setRequestedPIDsList(ArrayList<String> requestedPIDsList) {
        this.requestedPIDsList = requestedPIDsList;
        updateQueryPIDsList();
    }

    public ArrayList<String> getRequestedPIDsList() {
        return requestedPIDsList;
    }

    /*
	public HashSet<String> getLoggingPIDsSet() {
		return loggingPIDsSet;
	}
	
	public HashSet<String> getDashboardPIDsSet() {
		return dashboardPIDsSet;
	}
	
	public HashSet<String> getFeaturePIDsSet() {
		return featurePIDsSet;
	}*/
	
	private void updateQueryPIDsList(){
		HashSet<String> newSet = new HashSet<String>();
		if(requestedPIDsList != null){
			newSet.addAll(requestedPIDsList);
		}
		if(supportedPIDsList!=null){
			newSet.retainAll(supportedPIDsList);
		}				
		queryPIDsList = new ArrayList<String>(newSet);
	}

	public boolean isDebugModeOn() {	
		return settings.getBoolean(DEBUG_MODE, false);		
	}
	
	public void setDebugMode(boolean mode) {
		addSetting(DEBUG_MODE, mode);
	}

	
	public void addLogIfDebugOn(String message){
		if(isDebugModeOn()){
			logList.add(message);
		}
	}


}
