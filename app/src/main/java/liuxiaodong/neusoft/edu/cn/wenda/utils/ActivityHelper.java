package liuxiaodong.neusoft.edu.cn.wenda.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by DONG on 2017/2/10.
 */

public class ActivityHelper {
    public static final String APP_CONFIG = "liuxiaodong.neusoft.edu.cn.config";

    public static final String CONCERN_ID = "concernId";
    public static final String COLLECT_ID = "collectId";

    private ActivityHelper() {
    }

    public static String getShareData(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(APP_CONFIG, 0);
        return sp.getString(key, "");
    }

    public static String getShareData(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(APP_CONFIG, 0);
        return sp.getString(key, defValue);
    }

    public static int getIntShareData(Context context, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(APP_CONFIG, 0);
        return sp.getInt(key, defValue);
    }

    public static int getIntShareData(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(APP_CONFIG, 0);
        return sp.getInt(key, 0);
    }

    public static boolean getBooleanShareData(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(APP_CONFIG, 0);
        return sp.getBoolean(key, false);
    }

    public static boolean getBooleanShareData(Context context, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(APP_CONFIG, 0);
        return sp.getBoolean(key, defValue);
    }

    public static void putShareData(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(APP_CONFIG, 0);
        SharedPreferences.Editor et = sp.edit();
        et.putString(key, value);
        et.commit();
    }

    public static void putIntShareData(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(APP_CONFIG, 0);
        SharedPreferences.Editor et = sp.edit();
        et.putInt(key, value);
        et.commit();
    }

    public static void putBooleanShareData(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(APP_CONFIG, 0);
        SharedPreferences.Editor et = sp.edit();
        et.putBoolean(key, value);
        et.commit();
    }

    public static void putSetShareData(Context context, String key, Set<String> value) {
        SharedPreferences sp = context.getSharedPreferences(APP_CONFIG, 0);
        SharedPreferences.Editor et = sp.edit();
        et.putStringSet(key, value);
        et.commit();
    }

    public static Set<String> getSetShareData(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(APP_CONFIG, 0);
        return sp.getStringSet(key, new HashSet());
    }
}
