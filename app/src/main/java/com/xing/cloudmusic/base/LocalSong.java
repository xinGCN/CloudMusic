package com.xing.cloudmusic.base;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinG on 2018/2/10 0010.
 */

public class LocalSong implements Parcelable{
    private String id;
    private String name;
    private String address;
    private String artistName;
    private String albumName;
    private String albumPicUrl;

    public LocalSong(String id, String name, String address,String artistName,String albumName,String albumPicUrl){
        this.id = id;
        this.name = name;
        this.address = address;
        this.artistName = artistName;
        this.albumName = albumName;
        this.albumPicUrl = albumPicUrl;
    }

    public String getId() {
        return id;
    }

    public String getAddress() { return address; }

    public String getAlbumName() {
        return albumName;
    }

    public String getName() { return name; }

    public String getAlbumPicUrl() {
        return albumPicUrl;
    }

    public String getArtistName() {
        return artistName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        String []strs = new String[]{id,name,address,artistName,albumName,albumPicUrl};
        out.writeStringArray(strs);
    }

    public static final Creator<LocalSong> CREATOR = new Creator<LocalSong>() {
        @Override
        public LocalSong createFromParcel(Parcel source) {
            return new LocalSong(source);
        }

        @Override
        public LocalSong[] newArray(int size) {
            return new LocalSong[size];
        }
    };

    private LocalSong(Parcel in){
        String[] strs = new String[6];
        in.readStringArray(strs);
        id = strs[0];
        name = strs[1];
        address = strs[2];
        artistName = strs[3];
        albumName = strs[4];
        albumPicUrl = strs[5];
    }

    @Override
    public String toString() {
        return "LocalSong{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", artistName='" + artistName + '\'' +
                ", albumName='" + albumName + '\'' +
                ", albumPicUrl='" + albumPicUrl + '\'' +
                '}';
    }

    public Song toSong(){
        List<Artist> artists = new ArrayList<>();
        artists.add(new Artist(artistName,null));
        return new Song(name,id,address,artists,new Album(albumName,albumPicUrl));
    }
}
