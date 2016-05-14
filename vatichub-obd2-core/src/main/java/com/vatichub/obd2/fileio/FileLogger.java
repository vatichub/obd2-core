package com.vatichub.obd2.fileio;

import com.vatichub.obd2.OBD2CoreConfiguration;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class FileLogger implements FileIO{

	private String filename;
	PrintWriter pw;
	OBD2CoreConfiguration obd2CoreConfigs;
	
	public FileLogger(String filename) {
		this.filename= filename;
		obd2CoreConfigs = OBD2CoreConfiguration.getInstance();
	}
	
	public void openFile(){
		try {
			pw = new PrintWriter(filename);
			obd2CoreConfigs.addOpenedFile(this);
		} catch (FileNotFoundException e) {
			obd2CoreConfigs.sendMessageToUI("Error creating App log file at path: "+ filename);
			e.printStackTrace();
		}
	}
	
	public void print(String line){
		pw.print(line);
	}
	
	public void println(String line){
		pw.println(line);
	}
	
	@Override
	public void closeFile(){
		pw.close();
	}
	
}
