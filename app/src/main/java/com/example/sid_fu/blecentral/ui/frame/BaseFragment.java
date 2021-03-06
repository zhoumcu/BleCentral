package com.example.sid_fu.blecentral.ui.frame;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.BluetoothLeService;
import com.example.sid_fu.blecentral.ParsedAd;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.activity.MainFrameActivity;
import com.example.sid_fu.blecentral.activity.MainFrameForStartServiceActivity;
import com.example.sid_fu.blecentral.db.DbHelper;
import com.example.sid_fu.blecentral.db.entity.RecordData;
import com.example.sid_fu.blecentral.ui.BleData;
import com.example.sid_fu.blecentral.utils.BitmapUtils;
import com.example.sid_fu.blecentral.utils.CommonUtils;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.DataUtils;
import com.example.sid_fu.blecentral.utils.DimenUtil;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.RecycleBitmap;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.utils.SoundManager;
import com.example.sid_fu.blecentral.utils.ToastUtil;
import com.example.sid_fu.blecentral.utils.VibratorUtil;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * Created by Administrator on 2016/7/28.
 */
public abstract class BaseFragment extends Fragment{
    private ImageView imgTopleft;
    private ImageView imgTopright;
    private ImageView imgBottomleft;
    private ImageView imgBottomright;
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
    private ImageView topleft_voltage;
    private ImageView topright_voltage;
    private ImageView bottomleft_voltage;
    private ImageView bottomright_voltage;
    private TextView topleft_releat;
    private TextView topright_releat;
    private TextView bottomleft_releat;
    private TextView bottomright_releat;
    private TextView pressBottomleft;
    private TextView pressTopright;
    private TextView pressTopleft;
    private TextView pressBottomright;
    private TextView topleft_phone_rssi;
    private TextView topright_phone_rssi;
    private TextView bottomleft_phone_rssi;
    private TextView bottomright_phone_rssi;
    public MainFrameForStartServiceActivity mActivity;
    private DecimalFormat df1;
    private DecimalFormat df;
    private FragmentActivity context;
    private long vibratorTime = 1500;
//    private Map<View,int[]> recycleViews = new HashMap<>();

    protected abstract void initData();

    protected abstract void initRunnable();

    protected abstract void initConfig();
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mActivity = (MainFrameForStartServiceActivity) activity;
        }catch (IllegalStateException e)
        {
            Logger.e(e.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_pressure, container, false);
        initConfig();
        context = getActivity();
//        recycleViews.put(view,new int[]{R.id.img_battle,R.id.img_topleft});
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRunnable();
        initUI();
        df1=new DecimalFormat("######0.00");
        df=new DecimalFormat("######0.0");
        initData();
        //注册广播
        getActivity().registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }
    /**
     * 监听是否点击了home键将客户端推到后台
     */
    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    //表示按了home键,程序到了后台
                    //ToastUtil.show(getActivity(), "home", 1).show();
                    mActivity.isQuiting = true;
                    Logger.e("表示按了home键,程序到了后台");
                }else if(TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)){
                    //表示长按home键,显示最近使用的程序列表
                }
            }
        }
    };
    private void initUI() {

        topleft_preesure = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tv_preesure);
        topleft_temp = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tv_temp);
        topleft_note = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tv_note);
        topleft_voltage = (ImageView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.img_battle);
        topleft_releat = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tv_releat);
        topleft_phone_rssi = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.phone_rssi);
        imgTopleft =  (ImageView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.img_topleft);
        pressTopleft = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.press);
        TextView tempunitTopleft = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tempunit);

        topright_preesure = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.tv_preesure);
        topright_temp = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.tv_temp);
        topright_note = (TextView)getView(). findViewById(R.id.ll_topright).findViewById(R.id.tv_note);
        topright_voltage = (ImageView) getView().findViewById(R.id.ll_topright).findViewById(R.id.img_battle);
        topright_releat = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.tv_releat);
        topright_phone_rssi = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.phone_rssi);
        imgTopright =  (ImageView) getView().findViewById(R.id.ll_topright).findViewById(R.id.img_topleft);
        pressTopright = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.press);
        TextView tempunitTopright = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.tempunit);

        bottomleft_preesure = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_preesure);
        bottomleft_temp = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_temp);
        bottomleft_note = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_note);
        bottomleft_voltage = (ImageView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.img_battle);
        bottomleft_releat = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_releat);
        bottomleft_phone_rssi = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.phone_rssi);
        imgBottomleft =  (ImageView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.img_topleft);
        pressBottomleft = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.press);
        TextView tempunitBottomleft = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tempunit);

        bottomright_preesure = (TextView)getView(). findViewById(R.id.ll_bottomright).findViewById(R.id.tv_preesure);
        bottomright_temp = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.tv_temp);
        bottomright_note = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.tv_note);
        bottomright_voltage = (ImageView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.img_battle);
        bottomright_releat = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.tv_releat);
        bottomright_phone_rssi = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.phone_rssi);
        imgBottomright =  (ImageView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.img_topleft);
        pressBottomright = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.press);
        TextView tempunitBottomright = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.tempunit);

        pressTopleft.setText(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar"));
        pressTopright.setText(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar"));
        pressBottomleft.setText(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar"));
        pressBottomright.setText(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar"));
        tempunitTopleft.setText(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃"));
        tempunitTopright.setText(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃"));
        tempunitBottomleft.setText(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃"));
        tempunitBottomright.setText(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃"));

    }

    private boolean isLeftF,isRightF,isRightB,isLeftB;

    public void showRssi(BluetoothDevice device,int rssi)
    {
        if(device.getAddress().equals(mActivity.manageDevice.getLeftFDevice()))
        {
            topleft_phone_rssi.setVisibility(View.VISIBLE);
            topleft_phone_rssi.setText("手机RSSI："+rssi);
            if(isLeftF)
            {
                isLeftF = false;
                topleft_phone_rssi.setBackgroundColor(getResources().getColor(R.color.blue));
            }else
            {
                isLeftF = true;
                topleft_phone_rssi.setBackgroundColor(getResources().getColor(R.color.green));
            }

        }else if(device.getAddress().equals(mActivity.manageDevice.getRightFDevice()))
        {
            topright_phone_rssi.setVisibility(View.VISIBLE);
            topright_phone_rssi.setText("手机RSSI："+rssi);
            if(isRightF)
            {
                isRightF = false;
                topright_phone_rssi.setBackgroundColor(getResources().getColor(R.color.blue));
            }else
            {
                isRightF = true;
                topright_phone_rssi.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }else if(device.getAddress().equals(mActivity.manageDevice.getLeftBDevice()))
        {
            bottomleft_phone_rssi.setVisibility(View.VISIBLE);
            bottomleft_phone_rssi.setText("手机RSSI："+rssi);
            if(isLeftB)
            {
                isLeftB = false;
                bottomleft_phone_rssi.setBackgroundColor(getResources().getColor(R.color.blue));
            }else
            {
                isLeftB = true;
                bottomleft_phone_rssi.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }else if(device.getAddress().equals(mActivity.manageDevice.getRightBDevice()))
        {
            bottomright_phone_rssi.setVisibility(View.VISIBLE);
            bottomright_phone_rssi.setText("手机RSSI："+rssi);
            if(isRightB)
            {
                isRightB = false;
                bottomright_phone_rssi.setBackgroundColor(getResources().getColor(R.color.blue));
            }else
            {
                isRightB = true;
                bottomright_phone_rssi.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }
    }
    /**
     * 白天模式下 获取数据超时或者数据丢失 时间为3分钟
     * @param recordData
     * @param state
     */
    public void getDataTimeOutForDay(RecordData recordData, int state)
    {
        switch (state)
        {
            case 1001:
                imgTopleft.setImageDrawable(getResources().getDrawable(R.mipmap.link_off_left1));
                topleft_voltage.setImageDrawable(null);
                topleft_preesure.setText(getActivity().getString(R.string.defaulttemp));
                topleft_temp.setText(getActivity().getString(R.string.defaulttemp));
                topleft_releat.setText("");

                recordData.setName(mActivity.manageDevice.getLeftFDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getLeftFDevice(),recordData);

                break;
            case 1002:
                imgTopright.setImageDrawable(getResources().getDrawable(R.mipmap.link_off_right1));
                topright_voltage.setImageDrawable(null);
                topright_preesure.setText(getActivity().getString(R.string.defaulttemp));
                topright_temp.setText(getActivity().getString(R.string.defaulttemp));
                topright_releat.setText("");
                recordData.setName(mActivity.manageDevice.getRightFDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getRightFDevice(),recordData);

                break;
            case 1003:
                imgBottomleft.setImageDrawable(getResources().getDrawable(R.mipmap.link_off_left2));
                bottomleft_voltage.setImageDrawable(null);
                bottomleft_preesure.setText(getActivity().getString(R.string.defaulttemp));
                bottomleft_temp.setText(getActivity().getString(R.string.defaulttemp));
                bottomleft_releat.setText("");
                recordData.setName(mActivity.manageDevice.getLeftBDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getLeftBDevice(),recordData);

                break;
            case 1004:
                imgBottomright.setImageDrawable(getResources().getDrawable(R.mipmap.link_off_right2));
                bottomright_voltage.setImageDrawable(null);
                bottomright_preesure.setText(getActivity().getString(R.string.defaulttemp));
                bottomright_temp.setText(getActivity().getString(R.string.defaulttemp));
                bottomright_releat.setText("");

                recordData.setName(mActivity.manageDevice.getRightBDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getRightBDevice(),recordData);

                break;
        }
    }

    /**
     * 夜间模式下 获取数据超时或者数据丢失 时间为3分钟
     * @param recordData
     * @param state
     */
    public void getDataTimeOutForNight(RecordData recordData, int state)
    {
        switch (state)
        {
            case 1001:
                topleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_off));
                imgTopleft.setImageDrawable(getResources().getDrawable(R.mipmap.pmlink_off_left1));
                topleft_temp.setText(getActivity().getString(R.string.defaulttemp));
                topleft_preesure.setText(getActivity().getString(R.string.defaulttemp));
                topleft_releat.setText("");

                recordData.setName(mActivity.manageDevice.getLeftFDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getLeftFDevice(),recordData);

                break;
            case 1002:
                topright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_off));
                imgTopright.setImageDrawable(getResources().getDrawable(R.mipmap.pmlink_off_right1));
                topright_temp.setText(getActivity().getString(R.string.defaulttemp));
                topright_preesure.setText(getActivity().getString(R.string.defaulttemp));
                topright_releat.setText("");

                recordData.setName(mActivity.manageDevice.getRightFDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getRightFDevice(),recordData);

                break;
            case 1003:
                imgBottomleft.setImageDrawable(getResources().getDrawable(R.mipmap.pmlink_off_left2));
                bottomleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_off));
                bottomleft_preesure.setText(getActivity().getString(R.string.defaulttemp));
                bottomleft_temp.setText(getActivity().getString(R.string.defaulttemp));
                bottomleft_releat.setText("");

                recordData.setName(mActivity.manageDevice.getLeftBDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getLeftBDevice(),recordData);

                break;
            case 1004:
                imgBottomright.setImageDrawable(getResources().getDrawable(R.mipmap.pmlink_off_right2));
                bottomright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_off));
                bottomright_preesure.setText(getActivity().getString(R.string.defaulttemp));
                bottomright_temp.setText(getActivity().getString(R.string.defaulttemp));
                bottomright_releat.setText("");
                recordData.setName(mActivity.manageDevice.getRightBDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getRightBDevice(),recordData);
                break;
        }
    }

    /**
     * 白天模式下，发现设备广播，UI初始化
     * @param strAddress
     * @param noticeStr
     * @param date
     */
    public void dicoverBlueDeviceForDay(String strAddress, String noticeStr, float date)
    {
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice()))
        {
//                ll_bottomright.setVisibility(View.VISIBLE);
            imgBottomleft.setImageDrawable(getResources().getDrawable(R.mipmap.am_normal_left2));
            bottomleft_preesure.setTextColor(getResources().getColor(R.color.phone));
            bottomleft_releat.setTextColor(getResources().getColor(R.color.himtphone));
            bottomleft_releat.setText(noticeStr);
            pressBottomleft.setTextColor(getResources().getColor(R.color.phone));
            handleVoltageShow(bottomleft_voltage,date);
        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice()))
        {
//                ll_bottomleft.setVisibility(View.VISIBLE);
            imgBottomright.setImageDrawable(getResources().getDrawable(R.mipmap.am_normal_right2));
            bottomright_preesure.setTextColor(getResources().getColor(R.color.phone));
            bottomright_releat.setTextColor(getResources().getColor(R.color.himtphone));
            bottomright_releat.setText(noticeStr);
            pressBottomright.setTextColor(getResources().getColor(R.color.phone));
            handleVoltageShow(bottomright_voltage,date);
        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice()))
        {
//                ll_topleft.setVisibility(View.VISIBLE);
            imgTopleft.setImageDrawable(getResources().getDrawable(R.mipmap.am_normal_left1));
            topleft_preesure.setTextColor(getResources().getColor(R.color.phone));
            topleft_releat.setTextColor(getResources().getColor(R.color.himtphone));
            topleft_releat.setText(noticeStr);
            pressTopleft.setTextColor(getResources().getColor(R.color.phone));
            handleVoltageShow(topleft_voltage,date);
        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice()))
        {
//                ll_topright.setVisibility(View.VISIBLE);
            imgTopright.setImageDrawable(getResources().getDrawable(R.mipmap.am_normal_right1));
            topright_preesure.setTextColor(getResources().getColor(R.color.phone));
            topright_releat.setTextColor(getResources().getColor(R.color.himtphone));
            topright_releat.setText(noticeStr);
            pressTopright.setTextColor(getResources().getColor(R.color.phone));
            handleVoltageShow(topright_voltage,date);
        }
    }

    /**
     * 夜间模式下，发现设备广播，UI初始化
     * @param strAddress
     * @param noticeStr
     * @param date
     */
    public void dicoverBlueDeviceForNight(String strAddress, String noticeStr, float date)
    {
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice()))
        {
//                ll_bottomright.setVisibility(View.VISIBLE);
            imgBottomleft.setImageDrawable(getResources().getDrawable(R.mipmap.pm_normal_left2));
            bottomleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_on));
            bottomleft_preesure.setTextColor(getResources().getColor(R.color.blue_night));
            bottomleft_releat.setTextColor(getResources().getColor(R.color.blue_night));
            bottomleft_releat.setText(noticeStr);
            pressBottomleft.setTextColor(getResources().getColor(R.color.blue_night));
            handleVoltageShow(bottomleft_voltage,date);
        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice()))
        {
//                ll_bottomleft.setVisibility(View.VISIBLE);
            imgBottomright.setImageDrawable(getResources().getDrawable(R.mipmap.pm_normal_right2));
            bottomright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_on));
            bottomright_preesure.setTextColor(getResources().getColor(R.color.blue_night));
            bottomright_releat.setTextColor(getResources().getColor(R.color.blue_night));
            bottomright_releat.setText(noticeStr);
            pressBottomright.setTextColor(getResources().getColor(R.color.blue_night));
            handleVoltageShow(bottomright_voltage,date);
        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice()))
        {
//                ll_topleft.setVisibility(View.VISIBLE);
            imgTopleft.setImageDrawable(getResources().getDrawable(R.mipmap.pm_normal_left1));
            topleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_on));
            topleft_preesure.setTextColor(getResources().getColor(R.color.blue_night));
            topleft_releat.setTextColor(getResources().getColor(R.color.blue_night));
            topleft_releat.setText(noticeStr);
            pressTopleft.setTextColor(getResources().getColor(R.color.blue_night));
            handleVoltageShow(topleft_voltage,date);
        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice()))
        {
//                ll_topright.setVisibility(View.VISIBLE);
            imgTopright.setImageDrawable(getResources().getDrawable(R.mipmap.pm_normal_right1));
            topright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_on));
            topright_preesure.setTextColor(getResources().getColor(R.color.blue_night));
            topright_releat.setTextColor(getResources().getColor(R.color.blue_night));
            topright_releat.setText(noticeStr);
            pressTopright.setTextColor(getResources().getColor(R.color.blue_night));
            handleVoltageShow(topright_voltage,date);
        }
    }

    /**
     * 白天模式下，接收到蓝牙发送数据，进行异常报警UI
     * @param strAddress
     * @param noticeStr
     */
    public void bleIsExceptionForDay(String strAddress,String noticeStr)
    {
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice()))
        {
            imgBottomleft.setImageDrawable(getResources().getDrawable(R.mipmap.am_error_left2));
            bottomleft_releat.setTextColor(getResources().getColor(R.color.red));
            bottomleft_releat.setText(noticeStr);
            bottomleft_preesure.setTextColor(getResources().getColor(R.color.red));
            pressBottomleft.setTextColor(getResources().getColor(R.color.red));
//                if(noticeStr.contains("快漏气"))
            if(!noticeStr.equals(mActivity.manageDevice.leftB_preContent))
                mActivity.manageDevice.leftB_notify = false;
            mActivity.manageDevice.leftB_preContent = noticeStr;
            if(!mActivity.manageDevice.leftB_notify)
            {
                mActivity.manageDevice.leftB_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.leftB);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }
//                try {
//                    if(!isExit(soundList,SoundManager.leftB))
//                    {
//                        Logger.e("异常播报语音");
//                        soundList.add(SoundManager.leftB);
//                        App.getInstance().playSound(SoundManager.leftB);
////                        App.getInstance().playMutilSounds(soundList);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            bottomleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice()))
        {
            imgBottomright.setImageDrawable(getResources().getDrawable(R.mipmap.am_error_right2));
            bottomright_releat.setTextColor(getResources().getColor(R.color.red));
            bottomright_releat.setText(noticeStr);
            bottomright_preesure.setTextColor(getResources().getColor(R.color.red));
            pressBottomright.setTextColor(getResources().getColor(R.color.red));
//                if(!mActivity.manageDevice.rightB_notify||noticeStr.contains("快漏气")) {
//                    mActivity.manageDevice.rightB_notify = true;
//                    App.getInstance().playSound(SoundManager.rightB,noticeStr);
//                }
            if(!noticeStr.equals(mActivity.manageDevice.rightB_preContent))
                mActivity.manageDevice.rightB_notify = false;
            mActivity.manageDevice.rightB_preContent = noticeStr;
            if(!mActivity.manageDevice.rightB_notify)
            {
                mActivity.manageDevice.rightB_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.rightB);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }
            bottomright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice()))
        {
            imgTopleft.setImageDrawable(getResources().getDrawable(R.mipmap.am_error_left1));
            topleft_releat.setTextColor(getResources().getColor(R.color.red));
            topleft_releat.setText(noticeStr);
            topleft_preesure.setTextColor(getResources().getColor(R.color.red));
            pressTopleft.setTextColor(getResources().getColor(R.color.red));
//                if(!mActivity.manageDevice.leftF_notify||noticeStr.contains("快漏气")) {
//                    mActivity.manageDevice.leftF_notify = true;
//                    App.getInstance().playSound(SoundManager.leftF,noticeStr);
//                }

            if(!noticeStr.equals(mActivity.manageDevice.leftF_preContent))
                mActivity.manageDevice.leftF_notify = false;
            mActivity.manageDevice.leftF_preContent = noticeStr;
            if(!mActivity.manageDevice.leftF_notify)
            {
                mActivity.manageDevice.leftF_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.leftF);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }
            topleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice()))
        {
            imgTopright.setImageDrawable(getResources().getDrawable(R.mipmap.am_error_right1));
            topright_releat.setTextColor(getResources().getColor(R.color.red));
            topright_releat.setText(noticeStr);
            topright_preesure.setTextColor(getResources().getColor(R.color.red));
            pressTopright.setTextColor(getResources().getColor(R.color.red));
//                if(!mActivity.manageDevice.rightF_notify||noticeStr.contains("快漏气")) {
//                    mActivity.manageDevice.rightF_notify = true;
//                    App.getInstance().playSound(SoundManager.rightF,noticeStr);
//                }
            if(!noticeStr.equals(mActivity.manageDevice.rightF_preContent))
                mActivity.manageDevice.rightF_notify = false;
            mActivity.manageDevice.rightF_preContent = noticeStr;
            if(!mActivity.manageDevice.rightF_notify)
            {
                mActivity.manageDevice.rightF_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.rightF);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }
            topright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }
    }

    /**
     * 夜间模式下，接收到蓝牙发送数据，进行异常报警UI
     * @param strAddress
     * @param noticeStr
     */
    public void bleIsExceptionForNight(String strAddress,String noticeStr)
    {
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice()))
        {
            imgBottomleft.setImageDrawable(getResources().getDrawable(R.mipmap.pm_error_left2));
            bottomleft_releat.setTextColor(getResources().getColor(R.color.red));
            bottomleft_releat.setText(noticeStr);
            bottomleft_preesure.setTextColor(getResources().getColor(R.color.red));
            pressBottomleft.setTextColor(getResources().getColor(R.color.red));

//                App.getInstance().playSound(SoundManager.leftB,noticeStr);
            if(!noticeStr.equals(mActivity.manageDevice.leftB_preContent))
                mActivity.manageDevice.leftB_notify = false;
            mActivity.manageDevice.leftB_preContent = noticeStr;
            if(!mActivity.manageDevice.leftB_notify)
            {
                mActivity.manageDevice.leftB_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.leftB);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }
            bottomleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice()))
        {
            imgBottomright.setImageDrawable(getResources().getDrawable(R.mipmap.pm_error_right2));
            bottomright_releat.setTextColor(getResources().getColor(R.color.red));
            bottomright_releat.setText(noticeStr);
            bottomright_preesure.setTextColor(getResources().getColor(R.color.red));
            pressBottomright.setTextColor(getResources().getColor(R.color.red));

//                App.getInstance().playSound(SoundManager.rightB,noticeStr);
            if(!noticeStr.equals(mActivity.manageDevice.rightB_preContent))
                mActivity.manageDevice.rightB_notify = false;
            mActivity.manageDevice.rightB_preContent = noticeStr;
            if(!mActivity.manageDevice.rightB_notify)
            {
                mActivity.manageDevice.rightB_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.rightB);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }
            bottomright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice()))
        {
            imgTopleft.setImageDrawable(getResources().getDrawable(R.mipmap.pm_error_left1));
            topleft_releat.setTextColor(getResources().getColor(R.color.red));
            topleft_releat.setText(noticeStr);
            topleft_preesure.setTextColor(getResources().getColor(R.color.red));
            pressTopleft.setTextColor(getResources().getColor(R.color.red));

//                App.getInstance().playSound(SoundManager.leftF,noticeStr);
            if(!noticeStr.equals(mActivity.manageDevice.leftF_preContent))
                mActivity.manageDevice.leftF_notify = false;
            mActivity.manageDevice.leftF_preContent = noticeStr;
            if(!mActivity.manageDevice.leftF_notify)
            {
                mActivity.manageDevice.leftF_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.leftF);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }
            topleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice()))
        {
            imgTopright.setImageDrawable(getResources().getDrawable(R.mipmap.pm_error_right1));
            topright_releat.setTextColor(getResources().getColor(R.color.red));
            topright_releat.setText(noticeStr);
            topright_preesure.setTextColor(getResources().getColor(R.color.red));
            pressTopright.setTextColor(getResources().getColor(R.color.red));

//                App.getInstance().playSound(SoundManager.rightF,noticeStr);
            if(!noticeStr.equals(mActivity.manageDevice.rightF_preContent))
                mActivity.manageDevice.rightF_notify = false;
            mActivity.manageDevice.rightF_preContent = noticeStr;
            if(!mActivity.manageDevice.rightF_notify)
            {
                mActivity.manageDevice.rightF_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.rightF);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }
            topright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }
    }

    /**
     *  数据显示
     * @param device
     * @param bleData
     */
    public void showDataForUI(String device , BleData bleData)
    {
        String barData;
        if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Bar"))
        {
            barData = df.format(bleData.getPress());
        }else
        {
            barData = String.valueOf(Math.round(bleData.getPress()));
        }
        Logger.e(barData);
        if(device.equals(mActivity.manageDevice.getLeftBDevice()))
        {
            bottomleft_preesure.setText(barData);
            bottomleft_temp.setText(String.valueOf(bleData.getTemp()));
            handleVoltageShow(bottomleft_voltage,bleData.getVoltage());
        }else if(device.equals(mActivity.manageDevice.getRightBDevice()))
        {
            bottomright_preesure.setText(barData);
            bottomright_temp.setText(String.valueOf(bleData.getTemp()));
            handleVoltageShow(bottomright_voltage,bleData.getVoltage());
        }else if(device.equals(mActivity.manageDevice.getLeftFDevice()))
        {
            topleft_preesure.setText(barData);
            topleft_temp.setText(String.valueOf(bleData.getTemp()));
            handleVoltageShow(topleft_voltage,bleData.getVoltage());

        }else if(device.equals(mActivity.manageDevice.getRightFDevice()))
        {
            topright_preesure.setText(barData);
            topright_temp.setText(String.valueOf(bleData.getTemp()));
            handleVoltageShow(topright_voltage,bleData.getVoltage());
        }
    }
    /**
     * 电池变化情况指示
     * @param img
     * @param voltage
     */
    public void handleVoltageShow(ImageView img,float voltage)
    {
        if (!SharedPreferences.getInstance().getBoolean(Constants.DAY_NIGHT,false))
        {
            if(voltage>=Constants.vol)
            {
                img.setImageDrawable(getResources().getDrawable(R.mipmap.am_battle_100));
            }else if(voltage>Constants.vol*0.8&&voltage<Constants.vol){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.am_battle_80));
            }else if(voltage>Constants.vol*0.5&&voltage<Constants.vol*0.8){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.am_battle_50));
            }else if(voltage>Constants.vol*0.2&&voltage<Constants.vol*0.5){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.am_battle_20));
            }else if(voltage>0&&voltage<Constants.vol*0.2){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.am_battle_0));
            }

        }else{

            if(voltage>=Constants.vol)
            {
                img.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_100));
            }else if(voltage>Constants.vol*0.8&&voltage<Constants.vol){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_80));
            }else if(voltage>Constants.vol*0.5&&voltage<Constants.vol*0.8){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_50));
            }else if(voltage>Constants.vol*0.2&&voltage<Constants.vol*0.5){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_20));
            }else if(voltage>0&&voltage<Constants.vol*0.2){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_0));
            }
        }
    }
    public void setUnitTextSize()
    {
        float rate;
        if(SharedPreferences.getInstance().getString(Constants.LANDORPORT,Constants.DEFIED).equals("横屏"))
        {
            rate = (float) (CommonUtils.getScreenHeight(getActivity()) / 480)*18;
        }else{
            rate = (float) (CommonUtils.getScreenWidth(getActivity()) / 480)*15;
        }

        Logger.e("字体大小："+rate);
        if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Kpa"))
        {
            topleft_preesure.setTextSize(rate);
            topright_preesure.setTextSize(rate);
            bottomleft_preesure.setTextSize(rate);
            bottomright_preesure.setTextSize(rate);
        }else
        {
            topleft_preesure.setTextSize(DimenUtil.getDimension(R.dimen.press_size_kpa));
            topright_preesure.setTextSize(getResources().getDimension(R.dimen.press_size_kpa));
            bottomleft_preesure.setTextSize(getResources().getDimension(R.dimen.press_size_kpa));
            bottomright_preesure.setTextSize(getResources().getDimension(R.dimen.press_size_kpa));
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        BitmapUtils.releaseImageViewResouce(imgBottomleft);
//        BitmapUtils.releaseImageViewResouce(imgBottomright);
//        BitmapUtils.releaseImageViewResouce(imgTopleft);
//        BitmapUtils.releaseImageViewResouce(imgTopright);
//        BitmapUtils.releaseImageViewResouce(topright_voltage);
//        BitmapUtils.releaseImageViewResouce(bottomleft_voltage);
//        BitmapUtils.releaseImageViewResouce(bottomright_voltage);
//        BitmapUtils.releaseImageViewResouce(topleft_voltage);
//        RecycleBitmap.recycle(recycleViews);
        System.gc();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mHomeKeyEventReceiver);
        if(mActivity.isQuiting) return;
        BitmapUtils.releaseImageViewResouce(imgBottomleft);
        BitmapUtils.releaseImageViewResouce(imgBottomright);
        BitmapUtils.releaseImageViewResouce(imgTopleft);
        BitmapUtils.releaseImageViewResouce(imgTopright);
        BitmapDrawable bd = (BitmapDrawable)mActivity.background.getBackground();
        mActivity.background.setBackgroundResource(0);
        bd.setCallback(null);
        bd.getBitmap().recycle();
//        BitmapUtils.releaseImageViewResouce(topright_voltage);
//        BitmapUtils.releaseImageViewResouce(bottomleft_voltage);
//        BitmapUtils.releaseImageViewResouce(bottomright_voltage);
//        BitmapUtils.releaseImageViewResouce(topleft_voltage);
//        RecycleBitmap.recycle(recycleViews);
//        System.gc();
        Logger.e("onDestroy");
    }
}
