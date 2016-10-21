package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.base.BaseActivity;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

/**
 * Created by DONG on 2016/10/20.
 */

public class LoginActivity extends BaseActivity {
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
        showAcitvity(RegisterAcitivity.class);
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
                    finish();
                    showAcitvity(MainActivity.class);
                } else {
                    BmobConn.parseBmobException(e, LoginActivity.this);
                    Log.e(TAG, "done: " + e);
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
        setNavigationOnClick();
    }





}
