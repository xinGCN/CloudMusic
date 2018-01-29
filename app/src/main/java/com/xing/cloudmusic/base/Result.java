package com.xing.cloudmusic.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public class Result {
    private final List<Song> songs;
    private final int songCount;

    public Result(List<Song> songs, int songCount) {
        this.songs = songs;
        this.songCount = songCount;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public List<String> getID(){
        List<String> ids = null;
        if(songs != null){
            ids = new ArrayList<>();
            for (Song song:songs) {
                ids.add(song.getId());
            }
        }
        return ids;
    }

    @Override
    public String toString() {
        return "Result{" +
                "songs=" + (songs==null?"null":songs.toString()) +
                ", songCount=" + songCount +
                '}';
    }
}
