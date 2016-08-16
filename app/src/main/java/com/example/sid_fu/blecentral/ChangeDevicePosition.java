package com.example.sid_fu.blecentral;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sid_fu.blecentral.utils.ToastUtil;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ChangeDevicePosition extends ActionBarActivity implements View.OnClickListener {
    private final static String TAG = MainActivity_3.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 100000;
    private TextView editConfig;
    private ImageView imgTopleft;
    private TextView topleftAdjust;
    private ImageView imgTopright;
    private TextView toprightAdjust;
    private ImageView imgBottomleft;
    private TextView bottomleftAdjust;
    private ImageView imgBottomright;
    private TextView bottomrightAdjust;
//    private TextView topleft_preesure;
//    private TextView topleft_temp;
//    private TextView topright_preesure;
//    private TextView topright_temp;
//    private TextView bottomleft_preesure;
//    private TextView bottomleft_temp;
//    private TextView bottomright_preesure;
//    private TextView bottomright_temp;
//    private TextView topleft_note;
//    private TextView topright_note;
//    private TextView bottomleft_note;
//    private TextView bottomright_note;
    private String preStr;
    private String curStr;
//    private TextView topleft_voltage;
//    private TextView topright_voltage;
//    private TextView bottomleft_voltage;
//    private TextView bottomright_voltage;
//    private TextView topleft_releat;
//    private TextView topright_releat;
//    private TextView bottomleft_releat;
//    private TextView bottomright_releat;
    private ChangeDevicePosition mContext;
    private DecimalFormat df;
    private Timer timer;
    private int timeCount = 0;
    private int connectedNum;
    private Handler mHandler;
    private String engFh = "";
    private DecimalFormat df1;
//    private ManageDevice manageDevice;
    private String curStrAddr;
    private String preStrAddr;
    private MyBluetoothDevice leftBDevice = MainActivity_3.leftBDevice;;
    private MyBluetoothDevice rightBDevice = MainActivity_3.rightBDevice;;
    private MyBluetoothDevice rightFDevice = MainActivity_3.rightFDevice;;
    private MyBluetoothDevice leftFDevice = MainActivity_3.leftFDevice;;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_change_position);
        mContext =ChangeDevicePosition.this;
        initUI();
        /*显示App icon左侧的back键*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mHandler = new Handler();

        df=new DecimalFormat(".##");
        df1=new DecimalFormat(".#");

//       manageDevice =  new ManageDevice();
    }

    private void initUI() {
        editConfig = (TextView) findViewById(R.id.edit_config);
        imgTopleft = (ImageView) findViewById(R.id.img_topleft);
        imgTopright = (ImageView) findViewById(R.id.img_topright);
        imgBottomleft = (ImageView) findViewById(R.id.img_bottomleft);
        imgBottomright = (ImageView) findViewById(R.id.img_bottomright);
        topleftAdjust = (TextView) findViewById(R.id.topleft_adjust);
        toprightAdjust = (TextView) findViewById(R.id.topright_adjust);
        bottomleftAdjust = (TextView) findViewById(R.id.bottomleft_adjust);
        bottomrightAdjust = (TextView) findViewById(R.id.bottomright_adjust);

//        topleft_preesure = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_preesure);
//        topleft_temp = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_temp);
//        topleft_note = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_note);
//        topleft_voltage = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_voltage);
//        topleft_releat = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_releat);
//
//        topright_preesure = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_preesure);
//        topright_temp = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_temp);
//        topright_note = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_note);
//        topright_voltage = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_voltage);
//        topright_releat = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_releat);
//
//        bottomleft_preesure = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_preesure);
//        bottomleft_temp = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_temp);
//        bottomleft_note = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_note);
//        bottomleft_voltage = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_voltage);
//        bottomleft_releat = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_releat);
//
//        bottomright_preesure = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_preesure);
//        bottomright_temp = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_temp);
//        bottomright_note = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_note);
//        bottomright_voltage = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_voltage);
//        bottomright_releat = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_releat);

        bottomleftAdjust.setOnClickListener(this);
        bottomrightAdjust.setOnClickListener(this);
        topleftAdjust.setOnClickListener(this);
        toprightAdjust.setOnClickListener(this);
        editConfig.setOnClickListener(this);

//        initData(leftBDevice,topleftAdjust);
//        initData(rightBDevice,toprightAdjust);
//        initData(leftFDevice,bottomleftAdjust);
//        initData(rightFDevice,bottomrightAdjust);

    }
    private void initData(MyBluetoothDevice device,TextView btn)
    {
        if(device==null)
        {
            btn.setText(getResources().getString(R.string.erronofind));
            btn.setEnabled(false);
        }else if(!device.isSuccessComm())
        {
            btn.setText(getResources().getString(R.string.erroinfo));
            btn.setEnabled(false);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.topleft_adjust:
                if (topleftAdjust.getText().equals(getResources().getString(R.string.adjust))) {
                    topleftAdjust.setVisibility(View.GONE);
                    toprightAdjust.setText(getResources().getString(R.string.adjustTo));
                    bottomleftAdjust.setText(getResources().getString(R.string.adjustTo));
                    bottomrightAdjust.setText(getResources().getString(R.string.adjustTo));
                    preStr = "左前轮"+MainActivity_3.manageDevice.getLeftFDevice();
                    preStrAddr = MainActivity_3.manageDevice.getLeftFDevice();
                } else {
                    reset();
                    curStr = "左前轮"+MainActivity_3.manageDevice.getLeftFDevice();
                    curStrAddr = MainActivity_3.manageDevice.getLeftFDevice();
//                    ToastUtil.show(preStr + "与" + curStr + "对调");
                    showDialog(preStr,curStr,preStrAddr,curStrAddr);
                }

                break;
            case R.id.topright_adjust:

                if (toprightAdjust.getText().equals(getResources().getString(R.string.adjust))) {
                    toprightAdjust.setVisibility(View.GONE);
                    topleftAdjust.setText(getResources().getString(R.string.adjustTo));
                    bottomleftAdjust.setText(getResources().getString(R.string.adjustTo));
                    bottomrightAdjust.setText(getResources().getString(R.string.adjustTo));
                    preStr = "右前轮"+MainActivity_3.manageDevice.getRightFDevice();
                    preStrAddr = MainActivity_3.manageDevice.getRightFDevice();
                } else {
                    reset();
                    curStr = "右前轮"+MainActivity_3.manageDevice.getRightFDevice();
                    curStrAddr = MainActivity_3.manageDevice.getRightFDevice();
//                    ToastUtil.show(preStr + "与" + curStr + "对调");
                    showDialog(preStr,curStr,preStrAddr,curStrAddr);
                }
                break;
            case R.id.bottomleft_adjust:

                if (bottomleftAdjust.getText().equals(getResources().getString(R.string.adjust))) {
                    bottomleftAdjust.setVisibility(View.GONE);
                    topleftAdjust.setText(getResources().getString(R.string.adjustTo));
                    toprightAdjust.setText(getResources().getString(R.string.adjustTo));
                    bottomrightAdjust.setText(getResources().getString(R.string.adjustTo));
                    preStr = "左后轮"+MainActivity_3.manageDevice.getLeftBDevice();
                    preStrAddr = MainActivity_3.manageDevice.getLeftBDevice();
                } else {
                    reset();
                    curStr = "左后轮"+MainActivity_3.manageDevice.getLeftBDevice();
                    curStrAddr = MainActivity_3.manageDevice.getLeftBDevice();
//                    ToastUtil.show(preStr + "与" + curStr + "对调");
                    showDialog(preStr,curStr,preStrAddr,curStrAddr);
                }
                break;
            case R.id.bottomright_adjust:

                if (bottomrightAdjust.getText().equals(getResources().getString(R.string.adjust))) {
                    bottomrightAdjust.setVisibility(View.GONE);
                    topleftAdjust.setText(getResources().getString(R.string.adjustTo));
                    toprightAdjust.setText(getResources().getString(R.string.adjustTo));
                    bottomleftAdjust.setText(getResources().getString(R.string.adjustTo));
                    preStr = "右后轮"+MainActivity_3.manageDevice.getRightBDevice();
                    preStrAddr = MainActivity_3.manageDevice.getRightBDevice();
                } else {
                    reset();
                    curStr = "右后轮"+MainActivity_3.manageDevice.getRightBDevice();
                    curStrAddr = MainActivity_3.manageDevice.getRightBDevice();
//                    ToastUtil.show(preStr + "与" + curStr + "对调");
                    showDialog(preStr,curStr,preStrAddr,curStrAddr);
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

    /**
     *
     * @param preStr 被调换的位置
     * @param curStr
     */
    private void showDialog(final String preStr, final String curStr,final String preStrAddr,final String curStrAddr)
    {

        new AlertDialog.Builder(ChangeDevicePosition.this).setTitle("系统提示")//设置对话框标题
                .setMessage(preStr+"  与  "+curStr+"  进行对调？")//设置显示的内容
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
                @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        // TODO Auto-generated method stub
//                         finish();
                        dialog.dismiss();
                        if(preStr.contains("左前轮"))
                        {
                            MainActivity_3.manageDevice.setLeftFDevice(curStrAddr);
                        }else if(preStr.contains("右前轮"))
                        {
                            MainActivity_3.manageDevice.setRightFDevice(curStrAddr);
                        }else if(preStr.contains("左后轮"))
                        {
                            MainActivity_3.manageDevice.setLeftBDevice(curStrAddr);
                        }else if(preStr.contains("右后轮"))
                        {
                            MainActivity_3.manageDevice.setRightBDevice(curStrAddr);
                        }
                        if(curStr.contains("左前轮"))
                        {
                            MainActivity_3.manageDevice.setLeftFDevice(preStrAddr);

                        }else if(curStr.contains("右前轮"))
                        {
                            MainActivity_3.manageDevice.setRightFDevice(preStrAddr);

                        }else if(curStr.contains("左后轮"))
                        {
                            MainActivity_3.manageDevice.setLeftBDevice(preStrAddr);

                        }else if(curStr.contains("右后轮"))
                        {
                            MainActivity_3.manageDevice.setRightBDevice(preStrAddr);
                        }
                        ToastUtil.show(preStr + "与" + curStr + "已经对调");
                    }
                }).setNegativeButton("不用了",new DialogInterface.OnClickListener() {//添加返回按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {//响应事件
                    // TODO Auto-generated method stub
//                    Log.i("alertdialog"," 请保存数据！");
//                    finish();
                    dialog.dismiss();
                }
        }).show();//在按键响应事件中显示此对话框
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
        bottomleftAdjust.setText(getResources().getString(R.string.adjust));
        topleftAdjust.setText(getResources().getString(R.string.adjust));
        toprightAdjust.setText(getResources().getString(R.string.adjust));
        bottomrightAdjust.setText(getResources().getString(R.string.adjust));
        topleftAdjust.setVisibility(View.VISIBLE);
        toprightAdjust.setVisibility(View.VISIBLE);
        bottomleftAdjust.setVisibility(View.VISIBLE);
        bottomrightAdjust.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
