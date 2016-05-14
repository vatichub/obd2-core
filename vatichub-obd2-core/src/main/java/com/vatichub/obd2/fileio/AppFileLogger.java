package com.vatichub.obd2.fileio;

import com.vatichub.obd2.OBD2CoreConfiguration;

public class AppFileLogger extends FileLogger{
	
	private static final int MAX_APP_COUNT_FOR_LOGGING = 2000000; 
	private static final int MAX_WRITE_COUNT = 2000000; 
	
	private OBD2CoreConfiguration coreConfigs;
	private int writecount = 0;
	public AppFileLogger(String filename) {
		super(filename);
		coreConfigs = OBD2CoreConfiguration.getInstance();
	}
	
	@Override
	public void openFile() { 
		if (coreConfigs.getAppOpenCount() < MAX_APP_COUNT_FOR_LOGGING) {
			super.openFile();
		}

	}
	
	@Override
	public void println(String line) {
		if (coreConfigs.getAppOpenCount() < MAX_APP_COUNT_FOR_LOGGING && writecount < MAX_WRITE_COUNT) {
			super.println(line);
			writecount ++;
		}
	}
	
	
	@Override
	public void print(String line) {
		if (coreConfigs.getAppOpenCount() < MAX_APP_COUNT_FOR_LOGGING && writecount < MAX_WRITE_COUNT) {
			super.print(line);
			writecount ++;
		}
	}
		
	public void println(String line, boolean force) {
		if (coreConfigs.getAppOpenCount() < MAX_APP_COUNT_FOR_LOGGING) {
			if (force) {
				super.println(line);
			} else {
				println(line);
			}
		}
	}
	
	@Override
	public void closeFile() {
		if (coreConfigs.getAppOpenCount() < MAX_APP_COUNT_FOR_LOGGING) {
			super.closeFile();
		}
	}
}
