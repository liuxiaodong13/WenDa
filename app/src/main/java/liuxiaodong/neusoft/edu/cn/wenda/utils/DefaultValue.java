package liuxiaodong.neusoft.edu.cn.wenda.utils;

import cn.bmob.v3.datatype.BmobFile;
import liuxiaodong.neusoft.edu.cn.wenda.R;

/**
 * Created by DONG on 2016/11/13.
 */

public class DefaultValue {
    public static final String DEFAULT_USER_ICON_URL = "http://bmob-cdn-7023.b0.upaiyun.com/2016/11/11/218a7cbb408b836980cd6473c1e6f383.png";
    public static final String DEFAULT_USER_ICON_FILENAME = "me.png";
    public static final Boolean GENDER = false;
    public static final String NOT_SET = "尚未设置";
    public static final String DEFAULT_NOTIFICATION_TITLE = "您有一条新消息";
    public static final String SYSTEM_NOTIFICATION = "系统消息";
    public static final String DB_NAME = "WenDa.db";
    public static final String DRAFT_TABLE_NAME = "draft";

    public static final String NO_DATA_DRAFT = "草稿箱为空!";

    public static final String DELETED_QUESTION = "原提问 已删除！";

    public static final String NO_DATA = "没有数据！";

    public static final String AUDIO_DIR_NAME = "audio";


    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final int DEFAULT_THEME = R.style.BaseIndigo;

    public static BmobFile generateDefaultUserIcon() {
        return new BmobFile(DEFAULT_USER_ICON_FILENAME, null, DEFAULT_USER_ICON_URL);
    }
}
