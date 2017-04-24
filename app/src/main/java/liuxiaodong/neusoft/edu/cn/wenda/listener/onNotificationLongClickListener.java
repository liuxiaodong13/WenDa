package liuxiaodong.neusoft.edu.cn.wenda.listener;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.model.Notification;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

import static liuxiaodong.neusoft.edu.cn.wenda.base.BaseActivity.TAG;

/**
 * Created by DONG on 2017/1/13.
 */

public class onNotificationLongClickListener implements View.OnLongClickListener {
    private String questionId;
    private Activity activity;

    private String notificationId;

    public onNotificationLongClickListener(String questionId, Activity activity) {
        this.questionId = questionId;
        this.activity = activity;
    }

    public onNotificationLongClickListener(String questionId, String notificationId, Activity activity) {
        this.notificationId = notificationId;
        this.questionId = questionId;
        this.activity = activity;

    }


    @Override
    public boolean onLongClick(View v) {
        showSelectDialog();
        return true;
    }
    private void showSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String[] choices = {"删除"};
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showWarningDialog();
                }
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.confirm_delete);
        builder.setMessage("确认删除这条通知吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BmobConn.deleteItem(notificationId, Notification.class, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
//                        if (listener != null) {
//                            listener.onDeleteComplete();
//                        }
                        if (e == null) {

                            //发送更新通知列表 的 事件
                            EventManager.postSticky(new Event(Event.REFRESH_NOTIFICATIONS));
                            T.makeS(activity, "删除成功！");
                        }else {
                            T.makeS(activity, "删除失败！");
                            Log.e(TAG, "done: " + e);
                        }
                    }
                });

                dialog.dismiss();
            }
        })
                .setNegativeButton("取消", null);
        builder.create().show();
    }
}
