package com.example.lab05_map_sidorov493;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ScaleDB extends SQLiteOpenHelper {
    public ScaleDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ScaleDB(Context context)
    {
        this(context, "ScaleMap.db", null, 1);
    }

    public static ScaleDB GetDB(Context context)
    {
        return  new ScaleDB(context);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        SQLiteDatabase db = sqLiteDatabase;
        String urlTable = "CREATE TABLE Scale  (\n" +
                "  id INTEGER NOT NULL,\n" +
                "  level Integer NOT NULL,\n" +
                "  value Integer NOT NULL,\n" +
                "  PRIMARY KEY (id)\n" +
                ")";
        db.execSQL(urlTable);
        UpdateScale(db);
    }

    public int Count()
    {
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "SELECT Count(*) FROM Scale;";
            Cursor cur = db.rawQuery(sql, null);
            int count = 0;
            if (cur.moveToFirst() == true) {

                count = cur.getInt(0);
            }
            cur.close();
            return  count;
        } catch (Exception ex) {
            return  0;
        }
    }

    public void UpdateScale(SQLiteDatabase db)
    {
        UpdateScale(db, 0, MapHelper.DefaultScale());
    }

    public void UpdateScale(int level, int value)
    {
        UpdateScale(getReadableDatabase(), level, value);
    }

    public void UpdateScale(MapView map)
    {
        UpdateScale(map.current_level_index, map.ScaleValue_Index);
    }

    public void GetScale(MapView map)
    {
        try {
            try {
                SQLiteDatabase db = getReadableDatabase();
                String sql = "SELECT level, value FROM Scale;";
                Cursor cur = db.rawQuery(sql, null);
                if (cur.moveToFirst() == true) {

                    map.SetScale(cur.getInt(0), cur.getInt(1));
                    cur.close();
                }
            } catch (Exception ex) {
                //UpdateUrl();
            }
        }
        catch(Exception ex)
        {

        }
    }


    public void UpdateScale(SQLiteDatabase db, int level, float scale)
    {

        try {
            if(Count() < 1)
                throw new Exception();

            ContentValues context = new ContentValues();
            context.put("level", level);
            context.put("value", scale);
            db.update("Scale", context, null, null);
        }
        catch (Exception ex)
        {

            ContentValues context = new ContentValues();
            context.put("level", level);
            context.put("value", scale);
            context.put("id", 1);
            db.insert("Scale", null, context);
        }
        //GetUrl();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
