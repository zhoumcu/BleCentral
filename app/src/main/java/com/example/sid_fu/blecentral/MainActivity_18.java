package com.example.sid_fu.blecentral;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid_fu.blecentral.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity_18 extends AppCompatActivity {

    private static final int SCAN_INTERVAL_MS = 250;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 100000;
    private Handler scanHandler = new Handler();
    private List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
    private ScanSettings scanSettings;
    private boolean isScanning = false;
    private boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main01);
        Log.e(TAG, "sdfksdjf");
        tv = (TextView) findViewById(R.id.text);
        iniBle();
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv.append(String.valueOf(msg.obj)+"\n");
        }
    };
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //String str = DataUtils.btyesToAsciiString(scanRecord);
                    String str = DataUtils.bytesToHexString(scanRecord);
                    Log.e(TAG,str);
                    ParsedAd ad = DataUtils.parseData(scanRecord);
                    //byte[] bytes = DataUtils.adv_report_parse((byte) DataUtils.BLE_GAP_AD_TYPE_SLAVE_DATA,scanRecord);
                   // String strByte = DataUtils.bytesToHexString(bytes);
                    Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
                    t.setToNow(); // 取得系统时间。
                    Message msg = new Message();
                    msg.what =1;
                    msg.obj = String.valueOf(t.hour)+":"+String.valueOf(t.minute)+":"+String.valueOf(t.second)+"=="+device.getAddress()+":"+str
                    +"\n"+ad.localName+":"+ad.manufacturer+":"+ad.datas+"\n";
                    //+strByte;
                    handler.sendMessage(msg);

                    //                bleIsFind(device.getAddress());
                    //Logger.e(TAG,"发现新设备"+device.getAddress());
                    //scanBleForResult(device);
                    /*byte[] typedata = ManageDevice.adv_report_parse((byte) ManageDevice.BLE_GAP_AD_TYPE_SERVICE_DATA,scanRecord);
                    if(typedata != null)
                    {
                        String str = new String();
                        for(byte b:typedata)
                        {
                            str += (char)b;
                        }
                        Log.d(TAG,"data:"+str);   //打印出来！
                    }*/
//                    ParsedAd ad = ManageDevice.parseData(scanRecord);
//                    Log.d(TAG,"data:"+ad.localName+"--"+ad.manufacturer+"--"+ad.flags);   //打印出来！
                }
            });
        }
    };
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(SCAN_PERIOD);

                        if (mScanning) {
                            //Logger.e(TAG,"断开扫描");
                            mScanning = false;
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            invalidateOptionsMenu();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });//.start();
            //WinToast.makeText(mContext,"start scan");
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

        invalidateOptionsMenu();
    }
    private void iniBle() {
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(new Runnable() {
            public void run() {
                if (mBluetoothAdapter.isEnabled()) {
                    scanLeDevice(true);
                    mScanning = true;
                    //set timer to scan ble that is on changing
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        }).start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        scanLeDevice(true);
        mScanning = true;
        //set timer to scan ble that is on changing
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
}
