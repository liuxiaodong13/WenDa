package liuxiaodong.neusoft.edu.cn.wenda.conn;

import android.content.Context;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

/**
 * Created by DONG on 2016/10/21.
 */

public class BmobConn {
    public static void register(String username, String email, String password, SaveListener<MyUser> l) {
        MyUser user = new MyUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setNick(username);
        user.signUp(l);
    }

    public static void login(String username, String password, SaveListener<MyUser> l) {
        MyUser user = new MyUser();
        user.setUsername(username);
        user.setPassword(password);
        user.login(l);
    }

    public static void parseBmobException(BmobException e, Context context) {
        int code = e.getErrorCode();
        switch (code)
        {
            case 101:
                T.makeS(context, "用户名或密码错误!");
            case 202:
                T.makeS(context, "用户名已存在!");
            break;
            case 203:
                T.makeS(context, "邮箱已存在!");
                break;
            case 205:
                T.makeS(context, "没有找到此用户名的用户");
                break;
        }
    }

}
