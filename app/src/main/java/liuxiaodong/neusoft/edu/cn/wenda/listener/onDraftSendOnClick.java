package liuxiaodong.neusoft.edu.cn.wenda.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import liuxiaodong.neusoft.edu.cn.wenda.activity.NewAnswerActivity;
import liuxiaodong.neusoft.edu.cn.wenda.activity.NewQuestionActivity;
import liuxiaodong.neusoft.edu.cn.wenda.model.Draft;

/**
 * Created by DONG on 2017/1/18.
 */

public class onDraftSendOnClick  implements View.OnClickListener{
    private Context context;
    private Draft draft;

    public onDraftSendOnClick(Context context, Draft draft) {
        this.context = context;
        this.draft = draft;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        //先判断该draft的类型
        switch (draft.getType())
        {
            case QUESTION:
                intent = new Intent(context, NewQuestionActivity.class);
                break;
            case ANSWER:
                intent = new Intent(context, NewAnswerActivity.class);
                break;
        }
        intent.setAction(NewQuestionActivity.ACTION_FILL_BY_DRAFT);
        intent.putExtra(NewQuestionActivity.CONTENT_TYPE, draft.getType().ordinal());
        intent.putExtra("draft", draft);
        context.startActivity(intent);
    }


}
