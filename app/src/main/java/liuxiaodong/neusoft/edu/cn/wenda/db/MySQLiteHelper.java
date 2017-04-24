package liuxiaodong.neusoft.edu.cn.wenda.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.model.Draft;
import liuxiaodong.neusoft.edu.cn.wenda.utils.DefaultValue;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;

/**
 * Created by DONG on 2017/1/15.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TAG = "MySQLiteHelper";
    public static final String CREATE_DB = "create table if not exists " + DefaultValue.DRAFT_TABLE_NAME + " ("
            + "id integer primary key autoincrement, "
            + "type integer, "
            + "content text, "
            + "categoryName text, "
            + "time text, "
            + "pics text, "
            + "recordPath text, "
            + "recordTime text, "
            + "questionId text)";

    public static final String QUERY_ALL_SQL = "select * from " + DefaultValue.DRAFT_TABLE_NAME + " order by id desc";
    public static final String DELETE_DRAFT = "delete from " + DefaultValue.DRAFT_TABLE_NAME + " where id = ";

    private String separator = "+";
    private String separatorEscape = "\\+";

    public MySQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public MySQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int deleteDraft(Integer id) {
        SQLiteDatabase database = APP.getInstance().getSqLiteHelper().getWritableDatabase();
        // "delete from " + DefaultValue.DRAFT_TABLE_NAME + " where id = "
        return database.delete(DefaultValue.DRAFT_TABLE_NAME, "id=?", new String[]{String.valueOf(id)});

        //String deleteSQL = DELETE_DRAFT + id;
//        database.execSQL(deleteSQL);

    }

    public ArrayList<Draft> queryALlDrafts() {
        MySQLiteHelper sqLiteHelper = APP.getInstance().getSqLiteHelper();
        SQLiteDatabase readableDatabase = sqLiteHelper.getReadableDatabase();
        /*Logger.d(TAG, "queryALlDrafts: 查询成功:" + drafts);

        Logger.d(TAG, "queryALlDrafts: draftsSize:" + drafts.size());
        for (Draft draft : drafts) {
            Logger.d(TAG, "queryALlDrafts: draft:" + "id:"+ draft.getId()  + " size : "+  draft.getPicUrls().size());
        }*/

        return sqLiteHelper.queryAllDrafts(readableDatabase);
    }


    public void saveToDb(Draft draft, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put("type", draft.getType().ordinal());
        values.put("content", draft.getContent());
        values.put("categoryName", draft.getCategoryName());
        values.put("time", draft.getTime());
        values.put("pics", convertPicsStr(draft.getPicUrls()));
        values.put("recordPath", draft.getRecordPath());
        values.put("recordTime", draft.getRecordTime());

        values.put("questionId", draft.getQuestionId());

        Log.d(TAG, "saveToDb: " + database.insert(DefaultValue.DRAFT_TABLE_NAME, null, values)
        );
        database.close();
    }

    public int updateToDb(Draft draft, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put("type", draft.getType().ordinal());
        values.put("content", draft.getContent());
        values.put("categoryName", draft.getCategoryName());
        values.put("time", draft.getTime());
        values.put("pics", convertPicsStr(draft.getPicUrls()));

        values.put("recordPath", draft.getRecordPath());
        values.put("recordTime", draft.getRecordTime());
        values.put("questionId", draft.getQuestionId());

        int result = database.update(DefaultValue.DRAFT_TABLE_NAME, values, "id=?", new String[]{String.valueOf(draft.getId())});

        Logger.d("updateToDb():" + result);

        database.close();
        return result;

    }

    private String convertPicsStr(ArrayList<String> picUrls) {
        if (picUrls.size() == 0) {
            return "null";
        }
        StringBuilder builder = new StringBuilder();

        for (String picUrl : picUrls) {
            builder.append(picUrl);
            builder.append(separator);
        }
        Log.d(TAG, "convertPicsStr: 合成的str" + builder.toString());
        return builder.toString();

    }

    private ArrayList<String> reverseConvertPicStr(String pic) {
        if (pic.equals("null")) {
            return new ArrayList<>();
        }
        String[] split = pic.split(separatorEscape);
        Log.d(TAG, "reverseConvertPicStr: 拆分PicUrl : Size = " + split.length + "\n" + Arrays.toString(split));
        return new ArrayList<>(Arrays.asList(split));
    }

    public ArrayList<Draft> queryAllDrafts(SQLiteDatabase database) {
        ArrayList<Draft> drafts = new ArrayList<>();
        Cursor cursor = database.rawQuery(QUERY_ALL_SQL, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int type = cursor.getInt(1);
            String content = cursor.getString(2);
            String categoryName = cursor.getString(3);
            String time = cursor.getString(4);
            String pics = cursor.getString(5); //这里需要转换
            String recordPath = cursor.getString(6);
            String recordTime = cursor.getString(7);
            String questionId = cursor.getString(8);
            Draft draft = new Draft(type, id, content, categoryName, time, reverseConvertPicStr(pics), recordPath, recordTime, questionId);
            drafts.add(draft);
        }

        cursor.close();

        database.close();

        Logger.d("查询成功！:" + drafts.size() + "detail:" + drafts);

        return drafts;
    }
}
