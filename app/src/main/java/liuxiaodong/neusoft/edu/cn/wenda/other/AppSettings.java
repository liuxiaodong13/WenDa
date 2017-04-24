package liuxiaodong.neusoft.edu.cn.wenda.other;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.utils.ActivityHelper;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Consts;
import liuxiaodong.neusoft.edu.cn.wenda.utils.SystemUtils;

/**
 * Created by DONG on 2017/2/10.
 */

public class AppSettings {
    /**
     * 声音提醒
     *
     * @return
     */
    public static boolean isNotifySound() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(APP.getInstance());
        return prefs.getBoolean("pNotifySound", true);
    }

    /**
     * 返回设置的显示数据条目的数量
     *
     * @return
     */
    public static int getListItemCounts() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(APP.getInstance());
        String count = prefs.getString(Consts.SETTING_KEY_ITEM_COUNTS, "-1");
        if (count.contains("自动")) {
            int counts = 10;
            if (SystemUtils.getNetworkType(APP.getInstance()) == SystemUtils.NetWorkType.wifi) {
                counts = 30;
            } else if (SystemUtils.getNetworkType(APP.getInstance()) == SystemUtils.NetWorkType.mobile) {
                counts = 10;
            }
            return counts;

        }
        return Integer.parseInt(count);
    }

    public static String getItemCountsString() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(APP.getInstance());
        return prefs.getString(Consts.SETTING_KEY_ITEM_COUNTS, "-1");

    }

    public static boolean isNotifyNewAnswer() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(APP.getInstance());
        return prefs.getBoolean(Consts.SETTING_KEY_NOTIFY_NEW_ANSWER, true);
    }

    public static boolean isNotifyNewQuestion() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(APP.getInstance());
        return prefs.getBoolean(Consts.SETTING_KEY_NOTIFY_NEW_QUESTION, true);
    }

    public static boolean isNotifyConcernedQuestionUpdated() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(APP.getInstance());
        return prefs.getBoolean(Consts.SETTING_KEY_NOTIFY_CONCERNEDQS_UPDATE, true);
    }


    public static boolean isOnSendSuccessVibrate() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(APP.getInstance());
        return prefs.getBoolean(Consts.SETTING_KEY_VIBRATE_SEND_SUCCESS, true);
    }

    /**
     * LED提醒
     *
     * @return
     */
    public static boolean isNotifyLED() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(APP.getInstance());
        return prefs.getBoolean(Consts.SETTING_KEY_LED, true);
    }

    /**
     * 振动提醒
     *
     * @return
     */
    public static boolean isNotifyVibrate() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(APP.getInstance());
        return prefs.getBoolean("pNotifyVibrate", true);
    }


    public static int getThemeColor() {
        return ActivityHelper.getIntShareData(APP.getInstance(), "Theme_index", 0);
    }

    public static void setThemeColor(int themeIndex) {
        ActivityHelper.putIntShareData(APP.getInstance(), "Theme_index", themeIndex);
    }



   /* *//**
     * 提问加载数量
     *
     * @return
     *//*
    public static int getTimelineCount() {
//        if (true) return 5;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(APP.getInstance());
        int index = Integer.parseInt(prefs.getString("pTimelineCount", "0"));

        int count = countArr[0];
        if (index == 3 && APP.getInstance() != null) {
            if (SystemUtils.getNetworkType(APP.getInstance()) == SystemUtils.NetWorkType.wifi)
                count = 100;
        }
        else {
            count = countArr[index];
        }

        return count;
    }

    *//**
     * 评论加载数量
     *
     * @return
     *//*
    public static int getCommentCount() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(APP.getInstance());
        int index = Integer.parseInt(prefs.getString("pCommentCount", "0"));

        int count = 50;
        if (index == 3 && APP.getInstance() != null) {
            if (SystemUtils.getNetworkType(APP.getInstance()) == SystemUtils.NetWorkType.wifi)
                count = 100;
        }
        else {
            count = countArr[index];
        }

        return count;
    }*/
}
