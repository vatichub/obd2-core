package com.vatichub.obd2.realtime;

import com.vatichub.obd2.OBD2CoreConfiguration;
import com.vatichub.obd2.OBD2CoreConstants;
import com.vatichub.obd2.api.OBD2EventListener;
import com.vatichub.obd2.bean.OBD2Event;
import com.vatichub.obd2.bean.PIDTypePair;

import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.config.SiddhiConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class OBD2SiddhiAgentManager implements OBD2EventListener {
	
	private SiddhiManager siddhiManager;
	private ArrayList<OBD2SiddhiAgent> siddhiAgentList;
	private List<String> parameterList;
	private List<String> typeList;
	private OBD2CoreConfiguration obd2CoreConfigs;
	private HashMap<String, ArrayList<PIDTypePair>> streamToInputMap;
	private HashMap<String, OBD2SiddhiAgent> streamToSiddhiAgentMap;
	private HashMap<OBD2SiddhiAgent, ArrayList<PIDTypePair>> siddhiAgentToInputMap;
	private static OBD2SiddhiAgentManager instance;
	
	private OBD2SiddhiAgentManager() {

        SiddhiConfiguration siddhiConfiguration = new SiddhiConfiguration();

        /*
		 * todo
		 * make it configurable to add Siddhiconfiguration via the extended eventMonitor Class
		 */
        List extensionClasses = new ArrayList();
        extensionClasses.add(org.test.cep.extension.AccelerationFinderWindowProcessor.class);
        siddhiConfiguration.setSiddhiExtensions(extensionClasses);

        
		obd2CoreConfigs = OBD2CoreConfiguration.getInstance();
		siddhiManager = new SiddhiManager(siddhiConfiguration);
		siddhiAgentList = new ArrayList<OBD2SiddhiAgent>();
		streamToInputMap = new HashMap<String, ArrayList<PIDTypePair>>();
		streamToSiddhiAgentMap = new HashMap<String, OBD2SiddhiAgent>();
		siddhiAgentToInputMap = new HashMap<OBD2SiddhiAgent, ArrayList<PIDTypePair>>();
		
		typeList = Arrays.asList(OBD2CoreConstants.SIDDHI_DATA_TYPES);
		
		parameterList = new ArrayList<String>();
		ArrayList<String> pidList = obd2CoreConfigs.getAllPIDsList();
		for(int i=0;i<pidList.size();i++){
			parameterList.add(pidList.get(i));
		}
		parameterList.add(OBD2CoreConstants.SIDDHI_TIMESTAMP);
	}
	
	public static OBD2SiddhiAgentManager getInstance() {
		if(instance == null)
			instance = new OBD2SiddhiAgentManager();
		return instance;
	}
	

	public static void removeInstance(){ 
		instance = null;
	}
	
	public OBD2SiddhiAgent createSiddhiAgent(){
        OBD2SiddhiAgent obd2SiddhiAgent = new OBD2SiddhiAgent(this);
		siddhiAgentList.add(obd2SiddhiAgent);
		return obd2SiddhiAgent;
	}
	
	public SiddhiManager getSiddhiManager() {
		return siddhiManager;
	}
	
	public List<String> getParameterList() {
		return parameterList;
	}
	
	public List<String> getTypeList() {
		return typeList;
	}
	
	public HashMap<String, ArrayList<PIDTypePair>> getStreamInputMap() {
		return streamToInputMap;
	}
	
	public HashMap<String, OBD2SiddhiAgent> getStreamToSiddhiAgentMap() {
		return streamToSiddhiAgentMap;
	}
	
	public HashMap<OBD2SiddhiAgent, ArrayList<PIDTypePair>> getSiddhiAgentToInputMap() {
		return siddhiAgentToInputMap;
	}

	public Object convertTo(double val,String to){
		to = to.toLowerCase(Locale.US).trim();
		if(to.equals("float")){
			return (float)val;
		}else if(to.equals("int")){
			return (int)val;
		}else if(to.equals("double")){
			return (double)val;
		}else if(to.equals("long")){
			return (long)val;
		}else 
			return null;
	}
	
	public void send(String inputHandler,Object[] data) throws InterruptedException{
		siddhiManager.getInputHandler(inputHandler).send(data);
	}


    @Override
    public void receiveOBD2Event(OBD2Event event) {
        JSONObject obd2DataObj = event.getEventData();
        try {
            JSONObject allPIDValueObj= obd2DataObj.getJSONObject(OBD2CoreConstants.OBDII_REAL_TIME_DATA);

            Iterator<String> streamit = streamToInputMap.keySet().iterator();
            while(streamit.hasNext()){

                try{
                    String stream = streamit.next();
                    OBD2SiddhiAgent eventmonitor = streamToSiddhiAgentMap.get(stream);
                    if(eventmonitor.isEnabled()){
                        ArrayList<PIDTypePair> pidtypepairList = streamToInputMap.get(stream);

                        ArrayList<Object> params = new ArrayList<Object>();
                        for(int i=0;i<pidtypepairList.size();i++){
                            PIDTypePair pidtypepair = pidtypepairList.get(i);
                            JSONObject pidobj  = allPIDValueObj.getJSONObject(pidtypepair.getPid());
                            double val = pidobj.getDouble(OBD2CoreConstants.VALUE);
                            Object converted = convertTo(val,pidtypepair.getType());
                            params.add(converted);
                        }
                        Object[] topost = params.toArray();
                        siddhiManager.getInputHandler(stream).send(topost);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
