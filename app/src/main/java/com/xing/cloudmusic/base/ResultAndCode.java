package com.xing.cloudmusic.base;

import java.util.List;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public class ResultAndCode {
    private final Result result;
    private final int code;

    public ResultAndCode(Result result, int code) {
        this.result = result;
        this.code = code;
    }

    public List<String> getID(){
        return result.getID();
    }

    public List<Song> getSongs(){
        return result.getSongs();
    }
    @Override
    public String toString() {
        return "ResultAndCode{" +
                "result=" + result +
                ", code='" + code + '\'' +
                '}';
    }
}
