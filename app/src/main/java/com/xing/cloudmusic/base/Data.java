package com.xing.cloudmusic.base;

/**
 * Created by Administrator on 2018/1/17 0017.
 * "id":421563712,
 "url":"https://m8.music.126.net/20180117222809/7ebd35a79b725cb1c007ccb5cf891acd/ymusic/32a7/4793/ab08/739cea45a4cdf1ce264220afe3467b49.mp3",
 "br":128000,
 "size":4211400,
 "md5":"739cea45a4cdf1ce264220afe3467b49",
 "code":200,
 "expi":1200,
 */

public class Data {
    private final String id;
    private final String url;
    private final String br;
    private final String size;
    private final String type;

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", br='" + br + '\'' +
                ", size='" + size + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public Data(String id, String url, String br, String size, String type) {
        this.id = id;
        this.url = url;
        this.br = br;
        this.size = size;
        this.type = type;
    }
}
