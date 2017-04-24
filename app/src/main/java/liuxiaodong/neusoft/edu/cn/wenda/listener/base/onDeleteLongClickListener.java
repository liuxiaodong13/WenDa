package liuxiaodong.neusoft.edu.cn.wenda.listener.base;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import liuxiaodong.neusoft.edu.cn.wenda.R;

/**
 *
 * Created by DONG on 2016/11/13.
 */

public abstract class onDeleteLongClickListener implements View.OnLongClickListener {
    protected String objectId;
    protected Context context;
    protected Class acClass;

    private String titleType;
    public static final String QUESTION = "提问";
    public static final String ANSWER = "回答";
    public static final String NOTIFICATION = "通知";
    public static final String COLLECTION = "收藏";
    public static final String CONCERN = "关注";


    public onDeleteLongClickListener(String objectId, Context context, String titleType) {
        this.objectId = objectId;
        this.context = context;
        this.titleType = titleType;
    }

    public onDeleteLongClickListener(String objectId, Context context, Class acClass, String titleType) {
        this.objectId = objectId;
        this.context = context;
        this.acClass = acClass;
        this.titleType = titleType;
    }

    @Override
    public boolean onLongClick(View v) {
        showSelectMenuDialog();
        return true;
    }

    private void showSelectMenuDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String[] choices = {"删除"  + titleType};
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm_delete);
        builder.setMessage("确认删除 这条" + titleType  + "吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doDelete();

                dialog.dismiss();
            }
        })
        .setNegativeButton("取消", null);
        builder.create().show();

    }

    public abstract void doDelete();
    public abstract void onDeleteSuccess();

}
