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
import com.xing.cloudmusic.util.PlayListLocalManager;
import com.xing.cloudmusic.util.ToastFactory;

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
    public static final int ACTION_NEXT = 20006;
    public static final int ACTION_LAST = 20007;
    public static final int DEAL_DIALOG_ACITON_PLAY = 20008;
    public static final int DOWNLOAD_FINISH = 20009;

    private MediaPlayer mPlayer;
    private List<Song> playList;

    private int playingSong;

    private WifiManager.WifiLock wifiLock;
    private Handler mHandler;

    //缓存音乐信息在本地
    private PlayListLocalManager manager;

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

        manager = PlayListLocalManager.getInstance(this);
        playList = manager.readAll();
        LogUtil.LogE("playlist init : " + playList.toString());
        playingSong = -1;
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
        //if(playingSong == playList.size() - 1)  playingSong = 0;
        if(playingSong != -1){
            playingSong = playingSong==playList.size()-1?0:playingSong+1;
            searchForPlay();
        }
    }

    //因为需要playingSong始终指向正在播放歌曲的index所以需要仔细考虑playingSong的校正问题;
    public class Binder extends android.os.Binder{
        public void setAction(int action,Object obj){
            switch (action){
                case ACTION_PLAY:
                    //当没有正在播放的歌曲而点击播放按钮的情况下，自动校正为当前播放歌曲为歌单第一首
                    searchForPlay();
                    break;
                case ACTION_PAUSE:
                    if(mPlayer.isPlaying()) mPlayer.pause();
                    break;
                case PLAYLIST_ADD:
                    playList.add((Song)obj);
                    playingSong  = playList.size() - 1;
                    manager.save((Song)obj);
                    break;
                case ACTION_PLAY_FROMPAUSE:
                    if(playingSong == -1&&playList.size() > 0){
                        playingSong = 0;
                        searchForPlay();
                    }
                    mPlayer.start();
                    break;
                case ACTION_NEXT:
                    if(playingSong != -1){
                        playingSong = playingSong==playList.size()-1?0:playingSong+1;
                        searchForPlay();
                    }
                    break;
                case ACTION_LAST:
                    if(playingSong != -1){
                        playingSong = playingSong==0?playList.size()-1:playingSong-1;
                        searchForPlay();
                    }
                    break;
                case PLAYLIST_DELETE:
                    LogUtil.LogE("do delete : " + (String)obj);
                    for (Song song: playList) {
                        if(song.getId().equals ((String)obj)){
                            playList.remove(song);
                            break;
                        }
                    }
                    manager.remove((String) obj);
                    break;
                case DEAL_DIALOG_ACITON_PLAY:
                    for (Song song: playList) {
                        if(song.getId().equals((String)obj)){
                            playingSong = playList.indexOf(song);
                            break;
                        }
                    }
                    searchForPlay();
                    break;
                case DOWNLOAD_FINISH:
                    //传来的数据格式为 "&id+&address"
                    String data = (String )obj;
                    for (Song song:playList) {
                        if(song.getId().equals(data.substring(0,data.indexOf("+")))){
                            song.setAddress(data.substring(data.indexOf("+") + 1));
                            break;
                        }
                    }
                   //LogUtil.LogE("id : " + data.substring(0,data.indexOf("+")) + " address : " + data.substring(data.indexOf("+") + 1));
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
    private void searchForPlay() {
        LogUtil.LogE("searchID begin");
        if (playingSong == -1)
            return;
        //本地歌曲播放
        if (playList.get(playingSong).getAddress() != null ) {
            try {
                mPlayer.reset();
                mPlayer.setDataSource(playList.get(playingSong).getAddress());
                mPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { //在线歌曲播放
            Call<DataAndCode> call = CloudMusicApiImpl.searchID(playList.get(playingSong).getId());
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
    }

    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }

    public List<Song> getPlayList() {
        return playList;
    }
}
