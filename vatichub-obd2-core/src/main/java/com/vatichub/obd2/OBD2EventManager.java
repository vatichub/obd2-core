package com.vatichub.obd2;

import com.vatichub.obd2.api.OBD2EventListener;
import com.vatichub.obd2.bean.OBD2Event;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/* Manages all the graphs which are added to the UI. Whenever a graph is added to the UI, 
 * the related GeneralGraph object(super class) should be added to this. This will recieve
 * data as JSON objects to be drawn in the graphs and then the data is routed to the
 * related graph. Activities(Observers) will be notified when a graph is changed. For that 
 * they should be registered in this class(This is an Observable class).  
 */

/*Example format of the JSON object
 * 
 * "{"obd2_real_time_data":
 * 		{"obd2_rpm":
 * 			{"value":489,"time":0},
 * 		"obd2_speed":
 * 			{"value":6,"time":0}
 * 		}}\n";
 */

public class OBD2EventManager {

	private static OBD2EventManager instance;
	//private Hashtable<String, ArrayList<GeneralDisplay>> graphtablelist;
	//private DataLogger datalogger; todo add eventbased one
	private OBD2CoreConfiguration obd2CoreConfigs;
	//private TripDistanceCalculator tripDistanceCalculator;
	//private FuelEfficiencyCalculator fuelConsumptionCalculator;
    private List<OBD2EventListener> obd2EventListeners;
	
	private OBD2EventManager(){
		obd2CoreConfigs = OBD2CoreConfiguration.getInstance();
		//graphtablelist=new Hashtable<String, ArrayList<GeneralDisplay>>();
        obd2EventListeners = new ArrayList<OBD2EventListener>();
		//datalogger = obd2CoreConfigs.getDataLogger();
		//tripDistanceCalculator = obd2CoreConfigs.getTripDistanceCalculator();
		//fuelConsumptionCalculator = obd2CoreConfigs.getFuelConsumptionCalculator();
	}
	
	public static OBD2EventManager getInstance(){
		if (instance == null){
			instance=new OBD2EventManager();
			return instance;
		}else
			return instance;
	}
	
	public static void removeInstance(){ 
		instance = null;
	}

	public void addDataObj(JSONObject dataobj){

        for (OBD2EventListener listener : obd2EventListeners) {
            listener.receiveOBD2Event(new OBD2Event(dataobj));
        }

        /*
		try {
			datalogger.addDataObj(dataobj);
			tripDistanceCalculator.addDataObj(dataobj);
			fuelConsumptionCalculator.addDataObj(dataobj);
			
			String roottype=dataobj.names().getString(0);
			
			if(Constants.validateRootDataType(roottype)){
				ArrayList<GeneralDisplay> glist=graphtablelist.get(roottype);
				if(glist!=null){
					for (GeneralDisplay g : glist)
						g.addData(dataobj.getJSONObject(roottype));
				}
			}else{
				Log.e(Constants.APPTAG,"Unsupported root data type :" + roottype);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		*/
	}

    public void registerOBD2EventListener (OBD2EventListener listener) {
        if (!obd2EventListeners.contains(listener)) {
            obd2EventListeners.add(listener);
        }
    }

    public void unregisterOBD2EventListener (OBD2EventListener listener) {
        if (obd2EventListeners.contains(listener)) {
            obd2EventListeners.remove(listener);
        }
    }
}
