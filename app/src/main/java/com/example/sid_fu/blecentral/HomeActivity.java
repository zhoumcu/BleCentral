package com.example.sid_fu.blecentral;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid_fu.blecentral.activity.CarInfoDetailActivity;
import com.example.sid_fu.blecentral.activity.CarListViewActivity;
import com.example.sid_fu.blecentral.activity.MainFrameActivity;
import com.example.sid_fu.blecentral.activity.MainFrameForStartServiceActivity;
import com.example.sid_fu.blecentral.adapter.DeviceAdapter;
import com.example.sid_fu.blecentral.db.dao.DeviceDao;
import com.example.sid_fu.blecentral.db.dao.UserDao;
import com.example.sid_fu.blecentral.db.entity.Device;
import com.example.sid_fu.blecentral.db.entity.User;
import com.example.sid_fu.blecentral.ui.activity.BaseActivity;
import com.example.sid_fu.blecentral.utils.BitmapUtils;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.utils.ToastUtil;
import com.example.sid_fu.blecentral.widget.PictureView;
import com.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sid-fu on 2016/5/17.
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.img_scan)
    ImageView imgScan;
    @Bind(R.id.img_set)
    ImageView imgSet;

    private ListView listView;
    private static boolean isExit = false;
    private List<Device> articles = new ArrayList<>();
    private DeviceAdapter adapter;
    private HomeActivity mContext;
    private TextView tv_title;
    private TextView tv_content;
    private TextView tv_cartype;
    private ImageView img_icon;
    private TextView tv_normal;
    private TextView tv_current;
    private TextView tv_state;
    private View view;
    private TextView btn_details;
    private TextView btn_bund;
    private Device currentDevice;
    private TextView btn_normal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(SharedPreferences.getInstance().getString(Constants.LANDORPORT,Constants.DEFIED).equals("横屏"))
//        {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
//        }else{
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
//        }
        setContentView(R.layout.activity_main01);
        ButterKnife.bind(this);
        mContext = HomeActivity.this;
        intView();
//        saveToFile(textToSpeech,"正在打开蓝牙设备","test");
        Logger.e("onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        initDate();
        refreash();
        Logger.e("onResume");
    }

    private void initDate() {
//        articles = new DeviceDao(this).listByAll();
        articles = App.getDeviceDao().listByAll();
        if(articles.size()==0) view.setVisibility(View.GONE);
        for (Device device : articles)
        {
            if(device.getDefult()!=null)
            {
                if(device.getDefult().equals("true"))
                {
                    initData(device);
                }
            }
        }
    }

    private void intView() {
        listView = (ListView) findViewById(R.id.listView);
        ImageView img_add = (ImageView) findViewById(R.id.img_add);
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(HomeActivity.this, CarListViewActivity.class);
                startActivity(intent1);
            }
        });
        adapter = new DeviceAdapter(this, articles);
        listView.setAdapter(adapter);
        view  = findViewById(R.id.normal);
        view.setVisibility(View.GONE);
        tv_title = (TextView) findViewById(R.id.normal).findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.normal).findViewById(R.id.tv_content);
        tv_cartype = (TextView) findViewById(R.id.normal).findViewById(R.id.tv_cartype);
        img_icon = (ImageView) findViewById(R.id.normal).findViewById(R.id.img_icon);
        tv_normal = (TextView) findViewById(R.id.normal).findViewById(R.id.tv_normal);
        tv_current = (TextView) findViewById(R.id.normal).findViewById(R.id.tv_current);
        tv_state = (TextView) findViewById(R.id.normal).findViewById(R.id.tv_state);
        ImageView img_ecode = (ImageView) findViewById(R.id.normal).findViewById(R.id.img_ecode);
        btn_bund = (TextView) findViewById(R.id.normal).findViewById(R.id.btn_bund);
        btn_normal = (TextView) findViewById(R.id.normal).findViewById(R.id.btn_normal);
        btn_details = (TextView) findViewById(R.id.normal).findViewById(R.id.btn_details);
        TextView btn_delete = (TextView) findViewById(R.id.normal).findViewById(R.id.btn_delete);
        RelativeLayout btn_rl = (RelativeLayout) findViewById(R.id.normal).findViewById(R.id.btn_rl);
        img_ecode.setOnClickListener(this);
        btn_bund.setOnClickListener(this);
        btn_normal.setOnClickListener(this);
        btn_details.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_rl.setOnClickListener(this);
        btn_delete.setEnabled(false);
        btn_details.setEnabled(false);
        tv_state.setEnabled(true);
        adapter.setonCallBack(new DeviceAdapter.onCallBack() {
            @Override
            public void setOnClick(Device device) {
                currentDevice = device;
                initData(device);
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.img_ecode:

                break;
            case R.id.btn_bund:
                Intent intent2 = new Intent(mContext, CarInfoDetailActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putInt("id", currentDevice.getCarID());
                mBundle.putInt("state", CarInfoDetailActivity.DETAILS);
                Logger.e(currentDevice.toString());
                intent2.putExtras(mBundle);
                mContext.startActivity(intent2);
                break;
            case R.id.btn_normal:
                String contentString = "vlt_tpms_device" + "|" + currentDevice.getLeft_FD() + "|" +currentDevice.getRight_FD()
                        + "|" + currentDevice.getLeft_BD() + "|" + currentDevice.getRight_BD()
                        +"|"+currentDevice.getDeviceName()+"|"+currentDevice.getDeviceDescripe()
                        +"|"+currentDevice.getImagePath();
                Intent intent = new Intent();
                intent.setClass(mContext, PictureView.class);
                intent.putExtra("macImage", contentString);
                intent.putExtra("device",currentDevice);
                mContext.startActivity(intent);
                break;
            case R.id.btn_details:

                break;
            case R.id.btn_rl:
                Intent intent1 = new Intent();
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.setClass(mContext, MainFrameForStartServiceActivity.class);
                Bundle mBundle1 = new Bundle();
                mBundle1.putSerializable("DB_ARTICLES", currentDevice);
                mBundle1.putInt("DB_ID",currentDevice.getId());
                intent1.putExtras(mBundle1);
                mContext.startActivity(intent1);
                break;
        }
    }
    @OnClick(R.id.img_scan)
    public void scan()
    {
//        ToastUtil.show("扫一扫");
        //打开扫描界面扫描条形码或二维码
        Intent openCameraIntent = new Intent(HomeActivity.this, CaptureActivity.class);
        startActivityForResult(openCameraIntent, 0);
    }
    @OnClick(R.id.img_set)
    public void goSetting()
    {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, PersonSetting.class);
        startActivity(intent);
//        ToastUtil.show("设置");
    }

    private void initData(Device device)
    {
        currentDevice = device;
        view.setVisibility(View.VISIBLE);
        tv_title.setText(device.getDeviceName());
        tv_content.setText(device.getDeviceDescripe());
//        tv_cartype.setEnabled(true);
        btn_details.setText("已默认");
        Logger.e(device.toString());
        if(device.getIsShare().equals("false"))
        {
            btn_bund.setEnabled(true);
            btn_normal.setEnabled(true);
        }else
        {
            btn_bund.setEnabled(false);
            btn_normal.setEnabled(false);
        }
        img_icon.setImageBitmap(BitmapUtils.getImageFromAssetsFile(mContext,"logo/"+device.getImagePath()+".png"));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_set:
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, PersonSetting.class);
                startActivity(intent);
//                ToastUtil.show("个人");
                break;
            case R.id.menu_add:
//                ToastUtil.show("添加");
                Intent intent1 = new Intent();
                intent1.setClass(HomeActivity.this, CarListViewActivity.class);
                startActivity(intent1);
                break;
            case R.id.menu_sacan:
//                ToastUtil.show("扫一扫");
                //打开扫描界面扫描条形码或二维码
                Intent openCameraIntent = new Intent(HomeActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
//            ToastUtil.show(scanResult);
            //resultTextView.setText(scanResult);
            Logger.e("结果为："+scanResult);
            String[] date = scanResult.split("\\|");
            Logger.e(date.length+"");
            if (date[0].equals("vlt_tpms_device")&&date.length==8) {
                Logger.e(date[0]);
                //添加数据
                User user = new UserDao(this).get(1);
                Device deviceDate = new Device();
                deviceDate.setDeviceName(date[5]+"--授权");
                deviceDate.setDeviceDescripe(date[6]);
                deviceDate.setImagePath(date[7]);
                deviceDate.setRight_BD(date[4]);
                deviceDate.setLeft_BD(date[3]);
                deviceDate.setRight_FD(date[2]);
                deviceDate.setLeft_FD(date[1]);
                deviceDate.setIsShare("true");
                deviceDate.setUser(user);
                new DeviceDao(this).add(deviceDate);
                //刷新列表
                refreash();
            }else if (date[0].equals("vlt_tpms_device")&&date.length==7) {
                Logger.e(date[0]);
                //添加数据
                User user = new UserDao(this).get(1);
                Device deviceDate = new Device();
                deviceDate.setDeviceName(date[5]+"--授权");
                deviceDate.setDeviceDescripe(date[6]);
//                deviceDate.setImagePath(date[7]);
                deviceDate.setRight_BD(date[4]);
                deviceDate.setLeft_BD(date[3]);
                deviceDate.setRight_FD(date[2]);
                deviceDate.setLeft_FD(date[1]);
                deviceDate.setIsShare("true");
                deviceDate.setUser(user);
                new DeviceDao(this).add(deviceDate);
                //刷新列表
                refreash();
            }
        }
    }

    private void refreash() {
        articles = App.getDeviceDao().listByAll();
        adapter.updateData(fillterDate(articles));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次后退键退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {

            Logger.e("exit application");

            this.finish();
        }
    }

    public void saveToFile(TextToSpeech speech, String text, String file) {
        String destFileName = "/sdcard/tts/" + file + ".wav";
        speech.synthesizeToFile(text, null, destFileName);
    }

    public void readFromFile(TextToSpeech speech, String file) {
        String destFileName = "/sdcard/tts/" + file + ".wav";
        speech.addSpeech("2", destFileName);
        speech.speak("2", TextToSpeech.QUEUE_ADD, null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    public static List<Device> fillterDate(List<Device> devices)
    {
        List<Device> articles = new ArrayList<>();
        for (Device device :devices)
        {
            if (device.getDefult() != null) {
                if (device.getDefult().equals("true"))
                {
                    continue;
                }
            }
            articles.add(device);
        }
        return articles;
    }
}
