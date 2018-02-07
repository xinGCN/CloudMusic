package com.xing.cloudmusic.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.xing.cloudmusic.base.Song;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by xinG on 2018/2/6 0006.
 * 本类用于管理playList的信息缓存
 */

public class PlayListLocalManager {
    private static PlayListLocalManager manager;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private PlayListLocalManager (Context context){
        preferences = context.getSharedPreferences("cloudmusic",Context.MODE_PRIVATE);
        editor=preferences.edit();
    }

    public static PlayListLocalManager getInstance(Context context){
        if(manager == null) manager = new PlayListLocalManager(context);
        return manager;
    }

    public void save(Song song){
        if(contain(song)) return;
        editor.putString(song.getId() ,new Gson().toJson(song));
        editor.commit();
    }

    public void saveAll(List<Song> songs){
        for (Song song:songs) save(song);
    }

    public List<Song> readAll(){
        List<Song> songs = new ArrayList<>();
        Gson gson = new Gson();
        Collection<?> values = preferences.getAll().values();
        for (Object obj: values) {
            songs.add(gson.fromJson((String)obj,Song.class));
        }
        return songs;
    }

    private boolean contain(Song song){
        return preferences.getString(song.getId(),null)!=null;
    }

    public void remove(String songId){
        editor.remove(songId);
    }

}

