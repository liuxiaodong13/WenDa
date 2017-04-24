package liuxiaodong.neusoft.edu.cn.wenda.conn;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.model.Answer;
import liuxiaodong.neusoft.edu.cn.wenda.model.Category;
import liuxiaodong.neusoft.edu.cn.wenda.model.Collection;
import liuxiaodong.neusoft.edu.cn.wenda.model.Concern;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;
import liuxiaodong.neusoft.edu.cn.wenda.other.AppSettings;
import liuxiaodong.neusoft.edu.cn.wenda.utils.ActivityHelper;
import liuxiaodong.neusoft.edu.cn.wenda.utils.DefaultValue;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

/**
 * Created by DONG on 2016/10/21.
 */

public class BmobConn {
    public static int listCounts = AppSettings.getListItemCounts();
    public static final String TAG = "BmobConn";
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public static void register(String username, String email, String password, SaveListener<MyUser> l) {
        MyUser user = new MyUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setNick(username);
        user.setUserIcon(DefaultValue.generateDefaultUserIcon());
        user.setGender(DefaultValue.GENDER);
        user.setLocation(DefaultValue.NOT_SET);
        user.signUp(l);
    }

    public static void login(String username, String password, SaveListener<MyUser> l) {
        MyUser user = new MyUser();
        user.setUsername(username);
        user.setPassword(password);
        user.login(l);
    }

    public interface Config {
        void setArguments(BmobQuery query);
    }

    public interface AddConfig<T> {
        void setConfig(T t);
    }

    public static <T> void addObject(Class<? extends BmobObject> clazz, SaveListener<String> listener, AddConfig<T> c) {
        try {
            T t = (T) clazz.newInstance();
            if (listener != null) {
                c.setConfig(t);
            }
            BmobObject object = (BmobObject) t;
            object.save(listener);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static <T> void queryAll(Class<? extends BmobObject> clazz, FindListener<T> listener, Config c) {

        BmobQuery<T> query = new BmobQuery<>();
        if (c != null) {
            c.setArguments(query);
        }

        query.findObjects(listener);
    }

    public static <T> void queryCounts(Class<? extends BmobObject> clazz, CountListener listener, Config c) {

        BmobQuery<T> query = new BmobQuery<>();
        if (c != null) {
            c.setArguments(query);
        }

        query.count((Class<T>) clazz, listener);
    }

    public static <T> void queryAllCounts(Class<T> clazz, CountListener listener, Config c) {

        BmobQuery<T> query = new BmobQuery<>();
        if (c != null) {
            c.setArguments(query);
        }

        query.count(clazz, listener);
    }

    public static <T> void queryObject(Class<? extends BmobObject> clazz, String objectId, QueryListener<T> listener, Config c) {
        BmobQuery<T> query = new BmobQuery<>();
        if (c != null) {
            c.setArguments(query);
        }
        query.getObject(objectId, listener);

    }

    /*public static  void updateObject(BmobObject object, String objectId, UpdateListener listener, Config c) {
        if (c != null) {
            c.setArguments();
        }
        object.update(listener);
    }*/

    public static <T> void queryObject(Class<? extends BmobObject> clazz, String objectId, QueryListener<T> listener) {
        BmobQuery<T> query = new BmobQuery<>();

        query.getObject(objectId, listener);

    }

    public static void queryCollectsFromUserId(String userObjectId, FindListener<Question> listener) {
        MyUser user = new MyUser();
        user.setObjectId(userObjectId);
        queryCollectId(user, listener);
    }

    private static void queryCollectsFromCollectId(final String collectObjectId, FindListener<Question> l) {
        BmobConn.queryAll(Question.class, l, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                Collection collection = new Collection();
                collection.setObjectId(collectObjectId);
                query.include("author,category");
                query.order("-createdAt"); //按照创建时间 将序排列
                query.addWhereRelatedTo("collects", new BmobPointer(collection));
            }
        });

    }

    public static void queryAllFavorite(final String collectId, FindListener<Question> l) {
        BmobConn.queryAll(Question.class, l, new Config() {
            @Override
            public void setArguments(BmobQuery query) {
                Collection collection = new Collection();
                collection.setObjectId(collectId);
                query.include("author,category");
                query.order("-createdAt"); //按照创建时间 将序排列
                query.addWhereRelatedTo("collects", new BmobPointer(collection));
            }
        });
    }

    public static void queryAllFavoriteWithSkip(final String collectId, FindListener<Question> l, final int skipCounts) {
        BmobConn.queryAll(Question.class, l, new Config() {
            @Override
            public void setArguments(BmobQuery query) {
                Collection collection = new Collection();
                collection.setObjectId(collectId);
                query.include("author,category");
                query.setSkip(skipCounts);
                query.order("-createdAt"); //按照创建时间 将序排列
                query.addWhereRelatedTo("collects", new BmobPointer(collection));
            }
        });
    }


    public static void queryConcernedQuestionsFromConcernId(final String concernId, FindListener<Question> l, final int skipCounts) {
        BmobConn.queryAll(Question.class, l, new Config() {
            @Override
            public void setArguments(BmobQuery query) {
                Concern concern = new Concern();
                concern.setObjectId(concernId);
                query.include("author,category");
                query.setLimit(listCounts);
                if (skipCounts > 0)
                    query.setSkip(skipCounts);
                query.order("-createdAt"); //按照创建时间 将序排列
                query.addWhereRelatedTo("concerns", new BmobPointer(concern));
            }
        });

    }


    /**
     * 查询用户所有关注问题
     */
    public static void queryAllConcernedQs(final String concernId, FindListener<Question> l) {
        BmobConn.queryAll(Question.class, l, new Config() {
            @Override
            public void setArguments(BmobQuery query) {
                Concern concern = new Concern();
                concern.setObjectId(concernId);
                query.include("author,category");
                query.order("-createdAt"); //按照创建时间 将序排列
                query.addWhereRelatedTo("concerns", new BmobPointer(concern));
            }
        });

    }


    private static void queryConcernedQuestionCountsFromConcernId(final String concernId, CountListener l) {
        BmobConn.queryAllCounts(Question.class, l, new Config() {
            @Override
            public void setArguments(BmobQuery query) {
                Concern concern = new Concern();
                concern.setObjectId(concernId);
                query.include("author,category");
                query.addWhereRelatedTo("concerns", new BmobPointer(concern));
            }
        });

    }


    private static void queryCollectId(final MyUser user, final FindListener<Question> listener) {
        if (user.getObjectId() == null || user.getObjectId().equals("")) {
            throw new IllegalArgumentException("user is null!!");
        }

        BmobConn.queryAll(Collection.class, new FindListener<Collection>() {
            @Override
            public void done(List<Collection> list, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "done: 查询collectId成功！");
                    //collectId = list.get(0).getObjectId();
                    queryCollectsFromCollectId(list.get(0).getObjectId(), listener);
                } else {
                    Log.i(TAG, "done: " + e);
                }

            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                query.addWhereEqualTo("user", user);
            }
        });

    }

    public static void queryCollectIdOnly(final String userId, final FindListener<Collection> listener) {
        BmobConn.queryAll(Collection.class, listener, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                MyUser user = new MyUser();
                user.setObjectId(userId);
                query.addWhereEqualTo("user", user);
            }
        });

    }


    public static void queryRelatedAnswersCountFromQuestionId(final String questionId, CountListener l) {

        BmobConn.queryAllCounts(Answer.class, l, new Config() {
            @Override
            public void setArguments(BmobQuery query) {
                Question question = new Question();
                question.setObjectId(questionId);
                query.addWhereEqualTo("question", new BmobPointer(question));
            }
        });


    }


    public static void queryCategories(FindListener<Category> l) {
        queryAll(Category.class, l, null);
    }

    public static void queryGoodAt(final String userId, FindListener<Category> l) {

        queryAll(Category.class, l, new Config() {
            @Override
            public void setArguments(BmobQuery query) {
                MyUser user = new MyUser();
                user.setObjectId(userId);
                query.addWhereRelatedTo("good_at", new BmobPointer(user));
            }
        });
    }

    /**
     * 查询用户所有提问
     */
    public static void queryAllQuestions(final String userId, FindListener<Question> l, final int skipCounts) {
        queryAll(Question.class, l, new Config() {
            @Override
            public void setArguments(BmobQuery query) {
                MyUser user = new MyUser();
                user.setObjectId(userId);
                query.include("author,category");
                query.setLimit(listCounts);
                if (skipCounts > 0)
                    query.setSkip(skipCounts);
                query.order("-createdAt"); //按照创建时间 将序排列
                query.addWhereEqualTo("author", user);
            }
        });
    }

    /**
     * 查询用户所有关注问题
     */
//    public static void queryAllConcernedQuestions(final String userId, final FindListener<Question> l) {
//        //先查询concernId
//
//        queryConcernId(userId, new FindListener<Concern>() {
//            @Override
//            public void done(List<Concern> list, BmobException e) {
//                if (e == null) {
//                    //再根据concernId查所关注的所有问题
//                    Log.i(TAG, "done: 查询concernId成功：" + list.get(0).getObjectId());
//                    queryConcernedQuestionsFromConcernId(list.get(0).getObjectId(), l);
//                } else {
//                    Log.i(TAG, "done: " + e);
//                }
//
//            }
//        });
//    }

    public static void queryConcernedQuestions(FindListener<Question> l,int skipCounts) {
        String concernId = ActivityHelper.getShareData(APP.getInstance(), ActivityHelper.CONCERN_ID, "");
        queryConcernedQuestionsFromConcernId(concernId, l, skipCounts);

    }

    public static void queryAllConcernedQuestionCounts(final CountListener l) {
        //先查询concernId
        String concernId = ActivityHelper.getShareData(APP.getInstance(), ActivityHelper.CONCERN_ID, "");
        queryConcernedQuestionCountsFromConcernId(concernId, l);
    }


    public static void addConcernQuestion(String questionId, UpdateListener l) {
        MyUser user = MyUser.getCurrentUser(MyUser.class);
        Question question = new Question();
        question.setObjectId(questionId);
        BmobRelation relation = new BmobRelation();
        relation.add(user);
        question.setConcerned(relation);
        question.update(l);

        //更新concern表 方便 直接拉取用户关注的所有问题
        Question question1 = new Question();
        question1.setObjectId(questionId);
        BmobRelation relation1 = new BmobRelation();
        relation1.add(question1);

        Concern concern = new Concern();
        concern.setUser(MyUser.getCurrentUser(MyUser.class));
        concern.setConcerns(relation1);
        concern.update(APP.getUserConcernId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Logger.d("done: 更新concern表成功！关注成功！");
                } else {
                    Logger.e("done: 更新concern表失败！" + e);
                }
            }
        });

    }

    public static void removeConcernQuestion(String questionId, UpdateListener l) {
        //取消关注！
        //更新question表 concerned字段
        Question question = new Question();
        question.setObjectId(questionId);
        BmobRelation relation = new BmobRelation();
        relation.remove(MyUser.getCurrentUser(MyUser.class));
        question.setConcerned(relation);
        question.update(l);

        //更新concern表 方便 直接拉取用户关注的所有问题
        Question question1 = new Question();
        question1.setObjectId(questionId);
        BmobRelation relation1 = new BmobRelation();
        relation1.remove(question1);
        Concern concern = new Concern();
        concern.setConcerns(relation1);
        concern.setUser(MyUser.getCurrentUser(MyUser.class));
        concern.update(APP.getUserConcernId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Logger.d("done: 更新concern表成功！取消关注成功！");
                } else {
                    Logger.e("done: 更新concern表失败！" + e);
                }

            }
        });


    }

    public static void addCollect(String questionId, UpdateListener l) {
        Collection collection = new Collection();
        BmobRelation relation = new BmobRelation();
        Question question = new Question();
        question.setObjectId(questionId);
        relation.add(question);
        collection.setCollects(relation);
        collection.update(APP.getUserCollectId(), l);

    }

    public static void removeCollect(String questionId, UpdateListener l) {
        //取消收藏
        Collection collection = new Collection();
        BmobRelation relation = new BmobRelation();
        Question question1 = new Question();
        question1.setObjectId(questionId);

        relation.remove(question1);
        collection.setCollects(relation);
        collection.update(APP.getUserCollectId(), l);


    }

    public static void queryConcernId(String userId, FindListener<Concern> l) {
        BmobQuery<Concern> query = new BmobQuery<>();
        MyUser user = new MyUser();
        user.setObjectId(userId);
        query.addWhereEqualTo("user", user);
        query.findObjects(l);

    }

    public static void queryAllUserAnswers(final String userId, FindListener<Answer> l, final int skipCount) {
        queryAll(Answer.class, l, new Config() {
            @Override
            public void setArguments(BmobQuery query) {
                MyUser user = new MyUser();
                user.setObjectId(userId);
                query.include("question,author,question.author,question.category");
                query.setLimit(AppSettings.getListItemCounts());
                query.order("-createdAt"); //按照创建时间 将序排列
                if (skipCount > 0)
                    query.setSkip(skipCount);
                query.addWhereEqualTo("author", user);

            }
        });

    }


    public static void deleteItem(String objectId, Class<? extends BmobObject> clazz, UpdateListener l) {
        BmobObject instance = null;
        try {
            instance = clazz.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        if (instance != null) {

            instance.setObjectId(objectId);
            instance.delete(l);
        }
    }

    public static void queryAllAnswers(final String questionId, FindListener<Answer> l) {
        BmobConn.queryAll(Answer.class, l, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                query.include("author");
                Question question = new Question();
                question.setObjectId(questionId);
                //query.include("question,author,question.author,question.category");
//                query.setSkip((pageIndex - 1) * listItems);
//                query.setLimit(listItems);
                query.order("-createdAt");
                query.addWhereEqualTo("question", new BmobPointer(question));
            }
        });
    }


    /**
     * 删除用户发布的回答 并移除 所有与之关联的 回答  收藏  关注！
     *
     * @param questionId
     * @param l
     */
    public static void deleteUserQuestion(final String questionId, UpdateListener l) {
        //先移除关联
        //移除 其所有回答 的 原提问
        //1.先查询 该条提问的所有回答
        queryAllAnswers(questionId, new FindListener<Answer>() {
            @Override
            public void done(List<Answer> list, BmobException e) {
                if (e == null) {
                    Logger.d("查询到Answer:" + list.size());
                    //移除 其所有回答 的 原提问

                    for (Answer answer : list) {
                        updateItem(answer.getObjectId());
                    }
                } else {
                    Logger.e(e.toString());
                }
            }

            private void updateItem(final String objectId) {
                Answer answer = new Answer();
                answer.remove("question");
                answer.update(objectId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Logger.d("更新" + objectId + "成功！");
                        } else {
                            Logger.e("更新" + objectId + "失败！");
                        }

                    }
                });
            }

        });
        //移除 关注
        //自动移除

        //移除 收藏
        //自动移除

        //最后删除该条提问
        deleteItem(questionId, Question.class, l);

    }


    public static void parseBmobException(BmobException e, Context context) {
        int code = e.getErrorCode();
        switch (code) {
            case 101:
                T.makeS(context, "用户名或密码错误!");
                break;
            case 202:
                T.makeS(context, "用户名已存在!");
                break;
            case 203:
                T.makeS(context, "邮箱已存在!");
                break;
            case 205:
                T.makeS(context, "没有找到此用户名的用户");
                break;
            default:
                break;
        }

        Log.e(TAG, "parseBmobException: ", e);
    }


}
