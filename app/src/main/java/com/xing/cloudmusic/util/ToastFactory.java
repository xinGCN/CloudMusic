package com.xing.cloudmusic.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public class ToastFactory {
    private static Toast toast;

    public static void show(Context context, String s) {
        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        } else {
            toast.setText(s);
        }
        toast.show();
    }
}
