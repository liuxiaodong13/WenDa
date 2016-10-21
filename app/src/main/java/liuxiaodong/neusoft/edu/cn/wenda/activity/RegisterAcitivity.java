package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClientMiddleware;
import com.koushikdutta.async.http.socketio.EventEmitter;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.base.BaseActivity;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.utils.StringUtils;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

/**
 * Created by DONG on 2016/10/21.
 */

public class RegisterAcitivity extends BaseActivity {
    public static final String TAG = "RegisterAcitivity";
    @BindView(R.id.til_username)
    TextInputLayout tilUsername;
    @BindView(R.id.til_email)
    TextInputLayout tilEmail;
    @BindView(R.id.til_pwd)
    TextInputLayout tilPassword;
    @BindView(R.id.til_confirm_pwd)
    TextInputLayout tilConfirmPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildActivity(R.layout.activity_register);


    }

    @OnClick(R.id.btn_login)
    public void onLoginClick() {
        showAcitvity(LoginActivity.class);

    }

    @OnClick(R.id.btn_register)
    public void onRegisterClick() {
        String username = tilUsername.getEditText().getText().toString().trim();
        String email = tilEmail.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();
        String confirmPwd = tilConfirmPwd.getEditText().getText().toString();
        if (username.equals("")) {
            tilUsername.setError("请输入用户名!");
        } else if (email.equals("")) {
            tilEmail.setError("请输入邮箱!");
        } else if (!StringUtils.validateEmail(email)) {
            tilEmail.setError("请输入正确的邮箱!");
        }
        else if (password.equals("")) {
            tilPassword.setError("请输入密码!");
        } else if (confirmPwd.equals("")) {
            tilConfirmPwd.setError("输入确认密码!");
        } else {
            tilUsername.setErrorEnabled(false);
            tilPassword.setErrorEnabled(false);
            tilConfirmPwd.setErrorEnabled(false);
            doRegister(username.toLowerCase(), email.toLowerCase(), password);
        }


    }


    private void doRegister(String username,  String email, String password) {

        BmobConn.register(username, email, password, new SaveListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e == null) {
                    T.makeS(RegisterAcitivity.this, "注册成功!");
                    finish();
                    showAcitvity(LoginActivity.class);
                } else {
                    BmobConn.parseBmobException(e, RegisterAcitivity.this);
                    Log.e(TAG, "done: " + e);
                }

            }
        });


    }

    @Override
    protected void initView() {
        tilUsername.setHint(getString(R.string.username));
        tilPassword.setHint(getString(R.string.password));
        tilConfirmPwd.setHint(getString(R.string.confirmpassword));
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        toolbar.setTitle(getString(R.string.register));
        setSupportActionBar(toolbar);
        setNavigationOnClick();
    }
}
