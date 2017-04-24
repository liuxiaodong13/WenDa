package liuxiaodong.neusoft.edu.cn.wenda.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import liuxiaodong.neusoft.edu.cn.wenda.activity.QuestionInfoActivity;

/**
 * Created by DONG on 2017/1/13.
 */

public class onNotificationClickListener implements View.OnClickListener {
    private String questionId;
    private Context context;
    private String notificationId;


    public onNotificationClickListener(String questionId, String notificationId, Context context) {
        this.questionId = questionId;
        this.notificationId = notificationId;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, QuestionInfoActivity.class);
        intent.setAction(QuestionInfoActivity.ACTION_UPDATE_STATE);
        intent.putExtra(QuestionInfoActivity.QUESTION_ID, questionId);
        intent.putExtra(QuestionInfoActivity.NOTIFICATION_ID, notificationId);
        context.startActivity(intent);

    }
}
