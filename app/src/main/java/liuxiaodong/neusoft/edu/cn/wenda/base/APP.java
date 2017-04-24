package liuxiaodong.neusoft.edu.cn.wenda.base;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.bmob.v3.Bmob;
import liuxiaodong.neusoft.edu.cn.wenda.db.MySQLiteHelper;
import liuxiaodong.neusoft.edu.cn.wenda.other.AppSettings;
import liuxiaodong.neusoft.edu.cn.wenda.service.WenDaNotificationService;
import liuxiaodong.neusoft.edu.cn.wenda.utils.ActivityHelper;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Consts;
import liuxiaodong.neusoft.edu.cn.wenda.utils.DefaultValue;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;

import static liuxiaodong.neusoft.edu.cn.wenda.utils.AppUtils.getCurrentUser;
import static liuxiaodong.neusoft.edu.cn.wenda.utils.AppUtils.isLogin;

/**
 * Created by DONG on 2016/10/25.
 */

public class APP extends Application {

    public static boolean isLogin = false;


    public static final String TAG = "APP";

    private static Map<String, Activity> runningActivities = new HashMap<>();
    private MySQLiteHelper sqLiteHelper;

    private static APP app;
    Handler mHandler = new Handler() {
    };

    public Handler getHandler() {
        return mHandler;
    }

    public static void addActivity(Activity activity, String activityName) {
        runningActivities.put(activityName, activity);
    }

    public static void destroyActivity() {
        Set<String> keySet = runningActivities.keySet();
        for (String s : keySet) {
            runningActivities.get(s).finish();
        }
    }

    public static void removeActivity(String activityName) {
        runningActivities.remove(activityName);
    }

    public static void reloadAllActivity() {
        Set<String> keySet = runningActivities.keySet();
        for (String s : keySet) {
            Activity activity = runningActivities.get(s);
            if (activity instanceof IReload) {
                IReload reload = (IReload) activity;
                reload.reload();
            }

        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Log.d(TAG, "onCreate: APP");
        init();
    }

    private void init() {
        initBmob();
        initLoginState();
        initTools();


        if (isLogin())
            initService();
        initDB();
    }


    public void initLoginState() {
        isLogin = isLogin();

    }

    private void initTools() {
        Logger.init(this);
    }

    private void initDB() {
        sqLiteHelper = new MySQLiteHelper(this, DefaultValue.DB_NAME, null, 1);

    }

    public MySQLiteHelper getSqLiteHelper() {
        return sqLiteHelper;
    }

    private void initService() {
        boolean notifyNewAnswer = AppSettings.isNotifyNewAnswer();
        boolean notifyNewQuestion = AppSettings.isNotifyNewQuestion();
        if (notifyNewAnswer || notifyNewQuestion) {
            Intent intent = new Intent(this, WenDaNotificationService.class);
            startService(intent);
        }
    }

    private void initBmob() {
        Bmob.initialize(this, Consts.APP_KEY);
    }

    public static APP getInstance() {
        return app;
    }

    public static String getUserConcernId() {
        return ActivityHelper.getShareData(getInstance(), ActivityHelper.CONCERN_ID);
    }

    public static String getUserCollectId() {
        String data = ActivityHelper.getShareData(getInstance(), ActivityHelper.COLLECT_ID);
        Logger.d("getUserCollectId()："  + data);
        return data;
    }

    public static String getUserName() {
        return getCurrentUser().getNick();
    }


}
