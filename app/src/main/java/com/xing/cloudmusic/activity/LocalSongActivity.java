package com.xing.cloudmusic.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.xing.cloudmusic.R;
import com.xing.cloudmusic.adapter.LocalListAdapter;
import com.xing.cloudmusic.base.LocalSong;
import com.xing.cloudmusic.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinG on 2018/2/10 0010.
 */

public class LocalSongActivity extends Activity {
    private ListView listView;
    private LocalListAdapter adapter;
    public static final String key = "取钱暗号";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localshow);
        listView = findViewById(R.id.localList);
        adapter = new LocalListAdapter(this,getLocalSongs());
        listView.setAdapter(adapter);
    }

    //返回到Dialog界面
    public void back(View view){
        Intent intent = new Intent();
        ArrayList<LocalSong> localSongs = new ArrayList<>();
        for(int i = 0 ; i < adapter.getCount() ;i++){
            if(listView.isItemChecked(i)){
                localSongs.add((LocalSong) adapter.getItem(i));
            }
        }
        intent.putParcelableArrayListExtra(key,localSongs);
        setResult(1,intent);
        finish();
    }

    //全选按钮的判定
    public void chooseAll(View view){
        Button choose = (Button)view;
        if(choose.getText().equals("全选")){
            for(int i = 0 ; i < adapter.getCount() ; i++)
                listView.setItemChecked(i, true);
            choose.setText("取消全选");
        }else{
            for(int i = 0 ; i < adapter.getCount() ; i++)
                listView.setItemChecked(i, false);
            choose.setText("全选");
        }

    }


    //查询本地歌曲信息
    private List<LocalSong> getLocalSongs(){
        List<LocalSong> localSongs = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            // query failed, handle error.
        } else if (!cursor.moveToFirst()) {
            // no media on the device
        } else {
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);//音乐标题
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);//音乐id
            int dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);//文件路径
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int isMusic = cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC); // 是否为音乐
            do {
                long thisId = cursor.getLong(idColumn);
                String thisTitle = cursor.getString(titleColumn);
                String thisData = cursor.getString(dataColumn);
                String thisArtist = cursor.getString(artistColumn);
                String thisAlbum = cursor.getString(albumColumn);
                int thisIsMusic = cursor.getInt(isMusic);

                if(thisIsMusic == 1){
                    //此处的id为歌曲在本地的id，所有的歌曲都有一个本地文件id
                    //但是Song类的id为网易云提供的歌曲id，所以加l去重
                    LocalSong song = new LocalSong("l"+thisId,thisTitle,thisData,thisArtist,thisAlbum,null);
                    localSongs.add(song);
                }
                // ...process entry...
                //LogUtil.LogE(thisId + " " + thisTitle + " " + " " + thisArtist + " " + thisAlbum + " " + thisIsMusic + " "  + thisData);
            } while (cursor.moveToNext());
        }

        return localSongs;
    }

}
