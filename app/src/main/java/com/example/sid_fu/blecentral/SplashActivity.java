package com.example.sid_fu.blecentral;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.example.sid_fu.blecentral.activity.CarListViewActivity;
import com.example.sid_fu.blecentral.activity.CarTypeListViewActivity;
import com.example.sid_fu.blecentral.activity.LoginActivity;
import com.example.sid_fu.blecentral.activity.ViewPagerDemoActivity;
import com.example.sid_fu.blecentral.ui.activity.BaseFragmentActivity;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.utils.SoundManager;

/**
 * Created by tiansj on 15/7/29.
 */
public class SplashActivity extends BaseFragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(SharedPreferences.getInstance().getString(Constants.LANDORPORT,Constants.DEFIED).equals("横屏"))
//        {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
//            if(!App.getInstance().isFirst)
//            {
//                App.getInstance().isFirst = true;
//                App.getInstance().playSigleSound(SoundManager.wel);
//            }
//            Logger.e("横屏横屏");
//        }else{
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
//            App.getInstance().playSigleSound(SoundManager.wel);
//        }

        setContentView(R.layout.aty_splash);
        try {
            App.getInstance().playLocalAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        App.getInstance().playSigleSound(SoundManager.wel);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initActivity();
            }
        }, 4000);

//        App.getInstance().speak("欢迎使用小安胎压监控系统");
//        App.getInstance().playSound(1,1,1);

    }
    private void initActivity()
    {
        boolean firstTimeUse = SharedPreferences.getInstance().getBoolean(Constants.FIRST_CONFIG, false);
        if(firstTimeUse) {
            //initGuideGallery();
            Intent intent = new Intent();
            intent.setClass(SplashActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            //initLaunchLogo();
            Intent intent = new Intent();
            intent.setClass(SplashActivity.this,ViewPagerDemoActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            App.getInstance().playSigleSound(SoundManager.wel);
//        }else{
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.e("Ondestroy");
    }
}
