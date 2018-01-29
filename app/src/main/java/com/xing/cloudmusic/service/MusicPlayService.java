package com.xing.cloudmusic.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.xing.cloudmusic.activity.MainActivity;
import com.xing.cloudmusic.util.LogUtil;
import com.xing.cloudmusic.util.ToastFactory;

import java.io.IOException;

/**
 * Created by Administrator on 2018/1/29 0029.
 */

public class MusicPlayService extends Service implements MediaPlayer.OnPreparedListener{
    public static final int ACTION_PLAY = 20001;


    private MediaPlayer mPlayer;

    private WifiManager.WifiLock wifiLock;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.LogE("MusicPlayService onBind");
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.LogE("MusicPlayService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        LogUtil.LogE("MusicPlayService onCreate begin");
        mPlayer = new MediaPlayer();
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setOnPreparedListener(this);

        wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "cloudmusic lock");

        wifiLock.acquire();
        LogUtil.LogE("MusicPlayService onCreate end");
    }

    @Override
    public void onDestroy() {
        LogUtil.LogE("MusicPlayService onDestroy");
        if(mPlayer != null) mPlayer.release();
        if(wifiLock !=null) wifiLock.release();
    }


    //当PrepareAsync完成时自动播放
    @Override
    public void onPrepared(MediaPlayer mp) {
        LogUtil.LogE("MusicPlayService onPrepared");
        mPlayer.start();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.LogE("MusicPlayService onUnbind");
        return super.onUnbind(intent);
    }

    public class Binder extends android.os.Binder{
        public void setAction(int action,String arg){
            switch (action){
                case ACTION_PLAY:
                    try {
                        mPlayer.setDataSource(arg);
                        mPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

}
