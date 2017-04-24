package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UploadFileListener;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.adapter.PhotoListAdapter;
import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.base.BackNavigationActivity;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.db.MySQLiteHelper;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.model.Category;
import liuxiaodong.neusoft.edu.cn.wenda.model.Draft;
import liuxiaodong.neusoft.edu.cn.wenda.model.Photo;
import liuxiaodong.neusoft.edu.cn.wenda.model.Record;
import liuxiaodong.neusoft.edu.cn.wenda.other.FileUploader;
import liuxiaodong.neusoft.edu.cn.wenda.other.MediaManager;
import liuxiaodong.neusoft.edu.cn.wenda.ui.AudioRecorderButton;
import liuxiaodong.neusoft.edu.cn.wenda.ui.RecordView2;
import liuxiaodong.neusoft.edu.cn.wenda.ui.RecordWindow;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;


/**
 * Created by DONG on 2016/10/21.
 */

public abstract class NewContentBaseActivity extends BackNavigationActivity implements PhotoListAdapter.onPhotoPreviewListChangedListener, AudioRecorderButton.AudioFinishRecordListener, FileUploader.onUploadListener {
    public static final String ACTION_FILL_BY_DRAFT = "activity.question.fillByDraft";
    private static final String TAG = "NewQuestionActivity";
    public static final String CONTENT_TYPE = "content_type";
    //private static final int SELECT_PIC_KITKAT = 100;
    private static final int SELECT_PIC = 101;
    @BindView(R.id.selected_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.et_question_content)
    EditText etContent;
    @BindView(R.id.spinner_category_list)
    Spinner spinner;
    @BindView(R.id.rv_Record)
    RecordView2 mRecordView;
    private int defaultMaxCount = 9;
    protected List<Photo> photos = new ArrayList<>();
    protected PhotoListAdapter photoListAdapter;
    protected ArrayList<String> results = new ArrayList<>();
    protected Category currentCategory;
    private ProgressDialog progressDialog;
    protected String currentAction = null;
    protected Integer draftId;
    private Draft passedDraft;
    private RecordWindow mRecordWindow;
    private AudioRecorderButton mAudioRecorderButton;
    protected Record mRecord;

    protected BmobFile recordFile;
    protected String title;

    protected Draft.Type mContentType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildActivity(R.layout.activity_new_question);
        parseIntent();

    }

    @Override
    protected void getExtraData() {
        int index = getIntent().getIntExtra(CONTENT_TYPE, 0);
        mContentType = Draft.Type.values()[index];
    }

    private void parseIntent() {
        Logger.d("parseIntent()");
        String action1 = getIntent().getAction();
        if (action1 != null) {
            Log.d(TAG, "action1 !=null" + action1);
            switch (action1) {
                case ACTION_FILL_BY_DRAFT:
                    currentAction = ACTION_FILL_BY_DRAFT;
                    passedDraft = getIntent().getParcelableExtra("draft");

                    fillDataFromDraft(passedDraft);
                    break;
            }


        }
    }

    protected abstract void fillDataFromDraft(Draft draft);

    @OnClick(R.id.btn_voice)
    public void onClickVoice() {
        //显示录制音频的popwindow
        if (mRecord != null) {
            showRecordExistWarning();
            return;
        }

        showRecordWindow();


    }

    private void showRecordWindow() {
        mRecordWindow.setAnimationStyle(R.style.anim_popup_dir);
        mRecordWindow.showAtLocation(NewContentBaseActivity.this.findViewById(R.id.ll_root), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        dimBackground();
    }

    private void showRecordExistWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("警告");
        builder.setMessage("已有录音，继续录制将删除当前录音，是否继续?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showRecordWindow();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();

    }

    private void dimBackground() {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = .3f;
        getWindow().setAttributes(lp);
    }

    @OnClick(R.id.btn_pic)
    public void onSelectPicClick() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, PhotoPickerActivity.class);
        bundle.putInt(PhotoPickerActivity.MAX_SELECT_SIZE, defaultMaxCount);
        bundle.putBoolean(PhotoPickerActivity.IS_MULTI_SELECT, true);
        intent.putExtras(bundle);
        startActivityForResult(intent, SELECT_PIC);
    }


    @OnClick(R.id.btn_submit)
    public void onSubmitOnClick() {
        if (!isSubmittable()) {
            return;
        }

        if (needUpload()) {
            //需要上传
            FileUploader uploader = new FileUploader(mRecord, results, this);
            initProgressDialog();
            uploader.doUpload();
            return;
        }

        //不需要上传文件 直接提交
        submit(null);


    }

    @Override
    public void onProgress(int uploadedCount, int totalCounts) {
        progressDialog.setProgress(uploadedCount);
        progressDialog.setMax(totalCounts);
        progressDialog.setMessage("正在上传第" + uploadedCount + "个文件...");

    }


    @Override
    public void onComplete(BmobFile recordFile, List<BmobFile> picList, int totalCounts) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        NewContentBaseActivity.this.recordFile = recordFile;
        T.makeS(activity, "共" + totalCounts + "个文件上传成功！");
        submit(picList);


    }

    @Override
    public void onError(String info) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Logger.d(info);
        T.makeS(activity, "上传失败!");

    }


    private boolean needUpload() {
        if (mRecord != null)
            return true;
        return results.size() > 0;
    }


    private void uploadRecord() {

        BmobFile file = new BmobFile(new File(mRecord.getFilePath()));
        file.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Logger.d("录音上传成功！");
                    T.makeS(activity, "录音上传成功！");
                } else {
                    Logger.d("录音上传失败！" + e.toString());
                    T.makeS(activity, "录音上传成功！");

                }

            }
        });
    }


    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.uploading_files));
        progressDialog.show();

    }

    protected abstract void submit(List<BmobFile> list);

    protected abstract void onSubmitSuccess();

    protected abstract void onSubmitFailed(BmobException e);

    protected void deleteDraft() {

//        Intent intent = new Intent(ACTION_DELETE_DRAFT);
//        intent.putExtra("id", draftId);
//        localBroadcastManager.sendBroadcast(intent);
        int result = APP.getInstance().getSqLiteHelper().deleteDraft(draftId);
        deleteRecord();
        if (result >= 1)
            EventManager.postSticky(new Event(Event.REFRESH_DRAFTS));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PIC && resultCode == RESULT_OK) {
            //多选
            results = data.getStringArrayListExtra(PhotoPickerActivity.SELECT_RESULTS_ARRAY);
            if (results == null) {
                return;
            }
//            StringBuilder sb = new StringBuilder();
            convertToWrapper();
//            Logger.i(TAG, "onActivityResult: result" + sb);
            photoListAdapter.setData(photos);


        }
    }

    protected void convertToWrapper() {
        photos.clear();
        for (int i = 0; i < results.size(); i++) {
//                sb.append(i + 1).append('：').append(results.get(i)).append("\n");
            Photo photo = new Photo();
            photo.uri = results.get(i);
            photos.add(photo);
        }
    }

    @Override
    protected void initToolbar() {
        title = setTitle();
        initTitle(title);

    }

    protected abstract String setTitle();

    @Override
    protected void initView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        photoListAdapter = new PhotoListAdapter(this, photos, this);
        recyclerView.setAdapter(photoListAdapter);


        initRecordPopWindow();
        initRecordView();


    }

    private void initRecordView() {
        mRecordView.setLongClickable(true);
        mRecordView.setVisibility(View.GONE);
        mRecordView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteWarnDialog();

                return false;
            }

            private void showDeleteWarnDialog() {
                if (mRecord != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("确认删除?");
                    builder.setMessage("确定删除这条录音？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteRecord();
                            mRecordView.setVisibility(View.GONE);
                            dialog.dismiss();

                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.create().show();
                }

            }
        });
    }

    private void initRecordPopWindow() {
        mRecordWindow = new RecordWindow(this, true);
        mRecordWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色还原！
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        mAudioRecorderButton = mRecordWindow.getAudioRecorderButton();
        mAudioRecorderButton.setAudioFinishRecordListener(this);


    }

    @Override
    public void onFinish(float seconds, String filePath) {
        //如果当前文件存在 先删除
        if (mRecord != null) {
            deleteRecord();
        }

        mRecord = new Record(String.valueOf(Math.round(seconds)), filePath);
        //显示 record图标
        showRecordOnScreen();
        //录制完隐藏录音界面
        if (mRecordWindow.isShowing()) {
            mRecordWindow.dismiss();
        }

    }

    protected void deleteRecord() {
        if (mRecord != null) {

            File file = new File(mRecord.getFilePath());
            boolean result = file.delete();
            Logger.d("录音是否删除成功：" + result + " " + mRecord.getFilePath());
        }
    }

    protected void showRecordOnScreen() {
        //Math.round(record.getTime())

        mRecordView.setTime(mRecord.getSecond());
        mRecordView.setVisibility(View.VISIBLE);
        mRecordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordView.startAnim();
                MediaManager.playSound(mRecord.getFilePath(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mRecordView.stopAnim();
                    }
                });

            }

        });


    }


    private void initCategory(final List<Category> list) {
        List<String> categories = new ArrayList<>();
        for (Category category : list) {
            categories.add(category.getName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(arrayAdapter);
        setCheckedState(categories);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategory = list.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected void setCheckedState(List<String> categories) {
        int index = 0;
        Draft draft;
        Intent intent = getIntent();
        if (intent != null) {
            draft = intent.getParcelableExtra("draft");
            if (draft != null) {
                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).equals(draft.getCategoryName())) {
                        index = i;
                        break;
                    }
                }
                spinner.setSelection(index);

            }
        }

    }


    @Override
    protected void initData() {
        //拉取所有category

        BmobConn.queryCategories(new FindListener<Category>() {
            @Override
            public void done(List<Category> list, BmobException e) {
                if (e == null) {
                    initCategory(list);
                } else {
                    BmobConn.parseBmobException(e, NewContentBaseActivity.this);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Logger.d("onBackPressed()");
        Logger.d("currentAction:" + currentAction);
        if (currentAction != null && currentAction.equals(ACTION_FILL_BY_DRAFT)) {//说明是从草稿箱 启动的activity
            updateDraft();
            super.onBackPressed();
        } else {

            isNeedSave();
        }
        //super.onBackPressed();
    }

    private void isNeedSave() {
        if (isEdited()) {
            showSaveDialog();
        } else {
            NewContentBaseActivity.super.onBackPressed();
        }
    }

    /**
     * 需要同时修改onBackPressed()方法 两者皆为返回操作！
     */
    @Override
    protected void onNavigationOnClick() {
        onBackPressed();
    }

    private void updateDraft() {
        if (isNeedUpdate()) {
            Logger.d("需要更新草稿！");
            updateToDb();
        }

    }


    private boolean isNeedUpdate() {
        Draft currentDraft = getDraftInfo();
        currentDraft.setId(passedDraft.getId());
        return !currentDraft.equals(passedDraft);
    }


    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("保存到草稿箱？").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveAsDraft();
                NewContentBaseActivity.super.onBackPressed();
            }
        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cleanFiles();
                NewContentBaseActivity.super.onBackPressed();
            }
        });
        builder.create().show();
    }

    /**
     * 用户取消发送 或  发布内容成功
     */
    protected  void cleanFiles(){
        deleteRecord();
    }

    private void saveAsDraft() {
        Draft draftInfo = getDraftInfo();
        saveToDb(draftInfo);

    }


    private void saveToDb(Draft draftInfo) {
        Log.d(TAG, "saveToDb: " + draftInfo + "\n" + "图片数量:" + draftInfo.getPicUrls().size());
        MySQLiteHelper sqLiteHelper = APP.getInstance().getSqLiteHelper();
        SQLiteDatabase writableDatabase = sqLiteHelper.getWritableDatabase();
        sqLiteHelper.saveToDb(draftInfo, writableDatabase);
    }

    private void updateToDb() {
        MySQLiteHelper sqLiteHelper = APP.getInstance().getSqLiteHelper();
        SQLiteDatabase writableDatabase = sqLiteHelper.getWritableDatabase();
        Draft newDraft = getDraftInfo();
        newDraft.setId(passedDraft.getId());

        int result = sqLiteHelper.updateToDb(newDraft, writableDatabase);
        if (result > 0) {
            EventManager.postSticky(new Event(Event.REFRESH_DRAFTS));
            T.makeS(APP.getInstance(), "更新草稿成功！");
        }

    }

    /**
     * 获得当前草稿的信息
     * 1.草稿有两种状态  一种为answer  一种为question
     *
     * @return
     */
    protected abstract Draft getDraftInfo();


    public boolean isEdited() {
        String content = etContent.getText().toString().trim();
        if (!content.equals("")) {
            return true;
        }

        if (photos.size() > 0) {
            return true;
        }

        return mRecord != null;

    }


    /**
     * 检测当前提问是否可以提交
     * 1.有文字输入
     * 2.有图片
     * 3.有语音
     *
     * @return
     */
    public boolean isSubmittable() {

        if (!etContent.getText().toString().equals(""))

            return true;
        if (results.size() > 0)
            return true;
        if (mRecord != null)
            return true;

        T.makeS(activity, "请编辑您的内容！");
        return false;

    }

    @Override
    public void onChanged(List<Photo> photos) {
        Log.i(TAG, "List  onChanged: " + " " + photos.size());
        convert(photos);

    }

    private void convert(List<Photo> photos) {
        results.clear();
        for (Photo photo : photos) {
            results.add(photo.uri);
        }
        this.photos.clear();
        this.photos.addAll(photos);

    }


}
