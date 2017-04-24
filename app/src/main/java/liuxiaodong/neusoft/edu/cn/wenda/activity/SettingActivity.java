package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.other.AppSettings;
import liuxiaodong.neusoft.edu.cn.wenda.base.IReload;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.service.WenDaNotificationService;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Consts;
import liuxiaodong.neusoft.edu.cn.wenda.utils.SystemUtils;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;
import liuxiaodong.neusoft.edu.cn.wenda.utils.ThemeUtils;

/**
 * Created by DONG on 2016/10/20.
 */

public class SettingActivity extends PreferenceActivity implements
        View.OnClickListener,
        Preference.OnPreferenceChangeListener, IReload, SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = "SettingActivity";
    private Toolbar toolbar;
    private AppCompatDelegate delegate;
    private ListPreference themePreference;
    private ListPreference itemCountsPreference;
    private CheckBoxPreference newAnswerPreference;
    private CheckBoxPreference newQuestionPreference;
    private CheckBoxPreference concernedUpdatePreference;

    private CheckBoxPreference LEDPreference;
    private CheckBoxPreference sendSuccessVibratePreference;

    private Preference cleanCachePreference;



    private int theme = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: setting");
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);

        APP.addActivity(this, this.getClass().getName());

        if (savedInstanceState == null) {
            this.theme = this.configTheme();
        } else {
            this.theme = savedInstanceState.getInt("theme");
        }


        if (this.theme > 0) {
            this.setTheme(this.theme);
        }
        super.onCreate(savedInstanceState);

        //Logger.d(TAG, "onCreate: net state:" + SystemUtils.getNetworkType(APP.getInstance()));

        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("设置");
        addPreferencesFromResource(R.xml.app_preference);

        toolbar.setNavigationOnClickListener(this);
        initPreference();
        EventManager.register(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: onstart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: setting");
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        initSettingData();


    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Event stickEvent) {
        if (stickEvent.message.equals(Event.CLEAN_CACHE_SUCCESS))
        {
            cleanSuccess();
            EventManager.removeAllStickyEvents();
        }
    }

    private void cleanSuccess() {
        T.makeS(SettingActivity.this, "清理成功！");
        initSettingData();

    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
        APP.removeActivity(getClass().getName());
        EventManager.unregister(this);
    }

    private void initSettingData() {
        themePreference.setSummary(getThemeEntry());

        itemCountsPreference.setSummary(AppSettings.getItemCountsString());

        newAnswerPreference.setChecked(AppSettings.isNotifyNewAnswer());
        newQuestionPreference.setChecked(AppSettings.isNotifyNewQuestion());
        LEDPreference.setChecked(AppSettings.isNotifyLED());
        sendSuccessVibratePreference.setChecked(AppSettings.isOnSendSuccessVibrate());

        concernedUpdatePreference.setChecked(AppSettings.isNotifyConcernedQuestionUpdated());

        cleanCachePreference.setSummary(SystemUtils.getCacheSize());



    }

    private CharSequence getThemeEntry() {
        String[] stringArray = getResources().getStringArray(R.array.list_colors);
        return stringArray[AppSettings.getThemeColor()];

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("theme", this.theme);
    }

    private void initPreference() {
        themePreference = (ListPreference) findPreference(Consts.SETTING_KEY_THEME_COLOR);
        itemCountsPreference = (ListPreference) findPreference(Consts.SETTING_KEY_ITEM_COUNTS);
        newAnswerPreference = (CheckBoxPreference) findPreference(Consts.SETTING_KEY_NOTIFY_NEW_ANSWER);
        newQuestionPreference = (CheckBoxPreference) findPreference(Consts.SETTING_KEY_NOTIFY_NEW_QUESTION);
        LEDPreference = (CheckBoxPreference) findPreference(Consts.SETTING_KEY_LED);
        sendSuccessVibratePreference = (CheckBoxPreference) findPreference(Consts.SETTING_KEY_VIBRATE_SEND_SUCCESS);

        concernedUpdatePreference = (CheckBoxPreference) findPreference(Consts.SETTING_KEY_NOTIFY_CONCERNEDQS_UPDATE);
        cleanCachePreference = findPreference(Consts.SETTING_KEY_CLEANCACHE);

        cleanCachePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showCleanDialog();

                return true;
            }
        });




        themePreference.setOnPreferenceChangeListener(this);
    }

    private void showCleanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("确定清理?");
        builder.setMessage("清理将释放" + SystemUtils.getCacheSize() + "空间。");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SystemUtils.cleanCache();
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getDelegate().setContentView(layoutResID);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    private void setSupportActionBar(Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    private AppCompatDelegate getDelegate() {
        if (delegate == null) {
            delegate = AppCompatDelegate.create(this, null);
        }
        return delegate;
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "onPreferenceChange: newValue" + newValue);

        String value = newValue.toString();
        saveTheme(value);
        return true;
    }

    private void saveTheme(String value) {
        int index = 0;
        String[] stringArray = getResources().getStringArray(R.array.list_colors_value);
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i].equals(value)) {
                index = i;
                break;
            }

        }
        AppSettings.setThemeColor(index);
        reload();
        //APP.reloadAllActivity();
        Log.d(TAG, "saveTheme: " + "保存主题index:" + index);
    }


    @Override
    public void reload() {
        Intent intent = this.getIntent();
        this.overridePendingTransition(0, 0);
        intent.addFlags(65536);
        this.finish();
        this.overridePendingTransition(0, 0);
        this.startActivity(intent);

    }

    private int configTheme() {
        int theme = ThemeUtils.themeArr[AppSettings.getThemeColor()];
        return theme;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case Consts.SETTING_KEY_THEME_COLOR:
                themePreference.setSummary(getThemeEntry());
                break;
            case Consts.SETTING_KEY_ITEM_COUNTS:
                itemCountsPreference.setSummary(AppSettings.getItemCountsString());
                break;
            case Consts.SETTING_KEY_NOTIFY_NEW_ANSWER:
                newAnswerPreference.setChecked(AppSettings.isNotifyNewAnswer());
                //重启通知服务
                restartNotifyService();

                break;
            case Consts.SETTING_KEY_NOTIFY_NEW_QUESTION:
                newQuestionPreference.setChecked(AppSettings.isNotifyNewQuestion());
                //重启通知服务
                restartNotifyService();

                break;
            case Consts.SETTING_KEY_LED:
                LEDPreference.setChecked(AppSettings.isNotifyLED());

                break;
            case Consts.SETTING_KEY_VIBRATE_SEND_SUCCESS:
                sendSuccessVibratePreference.setChecked(AppSettings.isOnSendSuccessVibrate());
                break;
            case Consts.SETTING_KEY_NOTIFY_CONCERNEDQS_UPDATE:
                concernedUpdatePreference.setChecked(AppSettings.isNotifyConcernedQuestionUpdated());
                restartNotifyService();
                break;
        }

        Log.d(TAG, "onSharedPreferenceChanged: key:" + key);

    }

    private void restartNotifyService() {
        Intent intent = new Intent(this, WenDaNotificationService.class);
        stopService(intent);

        startService(intent);

    }



}
