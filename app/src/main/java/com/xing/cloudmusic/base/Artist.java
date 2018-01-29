package com.xing.cloudmusic.base;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public class Artist {
    private final String name;
    private final String id;

    public Artist(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
