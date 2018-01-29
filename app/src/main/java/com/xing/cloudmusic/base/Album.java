package com.xing.cloudmusic.base;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public class Album {
    private final String name;
    private final String picUrl;

    public Album(String name, String picUrl) {
        this.name = name;
        this.picUrl = picUrl;
    }

    public String getName() {
        return name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    @Override
    public String toString() {
        return "Album{" +
                "name='" + name + '\'' +
                ", picUrl='" + picUrl + '\'' +
                '}';
    }
}
