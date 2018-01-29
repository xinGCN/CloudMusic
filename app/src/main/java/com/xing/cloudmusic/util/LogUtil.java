package com.xing.cloudmusic.util;

import android.util.Log;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public class LogUtil {
    private static final String TAG = "xinG";

    private static final boolean needLog = true;

    public static void LogE(String message){
        if(needLog)
            Log.e(TAG,message);
    }

    public static void LogD(String message){
        if(needLog)
            Log.d(TAG,message);
    }

    public static void LogI(String message){
        if(needLog)
            Log.i(TAG,message);
    }

    public static void LogV(String message){
        if(needLog)
            Log.v(TAG,message);
    }

    public static void LogW(String message){
        if(needLog)
            Log.w(TAG,message);
    }


}
