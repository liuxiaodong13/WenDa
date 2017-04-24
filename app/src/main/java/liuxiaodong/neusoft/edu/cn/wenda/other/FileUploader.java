package liuxiaodong.neusoft.edu.cn.wenda.other;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;
import liuxiaodong.neusoft.edu.cn.wenda.model.Record;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;

/**
 * 文件上传器
 * Created by DONG on 2017/3/25.
 */

public class FileUploader {

    private AtomicInteger totalCounts = new AtomicInteger(0);
    private AtomicInteger uploadedCounts = new AtomicInteger(0);

    private Record mRecord;

    private ArrayList<String> pics;

    private onUploadListener mListener;
    private StringBuffer mErrorBuffer = new StringBuffer();

    private List<BmobFile> picList;
    private BmobFile mRecordBmob;

    public FileUploader(Record record, ArrayList<String> pics, onUploadListener listener) {
        mRecord = record;
        this.pics = pics;
        mListener = listener;
        calculateCounts();
    }

    private void calculateCounts() {
        if (mRecord != null) {
            totalCounts.incrementAndGet();
        }

        totalCounts.addAndGet(pics.size());
//        totalCounts += pics.size();
        Logger.d("需要上传的文件总数：" + totalCounts);

    }

    public interface onUploadListener {
        void onProgress(int uploadedCount, int totalCounts);

        void onComplete(BmobFile recordFile, List<BmobFile> picList, int totalCounts);

        void onError(String info);

    }

    public void setonUploadSuccessListener(onUploadListener listener) {
        mListener = listener;
    }

    public void doUpload() {
        uploadRecord();
        uploadPics(pics.toArray(new String[pics.size()]));

    }

    private void uploadRecord() {
        if (mRecord != null) {

            final BmobFile file = new BmobFile(new File(mRecord.getFilePath()));
            file.upload(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Logger.d("录音上传成功！");
                        mRecordBmob = file;
                        uploadedCounts.incrementAndGet();
                        if (uploadedCounts.get() == totalCounts.get()) {
                            if (mListener != null) {
                                mListener.onComplete(mRecordBmob, picList, totalCounts.intValue());
                            }
                        }

                    } else {
                        Logger.d("录音上传失败！" + e.toString());
                        mErrorBuffer.append(e.toString());
                        mErrorBuffer.append("\n");
                        if (mListener != null) {
                            mListener.onError(mErrorBuffer.toString());
                        }
                    }

                }
            });
        }

    }

    private void uploadPics(final String[] urls) {
        if (urls.length > 0) {

            BmobFile.uploadBatch(urls, new UploadBatchListener() {
                public void onSuccess(List<BmobFile> list, List<String> list1) {
                    if (list1.size() == urls.length) {
                        Logger.d("所有图片上传成功！SIze：" + list.size());
                        picList = list;
                        uploadedCounts.addAndGet(list.size());
//                        uploadedCounts += list.size();
                        if (uploadedCounts.get() == totalCounts.get()) {
                            if (mListener != null) {
                                mListener.onComplete(mRecordBmob, list, totalCounts.intValue());
                            }
                        }

                    }

                }

                @Override
                public void onProgress(int i, int i1, int i2, int i3) {

                    //1、curIndex--表示当前第几个文件正在上传
                    //2、curPercent--表示当前上传文件的进度值（百分比）
                    //3、total--表示总的上传文件数
                    //4、totalPercent--表示总的上传进度（百分比）
//                String subTitle = "正在上传第" + i + "个文件" + "," + "共" + i2 + "个文件。" + "\n" + "进度: " + i1 + "%";
//                progressDialog.setProgress(i1);
//                progressDialog.setMessage(subTitle);
                    int count = mRecordBmob == null ? 0 : 1;
                    if (mListener != null) {
                        mListener.onProgress(i + count, totalCounts.intValue());
                    }
                }

                @Override
                public void onError(int i, String s) {
//                if (progressDialog.isShowing())
//                    progressDialog.dismiss();
                    mErrorBuffer.append(s);
                    mErrorBuffer.append("\n");
                    if (mListener != null) {
                        mListener.onError(mErrorBuffer.toString());
                    }
                }
            });

        }

    }

}
