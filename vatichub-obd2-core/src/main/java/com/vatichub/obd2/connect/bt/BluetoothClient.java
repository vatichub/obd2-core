package com.vatichub.obd2.connect.bt;

public interface BluetoothClient {
	void cancel();
	void start();
	void write(byte[] out);
	void write(int out);
}
