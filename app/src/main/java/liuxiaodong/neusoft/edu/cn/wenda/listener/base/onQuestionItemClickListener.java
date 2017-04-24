package liuxiaodong.neusoft.edu.cn.wenda.listener.base;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import liuxiaodong.neusoft.edu.cn.wenda.activity.QuestionInfoActivity;

/**
 * Created by DONG on 2016/11/11.
 */

public class onQuestionItemClickListener implements View.OnClickListener {
    public onQuestionItemClickListener(String objectId, Activity activity) {
        this.objectId = objectId;
        this.activity = activity;
    }

    private String objectId;
    private Activity activity;

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(activity, QuestionInfoActivity.class);
        intent.putExtra(QuestionInfoActivity.QUESTION_ID, objectId);
        activity.startActivity(intent);

    }
}
