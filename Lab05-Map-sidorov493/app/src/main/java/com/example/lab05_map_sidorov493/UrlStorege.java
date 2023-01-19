package com.example.lab05_map_sidorov493;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class UrlStorege extends SQLiteOpenHelper {
    public UrlStorege(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public UrlStorege(Context context)
    {
        this(context, "Url.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        SQLiteDatabase db = sqLiteDatabase;
        String urlTable = "CREATE TABLE urlTable  (\n" +
                "  id INTEGER NOT NULL,\n" +
                "  domain VARCHAR(255) NOT NULL,\n" +
                "  port VARCHAR(255) NOT NULL,\n" +
                "  PRIMARY KEY (id)\n" +
                ")";
        db.execSQL(urlTable);
        UpdateUrl(db);
    }

    public void UpdateUrl()
    {
        UpdateUrl(getWritableDatabase());
    }

    public int Count()
    {
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "SELECT Count(*) FROM urlTable;";
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

    public void UpdateUrl(SQLiteDatabase db)
    {

        try {
            if(Count() < 1)
                throw new Exception();

            ContentValues context = new ContentValues();
            context.put("domain", MapHelper.DomainUrl);
            context.put("port", MapHelper.PortUrl);
            db.update("urlTable", context, null, null);
        }
        catch (Exception ex)
        {

            ContentValues context = new ContentValues();
            context.put("domain", MapHelper.DomainUrl);
            context.put("port", MapHelper.PortUrl);
            context.put("id", 1);
            db.insert("urlTable", null, context);
        }
        GetUrl();
    }

    public void GetUrl()
    {
        try {
            try {
                SQLiteDatabase db = getReadableDatabase();
                String sql = "SELECT domain, port FROM urlTable;";
                Cursor cur = db.rawQuery(sql, null);
                if (cur.moveToFirst() == true) {

                    MapHelper.DomainUrl = cur.getString(0);
                    MapHelper.PortUrl = cur.getString(1);
                    cur.close();
                }
            } catch (Exception ex) {
                UpdateUrl();
            }
        }
        catch(Exception ex)
        {

        }
    }

    public static UrlStorege GetDB(Context context)
    {
        return  new UrlStorege(context);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
