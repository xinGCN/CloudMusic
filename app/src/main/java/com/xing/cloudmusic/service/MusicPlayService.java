package com.xing.cloudmusic.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.xing.cloudmusic.activity.MainActivity;
import com.xing.cloudmusic.base.DataAndCode;
import com.xing.cloudmusic.base.Song;
import com.xing.cloudmusic.http.CloudMusicApiImpl;
import com.xing.cloudmusic.util.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2018/1/29 0029.
 */

public class MusicPlayService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener{
    public static final int ACTION_PLAY = 20001;
    public static final int ACTION_PAUSE = 20002;
    public static final int PLAYLIST_ADD = 20003;
    public static final int PLAYLIST_DELETE = 20004;
    public static final int ACTION_PLAY_FROMPAUSE = 20005;

    private MediaPlayer mPlayer;
    private List<Song> playList;

    private int playingSong;

    private WifiManager.WifiLock wifiLock;
    private Handler mHandler;

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
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnCompletionListener(this);

        wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "cloudmusic lock");
        wifiLock.acquire();

        playList = new ArrayList<>();
        playingSong = 0;
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
        mHandler.obtainMessage(MainActivity.UPDATE_BOTTOM,playList.get(playingSong)).sendToTarget();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.LogE("MusicPlayService onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(playingSong == playList.size() - 1)  playingSong = 0;
        searchForPlay(playList.get(playingSong).getId());
    }

    public class Binder extends android.os.Binder{
        public void setAction(int action,Object obj){
            switch (action){
                case ACTION_PLAY:
                    searchForPlay(playList.get(playingSong).getId());
                    break;
                case ACTION_PAUSE:
                    if(mPlayer.isPlaying()) mPlayer.pause();
                    break;
                case PLAYLIST_ADD:
                    playList.add((Song)obj);
                    playingSong  = playList.size() - 1;
                    break;
                case ACTION_PLAY_FROMPAUSE:
                    mPlayer.start();
                    break;
            }
        }

        public void setHandler(Handler h){
            mHandler = h;
        }

        public MusicPlayService getService(){
            return MusicPlayService.this;
        }
    }

    //查询某个id的歌曲，最重要为获得歌曲地址
    private void searchForPlay(String id){
        LogUtil.LogE("searchID begin");
        Call<DataAndCode> call = CloudMusicApiImpl.searchID(id);
        call.enqueue(new Callback<DataAndCode>() {
            @Override
            public void onResponse(Call<DataAndCode> call, Response<DataAndCode> resp) {
                try {
                    mPlayer.reset();
                    mPlayer.setDataSource(resp.body().getDataUrl());
                    mPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LogUtil.LogE("searchID response : " + resp.body());
            }

            @Override
            public void onFailure(Call<DataAndCode> call, Throwable t) {
                LogUtil.LogE("searchID fail : " + t.getMessage());
            }
        });
        LogUtil.LogE("searchID end");
    }

    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }
}
