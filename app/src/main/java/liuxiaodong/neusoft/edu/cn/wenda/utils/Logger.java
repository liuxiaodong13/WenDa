package liuxiaodong.neusoft.edu.cn.wenda.utils;

import android.content.Context;
import android.util.Log;


import liuxiaodong.neusoft.edu.cn.wenda.R;

/**
 * Created by DONG on 2017/3/11.
 */

public class Logger {
    private static boolean isDebug = true;
    private static  String TAG = "Wenda";

    private Logger(){
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void init(Context context) {
        TAG = context.getResources().getString(R.string.app_name);
    }

    // 下面四个是默认tag的函数
    public static void i(String msg)
    {
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg)
    {
        if (isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg)
    {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String msg)
    {
        if (isDebug)
            Log.v(TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void e(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void v(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }
}
