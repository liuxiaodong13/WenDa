package liuxiaodong.neusoft.edu.cn.wenda.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import liuxiaodong.neusoft.edu.cn.wenda.activity.UserInfoActivity;

/**
 * Created by DONG on 2016/11/11.
 */

public class onUserAreaClickListener implements View.OnClickListener {
    Activity activity;
    String userId;
    String userNick;

    public onUserAreaClickListener(Activity activity, String userId, String userNick) {
        this.activity = activity;
        this.userId = userId;
        this.userNick = userNick;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(activity, UserInfoActivity.class);
        intent.putExtra(UserInfoActivity.OBJECT_ID, userId);
        intent.putExtra(UserInfoActivity.USER_NICK, userNick);
        activity.startActivity(intent);
    }
}
