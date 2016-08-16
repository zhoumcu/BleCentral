package com.example.sid_fu.blecentral.comime;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.R.bool;
import android.R.integer;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;


public class SWDevice {
	private final String tagString = "ComtimeSDK";

	/**
	 * the device has been disconnected
	 */
	public final static int DEVICE_STATUS_DISCONNECTED = 0;
	/**
	 * the device is connecting
	 */
	public final static int DEVICE_STATUS_CONNECTING = 1;
	/**
	 * the device has been connected
	 */
	public final static int DEVICE_STATUS_CONNECTED = 2;

	// /**
	// * the device is OADing
	// */
	// public final static int DEVICE_STATUS_OADING = 6;

	public final static int COMMAND_FINDING_PHONE = 11;
	/**
	 * The command from device: device stop find your phone
	 */
	public final static int COMMAND_STOP_FINDING_PHONE = 10;
	/**
	 * The command from device: your wallet is open
	 */
	public final static int COMMAND_WALLET_OPEN = 30;
	/**
	 * The command from device: your phone should stop alarm now
	 */
	public final static int COMMAND_STOP_ALARM = 20;
	/**
	 * The command from device: your phone should start alarm now
	 */
	public final static int COMMAND_START_ALARM = 21;
	/**
	 * The command from device:device is low power
	 */
	public final static int COMMAND_LOW_BATTERY = 71;
	/**
	 * The command from device:device is power off
	 */
	public final static int COMMAND_NULL_BATTERY = 72;
	/**
	 * The command from device:device is power full
	 */
	public final static int COMMAND_FULL_BATTERY = 73;
	/**
	 * The command from device:device tick every minute
	 */
	public final static int COMMAND_REMIND_TICK = 6;

	/**
	 * setting: let the device ring
	 */
	public final static int SETTING_START_RING = 1;
	/**
	 * setting: stop the device ring
	 */
	public final static int SETTING_STOP_RING = 0;
	/**
	 * setting: device alert mode is ring
	 */
	public final static int SETTING_ALERT_MODE_RING = 21;
	/**
	 * setting: device alert mode is vibrate
	 */
	public final static int SETTING_ALERT_MODE_VIBRATE = 22;
	/**
	 * setting: device alert mode is ring and vibrate
	 */
	public final static int SETTING_ALERT_MODE_BOTH = 23;

	/**
	 * setting: turn on the function that notify your phone when wallet is open
	 */
	public final static int SETTING_WALLET_PUSH_ON = 51;
	/**
	 * setting: turn off the function that notify your phone when wallet is open
	 */

	public final static int SETTING_WALLET_PUSH_OFF = 50;
	/**
	 * setting: set the wallet open notification interval 10 seconds
	 */
	public final static int SETTING_WALLET_PUSH_INTERVAL_10S = 85;

	/**
	 * setting: set the wallet open notification interval 60 seconds
	 */
	public final static int SETTING_WALLET_PUSH_INTERVAL_60S = 86;
	/**
	 * setting: set the wallet open notification interval 120 seconds
	 */
	public final static int SETTING_WALLET_PUSH_INTERVAL_120S = 87;

	/**
	 * setting: set the device alert once when disconnect
	 */
	public final static int SETTING_DEVICE_ALERT_TINES_1 = 111;
	/**
	 * setting: set the device alert 3 times when disconnect
	 */
	public final static int SETTING_DEVICE_ALERT_TINES_3 = 113;
	/**
	 * setting:set the device alert 5 times when disconnect
	 */
	public final static int SETTING_DEVICE_ALERT_TINES_5 = 115;

	/**
	 * setting:turn on the function that: the device will sleep automatically if
	 * the device was not connected for 3 days
	 */
	public final static int SETTING_DEVICE_AUTO_SLEEP_ON = 102;
	/**
	 * setting:turn off the function that: the device will sleep automatically
	 * if the device was not connected for 3 days
	 */
	public final static int SETTING_DEVICE_AUTO_SLEEP_OFF = 103;
	/**
	 * setting:turn on the function that: the device will beep every 30 minutes
	 * if the device was not connected
	 */
	public final static int SETTING_DEVICE_BEEP_REMINDER_DISCONNECT_ON = 74;
	/**
	 * setting:turn off the function that: the device will beep every 30 minutes
	 * if the device was not connected
	 */
	public final static int SETTING_DEVICE_BEEP_REMINDER_DISCONNECT_OFF = 75;
	/**
	 * setting:turn on the function that: the device will beep when the wallet
	 * is open during the device not connected
	 */
	public final static int SETTING_WALLET_PUSH_BEEP_REMINDER_DISCONNECT_ON = 72;
	/**
	 * setting:turn off the function that: the device will beep when the wallet
	 * is open during the device not connected
	 */
	public final static int SETTING_WALLET_PUSH_BEEP_REMINDER_DISCONNECT_OFF = 73;
	/**
	 * setting:turn on the anti-lost function that: the device will alert when
	 * it is disconnected
	 */
	public final static int SETTING_ANTI_LOST_ON = 93;
	/**
	 * setting:turn off the anti-lost function that: the device will alert when
	 * it is disconnected
	 */
	public final static int SETTING_ANTI_LOST_OFF = 94;

	public final static int SETTING_InterimNotDisturb_ON = 95;
	/**
	 * setting:turn off the anti-lost function that: the device will alert when
	 * it is disconnected
	 */
	public final static int SETTING_InterimNotDisturb_OFF = 96;

	/**
	 * setting:turn on the function that: the device won't alert even if it is
	 * disconnected during the Not-disturb period
	 */
	public final static int SETTING_NOT_DISTURB_ON = 97;
	/**
	 * setting:turn off the function that: the device won't alert even if it is
	 * disconnected during the Not-disturb period
	 */
	public final static int SETTING_NOT_DISTURB_OFF = 98;

	/**
	 * setting: set the alert distance:far
	 */
	public final static int SETTING_DISTANCE_FAR = 32;

	/**
	 * setting: set the alert distance:near
	 */
	public final static int SETTING_DISTANCE_NEAR = 30;
	/**
	 * It will prolong the warning delay to 20 seconds
	 */
	public final static int SETTING_REDUCE_UNEXPECTED_WARNING_ON = 104;
	/**
	 * cancel the the 20-seconds warning delay
	 */
	public final static int SETTING_REDUCE_UNEXPECTED_WARNING_OFF = 105;

	/**
	 * set the device alert volume high
	 */
	public final static int SETTING_DEVICE_VOLUME_HIGH = 13;
	/**
	 * set the device alert volume normal
	 */
	public final static int SETTING_DEVICE_VOLUME_NORMAL = 11;
	private  DeviceListener deviceListener;

	private int tag;// 标签，比如设备1 ，设备2，设备3
	private int deviceStatus;
	// 0 未连接状态
	// 1正在连接状态
	// 2已连接状态
	// 3 手机ring device状态
	// 4 device ring 手机状态
	// 5 重连状态

	private boolean isStable;// 连接是否稳定
	private boolean isRing;// 是否正在寻找设备
	private boolean isNextCanWarning; // 是否下次允许报警
	private boolean isChecking; // // 是否正在进行断线5s检测。 同一时间 只有一个检测
	private boolean warningRightNow;// 是否立刻报警
	private BluetoothDevice bluetoothDevice;
	private BluetoothGatt mBluetoothGatt;
	private BluetoothGattCharacteristic batteryChar, nameChar, writeChar,
			keyChar;
	private String deviceName = "";

	private Context context;

	private boolean programming = false;// 正在OAD
	public boolean busy = false;

	public SWDevice(Context context, int tag) {
		super();
		this.context = context;
		this.tag = tag;
	}

	public SWDevice(Context context, int tag, BluetoothDevice device,
			DeviceListener deviceListener) {
		this.bluetoothDevice = device;
		this.deviceListener = deviceListener;
		this.deviceStatus = DEVICE_STATUS_DISCONNECTED;
		this.tag = tag;
		this.context = context;
	}

	public void setDeviceListener(DeviceListener deviceListener) {
		this.deviceListener = deviceListener;
	}

	/**
	 * set the device tag. you can distinguish between different devices by the
	 * tag
	 * 
	 * @param tag
	 */
	public void setTag(int tag) {
		this.tag = tag;
	}

	/**
	 * get the device tag
	 * 
	 * @return the device tag
	 */
	public int getTag() {
		return this.tag;
	}

	/**
	 * 
	 * @return the BluetoothDevice connected
	 */
	public BluetoothDevice getBluetoothDevice() {
		return bluetoothDevice;
	}

	/**
	 * 
	 * @param bluetoothDevice
	 */
	public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
		this.bluetoothDevice = bluetoothDevice;
	}

	/**
	 * 
	 * @return get device name that in BluetoothGattCharacteristic
	 */
	public String getDeviceName() {
		return deviceName;
	}

	// public int getConnectionState() {
	// if (mBluetoothGatt != null && bluetoothDevice != null) {
	// int state = ((BluetoothManager) context
	// .getSystemService(Context.BLUETOOTH_SERVICE))
	// .getConnectionState(bluetoothDevice, BluetoothGatt.GATT);
	// if (state == BluetoothGatt.STATE_CONNECTED) {
	// return DEVICE_STATUS_CONNECTED;
	// } else if (state == BluetoothGatt.STATE_DISCONNECTED) {
	// return DEVICE_STATUS_DISCONNECTED;
	// } else if (state == BluetoothGatt.STATE_CONNECTING) {
	// return DEVICE_STATUS_CONNECTING;
	// }
	// } else {
	// return DEVICE_STATUS_DISCONNECTED;
	// }
	// return DEVICE_STATUS_DISCONNECTED;
	// }

	/**
	 * start connect the device connectGatt and mBluetoothGatt.connect();
	 */
	public void connect() {

		// if(getConnectionState()==DEVICE_STATUS_CONNECTED){
		// Log.e(tagString,
		// "already connected");
		// return;
		// }
		if (bluetoothDevice != null) {
			if (mBluetoothGatt != null) {
				mBluetoothGatt.close();
				mBluetoothGatt.disconnect();

				mBluetoothGatt = null;
			}
			mBluetoothGatt = bluetoothDevice.connectGatt(this.context, false,
					gattCallback);
			mBluetoothGatt.connect();
		} else {
			Log.e(tagString,
					"the bluetoothDevice is null, please reset the bluetoothDevice");
		}
	}

	/**
	 * connectGatt 无 mBluetoothGatt.connect();
	 */
	public void connectGatt() {

		if (bluetoothDevice != null) {
			if (mBluetoothGatt != null) {
				mBluetoothGatt.close();
				mBluetoothGatt.disconnect();

				mBluetoothGatt = null;
			}
			mBluetoothGatt = bluetoothDevice.connectGatt(this.context, false,
					gattCallback);

		} else {
			Log.e(tagString,
					"the bluetoothDevice is null, please reset the bluetoothDevice");
		}
	}

	public void justConnect() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.connect();
		}
	}

	public void discoveryService() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.discoverServices();
		}
	}

	// /**
	// * start connect the device
	// */
	// public void reConnect() {
	// if (mBluetoothGatt != null)
	// mBluetoothGatt.connect();
	// }

	/**
	 * cancel the connection with the device
	 */
	public void disconnect() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.disconnect();
		} else {
			Log.e("ComtimeSDK",
					"disconnect failed,  the mBluetoothGatt  is null ");
		}

	}

	/**
	 * remove the connection with the device
	 */
	public void remove() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.close();
			mBluetoothGatt.disconnect();
			mBluetoothGatt = null;
		}
		deviceStatus = DEVICE_STATUS_DISCONNECTED;
		deviceName = "";
		bluetoothDevice = null;
		batteryChar = null;
		nameChar = null;
		writeChar = null;
		programming = false;
	}

	/**
	 * Descriptor value to read from the remote device
	 * 
	 * @return true, if the read operation was initiated successfully
	 */
	public boolean readRssi() {
		if (mBluetoothGatt != null && isConnected()) {
			return mBluetoothGatt.readRemoteRssi();
		}
		return false;
	}

	/**
	 * read the device name, the result will be on getName
	 */
	public void readDeviceName() {
		if (mBluetoothGatt != null) {
			if (nameChar == null) {
				List<BluetoothGattService> list = mBluetoothGatt.getServices();
				for (BluetoothGattService bluetoothGattService : list) {
					if (bluetoothGattService.getUuid().toString()
							.equals(UUIDUtils.NAME_SERVICE_UUID)) {

						for (BluetoothGattCharacteristic charac : bluetoothGattService
								.getCharacteristics()) {
							if (charac.getUuid().toString()
									.equals(UUIDUtils.NAME_CHAR_UUID)) {
								nameChar = charac;
								break;
							}
						}
						break;
					}
				}
				if (nameChar != null) {
					mBluetoothGatt.readCharacteristic(nameChar);
				} else {
					Log.e(tagString, "nameChar is null");
				}
			} else {
				mBluetoothGatt.readCharacteristic(nameChar);
			}
		}
	}

	/**
	 * read the device battery,the result will be on getBattery
	 */
	public void readBattery() {
		if (mBluetoothGatt != null) {
			if (batteryChar == null) {
				List<BluetoothGattService> list = mBluetoothGatt.getServices();
				for (BluetoothGattService bluetoothGattService : list) {
					if (bluetoothGattService.getUuid().toString()
							.equals(UUIDUtils.BATTERY_SERVICE_UUID)) {

						for (BluetoothGattCharacteristic charac : bluetoothGattService
								.getCharacteristics()) {
							if (charac.getUuid().toString()
									.equals(UUIDUtils.BATTERY_CHAR_UUID)) {
								batteryChar = charac;
								break;
							}
						}
						break;
					}
				}
				if (batteryChar != null) {
					mBluetoothGatt.readCharacteristic(batteryChar);
				} else {
					Log.e(tagString, "batteryChar is null");
				}
			} else {
				mBluetoothGatt.readCharacteristic(batteryChar);
			}

		}
	}

	/**
	 * start let the device ring
	 */
	public void startRing() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(SETTING_START_RING);
		}
	}

	/**
	 * stop the device ring
	 */
	public void stopRing() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(SETTING_STOP_RING);
		}
	}

	/**
	 * turn on the anti-lost function
	 */
	public void startAntilost() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(SETTING_ANTI_LOST_ON);
		}
	}

	/**
	 * turn off the anti-lost function
	 */
	public void stopAntilost() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(SETTING_ANTI_LOST_OFF);
		}
	}

	/**
	 * don't disturb when disconnected
	 */
	public void startNotDisturb() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(SETTING_NOT_DISTURB_ON);
		}
	}

	/**
	 * stop not disturb
	 */
	public void stopNotDisturb() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(SETTING_NOT_DISTURB_OFF);
		}
	}

	/**
	 * start not disturb interimly
	 */
	public void startInterimNotDisturb() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(95);
		}
	}

	/**
	 * stop not disturb interimly
	 */
	public void stopInterimNotDisturb() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(96);
		}
	}

	/**
	 * 
	 * @param intervalValue
	 *            SETTING_WALLET_PUSH_INTERVAL_10S;
	 *            SETTING_WALLET_PUSH_INTERVAL_60S
	 *            ;SETTING_WALLET_PUSH_INTERVAL_120S
	 */
	public void setWalletPushInterval(int intervalValue) {
		if (mBluetoothGatt != null && writeChar != null) {
			if (intervalValue == SETTING_WALLET_PUSH_INTERVAL_10S
					|| intervalValue == SETTING_WALLET_PUSH_INTERVAL_60S
					|| intervalValue == SETTING_WALLET_PUSH_INTERVAL_120S)
				sendToDevice(intervalValue);
		}
	}

	/**
	 * 
	 * @param times
	 *            SETTING_DEVICE_ALERT_TINES_1;SETTING_DEVICE_ALERT_TINES_3;
	 *            SETTING_DEVICE_ALERT_TINES_5
	 */
	public void setDeviceAlertTimes(int times) {
		if (mBluetoothGatt != null && writeChar != null) {
			if (times == SETTING_DEVICE_ALERT_TINES_1
					|| times == SETTING_DEVICE_ALERT_TINES_3
					|| times == SETTING_DEVICE_ALERT_TINES_5)
				sendToDevice(times);
		}
	}

	/**
	 * 
	 * @param volume
	 *            SETTING_DEVICE_VOLUME_HIGH;SETTING_DEVICE_VOLUME_NORMAL
	 */
	public void setDeviceVolumn(int volume) {
		if (mBluetoothGatt != null && writeChar != null) {
			if (volume == SETTING_DEVICE_VOLUME_HIGH
					|| volume == SETTING_DEVICE_VOLUME_NORMAL)
				sendToDevice(volume);
		}
	}

	/**
	 * turn on the function that: the device will sleep automatically if the
	 * device was not connected for 3 days
	 */
	public void startAutoSleep() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(SETTING_DEVICE_AUTO_SLEEP_ON);
		}
	}

	/**
	 * turn on the function that: the device will sleep automatically if the
	 * device was not connected for 3 days
	 */
	public void stopAutoSleep() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(SETTING_DEVICE_AUTO_SLEEP_OFF);
		}
	}

	public void startReduceUnexpectedWarning() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(SETTING_REDUCE_UNEXPECTED_WARNING_ON);
		}
	}

	public void stopReduceUnexpectedWarning() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(SETTING_REDUCE_UNEXPECTED_WARNING_OFF);
		}
	}

	/**
	 * turn on the function that notify your phone when wallet is open
	 */
	public void startWalletPushFunction() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(SETTING_WALLET_PUSH_ON);
		}
	}

	/**
	 * turn off the function that notify your phone when wallet is open
	 */
	public void stopWalletPushFunction() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(SETTING_WALLET_PUSH_OFF);
		}
	}

	/**
	 * 
	 * @param value
	 *            SETTING_DISTANCE_NEAR;SETTING_DISTANCE_FAR
	 */
	public void setAlertDistance(int value) {
		if (value != SETTING_DISTANCE_FAR && value != SETTING_DISTANCE_NEAR) {
			return;
		}
		if (mBluetoothGatt != null) {
			for (BluetoothGattService service : mBluetoothGatt.getServices()) {

				if (service.getUuid().toString()
						.equals(UUIDUtils.WRITE_SERVICE_UUID)) {
					for (BluetoothGattCharacteristic charac : service
							.getCharacteristics()) {
						if (charac.getUuid().toString()
								.equals(UUIDUtils.WRITE_CHAR_UUID_2A09)) {
							charac.setValue(new byte[] { (byte) value });
							mBluetoothGatt.writeCharacteristic(charac);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * get the device is connected or not
	 * 
	 * @return true is connected,false not
	 */
	public boolean isConnected() {
		if (deviceStatus >= 2 && deviceStatus < 5) {
			return true;
		}
		return false;
	}

	private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {

			super.onCharacteristicChanged(gatt, characteristic);
			String uuid = characteristic.getService().getUuid().toString();
			if (uuid.equals(UUIDUtils.DEVICE_SERVICE_UUID)) {
				int value = characteristic.getValue()[0];
				if (value < 0) {// 与钱包打开 回传有关

				} else {
					value = Integer.valueOf(Integer.toHexString(value), 10);
					deviceListener.onGetValue(tag, value, bluetoothDevice);
				}
			} else if (uuid.equals(UUIDUtils.OAD_SERVICE_UUID)) {

				int value = (characteristic.getValue()[1] << 8 & 0xff00)
						| (characteristic.getValue()[0] & 0xff);

				char type = ((value & 1) == 1) ? 'B' : 'A';
				Log.e("ComtimeSDK", "ver:" + value + " type:" + type);
				deviceListener.onGetImageVerAndType(tag, value, type,
						bluetoothDevice);

			} else if (uuid.equals(UUIDUtils.KEY_SERVICE_UUID)) {
				Log.e("ComtimeSDK", " getkey :"
						+ Bytes2HexString(characteristic.getValue()));
				deviceListener.onGetKeyValue(tag, characteristic.getValue(),
						bluetoothDevice);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicRead(gatt, characteristic, status);
			Log.e("", "onCharacteristicRead  " + status);

			if (characteristic.getUuid().toString()
					.equals(UUIDUtils.BATTERY_CHAR_UUID)) {// 获取到电量
				int battery = characteristic.getValue()[0];
				deviceListener.onGetBattery(tag, battery, bluetoothDevice);
			} else if (characteristic.getUuid().toString()
					.equals(UUIDUtils.NAME_CHAR_UUID)) {
				deviceName = new String(characteristic.getValue());
				deviceListener
						.onGetDeviceName(tag, deviceName, bluetoothDevice);
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);

			if (!programming) {//

				if (status == BluetoothGatt.GATT_SUCCESS) {

					if (characteristic.getService().getUuid().toString()
							.equals(UUIDUtils.WRITE_SERVICE_UUID)) {
						int backValue = characteristic.getValue()[0];
						Log.e("ComtimeSDK", "onCharacteristicWrite:" + status
								+ " value :" + backValue);

						deviceListener.onSendSuccess(tag, backValue,
								bluetoothDevice);
					} else if (characteristic.getService().getUuid().toString()
							.equals(UUIDUtils.KEY_SERVICE_UUID)) {
						Log.e(tagString,
								"keyValue onCharacteristicWrite:"
										+ status
										+ " "
										+ Bytes2HexString(characteristic
												.getValue()));
						deviceListener.onKeyValueSendSuccess(tag,
								characteristic.getValue(), bluetoothDevice);
					}
				}
			} else {
				Log.e("ComtimeSDK", "onCharacteristicWrite:" + status);
				if (status == BluetoothGatt.GATT_SUCCESS) {
					busy = false;
					Log.e(tagString, " busy =false ");
				}
			}

		}

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			super.onConnectionStateChange(gatt, status, newState);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (newState == BluetoothGatt.STATE_CONNECTED) {// 连接成功
					// deviceListener.onConn(tag, bluetoothDevice);
					gatt.discoverServices();// 寻找服务

				} else if (newState == BluetoothGatt.STATE_DISCONNECTED) {// 断开连接

					batteryChar = null;
					nameChar = null;
					keyChar = null;
					writeChar = null;
					deviceStatus = DEVICE_STATUS_DISCONNECTED;
					deviceListener.onDisconnected(tag, bluetoothDevice);

				}
			}
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorRead(gatt, descriptor, status);
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorWrite(gatt, descriptor, status);

			Log.e(tagString, "onDescriptorWrite :"
					+ descriptor.getCharacteristic().getUuid().toString());
			if (descriptor.getCharacteristic().getUuid().toString()
					.equals(UUIDUtils.DEVICE_CHAR_UUID)) {

			}
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			super.onReadRemoteRssi(gatt, rssi, status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				deviceListener.onGetRssi(tag, rssi, bluetoothDevice);
			}
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
			super.onReliableWriteCompleted(gatt, status);
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			super.onServicesDiscovered(gatt, status);

			if (status == BluetoothGatt.GATT_SUCCESS) {
				deviceStatus = DEVICE_STATUS_CONNECTED;
				final List<BluetoothGattService> services = gatt.getServices();
				displayGattServices(services);
				deviceListener.onConnected(tag, bluetoothDevice);
			}
		}
	};

	/**
     * 
     */
	private boolean enableNotification(boolean enable,
			BluetoothGattCharacteristic characteristic) {
		if (mBluetoothGatt == null || characteristic == null)
			return false;
		if (!mBluetoothGatt.setCharacteristicNotification(characteristic,
				enable))
			return false;
		BluetoothGattDescriptor clientConfig = characteristic
				.getDescriptor(UUIDUtils.CCC);
		if (clientConfig == null)
			return false;

		if (enable) {
			clientConfig
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		} else {
			clientConfig
					.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
		}
		return mBluetoothGatt.writeDescriptor(clientConfig);
	}

	public void sendToDevice(int sendValue) {
		if (isConnected() && mBluetoothGatt != null && writeChar != null
				&& !programming) {
			// writeChar.setValue(sendValue,
			// BluetoothGattCharacteristic.FORMAT_UINT8,
			// 0);
			writeChar.setValue(new byte[] { (byte) sendValue });
		 	writeChar
		 			.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
			mBluetoothGatt.writeCharacteristic(writeChar);
		}
	}

	public void sendToDevice(byte[] values) {
		if (isConnected() && mBluetoothGatt != null && keyChar != null
				&& !programming) {
			// writeChar.setValue(sendValue,
			// BluetoothGattCharacteristic.FORMAT_UINT8,
			// 0);
			keyChar.setValue(values);
 			writeChar
 					.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
			mBluetoothGatt.writeCharacteristic(keyChar);
		} else {
			if (keyChar == null) {
				Log.e(tagString, "keyChar is null");
			}
		}
	}

	public boolean writeCharacteristic(
			BluetoothGattCharacteristic characteristic, byte[] values) {
		if (isConnected() && mBluetoothGatt != null) {
			characteristic.setValue(values);
			return mBluetoothGatt.writeCharacteristic(characteristic);
		}
		return false;
	}

	private boolean writeCharacteristic(
			BluetoothGattCharacteristic characteristic) {
		// Log.e(tagString, " write1111111111111111");
		if (isConnected() && mBluetoothGatt != null) {
			// Log.e(tagString, " write22222");
			characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
			return mBluetoothGatt.writeCharacteristic(characteristic);
			// Log.e(tagString, "return == write:" + success);

		}
		return false;
	}

	public void sendKeyValue(byte[] values) {
		if (isConnected() && mBluetoothGatt != null && keyChar != null) {

			Log.e(tagString, "send keyValue:" + Bytes2HexString(values));
			keyChar.setValue(values);
			keyChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
			mBluetoothGatt.writeCharacteristic(keyChar);
		} else {
			if (keyChar == null) {
				Log.e(tagString, "keyChar is null");
			}
		}
	}
	private BluetoothGattCharacteristic mCharIdentify, mCharBlock;
	/**
	 * 
	 * @return hasOADfunction or not
	 */
	public boolean hasOADFunction() {
		if (mBluetoothGatt != null) {
			List<BluetoothGattCharacteristic> mCharListOad;
			for (BluetoothGattService service : mBluetoothGatt.getServices()) {
				if (service.getUuid().toString()
						.equals(UUIDUtils.OAD_SERVICE_UUID)
						&& service.getCharacteristics().size() > 0) {
					mCharListOad = service.getCharacteristics();
					if (mCharListOad.size() == 2) {
						mCharIdentify = mCharListOad.get(0);
						mCharBlock = mCharListOad.get(1);
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean hasKeyFunction() {
		return keyChar != null;
	}

	/**
	 * if success ,the result will be on DeviceListener.onGetImageVerAndType(int
	 * tag,int ver,char type, BluetoothDevice device);
	 */
	public void getImageVerAndType() {
		if (!hasOADFunction()) {
			Log.i("ComtimeSDK", "don't have OAD function");
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean ok = enableNotification(true, mCharIdentify);
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// Prepare data for request (try image A and B respectively,
				// only one of
				// them will give a notification with the image info)
				if (ok)
					ok = writeCharacteristic(mCharIdentify, new byte[] { 0 });
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (ok) {
					ok = writeCharacteristic(mCharIdentify, new byte[] { 1 });
				}
			}
		}).start();
	}

	private int iBytes; // Number of bytes programmed
	private short iBlocks; // Number of blocks programmed
	private short nBlocks; // Total number of blocks
	int MY_INTERVAL = 50;
	private int sleepInterval = 800;

	public void setOADInterval(int interval) {
		MY_INTERVAL = interval;
	}

	public void setOADSleep(int interval) {
		sleepInterval = interval;
	}

	/**
	 * start on air download
	 * 
	 * @param imageInfo
	 *            the information you want to update to
	 */
	public List<BluetoothGattService> getServices() {
		if (mBluetoothGatt != null) {
			return mBluetoothGatt.getServices();
		}
		return null;

	}

	int mm = 0;

	public void startOAD(final ImageInfo imageInfo) {
		mm = 0;
		Log.i(tagString, "startOAD ");
		if (!hasOADFunction()) {

			Log.i("ComtimeSDK", "don't have OAD function");
			return;
		}

		final int PKT_INTERVAL = 20;

		final int OAD_BLOCK_SIZE = 16;
		final int HAL_FLASH_WORD_SIZE = 4;
		final int OAD_BUFFER_SIZE = 2 + OAD_BLOCK_SIZE;
		final int OAD_IMG_HDR_SIZE = 8;

		programming = true;
		byte[] buf = new byte[OAD_IMG_HDR_SIZE + 2 + 2];
		buf[0] = Conversion.loUint16(imageInfo.ver);
		buf[1] = Conversion.hiUint16(imageInfo.ver);
		buf[2] = Conversion.loUint16((short) imageInfo.len);
		buf[3] = Conversion.hiUint16((short) imageInfo.len);
		System.arraycopy(imageInfo.uid, 0, buf, 4, 4);
		// Send image notification
		mCharIdentify.setValue(buf);
		writeCharacteristic(mCharIdentify);

		iBytes = 0;
		iBlocks = 0;
		nBlocks = 0;

		final Timer mTimer = new Timer();

		nBlocks = (short) (imageInfo.len / (OAD_BLOCK_SIZE / HAL_FLASH_WORD_SIZE));
		final TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				try {
					Thread.sleep(MY_INTERVAL);
				} catch (InterruptedException e1) {
					e1.printStackTrace();

				}
				Log.e(tagString, " run: " + mm);
				mm++;
				if (programming) {
					Log.e(tagString, "read to write busy:" + busy);
					if (!busy) {
						if (iBlocks < nBlocks) {
							byte[] mOadBuffer = new byte[OAD_BUFFER_SIZE];
							mOadBuffer[0] = Conversion.loUint16(iBlocks);
							mOadBuffer[1] = Conversion.hiUint16(iBlocks);
							System.arraycopy(imageInfo.fileBytes, iBytes,
									mOadBuffer, 2, OAD_BLOCK_SIZE);
							mCharBlock.setValue(mOadBuffer);
							busy = true;
							boolean success = writeCharacteristic(mCharBlock);

							if (success) {
								iBlocks++;
								Log.e(tagString, "write success:" + iBlocks
										+ " nBlocks:" + nBlocks);
								iBytes += OAD_BLOCK_SIZE;
								if ((iBlocks % 5) == 0) {

									deviceListener.onGetOADProgress(tag,
											(float) (iBlocks * 1.0 / nBlocks),
											bluetoothDevice);
								}
							} else {
								Log.e(tagString, "write unsuccess!!!");
								try {
									Thread.sleep(800);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								writeCharacteristic(mCharBlock);
								try {
									Thread.sleep(PKT_INTERVAL);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						} else {// 下发完成
							deviceListener.onGetOADProgress(tag, 1,
									bluetoothDevice);
							programming = false;
							mTimer.cancel();
						}
					} else {
						try {
							Log.e(tagString, "Thread.sleep(800)");
							Thread.sleep(sleepInterval);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						writeCharacteristic(mCharBlock);
					}
				}
			}
		};
		mTimer.scheduleAtFixedRate(timerTask, 10000, PKT_INTERVAL);

		// mTimer.schedule(timerTask, 10000, PKT_INTERVAL);
		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { try { Thread.sleep(10000); } catch
		 * (InterruptedException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); } while (programming) { Log.e(tagString,
		 * "read to write busy:" + busy); if (!busy) { if (iBlocks < nBlocks) {
		 * byte[] mOadBuffer = new byte[OAD_BUFFER_SIZE]; mOadBuffer[0] =
		 * Conversion.loUint16(iBlocks); mOadBuffer[1] =
		 * Conversion.hiUint16(iBlocks); System.arraycopy(imageInfo.fileBytes,
		 * iBytes, mOadBuffer, 2, OAD_BLOCK_SIZE);
		 * mCharBlock.setValue(mOadBuffer); Log.e(tagString,
		 * "================ write =========="); busy = true; boolean success =
		 * writeCharacteristic(mCharBlock); if (success) { iBlocks++;
		 * Log.e(tagString, "write success:" + iBlocks + " nBlocks:" + nBlocks);
		 * iBytes += OAD_BLOCK_SIZE; if ((iBlocks % 5) == 0) {
		 * 
		 * deviceListener.onGetOADProgress(tag, (float) (iBlocks * 1.0 /
		 * nBlocks), bluetoothDevice); } } else { Log.e(tagString,
		 * "write unsuccess!!!"); try { Thread.sleep(800); } catch
		 * (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } writeCharacteristic(mCharBlock); try {
		 * Thread.sleep(PKT_INTERVAL); } catch (InterruptedException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } } } else {
		 * deviceListener.onGetOADProgress(tag, 1, bluetoothDevice); programming
		 * = false;
		 * 
		 * } try { Thread.sleep(PKT_INTERVAL); } catch (InterruptedException e)
		 * { // TODO Auto-generated catch block e.printStackTrace(); } } else {
		 * Log.e(tagString, "Thread.sleep(800)"); try { Thread.sleep(800); }
		 * catch (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } writeCharacteristic(mCharBlock); try {
		 * Thread.sleep(PKT_INTERVAL); } catch (InterruptedException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } } } }
		 * }).start();
		 */
	}

	/**
	 * before startOAD,you need do this
	 */
	public void startOADMode() {
		if (mBluetoothGatt != null && writeChar != null) {
			sendToDevice(101);
		}
	}

	private final byte[] hex = "0123456789ABCDEF".getBytes();

	// 从字节数组到十六进制字符串转换
	private String Bytes2HexString(byte[] b) {
		byte[] buff = new byte[2 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
			buff[2 * i + 1] = hex[b[i] & 0x0f];
		}
		return new String(buff);
	}

	private boolean checkIsSamsung() {
		String brand = android.os.Build.BRAND;
		Log.e("", " brand:" + brand);
		if (brand.toLowerCase().equals("samsung")) {
			return true;
		}
		return false;
	}
	private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null)
			return;
		BluetoothGattCharacteristic Characteristic_cur = null;

		for (BluetoothGattService gattService : gattServices) {
			// -----Service的字段信息----//
			int type = gattService.getType();
/*
            Log.e(TAG, "-->service type:" + Utils.getServiceType(type));
            Log.e(TAG, "-->includedServices size:"
                    + gattService.getIncludedServices().size());
            Log.e(TAG, "-->service uuid:" + gattService.getUuid());
*/

			// -----Characteristics的字段信息----//
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();
			for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
/*
                Log.e(TAG, "---->char uuid:" + gattCharacteristic.getUuid());
*/

				int permission = gattCharacteristic.getPermissions();
               /* Log.e(TAG,
                        "---->char permission:"
                                + Utils.getCharPermission(permission));*/

				int property = gattCharacteristic.getProperties();
                /*Log.e(TAG,
                        "---->char property:"
                                + Utils.getCharPropertie(property));*/

				byte[] data = gattCharacteristic.getValue();
				if (data != null && data.length > 0) {
                    /*Log.e(TAG, "---->char value:" + new String(data));*/
				}

				if (gattCharacteristic.getUuid().toString().equals(UUID_CHAR5)) {
					gattCharacteristic_char5 = gattCharacteristic;
				}

				if (gattCharacteristic.getUuid().toString().equals(UUID_CHAR6)) {
					// 把char1 保存起来以方便后面读写数据时使用
					gattCharacteristic_char6 = gattCharacteristic;
					Characteristic_cur = gattCharacteristic;
					enableNotification(true,gattCharacteristic);
/*
                    Log.i(TAG, "+++++++++UUID_CHAR6");
*/
				}

				if (gattCharacteristic.getUuid().toString()
						.equals(UUID_HERATRATE)) {
					// 把heartrate 保存起来以方便后面读写数据时使用
					gattCharacteristic_heartrate = gattCharacteristic;
					Characteristic_cur = gattCharacteristic;
					// 接受Characteristic被写的�?�?收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
					enableNotification(true,gattCharacteristic);
/*
                    Log.i(TAG, "+++++++++UUID_HERATRATE");
*/
				}

				if (gattCharacteristic.getUuid().toString()
						.equals(UUID_KEY_DATA)) {
					// 把heartrate 保存起来以方便后面读写数据时使用
					gattCharacteristic_keydata = gattCharacteristic;
					Characteristic_cur = gattCharacteristic;
					// 接受Characteristic被写的�?�?收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
					enableNotification(true,gattCharacteristic);
/*
                    Log.i(TAG, "+++++++++UUID_KEY_DATA");
*/
				}

				if (gattCharacteristic.getUuid().toString()
						.equals(UUID_TEMPERATURE)) {
					// 把heartrate 保存起来�?以方便后面读写数据时使用
					gattCharacteristic_temperature = gattCharacteristic;
					Characteristic_cur = gattCharacteristic;
					// 接受Characteristic被写的�?�?收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
					enableNotification(true,gattCharacteristic);
/*
                    Log.i(TAG, "+++++++++UUID_TEMPERATURE");
*/
				}


				if (gattCharacteristic.getUuid().toString()
						.equals(UUID_0XFFA6)) {
					// 把heartrate 保存起来以方便后面读写数据时使用
					gattCharacteristic_0xffa6 = gattCharacteristic;
					Characteristic_cur = gattCharacteristic;
/*
                    Log.i(TAG, "+++++++++UUID_0XFFA6");
*/
				}

				// -----Descriptors的字段信�?----//
				List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic
						.getDescriptors();
				for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
/*
                    Log.e(TAG, "-------->desc uuid:" + gattDescriptor.getUuid());
*/
					int descPermission = gattDescriptor.getPermissions();
/*
                    Log.e(TAG,
                            "-------->desc permission:"
                                    + Utils.getDescPermission(descPermission));
*/

					byte[] desData = gattDescriptor.getValue();
					if (desData != null && desData.length > 0) {
/*
                        Log.e(TAG, "-------->desc value:" + new String(desData));
*/
					}
				}

			}
		}//
	}
	public static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";
	public static String UUID_CHAR1 = "0000fff1-0000-1000-8000-00805f9b34fb";
	public static String UUID_CHAR2 = "0000fff2-0000-1000-8000-00805f9b34fb";
	public static String UUID_CHAR3 = "0000fff3-0000-1000-8000-00805f9b34fb";
	public static String UUID_CHAR4 = "0000fff4-0000-1000-8000-00805f9b34fb";
	public static String UUID_CHAR5 = "0000fff5-0000-1000-8000-00805f9b34fb";
	public static String UUID_CHAR6 = "0000fff6-0000-1000-8000-00805f9b34fb";
	public static String UUID_CHAR7 = "0000fff7-0000-1000-8000-00805f9b34fb";
	public static String UUID_HERATRATE = "00002a37-0000-1000-8000-00805f9b34fb";
	public static String UUID_TEMPERATURE = "00002a1c-0000-1000-8000-00805f9b34fb";
	public static String UUID_0XFFA6 = "0000ffa6-0000-1000-8000-00805f9b34fb";
	static BluetoothGattCharacteristic gattCharacteristic_char1 = null;
	static BluetoothGattCharacteristic gattCharacteristic_char5 = null;
	static BluetoothGattCharacteristic gattCharacteristic_char6 = null;
	static BluetoothGattCharacteristic gattCharacteristic_heartrate = null;
	static BluetoothGattCharacteristic gattCharacteristic_keydata = null;
	static BluetoothGattCharacteristic gattCharacteristic_temperature = null;
	static BluetoothGattCharacteristic gattCharacteristic_0xffa6 = null;
}
