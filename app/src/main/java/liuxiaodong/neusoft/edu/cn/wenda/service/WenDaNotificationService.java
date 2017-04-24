package liuxiaodong.neusoft.edu.cn.wenda.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.ValueEventListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.activity.MainActivity;
import liuxiaodong.neusoft.edu.cn.wenda.other.AppSettings;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.model.Notification;
import liuxiaodong.neusoft.edu.cn.wenda.other.NotificationGenerator;
import liuxiaodong.neusoft.edu.cn.wenda.utils.AppUtils;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Consts;

public class WenDaNotificationService extends Service implements ValueEventListener, NotificationGenerator.onCompleteGenerateListener {
    public static final String TAG = "NotificationService";
    private String currentUserId;
    private BmobRealTimeData realTimeData;
    private NotificationGenerator generator;
    private final boolean notifyNewAnswer;
    private final boolean notifyNewQuestion;

    public WenDaNotificationService() {
        currentUserId = MyUser.getCurrentUser(MyUser.class).getObjectId();
        notifyNewAnswer = AppSettings.isNotifyNewAnswer();
        notifyNewQuestion = AppSettings.isNotifyNewQuestion();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: 启动NotificationService");
        init();
    }

    private void init() {
        Bmob.initialize(this, Consts.APP_KEY);
        generator = new NotificationGenerator(this);
        realTimeData = new BmobRealTimeData();
        if (AppUtils.isLogin())
            realTimeData.start(this);
    }

    @Override
    public void onDestroy() {
        //取消监听
        if (notifyNewQuestion)
            realTimeData.unsubTableUpdate(Consts.QUESTION_TABLE);
//        if (notifyNewAnswer)
            realTimeData.unsubTableUpdate(Consts.ANSWER_TABLE);
        super.onDestroy();
    }

    @Override
    public void onConnectCompleted(Exception e) {
        Log.d(TAG, "连接成功:" + realTimeData.isConnected());
        if (realTimeData.isConnected()) {
            //监听相关表更新
//            if (notifyNewAnswer)
            realTimeData.subTableUpdate(Consts.ANSWER_TABLE); //监听answer表
            if (notifyNewQuestion)
            realTimeData.subTableUpdate(Consts.QUESTION_TABLE);
            //realTimeData.subTableUpdate(Consts.CONCERN_TABLE);
        }
    }

    @Override
    public void onDataChange(JSONObject jsonObject) {
        Log.d(TAG, "onDataChange: ");
        Log.d(TAG, "(" + jsonObject.optString("action") + ")" + "数据：" + jsonObject);
        generator.generate(jsonObject.toString());

    }

    @Override
    public void onCompleteGenerate(Notification notification) {
        Log.d(TAG, "onCompleteGenerate: " + notification);
        //成功筛选出 通知的消息   进行弹窗通知和后台同步更新通知表

        //1.同步 后台的通知 表
        syncCloudTable(notification);

        //2.弹出Notification通知
        displayNotification(notification);
    }

    private void displayNotification(Notification notification) {

        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(this);
        builder.setContentTitle(notification.getTitle());
        builder.setContentText(notification.getContent());
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);

//        builder.setAutoCancel(true);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(MainActivity.ACTION_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(notification.getType(), applySetting(builder));

    }

    private android.app.Notification applySetting(NotificationCompat.Builder builder) {
        android.app.Notification notification = builder.build();

        if (AppSettings.isNotifyLED())
        {
            notification.ledARGB = Color.GREEN;
            notification.ledOnMS = 1000;
            notification.ledOffMS = 1000;
            notification.flags = android.app.Notification.FLAG_SHOW_LIGHTS;
        }
        return notification;
    }

    private void syncCloudTable(Notification notification) {

        notification.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.d(TAG, "done: " + s);
                } else {
                    Log.e(TAG, "done: " + s + e);
                }
            }
        });


    }
}
