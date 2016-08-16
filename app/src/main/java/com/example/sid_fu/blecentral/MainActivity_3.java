package com.example.sid_fu.blecentral;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.UiThread;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid_fu.blecentral.ui.BleData;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.DataUtils;
import com.example.sid_fu.blecentral.utils.DigitalTrans;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.utils.ToastUtil;
import com.example.sid_fu.blecentral.widget.LoadingDialog;
import com.example.sid_fu.blecentral.widget.WiperSwitch;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainActivity_3 extends ActionBarActivity implements View.OnClickListener ,SensorEventListener {
    private final static String TAG = MainActivity_3.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000000;
    private static final int CONNECTTIME = 8;//单位s
    private static final int RECONNECTTIME = 8;//单位s
    private static final int CONNECT = 1001;
    private int connectCount = 8 ;//单位s
    private int recConnectCount ;//单位s
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
    public static BluetoothAdapter mBluetoothAdapter = null;
    private boolean mScanning;
    private TextView topleft_voltage;
    private TextView topright_voltage;
    private TextView bottomleft_voltage;
    private TextView bottomright_voltage;
    private TextView topleft_releat;
    private TextView topright_releat;
    private TextView bottomleft_releat;
    private TextView bottomright_releat;
    public static MainActivity_3 mContext;
    private DecimalFormat df;
    private Timer timer;
    private MyTimerTask myTimerTask;
    private int timeCount = 0;
    private int connectedNum;
    private Handler mHandler;
    private String engFh = "";
    private DecimalFormat df1;

    public static MyBluetoothDevice leftBDevice =null;
    public static MyBluetoothDevice leftFDevice =null ;
    public static MyBluetoothDevice rightFDevice =null;
    public static MyBluetoothDevice rightBDevice =null;

    public static List<MyBluetoothDevice> mDeviceList = new ArrayList<>();
    private List<MyBluetoothDevice> mDiconnectDeviceList = new ArrayList<>();
    private int index = 0;
    private int indexRe = 0;
    private boolean isFirst = false;
    private ProgressDialog progressDialog;
    private int currentsecond;
    private SimpleDateFormat d;
    private String nowtime;
    private int count;
    public static ShakeListener mShakeListener;
    private int timeOfyundongCount = 10;
    private LoadingDialog loadDialog;
    private int indexConnect;
    public static ManageDevice manageDevice;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_pressure);
        mContext =MainActivity_3.this;

        /*显示App icon左侧的back键*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initUI();
        iniBle();
        //initSensor();
        intiShake();
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

                }else if(msg.what==1)
                {
                    if(mShakeListener!=null&&mBluetoothLeService!=null)
                    {
                        //mShakeListener.stop();
                        mBluetoothLeService.showDialog();
//                        new AlertDialog.Builder(MainActivity_3.this)
//                                .setTitle("系统提示")
//                                .setMessage("检测到应用长时间未工作，确定退出吗？")
//                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        finish();
//                                    }
//                                })
//                                .setNegativeButton("否", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        mShakeListener.start();
//                                }
//                            })
//                        .show();
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity_3.this);
//                        builder.setTitle("系统提示");
//                        builder .setMessage("检测到应用长时间未工作，确定退出吗？")
//                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        finish();
//                                    }
//                                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    mShakeListener.start();
//                                }
//                         });
//                        AlertDialog ad = builder.create();
////ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG); //系统中关机对话框就是这个属性
//                        ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                        ad.setCanceledOnTouchOutside(false);                                   //点击外面区域不会让dialog消失
//                        ad.show();
                    }
                }
            }
        };

        df1=new DecimalFormat("#.##");
        df=new DecimalFormat("#.#");

        initBlueDevice();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        Logger.d("Try to bindService=" + bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE));
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }
    private void initBlueDevice()
    {
        manageDevice =  new ManageDevice();
        manageDevice.setLeftFDevice(SharedPreferences.getInstance().getString(Constants.LEFT_F_DEVICE,""));
        manageDevice.setRightFDevice(SharedPreferences.getInstance().getString(Constants.RIGHT_F_DEVICE,""));
        manageDevice.setLeftBDevice(SharedPreferences.getInstance().getString(Constants.LEFT_B_DEVICE,""));
        manageDevice.setRightBDevice(SharedPreferences.getInstance().getString(Constants.RIGHT_B_DEVICE,""));
    }
    private  void intiShake()
    {
        mShakeListener = new ShakeListener(this);
        mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                //Toast.makeText(getApplicationContext(), "抱歉，暂时没有找到在同一时刻摇一摇的人。\n再试一次吧！", Toast.LENGTH_SHORT).show();
                //startAnim();  //开始 摇一摇手掌动画
                if(mShakeListener!=null)
                {
                    mShakeListener.stop();
                    //sndPool.play(soundPoolMap.get(0), (float) 1, (float) 1, 0, 0,(float) 1.2);
                    new Handler().postDelayed(new Runnable(){
                        public void run(){
                            //Toast.makeText(getApplicationContext(), "抱歉，暂时没有找到\n在同一时刻摇一摇的人。\n再试一次吧！", 500).setGravity(Gravity.CENTER,0,0).show();
                            //sndPool.play(soundPoolMap.get(1), (float) 1, (float) 1, 0, 0,(float) 1.0);
                            Toast mtoast;
                            mtoast = Toast.makeText(getApplicationContext(),
                                    "哈哈！我在动，别关我！！！！", Toast.LENGTH_SHORT);
                            //mtoast.setGravity(Gravity.CENTER, 0, 0);
                            mtoast.show();
                            timeOfyundongCount = 10;
                            //mVibrator.cancel();
                            if(mShakeListener!=null)
                                mShakeListener.start();

                        }
                    }, 2000);
                }
            }
        });
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(timeOfyundongCount--==0)
                {
                    timeOfyundongCount = 10;
                    mHandler.sendEmptyMessage(1);
                }
            }
        },1000,1000);
        loadDialog = new LoadingDialog(this);
        loadDialog.setText("设备扫描中...");
        loadDialog.show();
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
        public void run() {
            timeCount++;
//            multConnect();
            connect();
//            //30s扫描超时自动连接
//            if(mDeviceList.size()==4||timeCount>=30)
//            {
//                loadDialog.dismiss();
//                //.sendEmptyMessage(CONNECT);
//                if(mScanning&&timeCount<30)
//                {
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    mScanning = false;
//                    Logger.e(TAG,"全部扫描到,且关闭扫描"+mDeviceList.size());
//                }else
//                {
//                    Logger.e(TAG,"扫描超时，自动连接"+mDeviceList.size());
//                }
//                if(index<mDeviceList.size())
//                {
//                    mDeviceList.get(index).connectBle();
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }else
//                {
//                    index = 5;
//                    /*
//                    if(indexRe<mDeviceList.size())
//                    {
//                        if(!mDeviceList.get(indexRe).isSuccessComm())
//                        {
//                            Logger.e("i:"+indexRe);
//                            mDeviceList.get(indexRe).reConnectBle();
//                            try {
//                                Thread.sleep(5000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }else
//                    {
//                        indexRe = -1;
//                    }
//                    indexRe++;
//                    */
//                }
//                index++;
//            }else {
//                Logger.e(TAG,"扫描中。。。。。。"+mDeviceList.size());
//            }
//            if(mDiconnectDeviceList.size()>0)
//            {
//                if(indexConnect<mDiconnectDeviceList.size())
//                {
//                    mDiconnectDeviceList.get(indexConnect).reConnectBle();
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }else
//                {
//                    indexConnect = -1;
//                }
//                indexConnect++;
//            }
//            Logger.e(TAG,"重连列表。。。。。。"+mDiconnectDeviceList.size());
        }

    }

    /**
     * 旧方法
     */
    private void connect()
    {
        //30s扫描超时自动连接
        if(mDeviceList.size()==4||timeCount>=30)
        {
            loadDialog.dismiss();
            //.sendEmptyMessage(CONNECT);
            if(mScanning&&timeCount<30)
            {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
                Logger.e(TAG,"全部扫描到,且关闭扫描"+mDeviceList.size());
            }else
            {
                Logger.e(TAG,"扫描超时，自动连接"+mDeviceList.size());
            }
            if(index<mDeviceList.size())
            {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                        mDeviceList.get(index).connectBle(mDeviceList.get(index));
//                    }
//                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                index = 5;

                if(indexRe<mDeviceList.size())
                {
                    if(!mDeviceList.get(indexRe).isSuccessComm()&&!mDeviceList.get(indexRe).isRequestConnect())
                    {
                        Logger.e("i:"+indexRe);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
                                mDeviceList.get(indexRe).reConnectBle(mDeviceList.get(indexRe));
//                            }
//                        });
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
    private synchronized void multConnect()
    {
        if(mDeviceList.size()==4||timeCount>30)
        {
            loadDialog.dismiss();
            if(index<mDeviceList.size())
            {
                if(!mDeviceList.get(index).isRequestConnect()&&!mDeviceList.get(index).isSuccessComm())
                {
                    mDeviceList.get(index).reConnectBle(mDeviceList.get(index));
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(!mDeviceList.get(index).isSuccessComm())
//                            {
//                                index++;
//                            }
//                        }
//                    },10000);
                }
                if(mDeviceList.get(index).isSuccessComm()||!mDeviceList.get(index).isRequestConnect()/*||mDeviceList.get(index).isTimeout()*/)
                    index++;
            }else
            {
                index = 0;
            }
            /*else
            {
                index = 5;
                if(indexRe<mDeviceList.size())
                {
                    if(!mDeviceList.get(indexRe).isRequestConnect()&&!mDeviceList.get(indexRe).isSuccessComm())
                    {
                        mDeviceList.get(indexRe).reConnectBle();
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if(!mDeviceList.get(indexRe).isSuccessComm())
//                                {
//                                    indexRe++;
//                                }
//                            }
//                        },10000);
                    }
                    if(mDeviceList.get(indexRe).isSuccessComm()||!mDeviceList.get(index).isRequestConnect())
                        indexRe++;
                }else
                {
                    indexRe = -1;
                }
            }*/
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
                    for (String ble:manageDevice.blueDeviceTable)
                    {
                        if(ble.contains(device.getAddress()))
                        {
                            ParsedAd ad = DataUtils.parseData(scanRecord);
                            bleIsFind(device);
                            bleStringToDouble(device,true,ad.datas);
                        }
                    }
                    broadcastUpdate(BundBlueActivity.ACTION_CHANGE_RESULT,device,rssi,scanRecord);
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
        public void onReceive(Context context, final Intent intent) {
             String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                Logger.e("connected:"+device.getAddress());
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //断开
                onFailed(device);
                bleIsDisConnected(device.getAddress());
                //invalidateOptionsMenu();
                Logger.e("Disconneted GATT Services"+device.getAddress());
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //invalidateOptionsMenu();
                bleIsConnected(device.getAddress());
                onSuccess(device);
                Logger.e("Discover GATT Services"+device.getAddress());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                        BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                        //通讯成功
//                        onSuccess(device);
                        if (data != null) {
                            //Logger.e(data);
                            //bleIsConnected(device.getAddress());
                            bleStringToDouble(device,false,data);
                        }
                    }
                });
            }
        }
    };
    private void scanBleForResult(BluetoothDevice device)
    {
        if(device.getAddress().equals(manageDevice.getLeftFDevice()))
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
        }else if(device.getAddress().equals(manageDevice.getRightFDevice()))
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
        }else if(device.getAddress().equals(manageDevice.getLeftBDevice()))
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
        }else if(device.getAddress().equals(manageDevice.getRightBDevice()))
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

    private void onSuccess(BluetoothDevice device)
    {
        if(leftFDevice!=null&&leftFDevice.getDevice().equals(device))
        {
            ManageDevice.setSuccess(device,leftFDevice);

            if(ManageDevice.isEquals(mDiconnectDeviceList,leftFDevice.getDevice()))
                mDiconnectDeviceList.remove(leftFDevice);
            if(!SharedPreferences.getInstance().getBoolean(Constants.FIRST_USED_LF,false))
            {
                leftFDevice.writeChar6("AT+USED=1");
                SharedPreferences.getInstance().putBoolean(Constants.FIRST_USED_LF,true);
            }
            //if(!isFirst)
            //{
                //isFirst = true;
                //leftFDevice.writeChar6("AT+SIMU=1");
            /*
            if(leftFDevice.sendGetTime-->0)
            {
                leftFDevice.sendGetTime = -1;
                leftFDevice.writeChar6("AT+GET");
            }

            if(!leftFDevice.isSuccessComm())
                leftFDevice.writeChar6("AT+GET");

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
            if(ManageDevice.isEquals(mDiconnectDeviceList,rightFDevice.getDevice()))
                mDiconnectDeviceList.remove(rightFDevice);
            if(!SharedPreferences.getInstance().getBoolean(Constants.FIRST_USED_RF,false))
            {
                rightFDevice.writeChar6("AT+USED=1");
                SharedPreferences.getInstance().putBoolean(Constants.FIRST_USED_RF,true);
            }
            /*
            if(rightFDevice.sendGetTime--==0) {
                rightFDevice.sendGetTime = 3;
                rightFDevice.writeChar6("AT+GET");
            }
            Logger.e("发送次数："+rightFDevice.sendGetTime);

            if(!rightFDevice.isSuccessComm())
                rightFDevice.writeChar6("AT+GET");

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
            if(ManageDevice.isEquals(mDiconnectDeviceList,leftBDevice.getDevice()))
                mDiconnectDeviceList.remove(leftBDevice);
            if(!SharedPreferences.getInstance().getBoolean(Constants.FIRST_USED_LB,false))
            {
                leftBDevice.writeChar6("AT+USED=1");
                SharedPreferences.getInstance().putBoolean(Constants.FIRST_USED_LB,true);
            }
             /*
            if(leftBDevice.sendGetTime-->0) {
                leftBDevice.sendGetTime = -1;
                leftBDevice.writeChar6("AT+GET");
            }

            if(!leftBDevice.isSuccessComm())
                leftBDevice.writeChar6("AT+GET");

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
            if(ManageDevice.isEquals(mDiconnectDeviceList,rightBDevice.getDevice()))
                mDiconnectDeviceList.remove(rightBDevice);
            if(!SharedPreferences.getInstance().getBoolean(Constants.FIRST_USED_RB,false))
            {
                rightBDevice.writeChar6("AT+USED=1");
                SharedPreferences.getInstance().putBoolean(Constants.FIRST_USED_RB,true);
            }
             /*
            if(rightBDevice.sendGetTime-->0) {
                rightBDevice.sendGetTime = -1;
                rightBDevice.writeChar6("AT+GET");
            }
            /*
            if(!rightBDevice.isSuccessComm())
                rightBDevice.writeChar6("AT+GET");

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
        //记录因其他原因造成蓝牙断开，实现重连机制

        if(leftFDevice!=null&&leftFDevice.getDevice().equals(device))
        {
            ManageDevice.setFailed(device,leftFDevice);
//            if(leftFDevice.getBluetoothGatt()!=null)
//                leftFDevice.getBluetoothGatt().close();
            if(!ManageDevice.isEquals(mDiconnectDeviceList,leftFDevice.getDevice()))
                mDiconnectDeviceList.add(leftFDevice);
        }else
        if(rightFDevice!=null&&rightFDevice.getDevice().equals(device))
        {
            ManageDevice.setFailed(device,rightFDevice);
            //rightFDevice.writeChar6("AT+TEST = 1");
//            if(rightFDevice.getBluetoothGatt()!=null)
//             rightFDevice.getBluetoothGatt().close();
            if(!ManageDevice.isEquals(mDiconnectDeviceList,rightFDevice.getDevice()))
                mDiconnectDeviceList.add(rightFDevice);
        }else
        if(leftBDevice!=null&&leftBDevice.getDevice().equals(device))
        {
            ManageDevice.setFailed(device,leftBDevice);
//            if(leftBDevice.getBluetoothGatt()!=null)
//                leftBDevice.getBluetoothGatt().close();
            if(!ManageDevice.isEquals(mDiconnectDeviceList,leftBDevice.getDevice()))
                mDiconnectDeviceList.add(leftBDevice);
            //leftBDevice.writeChar6("AT+TEST = 1");
        }else
        if(rightBDevice!=null&&rightBDevice.getDevice().equals(device))
        {
            ManageDevice.setFailed(device,rightBDevice);
//            if(rightBDevice.getBluetoothGatt()!=null)
//                rightBDevice.getBluetoothGatt().close();
            if(!ManageDevice.isEquals(mDiconnectDeviceList,rightBDevice.getDevice()))
                mDiconnectDeviceList.add(rightBDevice);
            //rightBDevice.writeChar6("AT+TEST = 1");
        }
        if(isDisconnect)
        {
            Logger.e("主动退出应用");
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
        timer.schedule(myTimerTask,1000,1000);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void bleIsFind(BluetoothDevice device)
    {
        String strAddress = device.getAddress();
        if(strAddress.equals(manageDevice.getLeftBDevice()))
        {
            imgBottomleft.setBackgroundColor(getResources().getColor(R.color.white));
            if(leftBDevice==null)
                leftBDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
//            MySharePreferences.saveObject(mContext,ManageDevice.leftBDevice,leftBDevice);
//            leftBDevice.connectBle();
            if(!ManageDevice.isEquals(mDeviceList,device))
                mDeviceList.add(leftBDevice);
            leftBDevice.setBleScaned(true);
            //bleIsFind(device.getAddress());
        }else if(strAddress.equals(manageDevice.getRightBDevice()))
        {
            imgBottomright.setBackgroundColor(getResources().getColor(R.color.white));
            if(rightBDevice==null)
                rightBDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
//            MySharePreferences.saveObject(mContext,ManageDevice.rightBDevice,rightBDevice);
//            rightBDevice.connectBle();
            if(!ManageDevice.isEquals(mDeviceList,device))
                mDeviceList.add(rightBDevice);
            rightBDevice.setBleScaned(true);
            //bleIsFind(device.getAddress());
        }else if(strAddress.equals(manageDevice.getLeftFDevice()))
        {
            imgTopleft.setBackgroundColor(getResources().getColor(R.color.white));
            if(leftFDevice==null)
                leftFDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
//            MySharePreferences.saveObject(mContext,ManageDevice.leftFDevice,leftFDevice);
//            leftFDevice.connectBle();
            if(!ManageDevice.isEquals(mDeviceList,device))
                mDeviceList.add(leftFDevice);
            //leftFDevice.connectBle();
            leftFDevice.setBleScaned(true);
            //bleIsFind(device.getAddress());
        }else if(strAddress.equals(manageDevice.getRightFDevice()))
        {
            imgTopright.setBackgroundColor(getResources().getColor(R.color.white));
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
    }
    private void bleIsFind(BluetoothDevice device,byte[] data)
    {
        String strAddress = device.getAddress();
        try {
//            int state = DigitalTrans.byteToBin(data[0]);
//            int press = DigitalTrans.byteToAlgorism(data[1])*160/51;
//            int temp = DigitalTrans.byteToAlgorism(data[2])-50;

//            float voltage = ((float)(DigitalTrans.byteToAlgorism(data[0])-31)*20/21+160)/100;
            float press = ((float)DigitalTrans.byteToAlgorism(data[1])*160)/51/100;
            int temp = DigitalTrans.byteToAlgorism(data[2])-50;
            int state = DigitalTrans.byteToBin(data[0]);
            Logger.e("使用情况："+state+"\n");
            Logger.e(press+"\n");
            Logger.e(temp+"");
            if(strAddress.equals(manageDevice.getLeftBDevice()))
            {
                imgBottomleft.setBackgroundColor(getResources().getColor(R.color.white));
                bottomleft_preesure.setText(df.format(press));
                bottomleft_temp.setText(String.valueOf(temp));
                if(leftBDevice==null)
                    leftBDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
//            MySharePreferences.saveObject(mContext,ManageDevice.leftBDevice,leftBDevice);
//            leftBDevice.connectBle();
                if(!ManageDevice.isEquals(mDeviceList,device))
                    mDeviceList.add(leftBDevice);
                leftBDevice.setBleScaned(true);
                //bleIsFind(device.getAddress());
            }else if(strAddress.equals(manageDevice.getRightBDevice()))
            {
                imgBottomright.setBackgroundColor(getResources().getColor(R.color.white));
                bottomright_preesure.setText(df.format(press));
                bottomright_temp.setText(String.valueOf(temp));
                if(rightBDevice==null)
                    rightBDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
//            MySharePreferences.saveObject(mContext,ManageDevice.rightBDevice,rightBDevice);
//            rightBDevice.connectBle();
                if(!ManageDevice.isEquals(mDeviceList,device))
                    mDeviceList.add(rightBDevice);
                rightBDevice.setBleScaned(true);
                //bleIsFind(device.getAddress());
            }else if(strAddress.equals(manageDevice.getLeftFDevice()))
            {
                imgTopleft.setBackgroundColor(getResources().getColor(R.color.white));
                topleft_preesure.setText(df.format(press));
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
            }else if(strAddress.equals(manageDevice.getRightFDevice()))
            {
                imgTopright.setBackgroundColor(getResources().getColor(R.color.white));
                topright_preesure.setText(df.format(press));
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
    /*
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
    }*/
    private void bleIsConnected(String strAddress)
    {
//        Logger.e("ble is connect"+strAddress);
        if(strAddress.equals(manageDevice.getLeftBDevice()))
        {
            imgBottomleft.setBackgroundColor(getResources().getColor(R.color.white));
            leftBDevice.setRequestConnect(true);
//            leftBDevice.setGattCharacteristic_char6(gattCharacteristic);

        }else if(strAddress.equals(manageDevice.getRightBDevice()))
        {
            imgBottomright.setBackgroundColor(getResources().getColor(R.color.white));
            rightBDevice.setRequestConnect(true);
        }else if(strAddress.equals(manageDevice.getLeftFDevice()))
        {
            imgTopleft.setBackgroundColor(getResources().getColor(R.color.white));
            leftFDevice.setRequestConnect(true);
        }else if(strAddress.equals(manageDevice.getRightFDevice()))
        {
            imgTopright.setBackgroundColor(getResources().getColor(R.color.white));
            rightFDevice.setRequestConnect(true);
        }
    }
    private void bleIsDisConnected(String strAddress)
    {
//        Logger.e("ble is disconnect"+strAddress);
        if(strAddress.equals(manageDevice.getLeftBDevice()))
        {
            if(leftBDevice!=null&&!leftBDevice.isTimeOutDisconnect())
            {
                imgBottomleft.setBackground(null);
                bottomleft_note.setVisibility(View.GONE);
            }
        }else if(strAddress.equals(manageDevice.getRightBDevice()))
        {
            if(rightBDevice!=null&&!rightBDevice.isTimeOutDisconnect()) {
                imgBottomright.setBackground(null);
                bottomright_note.setVisibility(View.GONE);
            }
        }else if(strAddress.equals(manageDevice.getLeftFDevice()))
        {
            if(leftFDevice!=null&&!leftFDevice.isTimeOutDisconnect()) {
                imgTopleft.setBackground(null);
                topleft_note.setVisibility(View.GONE);
            }
        }else if(strAddress.equals(manageDevice.getRightFDevice()))
        {
            if(rightFDevice!=null&&!rightFDevice.isTimeOutDisconnect()) {
                imgTopright.setBackground(null);
                topright_note.setVisibility(View.GONE);
            }
        }
    }
    private void broadcastUpdate(final String action, BluetoothDevice gatt) {
        final Intent intent = new Intent(action);
        intent.putExtra("DEVICE_ADDRESS", gatt);
        sendBroadcast(intent);
    }
    private void broadcastUpdate(final String action, BluetoothDevice gatt,int rssi,byte[] scanResult) {
        final Intent intent = new Intent(action);
        intent.putExtra("DEVICE_ADDRESS", gatt);
        intent.putExtra("RSSI", rssi);
        intent.putExtra("SCAN_RECORD", scanResult);
        sendBroadcast(intent);
    }
    private void bleStringToDouble(BluetoothDevice device, boolean isNotify,byte[] data)
    {
        float voltage = 0.00f;
        float press;
        int temp;
        int state;
        BleData bleData = new BleData();
        Logger.e(DigitalTrans.byte2hex(data));
        if(isNotify&&data.length==3)
        {
            press = ((float)DigitalTrans.byteToAlgorism(data[1])*160)/51/100;
            temp = DigitalTrans.byteToAlgorism(data[2])-50;
            state = DigitalTrans.byteToBin(data[0]);
        }else
        {
            if(data.length==4)
            {
                voltage = ((float)(DigitalTrans.byteToAlgorism(data[0])-31)*20/21+160)/100;
                temp = DigitalTrans.byteToAlgorism(data[1])-50;
                press = ((float)DigitalTrans.byteToAlgorism(data[2])*160)/51/100;
                state = DigitalTrans.byteToBin(data[3]);
            }else
            {
                broadcastUpdate(BluetoothLeService.ACTION_RETURN_OK,device);
                return;
            }
        }
        Logger.e(TAG,"状态："+state+"\n");
        Logger.e(TAG,"压力值："+press+"\n");
        Logger.e(TAG,"温度："+temp+"\n");
        Logger.e(TAG,"电压"+voltage+"");

        bleData.setTemp (temp);
        bleData.setPress(press);
        //bleData.setPress(press);

        if(device.getAddress().equals(manageDevice.getLeftBDevice()))
        {
            bottomleft_preesure.setText(df.format(press));
            bottomleft_temp.setText(String.valueOf(temp));
            bottomleft_voltage.setText(df1.format(voltage));
            //bottomleft_releat.setText(engFh+String.valueOf(eng));
            handleException(bleData, "左后轮\n"+ manageDevice.getLeftBDevice()+"\n", bottomleft_note, imgBottomleft);

        }else if(device.getAddress().equals(manageDevice.getRightBDevice()))
        {
            bottomright_preesure.setText(df.format(press));
            bottomright_temp.setText(String.valueOf(temp));
            bottomright_voltage.setText(df1.format(voltage));
            //bottomright_releat.setText(engFh+String.valueOf(eng));
            handleException(bleData, "右后轮\n"+ manageDevice.getRightBDevice()+"\n", bottomright_note, imgBottomright);
        }else if(device.getAddress().equals(manageDevice.getLeftFDevice()))
        {
            topleft_preesure.setText(df.format(press));
            topleft_temp.setText(String.valueOf(temp));
            topleft_voltage.setText(df1.format(voltage));
            //topleft_releat.setText(engFh+String.valueOf(eng));
            handleException(bleData, "左前轮\n"+ manageDevice.getLeftFDevice()+"\n", topleft_note, imgTopleft);
        }else if(device.getAddress().equals(manageDevice.getRightFDevice()))
        {
            topright_preesure.setText(df.format(press));
            topright_temp.setText(String.valueOf(temp));
            topright_voltage.setText(df1.format(voltage));
            //topright_releat.setText(engFh+String.valueOf(eng));
            handleException(bleData, "右前轮\n"+ manageDevice.getRightFDevice()+"\n", topright_note, imgTopright);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!isDisconnect)
        {
            if(myTimerTask!=null)
                myTimerTask.cancel();
                timer = null;
            if (mBluetoothLeService != null) {
                mBluetoothLeService.disconnect();
                //mBluetoothLeService.close();
                //mBluetoothLeService = null;
            }
        }
//        mBluetoothAdapter.disable();
        mBluetoothLeService.close();
        mBluetoothLeService = null;
        leftFDevice = null;
        rightBDevice = null;
        leftBDevice = null;
        rightFDevice = null;
        mDeviceList.clear();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        unbindService(mServiceConnection);
        unregisterReceiver(mGattUpdateReceiver);
        mShakeListener.stop();
        mShakeListener = null;
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
                    timer.schedule(myTimerTask,1000,1000);
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
            case android.R.id.home:
                exit();
                break;
            case R.id.menu_change:
                ToastUtil.show("更换");
                showNext(BundBlueActivity.class);
                break;
            case R.id.menu_close:
                ToastUtil.show("解绑/绑定");
                showNext(UnBundBlueActivity.class);
                break;
            case R.id.menu_luntai:
                ToastUtil.show("轮胎转位");
                showNext(ChangeDevicePosition.class);
                break;
        }
        return true;
    }
    private void showNext(Class cl)
    {
        Intent intent = new Intent();
        intent.setClass(MainActivity_3.this,cl);
//        Bundle bundle = new Bundle();
//        if(leftBDevice!=null)
//            bundle.putSerializable("leftBDevice",leftBDevice);
//        if(rightBDevice!=null)
//            bundle.putSerializable("rightBDevice",rightBDevice);
//        if(rightFDevice!=null)
//            bundle.putSerializable("rightFDevice",rightFDevice);
//        if(leftFDevice!=null)
//            bundle.putSerializable("leftFDevice",leftFDevice);
//        intent.putExtras(bundle);
        startActivity(intent);
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
//        progressDialog = ProgressDialog.show(MainActivity_3.this, "Disconnect blue...", "Please wait...", true, false);
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

    private boolean isDisconnect = false;

    /**
     * 检测四个模块是否断开，彻底断开后退出
     */
    private void exit() {
        myTimerTask.cancel();
        myTimerTask = null;
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

    private SensorManager sensorManager;
    private Sensor magneticSensor;

    private Sensor accelerometerSensor;

    private Sensor gyroscopeSensor;
    // 将纳秒转化为秒
    private static final
    float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;
    private float angle[] =new float[3];

    private void initSensor()
    {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticSensor =sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor =sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor =sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//注册陀螺仪传感器，并设定传感器向应用中输出的时间间隔类型是SensorManager.SENSOR_DELAY_GAME(20000微秒)
//SensorManager.SENSOR_DELAY_FASTEST(0微秒)：最快。最低延迟，一般不是特别敏感的处理不推荐使用，该模式可能在成手机电力大量消耗，由于传递的为原始数据，诉法不处理好会影响游戏逻辑和UI的性能
//SensorManager.SENSOR_DELAY_GAME(20000微秒)：游戏。游戏延迟，一般绝大多数的实时性较高的游戏都是用该级别
//SensorManager.SENSOR_DELAY_NORMAL(200000微秒):普通。标准延时，对于一般的益智类或EASY级别的游戏可以使用，但过低的采样率可能对一些赛车类游戏有跳帧现象
//SensorManager.SENSOR_DELAY_UI(60000微秒):用户界面。一般对于屏幕方向自动旋转使用，相对节省电能和逻辑处理，一般游戏开发中不使用
        sensorManager.registerListener(this,gyroscopeSensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this,magneticSensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this,accelerometerSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }
//坐标轴都是手机从左侧到右侧的水平方向为x轴正向，从手机下部到上部为y轴正向，垂直于手机屏幕向上为z轴正向
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            // x,y,z分别存储坐标轴x,y,z上的加速度
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            // 根据三个方向上的加速度值得到总的加速度值a
            float a = (float) Math.sqrt(x * x + y * y + z * z);
            System.out.println("a---------->" + a);
            // 传感器从外界采集数据的时间间隔为10000微秒
            System.out.println("magneticSensor.getMinDelay()-------->"
                    + magneticSensor.getMinDelay());
            // 加速度传感器的最大量程
            System.out.println("event.sensor.getMaximumRange()-------->"
                    + event.sensor.getMaximumRange());
            System.out.println("x------------->" + x);
            System.out.println("y------------->" + y);
            System.out.println("z------------->" + z);
            Log.d("jarlen","x------------->" + x);
            Log.d("jarlen","y------------>" + y);
            Log.d("jarlen","z----------->" + z);
        // showTextView.setText("x---------->" + x + "\ny-------------->" +
        // y + "\nz----------->" + z);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
        // 三个坐标轴方向上的电磁强度，单位是微特拉斯(micro-Tesla)，用uT表示，也可以是高斯(Gauss),1Tesla=10000Gauss
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
        // 手机的磁场感应器从外部采集数据的时间间隔是10000微秒
            System.out.println("magneticSensor.getMinDelay()-------->"
                    + magneticSensor.getMinDelay());
        // 磁场感应器的最大量程
            System.out.println("event.sensor.getMaximumRange()----------->"
                    + event.sensor.getMaximumRange());
            System.out.println("x------------->" + x);
            System.out.println("y------------->" + y);
            System.out.println("z------------->" + z);
            // Log.d("TAG","x------------->" + x);
            // Log.d("TAG", "y------------>" + y);
            // Log.d("TAG", "z----------->" + z);
            // showTextView.setText("x---------->" + x + "\ny-------------->" +
            // y + "\nz----------->" + z);
        }
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            //从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值
            if(timestamp != 0){
            // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
                final float dT = (event.timestamp - timestamp) * NS2S;
            // 将手机在各个轴上的旋转角度相加，即可得到当前位置相对于初始位置的旋转弧度
                angle[0] += event.values[0] * dT;
                angle[1] += event.values[1] * dT;
                angle[2] += event.values[2] * dT;
            // 将弧度转化为角度
                float anglex = (float) Math.toDegrees(angle[0]);
                float angley = (float) Math.toDegrees(angle[1]);
                float anglez = (float) Math.toDegrees(angle[2]);
                System.out.println("anglex------------>" + anglex);
                System.out.println("angley------------>" + angley);
                System.out.println("anglez------------>" + anglez);
                System.out.println("gyroscopeSensor.getMinDelay()----------->"
                        + gyroscopeSensor.getMinDelay());
            }
            //将当前时间赋值给timestamp
            timestamp = event.timestamp;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //TODO Auto-generated method stub
    }
    @Override
    protected void onPause() {
        //TODO Auto-generated method stub
        super.onPause();
        //sensorManager.unregisterListener(this);
    }

    public  void startScan()
    {
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    public  void stopScan()
    {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

}
