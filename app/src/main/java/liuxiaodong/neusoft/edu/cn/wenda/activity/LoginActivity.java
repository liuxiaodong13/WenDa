package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.base.BackNavigationActivity;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.model.Collection;
import liuxiaodong.neusoft.edu.cn.wenda.model.Concern;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.service.WenDaNotificationService;
import liuxiaodong.neusoft.edu.cn.wenda.utils.ActivityHelper;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

/**
 * Created by DONG on 2016/10/20.
 */

public class LoginActivity extends BackNavigationActivity {
    public static final String TAG = "LoginActivity";
    @BindView(R.id.usernamewrapper)
    TextInputLayout textInputLayoutUser;
    @BindView(R.id.passwordwrapper)
    TextInputLayout textInputLayoutPass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildActivity(R.layout.activity_login);
    }


    @OnClick(R.id.btn_register)
    public void onRegisterClick() {
        showActivity(RegisterActivity.class);
    }

    @OnClick(R.id.btn_login)
    public void onClickLogin() {
        hideKeyboard();
        String username = textInputLayoutUser.getEditText().getText().toString().trim();
        String password = textInputLayoutPass.getEditText().getText().toString();
       if (username.equals("")) {
            textInputLayoutUser.setError("请输入用户名!");
        } else if (password.equals("")) {
            textInputLayoutPass.setError("请输入密码!");
        }
        else {
            textInputLayoutUser.setErrorEnabled(false);
            textInputLayoutPass.setErrorEnabled(false);
            doLogin(username, password);
        }
    }

    private void doLogin(String username, String password) {
        BmobConn.login(username, password, new SaveListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e == null) {
                    T.makeS(LoginActivity.this, "登陆成功!");
                    APP.getInstance().initLoginState();
//                    EventBusFactory.START.getBus().postSticky(new Event(Event.LOGIN_SUCCESS));
                    prepareData();
                    EventManager.postSticky(new Event(Event.LOGIN_SUCCESS));


                    finish();
                    showActivity(MainActivity.class);
                    //启动服务

                    startService(new Intent(LoginActivity.this, WenDaNotificationService.class));
                } else {
                    BmobConn.parseBmobException(e, LoginActivity.this);
                    Log.e(TAG, "done: " + e);
                }
            }
        });

    }

    private void prepareData() {
        BmobConn.queryConcernId(getCurrentUser().getObjectId(), new FindListener<Concern>() {
            @Override
            public void done(List<Concern> list, BmobException e) {
                if (e == null) {
                    Logger.d("数据concernId准备成功!");
                    ActivityHelper.putShareData(activity, ActivityHelper.CONCERN_ID, list.get(0).getObjectId());
                }else{
                    Logger.e("数据concernId准备失败！");
                }

            }
        });

        BmobConn.queryCollectIdOnly(getCurrentUser().getObjectId(), new FindListener<Collection>() {
            @Override
            public void done(List<Collection> list, BmobException e) {
                if (e == null) {
                    Logger.d("数据CollectId准备成功!");
                    ActivityHelper.putShareData(activity, ActivityHelper.COLLECT_ID, list.get(0).getObjectId());
                }else{
                    Logger.e("数据CollectId准备失败！");
                }
            }
        });
    }

    @Override
    protected void initView() {

        textInputLayoutUser.setHint(getString(R.string.username));
        textInputLayoutPass.setHint(getString(R.string.password));

    }

    @Override
    protected void initToolbar() {

        toolbar.setTitle(getString(R.string.login));
        setSupportActionBar(toolbar);

    }





}
