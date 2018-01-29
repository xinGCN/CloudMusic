package com.xing.cloudmusic.base;

import java.util.List;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public class DataAndCode {
    private final List<Data> data;
    private final int code;

    public DataAndCode(List<Data> data, int code) {
        this.data = data;
        this.code = code;
    }

    public String getDataUrl(){
        if(data!=null||data.size()>0) return data.get(0).getUrl();
        return null;
    }

    public String getDataType(){
        if(data!=null||data.size()>0) return data.get(0).getType();
        return null;
    }

    public String getDataId(){
        if(data!=null||data.size()>0) return data.get(0).getId();
        return null;
    }

    @Override
    public String toString() {
        return "DataAndCode{" +
                "data=" + (data==null?"null":data.toString()) +
                ", code=" + code +
                '}';
    }
}
