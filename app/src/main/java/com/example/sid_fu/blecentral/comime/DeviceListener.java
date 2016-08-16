package com.example.sid_fu.blecentral.comime;

import android.bluetooth.BluetoothDevice;

public interface DeviceListener {
	/**
	 * 
	 * @param tag
	 *            - the SWDevice's tag
	 * @param device
	 *            - the connected BluetoothDevice
	 */
	public void onConnected(int tag, BluetoothDevice device);

	/**
	 * 
	 * @param tag
	 *            - the SWDevice's tag
	 * @param device
	 *            - the disconnected BluetoothDevice
	 */
	public void onDisconnected(int tag, BluetoothDevice device);

	
	public void onConn(int tag, BluetoothDevice device);
	
	
	/**
	 * 
	 * @param tag
	 *            -the SWDevice's tag
	 * @param deviceName
	 *            - the SWDevice's name in BluetoothGattCharacteristic
	 * @param device
	 *            - the connected BluetoothDevice
	 */
	public void onGetDeviceName(int tag, String deviceName,
								BluetoothDevice device);

	/**
	 * 
	 * @param tag
	 *            - the SWDevice's tag
	 * @param battery
	 *            - the SWDevice's battery percent
	 * @param devicethe
	 *            - connected BluetoothDevice
	 */
	public void onGetBattery(int tag, int battery, BluetoothDevice device);

	/**
	 * 
	 * @param tag
	 *            - the SWDevice's tag
	 * @param rssi
	 * @param device
	 */
	public void onGetRssi(int tag, int rssi, BluetoothDevice device);

	/**
	 * 
	 * @param tag
	 * @param value
	 *            - the SWDevice's command; see SWDevice's final int Command
	 * @param device
	 */
	public void onGetValue(int tag, int value, BluetoothDevice device);

	/**
	 * 
	 * @param tag
	 * @param value
	 *            - the setting successfully sent
	 * @param device
	 */
	public void onSendSuccess(int tag, int value, BluetoothDevice device);

	/**
	 * 
	 * @param tag
	 * @param ver
	 *            - the SWDevice's current firmware version
	 * @param type
	 *            - the SWDevice's current firmware type
	 * @param device
	 */
	public void onGetImageVerAndType(int tag, int ver, char type,
									 BluetoothDevice device);

	/**
	 * 
	 * @param tag
	 * @param progress
	 *            - the SWDevice OAD progress (total is 1)
	 * @param device
	 */
	public void onGetOADProgress(int tag, float progress, BluetoothDevice device);
	
	
	public void onGetKeyValue(int tag, byte[] value, BluetoothDevice device) ;
	
	public void onKeyValueSendSuccess(int tag, byte[] value, BluetoothDevice device) ;
}
