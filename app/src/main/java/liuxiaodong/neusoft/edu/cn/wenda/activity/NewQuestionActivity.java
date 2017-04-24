package liuxiaodong.neusoft.edu.cn.wenda.activity;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import liuxiaodong.neusoft.edu.cn.wenda.other.AppSettings;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.model.Draft;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;
import liuxiaodong.neusoft.edu.cn.wenda.model.Record;
import liuxiaodong.neusoft.edu.cn.wenda.other.MediaManager;
import liuxiaodong.neusoft.edu.cn.wenda.utils.AppUtils;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;
import liuxiaodong.neusoft.edu.cn.wenda.utils.SystemUtils;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;


/**
 * 发送新提问
 * Created by DONG on 2016/10/21.
 */

public class NewQuestionActivity extends NewContentBaseActivity {
    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void getExtraData() {
        super.getExtraData();
    }

    /**
     * 填充draft类型为question的草稿
     *
     * @param draft
     */
    @Override
    protected void fillDataFromDraft(Draft draft) {
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
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

    @Override
    protected void submit(final List<BmobFile> list) {
        BmobConn.addObject(Question.class, new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    onSubmitSuccess();
                    if (currentAction != null) {
                        deleteDraft();
                    }
                } else {
                    onSubmitFailed(e);
                    BmobConn.parseBmobException(e, activity);
                }
            }
        }, new BmobConn.AddConfig<Question>() {
            @Override
            public void setConfig(Question question) {
                question.setAuthor(getCurrentUser());
                question.setCategory(currentCategory);
                question.setContent(etContent.getText().toString());
                if (list != null) {
                    question.addAllUnique("pics", list);
                }
                if (recordFile != null) {
                    question.setRecord(recordFile);
                    question.setRecordTime(mRecord.getSecond());
                }
            }
        });

    }

    @Override
    protected void onSubmitSuccess() {
        EventManager.postSticky(new Event(Event.REFRESH_QUESTION_LIST));
        T.makeS(activity, "发布提问成功!");
        if (AppSettings.isOnSendSuccessVibrate()) {
            SystemUtils.vibrate(getApplicationContext());
        }
        //发布成功 删除产生的临时文件
        cleanFiles();
        finish();

    }

    @Override
    protected void onSubmitFailed(BmobException e) {
        T.makeS(activity, "发布提问失败！");
        Logger.e(e.toString());
    }

    @Override
    protected String setTitle() {
        return "新提问";
    }

    /**
     * 把当前编辑的信息封装成草稿 类型为question
     *
     * @return
     */
    @Override
    protected Draft getDraftInfo() {
        Draft.Type type = Draft.Type.QUESTION;
        String content = etContent.getText().toString();
        String category = currentCategory.getName();
        String recordPath = "";
        String recordTime = "";
        if (mRecord != null) {
            recordPath = mRecord.getFilePath();
            recordTime = mRecord.getSecond();
        }
        String time = AppUtils.getCurrentTime();
        String questionId = "";
        return new Draft(type, content, category, time, results, recordPath, recordTime, questionId);
    }

}
