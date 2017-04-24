package liuxiaodong.neusoft.edu.cn.wenda.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;

/**
 * Created by DONG on 2016/12/28.
 */

public class AppUtils {
    public static final String TAG = "AppUtils";
    public static boolean isLogin() {
        return MyUser.getCurrentUser(MyUser.class) != null;
    }

    public static String getCurrentUserId() {
        return MyUser.getCurrentUser(MyUser.class).getObjectId();
    }

    public static MyUser getCurrentUser() {
        return MyUser.getCurrentUser(MyUser.class);
    }

    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat(DefaultValue.TIME_PATTERN);
        //DateFormat dateInstance = SimpleDateFormat.getDateInstance();

        Date date = new Date(System.currentTimeMillis());
        Log.d(TAG, "getCurrentTime: " + format.format(date));
        return format.format(date);
    }

    public static void newActivity(Context context, Intent intent) {

        context.startActivity(intent);
    }
}
