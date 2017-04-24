package liuxiaodong.neusoft.edu.cn.wenda.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import liuxiaodong.neusoft.edu.cn.wenda.activity.AnswerInfoActivity;
import liuxiaodong.neusoft.edu.cn.wenda.fragment.tabpagerfragment.AnswersPagerFragment;

/**
 * Created by DONG on 2016/11/11.
 */
public class onAnswerClickListener implements View.OnClickListener {
    String answerId;
    Activity activity;

    public onAnswerClickListener(String answerId, Activity activity) {
        this.answerId = answerId;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(activity, AnswerInfoActivity.class);
        intent.putExtra(AnswersPagerFragment.ANSWER_ID, answerId);
        activity.startActivity(intent);
    }
}
