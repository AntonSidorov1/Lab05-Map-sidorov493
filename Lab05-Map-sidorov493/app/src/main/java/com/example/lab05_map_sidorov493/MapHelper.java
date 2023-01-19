package com.example.lab05_map_sidorov493;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;

public class MapHelper {



    public static String DomainUrl="labs-api.spbcoit.ru";
    public static String PortUrl = "lab/tiles/api";


    public  static String GetUrl(Context context)
    {
        UrlStorege.GetDB(context).GetUrl();
        return Url(context);
    }

    public static String Url(@Nullable Context context) {
        String url = "";

        DomainUrl = DomainUrl.replace('\\', '/');
        PortUrl = PortUrl.replace('\\', '/');

        int end = 0;
        String end1 = "";
        if(PortUrl != "" && !PortUrl.equals("") && PortUrl != null) {
            try {
                if(DomainUrl.contains("/") || DomainUrl.contains(":") || DomainUrl.contains("?"))
                    throw  new Exception();
                String[] parts = PortUrl.split("/");
                Integer.parseInt(parts[0]);
                url = "http://" + DomainUrl + ":" + PortUrl;
            } catch (Exception ex) {
                end = DomainUrl.length() - 1;
                end1 = DomainUrl.substring(end, end + 1);
                String start = PortUrl.substring(0, 1);
                if (end1 != "/" && !end1.equals("/"))
                    if (start != "/" && !start.equals("/"))
                        url = "http://" + DomainUrl + "/" + PortUrl;
                    else
                        url = "http://" + DomainUrl + PortUrl;
                else
                    url = "http://" + DomainUrl + PortUrl;
            }
        }
        else
            url = "http://"+DomainUrl;
        end = url.length()-1;
        end1 = url.substring(end, end+1);
        if(end1 != "/" && !end1.equals("/"))
            url+="/";
        UrlStorege.GetDB(context).UpdateUrl();
        return  url;
    }

    public static String AppInfo()
    {
        return "Lab05 Map \n" +
                "sidorov493 \n" +
                "Сидоров Антон Дмитриевич";
    }

    public static String AppInfoWidthURL(Activity ctx)
    {
        return  AppInfo() + "\n"+GetUrl(ctx);
    }
    //static int length = 60;
    static int div = 2;
    static float length1 = 40f;
    static int length()
    {
        return (int)(length1*div);
    }

    public static int DefaultScale()
    {
        return ((int)(length()/length1)*10)-1;
    }

    public static float[] ScaleValues() {
        int length = length();
        float[] values = new float[length];
        float startValue = length1 / (float)length;
        startValue /= 10f;
        for (int i = 0; i < values.length; i++) {
            values[i] = startValue * (i + 1);
        }
        return values;
    }

    public static Boolean exit;

    public static MapView mv;

    public static String TimeCash = "1m";

    public static int GetValueTime()
    {
        try {
            String value = TimeCash.substring(0, TimeCash.length() - 1);
            int number = Integer.parseInt(value);
            if (GetMeasurementTime().equals("d") && number > 12) {
                TimeCash = "12d";
                return GetValueTime();
            } else if (GetMeasurementTime().equals("h") && number >= 24) {
                number /= 24;
                TimeCash = number + "d";
                return GetValueTime();
            } else if (GetMeasurementTime().equals("m") && number >= 60) {
                number /= 60;
                TimeCash = number + "h";
                return GetValueTime();
            } else if (GetMeasurementTime().equals("s") && number >= 60) {
                number /= 60;
                TimeCash = number + "m";
                return GetValueTime();
            }
            if(number < 1)
            {
                return 1;
            }
            return number;
        }
        catch (Exception ex)
        {
            return 1;
        }
    }

    public static boolean Loaded = false;
    public static void SetTime(String time)
    {
        TimeCash = time;
    }

    public static String GetTime()
    {
        String time = GetValueTime()+GetMeasurementTime();
        SetTime(time);

        return time;
    }

    public static String ChangeTime (String time)
    {
        SetTime(time);
        String time1 = GetTime();
        return time1;
    }

    public static String ChangeTime(String time, Context context)
    {
        String time1 = ChangeTime(time);
        TilesDB.GetDB(context).GetTimeCash();
        time1 = ChangeTime(GetTime());
        return time1;
    }

    public static String ChangeTime(Context context)
    {
        ChangeTime(GetTime(), context);
        Loaded = true;
        return ChangeTimeInDB(context);

    }

    public static String ChangeTimeInDB(Context context)
    {

        TilesDB.GetDB(context).UpdateTimeCash();

        return ChangeTime(GetTime(), context);

    }

    public static String GetMeasurementTime()
    {
        try
        {
            String measurement = TimeCash.substring(TimeCash.length() - 1, TimeCash.length());
            if(measurement.equals("m") || measurement.equals("h") || measurement.equals("s") || measurement.equals("d"))
                return  measurement;
            throw  new Exception();
        }
        catch (Exception ex)
        {
            return "m";
        }
    }
    public static Boolean api = false;
}
