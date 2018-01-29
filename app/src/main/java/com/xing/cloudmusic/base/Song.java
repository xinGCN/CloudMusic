package com.xing.cloudmusic.base;

import java.util.List;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public class Song {
    private final String name;
    private final String id;
    private final List<Artist> ar;
    private final Album al;

    public Song(String name, String id, List<Artist> ar, Album al) {
        this.name = name;
        this.id = id;
        this.ar = ar;
        this.al = al;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlbumName(){
        if(al != null) return al.getName();
        return null;
    }

    public String getAlbumPicUrl(){
        if(al != null) return al.getPicUrl();
        return null;
    }


    public String getArtistName(){
        if(ar != null&&ar.size() > 0){
            return ar.get(0).getName();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Song{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", ar=" + (ar==null?"null":ar.toString()) +
                ", al=" + al +
                '}';
    }
}
