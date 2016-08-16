package com.example.sid_fu.blecentral;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid_fu.blecentral.db.dao.DeviceDao;
import com.example.sid_fu.blecentral.db.dao.UserDao;
import com.example.sid_fu.blecentral.db.entity.Device;
import com.example.sid_fu.blecentral.db.entity.User;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sid-fu on 2016/5/16.
 */
public class ConfigDevice01 extends ActionBarActivity implements View.OnClickListener{
    private static final String TAG = "ConfigDevice";
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000000;
    private ImageView imgTopleft;
    private ImageView imgTopright;
    private ImageView imgBottomleft;
    private ImageView imgBottomright;
    private Button topleft_ok;
    private Button topleft_next;
    private Button topright_ok;
    private Button topright_next;
    private Button bottomleft_ok;
    private Button bottomleft_next;
    private Button bottomright_ok;
    private Button bottomright_next;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private int ScanTimeOut = 10000;
    public static int leftF = 1;
    public static int rightF = 2;
    public static int leftB = 3;
    public static int rightB =4;
    public static int none =5;
    private AlphaAnimation animation;
    private ProgressBar pb_left_from;
    private ProgressBar pb_right_from;
    private ProgressBar pb_left_back;
    private ProgressBar pb_right_back;
    private final int maxLenght = -65;
    private TextView tv_note_left_from;
    private TextView tv_note_right_from;
    private TextView tv_note_left_back;
    private TextView tv_note_right_back;
    private ScanDeviceRunnable leftFRunable;
    private ScanDeviceRunnable rightFRunable;
    private ScanDeviceRunnable leftBRunable;
    private ScanDeviceRunnable rightBRunable;
    private Device deviceDate;
    private LoadingDialog loadDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_config);
        initUI();
        iniBle();
        //setFlickerAnimation(imgTopright);
        /*
        animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); //
        setFlickerAnimation(imgTopleft);
        */
        deviceDate = (Device) getIntent().getExtras().getSerializable("device");
        User user = new UserDao(this).get(1);
//        deviceDate = new Device();
//        deviceDate.setDeviceName("宝马3系列");
//        deviceDate.setDeviceDescripe("宝马3系列是一款.....");
        deviceDate.setUser(user);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        Logger.d("Try to bindService=" + bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE));
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }
    private void initUI() {
        loadDialog = new LoadingDialog(this);
        imgTopleft = (ImageView) findViewById(R.id.img_topleft);
        imgTopright = (ImageView) findViewById(R.id.img_topright);
        imgBottomleft = (ImageView) findViewById(R.id.img_bottomleft);
        imgBottomright = (ImageView) findViewById(R.id.img_bottomright);

        pb_left_from = (ProgressBar) findViewById(R.id.pb_left_from);
        pb_left_from.setVisibility(View.GONE);
        pb_right_from = (ProgressBar) findViewById(R.id.pb_right_from);
        pb_left_back = (ProgressBar) findViewById(R.id.pb_left_back);
        pb_right_back = (ProgressBar) findViewById(R.id.pb_right_back);

        tv_note_left_from = (TextView) findViewById(R.id.tv_note_left_from);
        tv_note_right_from = (TextView) findViewById(R.id.tv_note_right_from);
        tv_note_left_back = (TextView) findViewById(R.id.tv_note_left_back);
        tv_note_right_back = (TextView) findViewById(R.id.tv_note_right_back);

        topleft_ok = (Button) findViewById(R.id.ll_topleft).findViewById(R.id.btn_ok);
        topleft_next = (Button) findViewById(R.id.ll_topleft).findViewById(R.id.btn_next);

        topright_ok = (Button) findViewById(R.id.ll_topright).findViewById(R.id.btn_ok);
        topright_next = (Button) findViewById(R.id.ll_topright).findViewById(R.id.btn_next);

        bottomleft_ok = (Button) findViewById(R.id.ll_bottomleft).findViewById(R.id.btn_ok);
        bottomleft_next = (Button) findViewById(R.id.ll_bottomleft).findViewById(R.id.btn_next);

        bottomright_ok = (Button) findViewById(R.id.ll_bottomright).findViewById(R.id.btn_ok);
        bottomright_next = (Button) findViewById(R.id.ll_bottomright).findViewById(R.id.btn_next);

        leftFRunable = new ScanDeviceRunnable(handler,leftF);
        rightFRunable = new ScanDeviceRunnable(handler,rightF);
        leftBRunable = new ScanDeviceRunnable(handler,leftB);
        rightBRunable = new ScanDeviceRunnable(handler,rightB);
        //默认开启左前边扫描
        //handler.postDelayed(leftFRunable,ScanTimeOut);
        showDialog("正在识别信号强度。。。",false);
        topleft_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevice(topleft_ok,topleft_next,pb_left_from,pb_right_from,leftFRunable,rightFRunable,rightF);
            }
        });
        topright_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevice(topright_ok,topright_next,pb_right_from,pb_right_back,rightFRunable,rightBRunable,rightB);
            }
        });
        bottomleft_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDeviceToNextActivity(bottomleft_ok,null,pb_left_back,null,leftBRunable,null,none);

            }
        });
        bottomright_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevice(bottomright_ok,bottomright_next,pb_right_back,pb_left_back,rightBRunable,leftBRunable,leftB);
            }
        });
        topleft_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBtn(topleft_ok,topleft_next,pb_left_from,leftFRunable);
                pairedDevice(topleft_ok,topleft_next,pb_left_from,pb_right_from,leftFRunable,rightFRunable,rightF);
            }
        });
        topright_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBtn(topright_ok,topright_next,pb_right_from,rightFRunable);
                pairedDevice(topright_ok,topright_next,pb_right_from,pb_right_back,rightFRunable,rightBRunable,rightB);
            }
        });
        bottomleft_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomleft_next.getText().equals("跳过"))
                {
                    Intent intent = new Intent();
                    intent.setClass(ConfigDevice01.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                    new DeviceDao(ConfigDevice01.this).add(deviceDate);
                    SharedPreferences.getInstance().putBoolean(Constants.FIRST_CONFIG,true);
                    return;
                }
                nextBtn(bottomleft_ok,bottomleft_next,pb_left_from,leftBRunable);
                pairedDeviceToNextActivity(bottomleft_ok,null,pb_left_back,null,leftBRunable,null,none);
            }
        });
        bottomright_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBtn(bottomright_ok,bottomright_next,pb_right_back,rightBRunable);
                pairedDevice(bottomright_ok,bottomright_next,pb_right_back,pb_left_back,rightBRunable,leftBRunable,leftB);
            }
        });
    }
    private void nextBtn(Button enterBtn,Button currentBtn,ProgressBar currentPb,ScanDeviceRunnable currentRunnable)
    {
        enterBtn.setText("确定");
        currentBtn.setVisibility(View.GONE);
        currentPb.setVisibility(View.GONE);
        enterBtn.setVisibility(View.GONE);
        //保存数据到本地
//        currentTv.setText("左前轮：\n"+device.getAddress());
//        state = none;
        handler.removeCallbacks(currentRunnable);
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
    private void pairedDeviceToNextActivity(Button currentBtn,Button nextBtn,ProgressBar currentPb,ProgressBar nextPb,ScanDeviceRunnable currentRunnable,ScanDeviceRunnable nextRunnable,int nextState)
    {
        if(currentBtn.getText().equals("确定"))
        {
            Intent intent = new Intent();
            intent.setClass(ConfigDevice01.this,HomeActivity.class);
            startActivity(intent);
            finish();
            new DeviceDao(this).add(deviceDate);
            SharedPreferences.getInstance().putBoolean(Constants.FIRST_CONFIG,true);
        }else
        {
            //setFlickerAnimation(imgTopright);
            currentBtn.setText("确定");
            currentBtn.setVisibility(View.GONE);
//            currentPb.setVisibility(View.VISIBLE);
            showDialog("正在识别信号强度。。。",false);
            handler.postDelayed(currentRunnable,ScanTimeOut);
        }
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
    private void pairedDevice(Button currentBtn,Button nextBtn,ProgressBar currentPb,ProgressBar nextPb,ScanDeviceRunnable currentRunnable,ScanDeviceRunnable nextRunnable,int nextState)
    {
        if(currentBtn.getText().equals("确定"))
        {
            currentBtn.setVisibility(View.GONE);
            nextBtn.setVisibility(View.GONE);
//            nextPb.setVisibility(View.VISIBLE);
            state = nextState;
            showDialog("正在识别信号强度。。。",false);
            handler.postDelayed(nextRunnable,ScanTimeOut);
        }else
        {
            //setFlickerAnimation(imgTopright);
            currentBtn.setText("确定");
            isFirst = false;
            currentBtn.setVisibility(View.GONE);
//            currentPb.setVisibility(View.VISIBLE);
            showDialog("正在识别信号强度。。。",false);
            handler.postDelayed(currentRunnable,ScanTimeOut);
        }
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
    private void handlerReScan(Button currentBtn,ProgressBar currentPb)
    {
        if(currentBtn.getText().equals("确定"))
        {
            currentBtn.setText("重试");
//            currentPb.setVisibility(View.GONE);
            currentBtn.setVisibility(View.VISIBLE);
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(state==none) return;
            Logger.e("重试");
            loadDialog.dismiss();
            switch (msg.what)
            {
                case 1:
                    handlerReScan(topleft_ok,pb_left_from);
                    break;
                case 2:
                    handlerReScan(topright_ok,pb_right_from);
                    break;
                case 3:
                    handlerReScan(bottomleft_ok,pb_left_back);
                    break;
                case 4:
                    handlerReScan(bottomright_ok,pb_right_back);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //animation.cancel();
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mBluetoothAdapter = null;
    }

    private void setFlickerAnimation(ImageView iv_chat_head) {
        iv_chat_head.setImageResource(R.color.white);
        iv_chat_head.setAnimation(animation);
    }

    private int state = leftF;
    private boolean isFirst = false;
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //ParsedAd ad = DataUtils.parseData(scanRecord);
                    for (BluetoothDevice ble : mDeviceList)
                    {
                        Logger.e("列表中存在的设备："+ble.getAddress());
                    }
                    if(!isFirst&&!ManageDevice.isConfigEquals(mDeviceList,device)&&rssi>maxLenght)
                    {
                        isFirst = true;
                        bleIsFind(device,rssi,scanRecord,state);
                        Logger.e("发现新设备"+device.getAddress());
                    }

                    // 发现小米3必须加以下的这3个语句，否则不更新数据，而三星的机子s3则没有这个问题
                    /*if (mScanning == true) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        mBluetoothAdapter.startLeScan(mLeScanCallback);
                    }
                    */
                }
            });
        }
    };
    private void scanForResult(BluetoothDevice device,ScanDeviceRunnable runnable)
    {
        handler.removeCallbacks(runnable);
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        Logger.e("开始连接："+device.getAddress());
        showDialog(device.getAddress()+"正在连接。。。",false);
        //开始连接蓝牙
        mBluetoothLeService.connect(device.getAddress());
    }
    private void bleIsFind(BluetoothDevice device, int rssi, byte[] data,int state)
    {
        /*
        try {
            ParsedAd ad = DataUtils.parseData(data);
            if(ad.datas==null) return;
            Logger.e(DigitalTrans.byte2hex(ad.datas));
            float press = ((float) DigitalTrans.byteToAlgorism(ad.datas[1]) * 160) / 51 / 100;
            int temp = DigitalTrans.byteToAlgorism(ad.datas[2]) - 50;
            int stateUse = DigitalTrans.byteToBin(ad.datas[0]);
            Logger.e("使用情况："+stateUse);
//            if(stateUse!=0) return;
        }catch (NullPointerException e)
        {
            Logger.e(TAG,"erro"+e.toString());
        }
        */
        Logger.e("信号强度"+rssi+data.toString());
        switch (state)
        {
            case 1:
                    scanForResult(device,leftFRunable);
                    //保存数据库
                    deviceDate.setLeft_FD(device.getAddress());
                break;
            case 2:
                    scanForResult(device,rightFRunable);
                    //保存数据库
                    deviceDate.setRight_FD(device.getAddress());
                break;
            case 3:
                    scanForResult(device,leftBRunable);
                    //保存数据库
                    deviceDate.setLeft_BD(device.getAddress());
                break;
            case 4:
                    scanForResult(device,rightBRunable);
                    //保存数据库
                    deviceDate.setRight_BD(device.getAddress());
                break;
            default:
                break;
        }
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
        super.onActivityResult(requestCode, resultCode, data);
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
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        }).start();
    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(SCAN_PERIOD);
                        if (mScanning) {
                            Logger.e(TAG,"断开扫描");
                            mScanning = false;
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            //invalidateOptionsMenu();
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

        //invalidateOptionsMenu();
    }

    @Override
    public void onClick(View v) {

    }

    private BluetoothLeService mBluetoothLeService;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            Log.e(TAG, "mBluetoothLeService is okay");
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                Logger.e("connected:"+device.getAddress());
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //断开
                showDialog("已完成",false);
                loadDialog.dismiss();
                Logger.e("Disconneted GATT Services"+device.getAddress());
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //onSuccess(device);
                mBluetoothLeService.writeChar6("AT+USED=1");
                mBluetoothLeService.writeChar6("AT+USED=1");
                showDialog("正在配置传感器。。。",true);
                Logger.e("Discover GATT Services"+device.getAddress());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                        BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                        //通讯成功 返回OK
                        if (data != null) {
                            Logger.e(data.toString());
                            onSuccess(device);
                        }
                    }
                });
            }
        }
    };


    private void onSuccess(BluetoothDevice device)
    {
        switch (state) {
            case 1:
                setUsedToTrue(device,topleft_ok,pb_left_from,tv_note_left_from);
                break;
            case 2:
                setUsedToTrue(device,topright_ok,pb_right_from,tv_note_right_from);
                break;
            case 3:
                setUsedToTrue(device,bottomleft_ok,pb_left_back,tv_note_left_back);
                break;
            case 4:
                setUsedToTrue(device,bottomright_ok,pb_right_back,tv_note_right_back);
                break;
        }
    }
    private void setUsedToTrue(BluetoothDevice device,Button currentBtn,ProgressBar currentPb,TextView currentTv)
    {
        state = none;
        currentBtn.setText("确定");
        currentBtn.setVisibility(View.VISIBLE);
        currentPb.setVisibility(View.GONE);
        //保存数据到本地
        currentTv.setText("左前轮：\n"+device.getAddress());
        showDialog("正在复位传感器。。。",true);
        isFirst = false;
        mDeviceList.add(device);
        mBluetoothLeService.disconnect();
    }

    private void showDialog(String str,boolean isConnect)
    {
        loadDialog.setText(str);
        loadDialog.show();
        //if(isConnect) mBluetoothLeService.disconnect();
        /*
        switch (state) {
            case 1:
                handler.removeCallbacks(leftFRunable);
                handler.postDelayed(leftFRunable,ScanTimeOut);
                break;
            case 2:
                handler.removeCallbacks(rightFRunable);
                handler.postDelayed(rightFRunable,ScanTimeOut);
                break;
            case 3:
                handler.removeCallbacks(leftBRunable);
                handler.postDelayed(leftBRunable,ScanTimeOut);
                break;
            case 4:
                handler.removeCallbacks(rightBRunable);
                handler.postDelayed(rightBRunable,ScanTimeOut);
                break;
        }*/
    }
    private void startCount()
    {
        switch (state) {
            case 1:
//                handler.removeCallbacks(leftFRunable);
                handler.postDelayed(leftFRunable,ScanTimeOut);
                break;
            case 2:
//                handler.removeCallbacks(rightFRunable);
                handler.postDelayed(rightFRunable,ScanTimeOut);
                break;
            case 3:
//                handler.removeCallbacks(leftBRunable);
                handler.postDelayed(leftBRunable,ScanTimeOut);
                break;
            case 4:
//                handler.removeCallbacks(rightBRunable);
                handler.postDelayed(rightBRunable,ScanTimeOut);
                break;
        }
    }
    private void stopCount()
    {
        switch (state) {
            case 1:
                handler.removeCallbacks(leftFRunable);
//                handler.postDelayed(leftFRunable,ScanTimeOut);
                break;
            case 2:
                handler.removeCallbacks(rightFRunable);
 //               handler.postDelayed(rightFRunable,ScanTimeOut);
                break;
            case 3:
                handler.removeCallbacks(leftBRunable);
//                handler.postDelayed(leftBRunable,ScanTimeOut);
                break;
            case 4:
                 handler.removeCallbacks(rightBRunable);
  //              handler.postDelayed(rightBRunable,ScanTimeOut);
                break;
        }
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_NAME_RSSI);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }
}