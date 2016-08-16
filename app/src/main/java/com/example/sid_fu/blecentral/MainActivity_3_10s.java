package com.example.sid_fu.blecentral;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid_fu.blecentral.ui.BleData;
import com.example.sid_fu.blecentral.utils.DataUtils;
import com.example.sid_fu.blecentral.utils.DigitalTrans;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.ToastUtil;
import com.example.sid_fu.blecentral.widget.WiperSwitch;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainActivity_3_10s extends ActionBarActivity implements View.OnClickListener {
    private final static String TAG = MainActivity_3_10s.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000000;
    private TextView editConfig;
    private WiperSwitch switch1;
    private ImageView imgTopleft;
    private TextView topleftAdjust;
    private ImageView imgTopright;
    private TextView toprightAdjust;
    private ImageView imgBottomleft;
    private TextView bottomleftAdjust;
    private ImageView imgBottomright;
    private TextView bottomrightAdjust;
    private TextView topleft_preesure;
    private TextView topleft_temp;
    private TextView topright_preesure;
    private TextView topright_temp;
    private TextView bottomleft_preesure;
    private TextView bottomleft_temp;
    private TextView bottomright_preesure;
    private TextView bottomright_temp;
    private TextView topleft_note;
    private TextView topright_note;
    private TextView bottomleft_note;
    private TextView bottomright_note;
    private String preStr;
    private String curStr;
    private BluetoothLeService mBluetoothLeService = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private boolean mScanning;
    private TextView topleft_voltage;
    private TextView topright_voltage;
    private TextView bottomleft_voltage;
    private TextView bottomright_voltage;
    private TextView topleft_releat;
    private TextView topright_releat;
    private TextView bottomleft_releat;
    private TextView bottomright_releat;
    private MainActivity_3_10s mContext;
    private DecimalFormat df;
    private Timer timer;
    private MyTimerTask myTimerTask;
    private int timeCount = 0;
    private int connectedNum;
    private Handler mHandler;
    private String engFh = "";
    private DecimalFormat df1;

    private MyBluetoothDevice leftBDevice =null;
    private MyBluetoothDevice leftFDevice =null ;
    private MyBluetoothDevice rightFDevice =null;
    private MyBluetoothDevice rightBDevice =null;
    private List<MyBluetoothDevice> mDeviceList = new ArrayList<>();
    private int index = 0;
    private int indexRe = 0;
    private boolean isFirst = false;
    private ProgressDialog progressDialog;
    private int currentsecond;
    private SimpleDateFormat d;
    private String nowtime;
    private int count;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_pressure);
        mContext =MainActivity_3_10s.this;

        /*显示App icon左侧的back键*/
        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);

        initUI();
        iniBle();
        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
        t.setToNow(); // 取得系统时间。
        int minute = t.minute;
        currentsecond = t.second;

        d= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化时间
        nowtime=d.format(new Date());//按以上格式 将当前时间转换成字符串

        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0)
                {
                    String testtime=d.format(new Date());//按以上格式 将当前时间转换成字符串
                    try {
                        long result=(d.parse(testtime).getTime()-d.parse(nowtime).getTime())/1000;
                        ToastUtil.show("扫描四个耗时：" + result+"秒");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        df=new DecimalFormat(".##");
        df1=new DecimalFormat(".#");

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        Logger.d("Try to bindService=" + bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE));
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private void initUI() {
        editConfig = (TextView) findViewById(R.id.edit_config);
        switch1 = (WiperSwitch) findViewById(R.id.switch1);
        imgTopleft = (ImageView) findViewById(R.id.img_topleft);
        imgTopright = (ImageView) findViewById(R.id.img_topright);
        imgBottomleft = (ImageView) findViewById(R.id.img_bottomleft);
        imgBottomright = (ImageView) findViewById(R.id.img_bottomright);
        topleftAdjust = (TextView) findViewById(R.id.topleft_adjust);
        toprightAdjust = (TextView) findViewById(R.id.topright_adjust);
        bottomleftAdjust = (TextView) findViewById(R.id.bottomleft_adjust);
        bottomrightAdjust = (TextView) findViewById(R.id.bottomright_adjust);

        topleft_preesure = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_preesure);
        topleft_temp = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_temp);
        topleft_note = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_note);
        topleft_voltage = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_voltage);
        topleft_releat = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_releat);

        topright_preesure = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_preesure);
        topright_temp = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_temp);
        topright_note = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_note);
        topright_voltage = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_voltage);
        topright_releat = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_releat);

        bottomleft_preesure = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_preesure);
        bottomleft_temp = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_temp);
        bottomleft_note = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_note);
        bottomleft_voltage = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_voltage);
        bottomleft_releat = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_releat);

        bottomright_preesure = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_preesure);
        bottomright_temp = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_temp);
        bottomright_note = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_note);
        bottomright_voltage = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_voltage);
        bottomright_releat = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_releat);

        bottomleftAdjust.setOnClickListener(this);
        bottomrightAdjust.setOnClickListener(this);
        topleftAdjust.setOnClickListener(this);
        toprightAdjust.setOnClickListener(this);
        editConfig.setOnClickListener(this);
    }

    private  class MyTimerTask extends TimerTask {
        @Override
        public synchronized void run() {
            timeCount++;
            if(mDeviceList.size()==4||timeCount>=30)
            {
                Logger.e(TAG,"全部扫描到,且关闭扫描"+mDeviceList.size());
                if(mScanning&&timeCount<30)
                {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                if(index<mDeviceList.size())
                {
                    mDeviceList.get(index).connectBle(mDeviceList.get(index));
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else
                {
                    index = 5;

                    if(indexRe<mDeviceList.size())
                    {
                        Logger.e("i:"+indexRe);
                        mDeviceList.get(indexRe).reConnectBle(mDeviceList.get(indexRe));
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else
                    {
                        indexRe = -1;
                    }
                    indexRe++;
                }
                index++;
            }else {
                Logger.e(TAG,"扫描中。。。。。。"+mDeviceList.size());
            }
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
         runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Logger.e("发现新设备"+device.getAddress());
                    ParsedAd ad = DataUtils.parseData(scanRecord);
//                    bleIsFind(device,ad.datas);
                    //scanBleForResult(device);
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

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                Logger.e("connected:"+device.getAddress());
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //断开
                onFailed(device);
//                bleIsDisConnected(device.getAddress());
                invalidateOptionsMenu();
                Logger.e("Disconneted GATT Services"+device.getAddress());
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
//                bleIsConnected(device.getAddress());
                onSuccess(device);
                invalidateOptionsMenu();
                Logger.e("Discover GATT Services"+device.getAddress());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //通讯成功
                //onSuccess(device);
                if (data != null) {
                    //Logger.e(data);
//                    bleStringToDouble(device,data);
                }
            }
        }
    };
    /*
    private void scanBleForResult(BluetoothDevice device)
    {
        if(device.getAddress().equals(ManageDevice.leftFDevice))
        {
 //                   connectBle(device,1);
            if(leftFDevice==null)
                leftFDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
//            MySharePreferences.saveObject(mContext,ManageDevice.leftFDevice,leftFDevice);
//            leftFDevice.connectBle();
            if(!ManageDevice.isEquals(mDeviceList,device))
                mDeviceList.add(leftFDevice);
            //leftFDevice.connectBle();
            leftFDevice.setBleScaned(true);
            //bleIsFind(device.getAddress());
        }else if(device.getAddress().equals(ManageDevice.rightFDevice))
        {
//                    connectBle(device,1);
            if(rightFDevice==null)
                rightFDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
//            MySharePreferences.saveObject(mContext,ManageDevice.rightFDevice,rightFDevice);
//            rightFDevice.connectBle();
            if(!ManageDevice.isEquals(mDeviceList,device))
                mDeviceList.add(rightFDevice);
            rightFDevice.setBleScaned(true);
            //bleIsFind(device.getAddress());
        }else if(device.getAddress().equals(ManageDevice.leftBDevice))
        {
//                    connectBle(device,1);
            if(leftBDevice==null)
                leftBDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
//            MySharePreferences.saveObject(mContext,ManageDevice.leftBDevice,leftBDevice);
//            leftBDevice.connectBle();
            if(!ManageDevice.isEquals(mDeviceList,device))
                mDeviceList.add(leftBDevice);
            leftBDevice.setBleScaned(true);
            //bleIsFind(device.getAddress());
        }else if(device.getAddress().equals(ManageDevice.rightBDevice))
        {
//                    connectBle(device,1);
            if(rightBDevice==null)
                rightBDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
//            MySharePreferences.saveObject(mContext,ManageDevice.rightBDevice,rightBDevice);
//            rightBDevice.connectBle();
            if(!ManageDevice.isEquals(mDeviceList,device))
                mDeviceList.add(rightBDevice);
            rightBDevice.setBleScaned(true);
            //bleIsFind(device.getAddress());
        }
    }
    */
    private void onSuccess(BluetoothDevice device)
    {
        if(leftFDevice!=null&&leftFDevice.getDevice().equals(device))
        {
            ManageDevice.setSuccess(device,leftFDevice);
            //if(!isFirst)
            //{
                //isFirst = true;
                //leftFDevice.writeChar6("AT+SIMU=1");
                leftFDevice.writeChar6("AT+GET");
            /*
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            leftFDevice.writeChar6("AT+GET");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            leftFDevice.writeChar6("AT+GET");

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        leftFDevice.writeChar6("AT+GET");
                        leftFDevice.writeChar6("AT+GET");
                        leftFDevice.writeChar6("AT+GET");
                        leftFDevice.writeChar6("AT+GET");
                    }
                },2000);
                */
            //}
        }else
        if(rightFDevice!=null&&rightFDevice.getDevice().equals(device))
        {
            ManageDevice.setSuccess(device,rightFDevice);
            rightFDevice.writeChar6("AT+GET");
            /*
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rightFDevice.writeChar6("AT+GET");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rightFDevice.writeChar6("AT+GET");*/
        }else
        if(leftBDevice!=null&&leftBDevice.getDevice().equals(device))
        {
            ManageDevice.setSuccess(device,leftBDevice);
            leftBDevice.writeChar6("AT+GET");/*
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            leftBDevice.writeChar6("AT+GET");*/
        }else
        if(rightBDevice!=null&&rightBDevice.getDevice().equals(device))
        {
            ManageDevice.setSuccess(device,rightBDevice);
            rightBDevice.writeChar6("AT+GET");
            /*
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rightBDevice.writeChar6("AT+GET");*/
        }
    }
    private  void onFailed(BluetoothDevice device)
    {
        if(leftFDevice!=null&&leftFDevice.getDevice().equals(device))
        {
            ManageDevice.setFailed(device,leftFDevice);

        }else
        if(rightFDevice!=null&&rightFDevice.getDevice().equals(device))
        {
            ManageDevice.setFailed(device,rightFDevice);
            //rightFDevice.writeChar6("AT+TEST = 1");
        }else
        if(leftBDevice!=null&&leftBDevice.getDevice().equals(device))
        {
            ManageDevice.setFailed(device,leftBDevice);
            //leftBDevice.writeChar6("AT+TEST = 1");
        }else
        if(rightBDevice!=null&&rightBDevice.getDevice().equals(device))
        {
            ManageDevice.setFailed(device,rightBDevice);
            //rightBDevice.writeChar6("AT+TEST = 1");
        }
        if(isDisconnect)
        {
            for(MyBluetoothDevice ble : mDeviceList)
            {
                if(ble.getDevice().equals(device))
                {
                    if(!ble.isSuccessComm())
                        count++;
                }
            }
            if(count==mDeviceList.size())
            //if(!rightFDevice.isSuccessComm()&&!rightBDevice.isSuccessComm()&&!leftBDevice.isSuccessComm()&&!leftFDevice.isSuccessComm())
            {
                Logger.e("已经关闭所有蓝牙设备了！！！！！！！！");
                if(progressDialog!=null)
                    progressDialog.dismiss();
                finish();
            }
        }
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }
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
                            invalidateOptionsMenu();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            //WinToast.makeText(mContext,"start scan");
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

        invalidateOptionsMenu();
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
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask,2000,2000);
        super.onActivityResult(requestCode, resultCode, data);
    }
    /*
    private void bleIsFind(String strAddress)
    {
//        Logger.e("ble is connect"+strAddress);
        if(strAddress.equals(ManageDevice.leftBDevice))
        {
            imgBottomleft.setBackgroundColor(getResources().getColor(R.color.green));

        }else if(strAddress.equals(ManageDevice.rightBDevice))
        {
            imgBottomright.setBackgroundColor(getResources().getColor(R.color.green));
        }else if(strAddress.equals(ManageDevice.leftFDevice))
        {
            imgTopleft.setBackgroundColor(getResources().getColor(R.color.green));
        }else if(strAddress.equals(ManageDevice.rightFDevice))
        {
            imgTopright.setBackgroundColor(getResources().getColor(R.color.green));
        }
    }

    private void bleIsFind(BluetoothDevice device,byte[] data)
    {
        String strAddress = device.getAddress();
        try {
            int state = DigitalTrans.byteToBin(data[0]);
            int press = DigitalTrans.byteToAlgorism(data[1])*160/51;
            int temp = DigitalTrans.byteToAlgorism(data[2])-50;
            Logger.e("使用情况："+state+"\n");
            Logger.e(press+"\n");
            Logger.e(temp+"");
            if(strAddress.equals(ManageDevice.leftBDevice))
            {
                imgBottomleft.setBackgroundColor(getResources().getColor(R.color.green));
                bottomleft_preesure.setText(String.valueOf(press));
                bottomleft_temp.setText(String.valueOf(temp));
                if(leftBDevice==null)
                    leftBDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
//            MySharePreferences.saveObject(mContext,ManageDevice.leftBDevice,leftBDevice);
//            leftBDevice.connectBle();
                if(!ManageDevice.isEquals(mDeviceList,device))
                    mDeviceList.add(leftBDevice);
                leftBDevice.setBleScaned(true);
                //bleIsFind(device.getAddress());
            }else if(strAddress.equals(ManageDevice.rightBDevice))
            {
                imgBottomright.setBackgroundColor(getResources().getColor(R.color.green));
                bottomright_preesure.setText(String.valueOf(press));
                bottomright_temp.setText(String.valueOf(temp));
                if(rightBDevice==null)
                    rightBDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
//            MySharePreferences.saveObject(mContext,ManageDevice.rightBDevice,rightBDevice);
//            rightBDevice.connectBle();
                if(!ManageDevice.isEquals(mDeviceList,device))
                    mDeviceList.add(rightBDevice);
                rightBDevice.setBleScaned(true);
                //bleIsFind(device.getAddress());
            }else if(strAddress.equals(ManageDevice.leftFDevice))
            {
                imgTopleft.setBackgroundColor(getResources().getColor(R.color.green));
                topleft_preesure.setText(String.valueOf(press));
                topleft_temp.setText(String.valueOf(temp));
                if(leftFDevice==null)
                    leftFDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
//            MySharePreferences.saveObject(mContext,ManageDevice.leftFDevice,leftFDevice);
//            leftFDevice.connectBle();
                if(!ManageDevice.isEquals(mDeviceList,device))
                    mDeviceList.add(leftFDevice);
                //leftFDevice.connectBle();
                leftFDevice.setBleScaned(true);
                //bleIsFind(device.getAddress());
            }else if(strAddress.equals(ManageDevice.rightFDevice))
            {
                imgTopright.setBackgroundColor(getResources().getColor(R.color.green));
                topright_preesure.setText(String.valueOf(press));
                topright_temp.setText(String.valueOf(temp));
                if(rightFDevice==null)
                    rightFDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
//            MySharePreferences.saveObject(mContext,ManageDevice.rightFDevice,rightFDevice);
//            rightFDevice.connectBle();
                if(!ManageDevice.isEquals(mDeviceList,device))
                    mDeviceList.add(rightFDevice);
                rightFDevice.setBleScaned(true);
                //bleIsFind(device.getAddress());
            }
            if(mDeviceList.size()==4)
            {
                mHandler.sendEmptyMessage(0);
            }
        }catch (NullPointerException e)
        {
            Logger.e("erro"+e.toString());
        }finally {
            Logger.e("run finally");
        }
    }
    private void bleIsFind(String strAddress,byte[] data)
    {
        try {
            int press = DigitalTrans.byteToAlgorism(data[0])*160/51;
            int temp = DigitalTrans.byteToAlgorism(data[1])-50;
            Logger.e(TAG,press+"\n");
            Logger.e(TAG,temp+"");
            if(strAddress.equals(ManageDevice.leftBDevice))
            {
                imgBottomleft.setBackgroundColor(getResources().getColor(R.color.green));
                bottomleft_preesure.setText(String.valueOf(press));
                bottomleft_temp.setText(String.valueOf(temp));

            }else if(strAddress.equals(ManageDevice.rightBDevice))
            {
                imgBottomright.setBackgroundColor(getResources().getColor(R.color.green));
                bottomright_preesure.setText(String.valueOf(press));
                bottomright_temp.setText(String.valueOf(temp));
            }else if(strAddress.equals(ManageDevice.leftFDevice))
            {
                imgTopleft.setBackgroundColor(getResources().getColor(R.color.green));
                topleft_preesure.setText(String.valueOf(press));
                topleft_temp.setText(String.valueOf(temp));
            }else if(strAddress.equals(ManageDevice.rightFDevice))
            {
                imgTopright.setBackgroundColor(getResources().getColor(R.color.green));
                topright_preesure.setText(String.valueOf(press));
                topright_temp.setText(String.valueOf(temp));
            }
        }catch (NullPointerException e)
        {
            Logger.e(TAG,"erro"+e.toString());
        }
//        Logger.e("ble is connect"+strAddress);
    }
    private void bleIsConnected(String strAddress)
    {
//        Logger.e("ble is connect"+strAddress);
        if(strAddress.equals(ManageDevice.leftBDevice))
        {
            imgBottomleft.setBackgroundColor(getResources().getColor(R.color.white));
            leftBDevice.setRequestConnect(true);

        }else if(strAddress.equals(ManageDevice.rightBDevice))
        {
            imgBottomright.setBackgroundColor(getResources().getColor(R.color.white));
            rightBDevice.setRequestConnect(true);
        }else if(strAddress.equals(ManageDevice.leftFDevice))
        {
            imgTopleft.setBackgroundColor(getResources().getColor(R.color.white));
            leftFDevice.setRequestConnect(true);
        }else if(strAddress.equals(ManageDevice.rightFDevice))
        {
            imgTopright.setBackgroundColor(getResources().getColor(R.color.white));
            rightFDevice.setRequestConnect(true);
        }
    }
    private void bleIsDisConnected(String strAddress)
    {
//        Logger.e("ble is disconnect"+strAddress);
        if(strAddress.equals(ManageDevice.leftBDevice))
        {
            imgBottomleft.setBackground(null);
            bottomleft_note.setVisibility(View.GONE);
        }else if(strAddress.equals(ManageDevice.rightBDevice))
        {
            imgBottomright.setBackground(null);
            bottomright_note.setVisibility(View.GONE);
        }else if(strAddress.equals(ManageDevice.leftFDevice))
        {
            imgTopleft.setBackground(null);
            topleft_note.setVisibility(View.GONE);
        }else if(strAddress.equals(ManageDevice.rightFDevice))
        {
            imgTopright.setBackground(null);
            topright_note.setVisibility(View.GONE);
        }
    }
    private synchronized void bleStringToDouble(BluetoothDevice device, byte[] data)
    {
        BleData bleData = new BleData();
        Logger.e(DigitalTrans.byte2hex(data));

        int voltage = (DigitalTrans.byteToAlgorism(data[0])-31)*20/21+160;
        int press = DigitalTrans.byteToAlgorism(data[2])*160/51;
        int temp = DigitalTrans.byteToAlgorism(data[1])-50;
        int state = DigitalTrans.byteToBin(data[3]);

        Logger.e(TAG,"状态："+state+"\n");
        Logger.e(TAG,"压力值："+press+"\n");
        Logger.e(TAG,"温度："+temp+"\n");
        Logger.e(TAG,"电压"+voltage+"");

        bleData.setTemp (temp);
        bleData.setPress(press);
        bleData.setPress(press);

        if(device.getAddress().equals(ManageDevice.leftBDevice))
        {
            bottomleft_preesure.setText(df1.format(press));
            bottomleft_temp.setText(String.valueOf(temp));
            bottomleft_voltage.setText(df.format(voltage));
            //bottomleft_releat.setText(engFh+String.valueOf(eng));
            handleException(bleData, "左后轮\n"+ ManageDevice.leftBDevice+"\n", bottomleft_note, imgBottomleft);

        }else if(device.getAddress().equals(ManageDevice.rightBDevice))
        {
            bottomright_preesure.setText(df1.format(press));
            bottomright_temp.setText(String.valueOf(temp));
            bottomright_voltage.setText(df.format(voltage));
            //bottomright_releat.setText(engFh+String.valueOf(eng));
            handleException(bleData, "右后轮\n"+ ManageDevice.rightBDevice+"\n", bottomright_note, imgBottomright);
        }else if(device.getAddress().equals(ManageDevice.leftFDevice))
        {
            topleft_preesure.setText(df1.format(press));
            topleft_temp.setText(String.valueOf(temp));
            topleft_voltage.setText(df.format(voltage));
            //topleft_releat.setText(engFh+String.valueOf(eng));
            handleException(bleData, "左前轮\n"+ ManageDevice.leftFDevice+"\n", topleft_note, imgTopleft);
        }else if(device.getAddress().equals(ManageDevice.rightFDevice))
        {
            topright_preesure.setText(df1.format(press));
            topright_temp.setText(String.valueOf(temp));
            topright_voltage.setText(df.format(voltage));
            //topright_releat.setText(engFh+String.valueOf(eng));
            handleException(bleData, "右前轮\n"+ ManageDevice.rightFDevice+"\n", topright_note, imgTopright);
        }
    }
    private void handleException(BleData date, String str, TextView v, ImageView img) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(str + ":");
        buffer.append(date.getPress() > 3.2 ? "高压" + "\n" : "");
        buffer.append(date.getPress() < 1.8 ? "低压" + "\n" : "");
        buffer.append(date.getTemp() > 35 ? "高温" + "\n" : "");
        buffer.append(date.getTemp() < 20 ? "低温" + "\n" : "");
        buffer.append(date.getSensorFailure() == 1 ? "传感器失效" + "\n" : "");
        buffer.append(date.getSensorLow() == 1 ? "传感器低电" + "\n" : "");
        buffer.append(date.getLeakage() == 1 ? "慢漏气" + "\n" : "");
        buffer.append(date.getLeakageQucik() == 1 ? "快漏气" + "\n" : "");

        if (!TextUtils.isEmpty(buffer) && buffer != null && !buffer.toString().equals(str + ":")) {
//            img.setBackgroundColor(getResources().getColor(R.color.white));
//            img.setAnimation(alphaAnimation1);
//            alphaAnimation1.start();
            v.setVisibility(View.VISIBLE);
            v.setText(buffer.toString());
//            ToastUtil.show(buffer.toString() + "异常警报");
//            showDialog(buffer.toString());
        } else {
//            if (alphaAnimation1.isFillEnabled())
//                alphaAnimation1.cancel();
//            img.setBackground(null);
            v.setVisibility(View.GONE);
        }

    }
 */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!isDisconnect)
        {
            myTimerTask.cancel();
            timer = null;
            if (mBluetoothLeService != null) {
                mBluetoothLeService.disconnect();
                //mBluetoothLeService.close();
                //mBluetoothLeService = null;
            }
        }
//        mBluetoothAdapter.disable();
        mBluetoothLeService = null;
        leftFDevice = null;
        rightBDevice = null;
        leftBDevice = null;
        rightFDevice = null;
        mDeviceList.clear();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        unbindService(mServiceConnection);
        unregisterReceiver(mGattUpdateReceiver);
        Logger.e("MainActivity closed!!!");
       // System.exit(0);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.topleft_adjust:
                if (topleftAdjust.getText().equals("校验")) {
                    topleftAdjust.setVisibility(View.GONE);
                    toprightAdjust.setText("对调");
                    bottomleftAdjust.setText("对调");
                    bottomrightAdjust.setText("对调");
                    preStr = "左前轮";
                } else {
                    reset();
//                    topleftAdjust.setVisibility(View.GONE);
//                    toprightAdjust.setText("对调");
//                    bottomleftAdjust.setText("对调");
//                    bottomrightAdjust.setText("对调");
                    curStr = "左前轮";
                    ToastUtil.show(preStr + "与" + curStr + "对调");
                }

                break;
            case R.id.topright_adjust:

                if (toprightAdjust.getText().equals("校验")) {
                    toprightAdjust.setVisibility(View.GONE);
                    topleftAdjust.setText("对调");
                    bottomleftAdjust.setText("对调");
                    bottomrightAdjust.setText("对调");
                    preStr = "右前轮";
                } else {
                    reset();
//                    topleftAdjust.setVisibility(View.GONE);
//                    toprightAdjust.setText("对调");
//                    bottomleftAdjust.setText("对调");
//                    bottomrightAdjust.setText("对调");
                    curStr = "右前轮";
                    ToastUtil.show(preStr + "与" + curStr + "对调");
                }
                break;
            case R.id.bottomleft_adjust:

                if (bottomleftAdjust.getText().equals("校验")) {
                    bottomleftAdjust.setVisibility(View.GONE);
                    topleftAdjust.setText("对调");
                    toprightAdjust.setText("对调");
                    bottomrightAdjust.setText("对调");
                    preStr = "左后轮";
                } else {
                    reset();
//                    topleftAdjust.setVisibility(View.GONE);
//                    toprightAdjust.setText("对调");
//                    bottomleftAdjust.setText("对调");
//                    bottomrightAdjust.setText("对调");
                    curStr = "左后轮";
                    ToastUtil.show(preStr + "与" + curStr + "对调");
                }
                break;
            case R.id.bottomright_adjust:

                if (bottomrightAdjust.getText().equals("校验")) {
                    bottomrightAdjust.setVisibility(View.GONE);
                    topleftAdjust.setText("对调");
                    toprightAdjust.setText("对调");
                    bottomleftAdjust.setText("对调");
                    preStr = "右后轮";
                } else {
//                    topleftAdjust.setVisibility(View.GONE);
//                    toprightAdjust.setText("对调");
//                    bottomleftAdjust.setText("对调");
//                    bottomrightAdjust.setText("对调");
                    reset();
                    curStr = "右后轮";
                    ToastUtil.show(preStr + "与" + curStr + "对调");
                }
                break;
            case R.id.edit_config:
                if (editConfig.getText().equals("校准")) {
                    showEditUI();
                    editConfig.setText("保存");
                } else if (editConfig.getText().equals("保存")) {
                    editConfig.setText("校准");
                    dissmEditUI();
                }
                break;
        }
    }

    private void showEditUI() {
        reset();
    }

    private void dissmEditUI() {
        topleftAdjust.setVisibility(View.GONE);
        toprightAdjust.setVisibility(View.GONE);
        bottomleftAdjust.setVisibility(View.GONE);
        bottomrightAdjust.setVisibility(View.GONE);
    }

    private void reset() {
        bottomleftAdjust.setText("校验");
        topleftAdjust.setText("校验");
        toprightAdjust.setText("校验");
        bottomrightAdjust.setText("校验");
        topleftAdjust.setVisibility(View.VISIBLE);
        toprightAdjust.setVisibility(View.VISIBLE);
        bottomleftAdjust.setVisibility(View.VISIBLE);
        bottomrightAdjust.setVisibility(View.VISIBLE);
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
                    timer = new Timer();
                    myTimerTask = new MyTimerTask();
                    timer.schedule(myTimerTask,3000,1000);
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_set:
                ToastUtil.show("设置");
                break;
            //case R.id.home:
              //  finish();
              //  break;
        }
        return true;
    }

    private void clearDevice() {
        mBluetoothLeService.disconnect();
//        mDeviceContainer.clear();
//        mDeviceList.clear();
//        mRemovedDeviceList.clear();
//        mDataField.setText("");
//        numDevice.setText(deviceText + "0");
    }

    private void showDialog()
    {
        //显示ProgressDialog
        progressDialog = ProgressDialog.show(MainActivity_3_10s.this, "Disconnect blue...", "Please wait...", true, false);
    }
    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private static boolean isDisconnect = false;

    /**
     * 检测四个模块是否断开，彻底断开后退出
     */
    private void exit() {
        myTimerTask.cancel();
        timer = null;
        for(MyBluetoothDevice ble:mDeviceList)
        {
            if(ble.isSuccessComm())
            {
                if (mBluetoothLeService != null) {
                    mBluetoothLeService.disconnect();
                    //mBluetoothLeService.close();
                    //mBluetoothLeService = null;
                }
                showDialog();
                // 利用handler延迟发送更改状态信息
                isDisconnect = true;
                break;
            }
        }
        finish();
        /*if(!rightFDevice.isSuccessComm()&&!rightBDevice.isSuccessComm()&&!leftBDevice.isSuccessComm()&&!leftFDevice.isSuccessComm())
        {
            //progressDialog.dismiss();
            finish();
        }*/
    }
}
