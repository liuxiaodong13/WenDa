package liuxiaodong.neusoft.edu.cn.wenda.listener.base;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

/**
 * Created by DONG on 2017/1/19.
 */

public abstract class OnItemDeleteClickListener implements View.OnClickListener {
    protected String message;
    protected Context context;

    public OnItemDeleteClickListener(String message, Context context) {
        this.message = message;
        this.context = context;
    }

    protected abstract void doDelete();

    protected abstract void doCancel();


    @Override
    public void onClick(View v) {
        showWarningDialog();
    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doDelete();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doCancel();
            }
        });
        builder.create().show();
    }
}
