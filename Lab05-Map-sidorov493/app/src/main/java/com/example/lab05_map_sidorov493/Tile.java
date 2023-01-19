package com.example.lab05_map_sidorov493;

import android.app.Activity;
import android.graphics.Bitmap;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tile {
    public int scale;
    public int x;
    public int y;
    Bitmap bmp;
    public int ID = -1;

    public Bitmap GetBMP()
    {
        return bmp;
    }

    public void SetBMP(Bitmap bmp)
    {
        this.bmp = bmp;
    }

    public Tile(int x, int y, int scale, Bitmap bmp)
    {
        this(x, y, scale);
        SetBMP(bmp);
    }

    public Tile(int x, int y, int scale)
    {
        this();
        this.x = x;
        this.y = y;
        this.scale = scale;
        nowTime = new Tile();
    }

    Tile nowTime;
    public Tile()
    {
        SetDatetimeNow();
    }


    public void SetDatetimeNow()
    {

        SetTimeStamp(new Date());
    }

    public String TimeStamp;

    public void SetdateTime(String date)
    {
        SetTimeStamp(new Date(date));
    }

    public void SetDateTime(String date)
    {
        TimeStamp = date;
    }

    public void SetTimeStamp(Date date)
    {
        TimeStamp = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(date);
    }

    public void SetDateTime(int hour, int minute, int second, int day, int month, int year)
    {
        //SetTimeStamp(new Date(hour, minute, second, day, month, year));
        SetTimeStamp(new Date(year-1900, month, day, hour, minute, second));
    }

    public void SetTimeStamp(long date)
    {
        SetTimeStamp(new Date(date));
    }

    public String GetDatetime()
    {
        return TimeStamp;
    }

    public Date GetTimeStamp()
    {
        return new Date(Date.parse(TimeStamp));
    }

    public Date GetTimeStamp1()
    {
        return new Date(Years()-1900, Monthes()-1, Days(), Hours(), Minutes(), Seconds());
    }

    public int Days()
    {
        //return Integer.parseInt(GetDate().split(".")[0]);
        return Integer.parseInt(GetDate1().split("/")[0]);
    }

    public String GetDate1()
    {
        return GetDate().replace('.', '/');
    }

    public int Monthes()
    {
        return Integer.parseInt(GetDate1().split("/")[1]);
    }

    public int Years()
    {
        return Integer.parseInt(GetDate1().split("/")[2]);
    }

    public int DeltaDays() {
        nowTime.SetDatetimeNow();
        int deltaDays = DeltaDays2();
        if (deltaDays < 0)
            deltaDays += 31;
        return deltaDays;
    }

    public int DeltaDays2()
    {
        nowTime.SetDatetimeNow();
        int deltaDays = DeltaDays1();
        if(DeltaHours2() < 0)
            deltaDays--;
        if(DeltaHours2() > 24)
            deltaDays++;
        return deltaDays;
    }

    public int DeltaDays1()
    {
        nowTime.SetDatetimeNow();
        return nowTime.Days() - Days();
    }

    public int DeltaHours()
    {
        int deltaHours = DeltaHours2();
        if(deltaHours < 0)
            deltaHours+=24;
        if(deltaHours >= 24)
            deltaHours-=24;

        return deltaHours;
    }

    public int DeltaHours2()
    {
        nowTime.SetDatetimeNow();
        int deltaHours = DeltaHours1();
        if(DeltaMinutes2() < 0)
            deltaHours--;
        if(DeltaMinutes2() >= 60)
            deltaHours++;
        return deltaHours;
    }

    public int DeltaHours1()
    {
        nowTime.SetDatetimeNow();
        return nowTime.Hours() - Hours();
    }

    public int Hours()
    {
        return Integer.parseInt(GetTime().split(":")[0]);
    }

    public int Minutes()
    {
        return Integer.parseInt(GetTime().split(":")[1]);
    }

    public int DeltaMinutes()
    {
        int deltaMinutes = DeltaMinutes2();
        if(deltaMinutes < 0)
            deltaMinutes+=60;
        if(deltaMinutes >= 60)
            deltaMinutes-=60;

        return deltaMinutes;
    }

    public int DeltaMinutes2()
    {
        nowTime.SetDatetimeNow();
        int deltaMinute = DeltaMinutes1();
        if(DeltaSeconds1() < 0)
            deltaMinute--;
        if(DeltaSeconds1() >= 60)
            deltaMinute++;
        return deltaMinute;
    }

    public int DeltaMinutes1()
    {
        nowTime.SetDatetimeNow();
        int minute1 = nowTime.Minutes();
        int minute2 = this.Minutes();
        int deltaMinute = minute1 - minute2;
        return deltaMinute;
    }


    public int Seconds()
    {
        return Integer.parseInt(GetTime().split(":")[2]);
    }

    public int DeltaSeconds1()
    {
        nowTime.SetDatetimeNow();
        int second1 = nowTime.Seconds();
        int second2 = this.Seconds();
        int deltaSecond = second1 - second2;
        return deltaSecond;
    }

    public int DeltaSeconds()
    {
        int deltaSecond = DeltaSeconds1();
        if(deltaSecond < 0)
            deltaSecond += 60;
        if(deltaSecond >= 60)
            deltaSecond -= 60;
        return deltaSecond;
    }

    public Boolean YesTime()
    {
        try {
            int value = MapHelper.GetValueTime();
            String measurement = MapHelper.GetMeasurementTime();
            if (measurement.equals("s"))
                return DeltaSeconds() < value;
            if (measurement.equals("m"))
                return DeltaMinutes() < value;
            if (measurement.equals("h"))
                return DeltaHours() < value;
            if(measurement.equals("d"))
                return DeltaDays() < value;
            return false;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public String GetDate()
    {
        return GetDatetime().split(" ")[0];
    }

    public String GetTime()
    {
        return GetDatetime().split(" ")[1];
    }

    public Tile(int x, int y, int scale, Activity ctx)
    {
        this(x, y, scale);
        try {
            Tile tile = TilesDB.GetDB(ctx).GetTile(x, y, scale);
            SetBMP(tile.bmp);
        } catch (Exception e) {
            e.printStackTrace();
            if(!MapHelper.api)
            {
                return;
            }

            final Bitmap[] bmp = new Bitmap[1];
            ApiHelper req = new ApiHelper(ctx) {
                @Override
                public void on_ready(String res) {
                    if (!Ready)
                        return;
                    super.on_ready(res);
                    try {
                        bmp[0] = BitmapFromJsonObject(res, "data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            req.SendNoParamsStop(MapHelper.GetUrl(ctx) + "raster", String.format("%d/%d-%d", scale, x, y));
            SetBMP(bmp[0]);
        }

    }
}
