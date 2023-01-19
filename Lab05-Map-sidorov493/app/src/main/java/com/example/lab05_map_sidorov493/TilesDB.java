package com.example.lab05_map_sidorov493;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;

public class TilesDB extends SQLiteOpenHelper {
    public TilesDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static TilesDB GetDB(Context context)
    {
        return  new TilesDB(context);
    }

    public TilesDB(Context context)
    {
        this(context, "Tiles.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        SQLiteDatabase db = sqLiteDatabase;

        String cash = "CREATE TABLE Cash  (\n" +
                "  id INTEGER NOT NULL,\n" +
                "  time varchar(255), \n"+
                "  PRIMARY KEY (id)\n" +
                ")";
        db.execSQL(cash);
        UpdateTimeCash(db);

        String tileTable = "CREATE TABLE Tile  (\n" +
                "  id INTEGER NOT NULL,\n" +
                "  x INTEGER NOT NULL,\n" +
                "  y INTEGER NOT NULL,\n" +
                "  scale INTEGER NOT NULL,\n" +
                "  bmp BLOB,\n" +
                "  day int NOT NULL,\n"+
                "  month int NOT NULL,\n"+
                "  year int NOT NULL,\n"+
                "  hour int NOT NULL,\n"+
                "  minute int NOT NULL,\n"+
                "  second int NOT NULL,\n"+
                "  PRIMARY KEY (id)\n" +
                ")";
        db.execSQL(tileTable);
        //  UpdateUrl(db);
    }

    public Tile GetTile(Tile tile) throws Exception {
        return GetTile(tile.x, tile.y, tile.scale);
    }

    public void ClearCash()
    {
        SQLiteDatabase db = getWritableDatabase();
        String clearCash = "Delete from Tile";
        db.execSQL(clearCash);
    }

    public Tile GetTile(int x, int y, int scale) throws Exception
    {
        try {
            SQLiteDatabase db = getReadableDatabase();
            Tile tile = new Tile(x, y, scale);
            String TileInfo = "Select bmp, id, day, month, year, hour, minute, second From Tile where x=" + x + " and y=" + y + " and scale=" + scale;
            Cursor cur = db.rawQuery(TileInfo, null);
            if (cur.moveToFirst() == true) {

                tile.bmp = getImage(cur.getBlob(0));
                tile.ID = cur.getInt(1);
                int day = cur.getInt(2);
                int month = cur.getInt(3);
                int year = cur.getInt(4);
                int hour = cur.getInt(5);
                int minute = cur.getInt(6);
                int second = cur.getInt(7);
                tile.SetDateTime(hour, minute, second, day, month, year);
                cur.close();
            }
            else
            {
                throw new Exception();
            }
            if(!tile.YesTime())
            {
                db = getWritableDatabase();
                String delete = "Delete From Tile where id="+tile.ID;
                db.execSQL(delete);
                tile = GetTile(tile);

            }
            return tile;
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public Tile insert(Tile tile) {
        try {
            return GetTile(tile);
        } catch (Exception e) {
            e.printStackTrace();


            try {
                ContentValues content = createContentValues(tile.bmp);
                content.put("id", CountTile() + 1);
                content.put("x", tile.x);
                content.put("y", tile.y);
                content.put("scale", tile.scale);
                content.put("day", tile.Days());
                content.put("month", tile.Monthes());
                content.put("year", tile.Years());
                content.put("hour", tile.Hours());
                content.put("minute", tile.Minutes());
                content.put("second", tile.Seconds());
                SQLiteDatabase db = getWritableDatabase();
                db.insert("Tile", null, content);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            return tile;
        }
    }

    public void UpdateTimeCash()
    {
        UpdateTimeCash(getWritableDatabase());
    }

    public void UpdateTimeCash(SQLiteDatabase db)
    {
        try {
            if(Count() < 1)
                throw new Exception();

            ContentValues context = new ContentValues();
            context.put("time", MapHelper.GetTime());
            db.update("Cash", context, null, null);
        }
        catch (Exception ex)
        {

            ContentValues context = new ContentValues();
            context.put("time", MapHelper.GetTime());
            context.put("id", 1);
            db.insert("Cash", null, context);
        }
        GetTimeCash();
    }

    public void GetTimeCash()
    {
        try {
            try {
                SQLiteDatabase db = getReadableDatabase();
                String sql = "SELECT time FROM Cash;";
                Cursor cur = db.rawQuery(sql, null);
                if (cur.moveToFirst() == true) {

                    MapHelper.SetTime( cur.getString(0));
                    cur.close();
                }
            } catch (Exception ex) {
                UpdateTimeCash();
            }
        }
        catch(Exception ex)
        {

        }
    }

    public int Count()
    {
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "SELECT Count(*) FROM Cash;";
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

    public int CountTile()
    {
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "SELECT Count(*) FROM Tile;";
            Cursor cur = db.rawQuery(sql, null);
            int count = 0;
            if (cur.moveToFirst() == true) {

                count = cur.getInt(0);
            }
            cur.close();
            return  count + Count();
        } catch (Exception ex) {
            return  0;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static ContentValues createContentValues(String column, byte[] image) {
        ContentValues cv = new ContentValues();
        cv.put(column, image);
        return cv;
    }

    public static ContentValues createContentValues(byte[] image)
    {
        return createContentValues("bmp", image);
    }

    public static ContentValues createContentValues(String column, Bitmap image) {
        return createContentValues(column, getBytes(image));
    }

    public static ContentValues createContentValues(Bitmap image) {
        return createContentValues(getBytes(image));
    }

}
