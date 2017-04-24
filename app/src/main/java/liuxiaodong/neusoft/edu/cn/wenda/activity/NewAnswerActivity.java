package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.view.View;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import liuxiaodong.neusoft.edu.cn.wenda.other.AppSettings;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.model.Answer;
import liuxiaodong.neusoft.edu.cn.wenda.model.Draft;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;
import liuxiaodong.neusoft.edu.cn.wenda.model.Record;
import liuxiaodong.neusoft.edu.cn.wenda.utils.AppUtils;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;
import liuxiaodong.neusoft.edu.cn.wenda.utils.SystemUtils;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

/**
 * Created by DONG on 2016/10/21.
 */

public class NewAnswerActivity extends NewContentBaseActivity {
    public static final String QUESTION_ID = "questionId";

    private String questionId;

    @Override
    protected void initData() {
        //重写 不用拉取category

    }

    @Override
    protected void initView() {
        super.initView();
        spinner.setVisibility(View.GONE); //隐藏spinner
    }

    @Override
    protected void submit(final List<BmobFile> list) {

        BmobConn.addObject(Answer.class, new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    onSubmitSuccess();
                    if (currentAction != null) {
                        deleteDraft();
                    }
                } else {
                    onSubmitFailed(e);
                }

            }
        }, new BmobConn.AddConfig<Answer>() {
            @Override
            public void setConfig(Answer answer) {
                Question question = new Question();
                question.setObjectId(questionId);
                answer.setAuthor(getCurrentUser());
                answer.setLikescount(0); //默认设置点赞数为0
                answer.setQuestion(question);
                answer.setContent(etContent.getText().toString());
                if (list != null)
                    answer.addAllUnique("pics", list);
                if (recordFile != null) {
                    answer.setRecord(recordFile);
                    answer.setRecordTime(mRecord.getSecond());
                }

            }
        });
    }
    @Override
    protected void onSubmitFailed(BmobException e) {
        T.makeS(activity, "回答失败！");
        Logger.e(e.toString());

    }

    @Override
    protected void onSubmitSuccess() {
        EventManager.postSticky(new Event(Event.REFRESH_ANSWER_LIST));
        T.makeS(activity, "回答成功!");
        if (AppSettings.isOnSendSuccessVibrate()) {
            SystemUtils.vibrate(getApplicationContext());
        }
        //发送 事件

        cleanFiles();
        finish();

    }

    @Override
    protected void getExtraData() {
        questionId = getIntent().getStringExtra(QUESTION_ID);
        super.getExtraData();
    }

    /**
     * 填充answer类型的draft
     * @param draft
     */
    @Override
    protected void fillDataFromDraft(Draft draft) {
        questionId = draft.getQuestionId();
        draftId = draft.getId();
        results.addAll(draft.getPicUrls());
        convertToWrapper();
        photoListAdapter.setData(photos);
        etContent.setText(draft.getContent());


        //设置当前录音
        if (!draft.getRecordPath().equals("")) {
            //说明存在录音
            mRecord = new Record(draft.getRecordTime(), draft.getRecordPath());
            showRecordOnScreen();
        }
    }



    @Override
    protected String setTitle() {
        return "新回答";
    }

    /**
     * 将当前内容封装为 草稿 类型为answer 特有字段是 questionId  没有category
     *
     * @return
     */
    @Override
    protected Draft getDraftInfo() {
        Draft.Type type = Draft.Type.ANSWER;
        String content = etContent.getText().toString();
        String category = ""; //没有category
        String recordPath = "";
        String recordTime = "";

        if (mRecord != null) {
            //存在录音 则设置数据
            recordPath = mRecord.getFilePath();
            recordTime = mRecord.getSecond();
        }

        String time = AppUtils.getCurrentTime();


        return new Draft(type, content, category, time, results, recordPath, recordTime, questionId);
    }
}
