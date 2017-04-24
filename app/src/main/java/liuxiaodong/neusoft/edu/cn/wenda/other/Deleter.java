package liuxiaodong.neusoft.edu.cn.wenda.other;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteBatchListener;
import cn.bmob.v3.listener.UpdateListener;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;

/**
 * Created by DONG on 2017/3/29.
 */

public abstract class Deleter {
    protected String objectId;
    protected Class acClass;

    /**
     * cn.bmob.v3.datatype.BmobReturn cannot be cast to java.lang.String[]
     */
    public static final int EXCEPTION_ID = 9015;

    public interface onDeleteListener {
        void onSuccess();

        void onFailed(String[] failUrls, BmobException e);

        void onDeleteRecordFailed(BmobException e);

    }

    public Deleter(Class acClass, onDeleteListener listener) {
        this.acClass = acClass;
        mListener = listener;
    }

    private onDeleteListener mListener;
    protected String[] urls = new String[]{};

    public void delete() {
        //1.删除相关文件
        prepareData();
        if (urls.length > 0) {

            Logger.d("要删除的文件个数:" + urls.length);
            BmobFile.deleteBatch(urls, new DeleteBatchListener() {
                @Override
                public void done(String[] failUrls, BmobException e) {
                    if (e == null) {
                        Logger.d("文件删除成功！");
                        deleteItem();

                    } else {
                        //有异常

                        if (failUrls != null) {
//                        toast("删除失败个数：" + failUrls.length + "," + e.toString());
                            if (mListener != null) {
                                mListener.onFailed(failUrls, e);
                            }
                        } else {
                            if (e.getErrorCode() == EXCEPTION_ID)
                            {
                                Logger.d("文件删除成功！");
                                deleteItem();
                            }

//                        toast("全部文件删除失败：" + e.getErrorCode() + "," + e.toString());
                            Logger.d("全部文件删除失败：" + e.getErrorCode() + e.toString());

                        }
                    }
                }

            });
        } else {
            deleteItem();
        }

    }

    protected abstract void prepareData();

    private void deleteItem() {
        BmobConn.deleteItem(objectId, acClass, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    if (mListener != null) {
                        mListener.onSuccess();
                    }
                } else {
                    //删除这条数据失败
                    if (mListener != null) {
                        mListener.onDeleteRecordFailed(e);
                    }

                }
            }
        });
    }


}
