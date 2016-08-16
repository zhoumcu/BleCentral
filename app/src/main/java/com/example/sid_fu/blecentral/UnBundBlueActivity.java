package com.example.sid_fu.blecentral;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.utils.ToastUtil;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/5/27.
 */
public class UnBundBlueActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = "ConfigDevice";
    private ProgressBar pb_left_from;
    private ProgressBar pb_right_from;
    private ProgressBar pb_left_back;
    private ProgressBar pb_right_back;
    private final int maxLenght = -50;
    private TextView tv_note_left_from;
    private TextView tv_note_right_from;
    private TextView tv_note_left_back;
    private TextView tv_note_right_back;
    private ScanDeviceRunnable leftFRunable;
    private ScanDeviceRunnable rightFRunable;
    private ScanDeviceRunnable leftBRunable;
    private ScanDeviceRunnable rightBRunable;
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
    private MyBluetoothDevice leftBDevice = MainActivity_3.leftBDevice;
    private MyBluetoothDevice rightBDevice = MainActivity_3.rightBDevice;
    private MyBluetoothDevice rightFDevice = MainActivity_3.rightFDevice;
    private MyBluetoothDevice leftFDevice = MainActivity_3.leftFDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_unbund);
        ButterKnife.bind(this);

//        leftBDevice = MainActivity_3.leftBDevice;//(MyBluetoothDevice) getIntent().getExtras().getSerializable("leftBDevice");
//        rightBDevice = MainActivity_3.rightBDevice;//(MyBluetoothDevice) getIntent().getExtras().getSerializable("rightBDevice");
//        rightFDevice = MainActivity_3.rightFDevice;//(MyBluetoothDevice) getIntent().getExtras().getSerializable("rightFDevice");
//        leftFDevice = MainActivity_3.leftFDevice;//(MyBluetoothDevice) getIntent().getExtras().getSerializable("leftFDevice");

        initUI();
        initIsCofigBle();
     /*显示App icon左侧的back键*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private void initIsCofigBle() {
        if(SharedPreferences.getInstance().getString(Constants.LEFT_F_DEVICE,"").equals(""))
            topleft_next.setText("点击绑定");
        if(SharedPreferences.getInstance().getString(Constants.RIGHT_F_DEVICE,"").equals(""))
            topright_next.setText("点击绑定");
        if(SharedPreferences.getInstance().getString(Constants.LEFT_B_DEVICE,"").equals(""))
            bottomleft_next.setText("点击绑定");
        if(SharedPreferences.getInstance().getString(Constants.RIGHT_B_DEVICE,"").equals(""))
            bottomright_next.setText("点击绑定");
    }

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
            if (BluetoothLeService.ACTION_RETURN_OK.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                Logger.e("set success:"+device.getAddress());
                success(device);
            }
        }
    };
    private void initUI() {
        imgTopleft = (ImageView) findViewById(R.id.img_topleft);
        imgTopright = (ImageView) findViewById(R.id.img_topright);
        imgBottomleft = (ImageView) findViewById(R.id.img_bottomleft);
        imgBottomright = (ImageView) findViewById(R.id.img_bottomright);

        pb_left_from = (ProgressBar) findViewById(R.id.pb_left_from);
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

        topleft_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topleft_next.getText().equals(getResources().getString(R.string.unbund)))
                {
//                    topleft_next.setVisibility(View.GONE);
//                    pb_left_from.setVisibility(View.VISIBLE);
                    leftFDevice.writeChar6("AT+USED=0");
                    ToastUtil.show(leftFDevice.getDevice().getAddress()+"发送命令："+"AT+USED=0");
                }else if(topleft_next.getText().equals(getResources().getString(R.string.unbund_success))){
                    leftFDevice.writeChar6("AT+USED=1");
                    ToastUtil.show(leftFDevice.getDevice().getAddress()+"发送命令："+"AT+USED=1");
                }
            }
        });
        topright_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topright_next.getText().equals(getResources().getString(R.string.unbund))) {
//                    topright_next.setVisibility(View.GONE);
//                    pb_right_from.setVisibility(View.VISIBLE);
                    rightFDevice.writeChar6("AT+USED=0");
                    ToastUtil.show(rightFDevice.getDevice().getAddress()+"发送命令："+"AT+USED=0");
                }else if(topright_next.getText().equals(getResources().getString(R.string.unbund_success)))
                {
                    rightFDevice.writeChar6("AT+USED=1");
                    ToastUtil.show(rightFDevice.getDevice().getAddress()+"发送命令："+"AT+USED=1");
                }
            }
        });
        bottomleft_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomleft_next.getText().equals(getResources().getString(R.string.unbund))) {
//                    bottomleft_next.setVisibility(View.GONE);
//                    pb_left_back.setVisibility(View.VISIBLE);
                    leftBDevice.writeChar6("AT+USED=0");
                    ToastUtil.show(leftBDevice.getDevice().getAddress()+"发送命令："+"AT+USED=0");
                }else if(bottomleft_next.getText().equals(getResources().getString(R.string.unbund_success)))
                {
                    leftBDevice.writeChar6("AT+USED=1");
                    ToastUtil.show(leftBDevice.getDevice().getAddress()+"发送命令："+"AT+USED=1");
                }
            }
        });
        bottomright_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomright_next.getText().equals(getResources().getString(R.string.unbund))) {
//                    bottomright_next.setVisibility(View.GONE);
//                    pb_right_back.setVisibility(View.VISIBLE);
                    rightBDevice.writeChar6("AT+USED=0");
                    ToastUtil.show(rightBDevice.getDevice().getAddress()+"发送命令："+"AT+USED=0");
                }else if(bottomright_next.getText().equals(getResources().getString(R.string.unbund_success)))
                {
                    rightBDevice.writeChar6("AT+USED=0");
                    ToastUtil.show(rightBDevice.getDevice().getAddress()+"发送命令："+"AT+USED=0");
                }
            }
        });
//        initImg();
        initData(leftBDevice,bottomleft_next);
        initData(rightBDevice,bottomright_next);
        initData(leftFDevice,topleft_next);
        initData(rightFDevice,topright_next);
    }

    private void initData(MyBluetoothDevice device,Button btn)
    {
        if(device==null)
        {
            btn.setText(getResources().getString(R.string.erronofind));
        }else if(!device.isSuccessComm())
        {
            btn.setText(getResources().getString(R.string.erroinfo));
        }
    }
    private void initImg()
    {
        imgBottomleft.setBackgroundColor(getResources().getColor(R.color.white));
        imgBottomright.setBackgroundColor(getResources().getColor(R.color.white));
        imgTopleft.setBackgroundColor(getResources().getColor(R.color.white));
        imgTopright.setBackgroundColor(getResources().getColor(R.color.white));
    }
    private void success(BluetoothDevice device)
    {
        ToastUtil.show(device.getAddress()+"解绑成功！");
        if(leftFDevice!=null&&device.getAddress().equals(leftFDevice.getDevice().getAddress()))
        {
            topleft_next.setText(getResources().getString(R.string.unbund_success));
        }else if(rightFDevice!=null&&device.getAddress().equals(rightFDevice.getDevice().getAddress()))
        {
            topright_next.setText(getResources().getString(R.string.unbund_success));
        }else if(leftBDevice!=null&&device.getAddress().equals(leftBDevice.getDevice().getAddress()))
        {
            bottomleft_next.setText(getResources().getString(R.string.unbund_success));
        }else if(rightBDevice!=null&&device.getAddress().equals(rightBDevice.getDevice().getAddress()))
        {
            bottomright_next.setText(getResources().getString(R.string.unbund_success));
        }
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_RETURN_OK);
        return intentFilter;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {

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