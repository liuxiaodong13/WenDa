package liuxiaodong.neusoft.edu.cn.wenda.other;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.model.Category;
import liuxiaodong.neusoft.edu.cn.wenda.model.Notification;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;
import liuxiaodong.neusoft.edu.cn.wenda.utils.AppUtils;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Consts;
import liuxiaodong.neusoft.edu.cn.wenda.utils.DefaultValue;

/**
 * Created by DONG on 2016/12/28.
 */

public class NotificationGenerator {
    public static final String TAG = "NotificationGenerator";
    public static final int NEW_ANSWER = 1;
    public static final int NEW_CONCERNED = 2;
    public static final int NEW_LIKED = 3;
    public static final int NEW_GOODAT_QUESTION = 4;

    public static final int NEW_CONCERNEDQS_ANSWER = 5;
    public static final String NEW_ANSWER_CONTENT = "您的提问有新回答。";

    public static final String NEW_CONCERNED_CONTENT= "您的提问被关注。"; //暂时无法实现

    public static final String NEW_LIKED_CONTENT = "您的回答被赞。"; //暂时无法实现

    public static final String NEW_GOODAT_QUESTION_CONTENT = "您的擅长领域有新提问。";

    public static final String NEW_CONCERNEDQS_ANSWER_CONTENT = "您关注的问题有新回答";

    public static final String NEW_MESSAGE_CONTENT = "您有一条新私信.";




    private String defaultNotificationTitle;
    private String defaultTypeDesc;

    public NotificationGenerator(onCompleteGenerateListener listener) {
        this.listener = listener;
        defaultNotificationTitle = DefaultValue.DEFAULT_NOTIFICATION_TITLE;
        defaultTypeDesc = DefaultValue.SYSTEM_NOTIFICATION;


    }



    private onCompleteGenerateListener listener;

    public interface onCompleteGenerateListener {
        void onCompleteGenerate(Notification notification);
    }

    public Notification generate(String json) {

        Notification notification = new Notification();
        notification.setUser(AppUtils.getCurrentUser());
        try {
            JSONObject jsonObject = new JSONObject(json);
            String tableName = jsonObject.getString("tableName");
            String action = jsonObject.getString("action");
            switch (tableName) {
                case Consts.ANSWER_TABLE:
                    //当前是answer表的修改
                    if (action.equals("updateTable")) {

                        JSONObject data = jsonObject.getJSONObject("data");

                        String createdAt = data.getString("createdAt");
                        String updatedAt = data.getString("updatedAt");
                        String questionId = data.getString("question");
                        if (createdAt.equals(updatedAt)) {
                            ///说明是 新增加回答
                            //您的提问有新回答
                            notification.setType(NEW_ANSWER);
                            notification.setTitle(defaultNotificationTitle);
                            notification.setContent(NEW_ANSWER_CONTENT);
                            notification.setTypeDesc(defaultTypeDesc);
                            notification.setRead(false);
                            if (AppSettings.isNotifyNewAnswer())
                            isForCurrentUser(questionId, notification);


                            //您关注的问题有新回答
                            if (AppSettings.isNotifyConcernedQuestionUpdated()) {
                                Notification notification1 = new Notification();
                                notification1.setType(NEW_CONCERNEDQS_ANSWER);
                                notification1.setTitle(defaultNotificationTitle);
                                notification1.setContent(NEW_CONCERNEDQS_ANSWER_CONTENT);
                                notification1.setTypeDesc(defaultTypeDesc);
                                notification1.setRead(false);
                                isForCurrentUserConcern(questionId, notification1);
                            }

                        }
                    } else {
                        notification = null;
                    }
                    break;

                case Consts.QUESTION_TABLE:
                    //当前是question表的修改    //"您的擅长领域有新提问。"
                    //查询当前
                    if (action.equals("updateTable")) {
                        JSONObject data = jsonObject.getJSONObject("data");

                        String categoryId = data.getString("category");
                        String createdAt = data.getString("createdAt");
                        String updatedAt = data.getString("updatedAt");
                        String questionId = data.getString("objectId");
                        if (createdAt.equals(updatedAt)) {
                            ///说明是 新问题

                            ////"您的擅长领域有新提问。"
                            notification.setType(NEW_GOODAT_QUESTION);
                            notification.setTitle(defaultNotificationTitle);
                            notification.setContent(NEW_GOODAT_QUESTION_CONTENT);
                            notification.setTypeDesc(defaultTypeDesc);
                            notification.setRead(false);
                            isCurrentUserInterested(categoryId, questionId, notification);






                        }

                        else {
                            notification = null;
                        }
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return notification;
    }

    private void isForCurrentUserConcern(final String questionId, final Notification notification) {
        //查询当前问题id 是否是当前用户所关注的
        BmobConn.queryAllConcernedQs(APP.getUserConcernId(), new FindListener<Question>() {
            @Override
            public void done(List<Question> list, BmobException e) {
                for (Question question : list) {
                    if (question.getObjectId().equals(questionId))
                    {
                        //说明  当前问题id是 当前用户所关注
                        notification.setExtra(questionId);
                        if (listener != null) {
                            listener.onCompleteGenerate(notification);
                        }
                    }
                }
            }
        });


    }

    private void isCurrentUserInterested(final String categoryId, final String questionId, final Notification notification) {
        BmobConn.queryGoodAt(AppUtils.getCurrentUserId(), new FindListener<Category>() {
            @Override
            public void done(List<Category> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        for (Category category : list) {
                            if (category.getObjectId().equals(categoryId)) {
                                notification.setExtra(questionId);
                                if (listener != null) {
                                    listener.onCompleteGenerate(notification);
                                }
                                break;
                            }
                        }

                    }

                } else {
                    Log.i(TAG, "done: " + e);
                }
            }
        });

    }

    private void isForCurrentUser(final String questionId, final Notification notification) {
        //
        BmobConn.queryObject(Question.class, questionId, new QueryListener<Question>() {
            @Override
            public void done(Question question, BmobException e) {
                if (e == null) {
                    if (question.getAuthor().getObjectId().equals(AppUtils.getCurrentUserId())) {
                        //说明是当前用户新增加的 回答
                        notification.setExtra(questionId);
                        if (listener != null) {
                            listener.onCompleteGenerate(notification);
                        }
                    }

                } else {
                    Log.e(TAG, "done: " + e);
                }
            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                query.include("author");
            }
        });


    }


}
