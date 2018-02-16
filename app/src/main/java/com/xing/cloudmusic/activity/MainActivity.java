package com.xing.cloudmusic.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xing.cloudmusic.R;
import com.xing.cloudmusic.adapter.CmAdapter;
import com.xing.cloudmusic.base.DataAndCode;
import com.xing.cloudmusic.base.LocalSong;
import com.xing.cloudmusic.base.ResultAndCode;
import com.xing.cloudmusic.base.Song;
import com.xing.cloudmusic.customcontrol.PlayListDialog;
import com.xing.cloudmusic.http.CloudMusicApiImpl;
import com.xing.cloudmusic.service.MusicPlayService;
import com.xing.cloudmusic.util.LogUtil;
import com.xing.cloudmusic.util.PlayListLocalManager;
import com.xing.cloudmusic.util.ToastFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, ServiceConnection {
    private EditText s;
    private ListView show;
    private Button playAndPause;
    private RelativeLayout bottom;
    private GestureDetector myGD;

    private CmAdapter adapter;
    private ImageView bottomImage;
    private TextView bottmText1;
    private TextView bottmText2;

    private PlayListDialog plDialog;

    private static final int SEARCHS_BACK = 10000;
    private static final int SEARCHID_BACK = 10001;
    private static final int GO_DOWNLOAD = 10002;
    public static final int DIALOG_DOWNLOAD_MUSIC = 10003;
    public static final int UPDATE_BOTTOM = 10004;
    public static final int DELETE_PLAYLISTITEM = 10005;
    public static final int DIALOG_ACTION_PLAY = 10006;
    public static final int DIALOG_START_LOCALACTIVITY = 10007;

    private MusicPlayService.Binder mBinder;
    private Intent musicPlayService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.havetry);
        s = findViewById(R.id.editText);
        show = findViewById(R.id.listView);
        show.setOnItemClickListener(this);
        playAndPause = findViewById(R.id.playAndPause);
        bottomImage = findViewById(R.id.bottom_image);
        bottmText1 = findViewById(R.id.bottom_text1);
        bottmText2 = findViewById(R.id.bottom_text2);
        bottom = findViewById(R.id.bottom);

        myGD = new GestureDetector(this,new CloudMusicGestureDetector());


        bottom.setClickable(true);
        bottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                myGD.onTouchEvent(event);
                return true;
            }
        });

        plDialog = new PlayListDialog(MainActivity.this,mHandler);

    }


    @Override
    protected void onStart() {
        LogUtil.LogE("MainActivity onStart musicPlayService start");
        initMusicPlayService();
        super.onStart();
    }

    //初始化服务和绑定
    private void initMusicPlayService() {
        musicPlayService = new Intent(MainActivity.this,MusicPlayService.class);
        startService(musicPlayService);
        bindService(musicPlayService,this, Context.BIND_AUTO_CREATE);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEARCHS_BACK:
                    //查询searchS返回
                    LogUtil.LogE("handler SEARCHS_BACK");
                    adapter = new CmAdapter(MainActivity.this,(ResultAndCode) msg.obj);
                    show.setAdapter(adapter);
                    break;
                case SEARCHID_BACK:
                    //查询searchID返回
                    LogUtil.LogE("handler SEARCHID_BACK");
                    DataAndCode adc = (DataAndCode) msg.obj;
                    mBinder.setAction(MusicPlayService.ACTION_PLAY,adc.getDataUrl());
                    break;
                case DIALOG_DOWNLOAD_MUSIC:
                    //下载完成
                    Song s = (Song)msg.obj;
                    if(s.getAddress() != null)
                        ToastFactory.show(MainActivity.this,"该歌曲为本地歌曲。");
                    else
                        searchIDForDownload(s.getId(),s.getArtistName() + " - " + s.getName());
                    break;
                case UPDATE_BOTTOM:
                    //更新播放按钮图标，并且加载当前播放歌曲的信息
                    playAndPause.setBackgroundResource(R.mipmap.pause_btn);
                    Song song = (Song) msg.obj;
                    Picasso.with(MainActivity.this)
                            .load(song.getAlbumPicUrl())
                            .into(bottomImage);
                    bottmText1.setText(song.getName());
                    bottmText2.setText(song.getArtistName());
                    break;
                case DELETE_PLAYLISTITEM:
                    mBinder.setAction(MusicPlayService.PLAYLIST_DELETE,msg.obj);
                    plDialog.invalidate();
                    break;
                case DIALOG_ACTION_PLAY:
                    mBinder.setAction(MusicPlayService.DEAL_DIALOG_ACITON_PLAY,msg.obj);
                    break;
                case DIALOG_START_LOCALACTIVITY:
                    Intent intent = new Intent(MainActivity.this,LocalSongActivity.class);
                    startActivityForResult(intent,1);
                    break;
            }
        }
    };

    public void search(View view){
        LogUtil.LogE("search begin");
        searchS(s.getText().toString());
        LogUtil.LogE("search end");
//        if(mBinder != null) mBinder.setAction(MusicPlayService.ACTION_PLAY,Environment.getExternalStorageDirectory()+"/怪不得.mp3");
//        LogUtil.LogE("search " + (mBinder ==null?"null":" not null"));
    }

    //查询某个歌曲的信息，最重要为id信息
    private void searchS(String s){
        LogUtil.LogE("searchS begin");
        Call<ResultAndCode> call = CloudMusicApiImpl.searchS(s);
        call.enqueue(new Callback<ResultAndCode>() {
            @Override
            public void onResponse(Call<ResultAndCode> call, Response<ResultAndCode> resp) {
                mHandler.obtainMessage(SEARCHS_BACK,resp.body()).sendToTarget();
                LogUtil.LogE("searchS response : " + resp.body());
            }

            @Override
            public void onFailure(Call<ResultAndCode> call, Throwable t) {
                LogUtil.LogE("searchS fail : " + t.getMessage());
            }
        });
        LogUtil.LogE("searchS end");
    }

    //查询某个id的歌曲，最重要为获得歌曲地址
    private void searchIDForDownload(final String id, final String name){
        LogUtil.LogE("searchID begin");
        ToastFactory.show(MainActivity.this,"下载开始，不要瞎鸡巴多按很多次！");
        Call<DataAndCode> call = CloudMusicApiImpl.searchID(id);
        call.enqueue(new Callback<DataAndCode>() {
            @Override
            public void onResponse(Call<DataAndCode> call, Response<DataAndCode> resp) {
                downloadFile(resp.body().getDataUrl(),name + "." +resp.body().getDataType(),id);
                LogUtil.LogE("searchID response : " + resp.body());
            }

            @Override
            public void onFailure(Call<DataAndCode> call, Throwable t) {
                LogUtil.LogE("searchID fail : " + t.getMessage());
            }
        });
        LogUtil.LogE("searchID end");
    }

    private void downloadFile(String url, final String name, final String id){
        LogUtil.LogE("download begin");
        Call<ResponseBody> call = CloudMusicApiImpl.downloadFile(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> resp) {
                LogUtil.LogE("on Response start");
                try {
                    File path = new File(Environment.getExternalStorageDirectory()+"/xinGCloudMusic/");
                    if(!path.exists())
                        path.mkdirs();
                    File file = new File(Environment.getExternalStorageDirectory()+"/xinGCloudMusic/" + name);
                    OutputStream os = new FileOutputStream(file);
                    InputStream is = resp.body().byteStream();
                    byte[] bytes = new byte[1024];
                    int len;
                    while(( len = is.read(bytes) )!= -1 )
                        os.write(bytes,0,len);

                    is.close();
                    os.close();
                    mBinder.setAction(MusicPlayService.DOWNLOAD_FINISH,id + "+"+file.getPath());
                    LogUtil.LogE("download end");
                    ToastFactory.show(MainActivity.this,"download finish");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    LogUtil.LogE("file not found" + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.LogE("io");
                }

                LogUtil.LogE("on Response end");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.LogE("on failure");
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Song song = (Song)show.getItemAtPosition(position);
        mBinder.setAction(MusicPlayService.PLAYLIST_ADD,song);
        mBinder.setAction(MusicPlayService.ACTION_PLAY,null);
    }

    @Override
    protected void onDestroy() {
        unbindService(this);
        stopService(musicPlayService);
        super.onDestroy();
    }

    //当服务连接上时启用,功能为初始化通信用binder
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mBinder = (MusicPlayService.Binder) service;
        mBinder.setHandler(mHandler);
        LogUtil.LogE("bind success");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
         LogUtil.LogE("bind fail");
    }

    public void test(View view){
        LocalSong ls = new LocalSong("id","name","address","artistname","albumName","albumPicUrl");
        LogUtil.LogE("1 : "+ls.toSong().toString());

        mBinder.setAction(MusicPlayService.PLAYLIST_ADD,ls.toSong());
    }

    //播放按钮点击事件
    public void playAndPause(View view){
        if(mBinder.getService().isPlaying()){
            mBinder.setAction(MusicPlayService.ACTION_PAUSE,null);
            playAndPause.setBackgroundResource(R.mipmap.play_btn);
        }else{
            mBinder.setAction(MusicPlayService.ACTION_PLAY_FROMPAUSE,null);
            playAndPause.setBackgroundResource(R.mipmap.pause_btn);
        }
    }

    //列表按钮点击事件
    public void showPlayList(View view){
        List<Song> playList = mBinder.getService().getPlayList();
        plDialog.setAdapter(playList);
        plDialog.show();

    }

    //处理bottom控件的滑动事件
    class CloudMusicGestureDetector extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            DisplayMetrics dm = new DisplayMetrics();
            MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
            if(e2.getX() - e1.getX() > dm.widthPixels/4){
                //next song
                mBinder.setAction(MusicPlayService.ACTION_NEXT,null);
            }else if(e1.getX() - e2.getX() > dm.widthPixels/4){
                //last song
                mBinder.setAction(MusicPlayService.ACTION_LAST,null);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&requestCode==1){
            ArrayList<LocalSong> localSongs = data.getParcelableArrayListExtra(LocalSongActivity.key);
            for (LocalSong l:localSongs) {
                //把歌曲添加进播放列表
                mBinder.setAction(MusicPlayService.PLAYLIST_ADD,l.toSong());
            }
        }
        plDialog.invalidate();
    }
}
