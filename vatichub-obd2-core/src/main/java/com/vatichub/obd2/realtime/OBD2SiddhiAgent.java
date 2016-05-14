package com.vatichub.obd2.realtime;

import com.vatichub.obd2.bean.PIDTypePair;

import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

// A wrapper for SiddhiManager  
public class OBD2SiddhiAgent {

	private OBD2SiddhiAgentManager siddhiAgentManager;
	private SiddhiManager siddhiManager;
	private boolean isenabled = false;

	public OBD2SiddhiAgent(OBD2SiddhiAgentManager siddhiAgentManager){
		this.siddhiAgentManager = siddhiAgentManager;
		siddhiManager = siddhiAgentManager.getSiddhiManager();
	}
	
	public void defineStreamOBD2(String streamDefinition){
		
		try {
			List<String> parameterList = siddhiAgentManager.getParameterList();
			List<String> typeList = siddhiAgentManager.getTypeList();
			
			ArrayList<PIDTypePair> pidtypepairs;
			
			pidtypepairs = parseStreamDef(streamDefinition, parameterList, typeList);
	
			String streamname = getStreamName(streamDefinition);
			siddhiAgentManager.getStreamInputMap().put(streamname, pidtypepairs);
			siddhiAgentManager.getStreamToSiddhiAgentMap().put(streamname, this);
			siddhiAgentManager.getSiddhiAgentToInputMap().put(this, pidtypepairs);
			
			siddhiManager.defineStream(streamDefinition);
		
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public InputHandler defineStreamGeneric(String streamDefinition){
		return siddhiManager.defineStream(streamDefinition);
	}
	
	public String addQuery(String query){
		return siddhiManager.addQuery(query);
	}
	
	public void addCallback(String streamId, StreamCallback streamCallback){
		siddhiManager.addCallback(streamId, streamCallback);
	}
	
	public void removeQuery(String queryId){
		siddhiManager.removeQuery(queryId);
	}
	
	
	public boolean isEnabled(){
		return isenabled;
	}
	
	public void setEnabled(boolean isenabled) {
		this.isenabled = isenabled;
	}

	public String getStreamName(String streamDef) throws ParseException{
		ArrayList<String> foundWords= getWordList(streamDef);
		if(foundWords.size()<3){
			throw new ParseException("Stream definition should contain at least 3 words", -1);
		}else{
			return foundWords.get(2);
		}
	}
	
	private ArrayList<String> getWordList(String streamDef){
		String[] words = streamDef.split("\\ |\\(|\\)|,");
		
		ArrayList<String> foundWords = new ArrayList<String>();
		
		//clean the array
		for(int i=0;i<words.length;i++){
			words[i]=words[i].trim();
			if (words[i].length()>0){
				foundWords.add(words[i]);
			}
		}
		
		return foundWords;
		
	}
	
	private ArrayList<PIDTypePair> parseStreamDef(String streamDef, List<String> pidList, List<String> typeList) throws ParseException{
		
		ArrayList<String> foundWords= getWordList(streamDef);
		ArrayList<String> foundLiterals = new ArrayList<String>();

		for(int i=0;i<foundWords.size();i++){
			String word=foundWords.get(i);
			if(pidList.contains(word) || typeList.contains(word)){
				foundLiterals.add(word);
			}
			
		}
		
		ArrayList<PIDTypePair> pidtypepairs = new ArrayList<PIDTypePair>();
		
		for(int i=0;i<foundLiterals.size()-1 ;i+=2){
			String pid = foundLiterals.get(i);
			String type = foundLiterals.get(i+1);
			
			if(pidList.contains(pid) && typeList.contains(type)){
				PIDTypePair pidtypepair = new PIDTypePair(pid, type);
				pidtypepairs.add(pidtypepair);
			}else{
				throw new ParseException("PIDs and types are not consecutive at pair number " + i , -1);
			}
			
		}
		
		return pidtypepairs;
		
	}
	
}
