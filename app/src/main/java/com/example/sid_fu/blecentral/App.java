package com.example.sid_fu.blecentral;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.example.sid_fu.blecentral.db.dao.DeviceDao;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SoundManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by bob on 2015/1/30.
 */
public class App extends Application implements TextToSpeech.OnInitListener{

    public static App app;
    public TextToSpeech textToSpeech;
    private SoundPool sp;
    private HashMap<String, Integer> spMap;
    private HashMap<String, String> hasMap  = new HashMap<String, String>();
//    public SoundManager soundManager;
    private Handler mHandler = new Handler();
    private Vector<Integer> mKillSoundQueue = new Vector<Integer>();
    private long delay = 6000;
    private long seperateTime = 2000;
    public boolean isFirst = false;
    public static DeviceDao getDeviceDao() {
        return deviceDao;
    }

    private static DeviceDao deviceDao;
    private List<Activity> mList = new LinkedList<Activity>();

    public App() {
        app = this;
    }

    public static synchronized App getInstance() {
        if (app == null) {
            app = new App();
        }
        return app;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        deviceDao= new DeviceDao(this);
//        textToSpeech = new TextToSpeech(this, this);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        String device_token = UmengRegistrar.getRegistrationId(this);
        Logger.e(device_token);

        initSound();

//        soundManager = SoundManager.getInstance();
//        soundManager.initSounds(this);
    }
    public void add(String str)
    {
        textToSpeech.addSpeech(str,"test");
        speak(str);
    }
    public void speak(String text)
    {
        //if(!textToSpeech.isSpeaking())
            //textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH,null);
    }
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //System.exit(0);
        }
    }

    @Override
    public void onInit(int status) {
//        if (status == TextToSpeech.SUCCESS) {
//            int result = textToSpeech.setLanguage(Locale.CHINA);
//            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.ERROR) {
//                Toast.makeText(this, "数据丢失或语言不支持", Toast.LENGTH_SHORT).show();
//            }
//            if (result == TextToSpeech.LANG_AVAILABLE) {
////                Toast.makeText(this, "支持该语言", Toast.LENGTH_SHORT).show();
//            }
////            Toast.makeText(this, "初始化成功", Toast.LENGTH_SHORT).show();
//        }
    }
    public void initSound() {
        sp = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        spMap = new HashMap<String, Integer>();
        spMap.put(SoundManager.wel, sp.load(this, R.raw.welcome, 1));
        spMap.put(SoundManager.bund, sp.load(this, R.raw.kspd, 1));
        spMap.put(SoundManager.rightB, sp.load(this, R.raw.yh, 2));
        spMap.put(SoundManager.rightF, sp.load(this, R.raw.yq, 3));
        spMap.put(SoundManager.leftB, sp.load(this, R.raw.zh, 4));
        spMap.put(SoundManager.leftF, sp.load(this, R.raw.zq, 5));
//        playSound(1,0);
    }

    public void playSound(final String sound, String key)  {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = volumnCurrent / audioMaxVolumn;
//        if(hasMap.get(sound).contains(key))
//        {
//            return;
//        }
//        hasMap.put(sound,key);
//        int soundId = sp.play(spMap.get(sound), volumnRatio, volumnRatio, 1, 0, 1f);
//        mKillSoundQueue.add(soundId);
//        if (!mKillSoundQueue.isEmpty()&&mKillSoundQueue.size()>1)
//        {
//            sp.stop(mKillSoundQueue.firstElement());
//            mKillSoundQueue.remove(0);
//        }
//        // schedule the current sound to stop after set milliseconds
//        mHandler.postDelayed(new Runnable() {
//            public void run() {
//                if (!mKillSoundQueue.isEmpty()) {
//                    sp.stop(mKillSoundQueue.firstElement());
//                    hasMap.remove(sound);
//                }
//            }
//        }, delay);
    }
    public void playSound(final String sound)  {
        //AudioTrack for incoming audio to play as below:

        int mMaxJitter = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
        new AudioTrack(AudioManager.STREAM_VOICE_CALL,8000,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                mMaxJitter, AudioTrack.MODE_STREAM);
//To register broadcast receiver for bluetooth audio routing
//        IntentFilter ifil = new IntentFilter();
//        ifil.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
//        this.registerReceiver(<receiver instance>,ifil);

        //To get AudioManager service
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        //Whenever user select to route audio to Bluetooth
        am.setMode(AudioManager.MODE_IN_CALL);//tried setting with other mode also viz. MODE_NORMAL, MODE_IN_COMMUNICATION but no luck
        am.startBluetoothSco();//after this I get AudioManager.SCO_AUDIO_STATE_CONNECTED state in the receiver
        am.setBluetoothScoOn(true);
        am.setSpeakerphoneOn(false);

        //Whenever user select to route audio to Phone Speaker
        am.setMode(AudioManager.MODE_NORMAL);
        am.stopBluetoothSco();//after this I get      AudioManager.SCO_AUDIO_STATE_DISCONNECTED state in the receiver
        am.setBluetoothScoOn(false);
        am.setSpeakerphoneOn(true);

//        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        changeToSpeaker(am);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_RING);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_RING);
        float volumnRatio = volumnCurrent / audioMaxVolumn;
        if(volumnCurrent<audioMaxVolumn*0.2) volumnRatio = audioMaxVolumn / 3;
//        am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) volumnRatio, 1);
        Logger.e("最大音量："+audioMaxVolumn+"当前系统音量："+volumnCurrent+"设定音量："+volumnRatio);
        int soundId = sp.play(spMap.get(sound), volumnRatio, volumnRatio, 1, 0, 1f);
//        sp.setVolume(soundId,volumnRatio,volumnRatio);
        mKillSoundQueue.add(soundId);
//        if (!mKillSoundQueue.isEmpty()&&mKillSoundQueue.size()>1)
//        {
//            sp.stop(mKillSoundQueue.firstElement());
//            mKillSoundQueue.remove(0);
//        }
        // schedule the current sound to stop after set milliseconds
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (!mKillSoundQueue.isEmpty()) {
                    sp.stop(mKillSoundQueue.firstElement());
                }
            }
        }, delay);

    }
    public void playSigleSound(final String sound)  {
        int mMaxJitter = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
        new AudioTrack(AudioManager.STREAM_VOICE_CALL,8000,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                mMaxJitter, AudioTrack.MODE_STREAM);
//To register broadcast receiver for bluetooth audio routing
//        IntentFilter ifil = new IntentFilter();
//        ifil.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
//        this.registerReceiver(<receiver instance>,ifil);

        //To get AudioManager service
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        //Whenever user select to route audio to Bluetooth
        am.setMode(AudioManager.MODE_IN_CALL);//tried setting with other mode also viz. MODE_NORMAL, MODE_IN_COMMUNICATION but no luck
        am.startBluetoothSco();//after this I get AudioManager.SCO_AUDIO_STATE_CONNECTED state in the receiver
        am.setBluetoothScoOn(true);
        am.setSpeakerphoneOn(false);

        //Whenever user select to route audio to Phone Speaker
        am.setMode(AudioManager.MODE_NORMAL);
        am.stopBluetoothSco();//after this I get      AudioManager.SCO_AUDIO_STATE_DISCONNECTED state in the receiver
        am.setBluetoothScoOn(false);
        am.setSpeakerphoneOn(true);

//        changeToSpeaker(am);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_RING);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_RING);
        float volumnRatio = volumnCurrent / audioMaxVolumn;
        if(volumnCurrent<audioMaxVolumn*0.2) volumnRatio = audioMaxVolumn / 3;
//        am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) volumnRatio, 1);
        Logger.e("最大音量："+audioMaxVolumn+"当前系统音量："+volumnCurrent+"设定音量："+volumnRatio);
        int soundId = sp.play(spMap.get(sound), volumnRatio, volumnRatio, 1, 0, 1f);
//        sp.setVolume(soundId,volumnRatio,volumnRatio);
        mKillSoundQueue.add(soundId);
        // schedule the current sound to stop after set milliseconds
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (!mKillSoundQueue.isEmpty()) {
                    sp.stop(mKillSoundQueue.firstElement());
                }
            }
        }, delay);
    }
    /**
    * 切换到外放
    */
    public void changeToSpeaker(AudioManager audioManager){
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
    }

    /**
     * play the sounds have loaded in SoundPool
     * @param keys the files key stored in the map
     * @throws InterruptedException
     */
    public void playMutilSounds(List<String> keys)
            throws InterruptedException {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = volumnCurrent / audioMaxVolumn;
        for (String key : keys) {
            Logger.e("playMutilSounds"+keys.size());
            if (spMap.containsKey(key)) {
                int soundId = sp.play(spMap.get(key), volumnCurrent, volumnRatio, 1, 0, 1.0f);
                //sleep for a while for SoundPool play
                Thread.sleep(seperateTime);
                mKillSoundQueue.add(soundId);
                keys.remove(key);
            }
        }

        // schedule the current sound to stop after set milliseconds
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (!mKillSoundQueue.isEmpty()) {
                    sp.stop(mKillSoundQueue.firstElement());
                }
            }
        }, delay);

    }
    public void pauseSound(){
        sp.autoPause();
    }
    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Logger.d("onTerminate");
        super.onTerminate();
        isFirst = false;
    }
//    private void playLocalAudio_usingDescriptor() throw Exception {
//        AssetFileDescriptor fileDesc = this.getResources().openRawResourceFd(R.raw.welcome);
//        MediaPlayer player = null;
//        if (fileDesc != null) {
//            player = new MediaPlayer();
//            player.setDataSource(fileDesc, fileDesc.getStartOffset(), fileDesc.getLength());
//
//            try {
//                fileDesc.close();
//                player.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            player.start();
//        }
//    }
    // 方法1 ： 使用mediaplayer播放本地raw/下的audio文件的
    public void playLocalAudio() throws Exception {
        MediaPlayer player = MediaPlayer.create(this, R.raw.welcome);
        player.start();
    }
}
