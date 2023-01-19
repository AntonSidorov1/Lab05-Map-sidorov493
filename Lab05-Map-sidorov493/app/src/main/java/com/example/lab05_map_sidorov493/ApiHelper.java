package com.example.lab05_map_sidorov493;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Base64;


public class ApiHelper
{
    public Activity ctx;

    public String Method = "GET";


    public float DoubleFromJson(String res, String punkt, String Pole) throws JSONException {
        try {            JSONObject doc = new JSONObject(res);
            JSONObject curr = doc.getJSONObject(punkt);
            return (float) curr.getDouble(Pole);
        }
        catch(Exception e)
        {
            throw  new JSONException(e.getMessage());
        }
    }

    public float DoubleFromJsonObject(String res, String Pole) throws JSONException {
        try {            JSONObject doc = new JSONObject(res);
            JSONObject curr = doc;
            return (float) curr.getDouble(Pole);
        }
        catch(Exception e)
        {
            throw  new JSONException(e.getMessage());
        }
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    public Bitmap BitmapFromJsonObject(String res, String Pole) throws JSONException {
        try {            JSONObject doc = new JSONObject(res);
            JSONObject curr = doc;
            String image = curr.getString(Pole);
            //byte[] jpeg = Base64.getDecoder().decode(image);
            byte [] jpeg = Base64.decode(image, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
            return bmp;
        }
        catch(Exception e)
        {
            throw  new JSONException(e.getMessage());
        }
    }

    public int IntFromJsonObject(String res, String Pole) throws JSONException {
        try {            JSONObject doc = new JSONObject(res);
            JSONObject curr = doc;
            return (int) curr.getInt(Pole);
        }
        catch(Exception e)
        {
            throw  new JSONException(e.getMessage());
        }
    }

    public String StringFromJson(String res, String punkt, String Pole) throws JSONException {
        try {            JSONObject doc = new JSONObject(res);
            JSONObject curr = doc.getJSONObject(punkt);
            return curr.getString(Pole);
        }
        catch(Exception e)
        {
            throw  new JSONException(e.getMessage());
        }
    }

    public String StringFromJsonObject(String res, String Pole) throws JSONException {
        try {            JSONObject doc = new JSONObject(res);
            JSONObject curr = doc;
            return curr.getString(Pole);
        }
        catch(Exception e)
        {
            throw  new JSONException(e.getMessage());
        }
    }


    public ApiHelper(Activity ctx)
    {
        this.ctx = ctx;
    }

    public void on_ready(String res){
        MessageOutput(GetMessageReady());
        MessageReadyOutput(GetMessageReady());
        Ready = true;
    }

    public void on_fail()
    {
        ctx.runOnUiThread(() -> {
            MessageOutput(GetMessageFatal());
            MessageFatalOutput(GetMessageFatal());
            Ready = false;
        });
    }

    public String GetMessageFatal()
    {
        return "";
    }

    public void MessageFatalOutput(String message)
    {

    }

    public void MessageOutput(String message)
    {

    }

    public String GetMessageReady()
    {
        return "";
    }

    public void MessageReadyOutput(String message)
    {

    }

    String http_get(String req, String payload) throws IOException
    {
        return http_get(req, payload, "GET");
    }

    String http_put(String req, String payload) throws IOException
    {
        return http_get(req, payload, "PUT");
    }

    String http_post(String req, String payload) throws IOException
    {
        return http_get(req, payload, "POST");
    }

    String http_delete(String req, String payload) throws IOException
    {
        return http_get(req, payload, "DELETE");
    }

    public Boolean JsonInput = false;
    public Boolean Params = true;

    String http_get(String req, String payload, String method) throws IOException
    {
        return http_get(req, payload,method,JsonInput);
    }

    String http_get(String req, String payload, String method, Boolean JsonInput) throws IOException
    {
        return http_get(req, payload, method, JsonInput, Params);
    }

    String http_get(String req, String payload, String method, Boolean JsonInput, Boolean params) throws IOException
    {
        Ready = false;
        String url1 = req;
        if(!JsonInput) {
            if(params) {
                url1 += "?" + payload;
            }
            else if(!payload.equals("")) {

                url1 += "/";
                url1 += payload;
            }
        }
        URL url = new URL(url1);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        byte[] outmsg = payload.getBytes("utf-8");

        con.setRequestMethod(method);
        if(JsonInput) {
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Content-Length", String.valueOf(outmsg.length));
            con.setDoOutput(true);
            con.setDoInput(true);
            BufferedOutputStream out = new BufferedOutputStream(con.getOutputStream());
            out.write(outmsg);
            out.flush();
        }

        InputStream is = con.getInputStream();
        BufferedInputStream inp = new BufferedInputStream(is);

        byte[] buf = new byte[1024];
        String res = "";

        while (true)
        {
            int num = inp.read(buf);
            if (num < 0) break;

            res += new String(buf, 0, num);
        }

        con.disconnect();

        Ready = true;
        return res;
    }


    public String res, session;

    public ApiHelper GetAPIHelper()
    {
        return  this;
    }

    public class NetOp implements Runnable
    {
        public String req, payload;

        public void run()
        {
            try
            {
                final String res = http_get(req, payload, Method);

                ctx.runOnUiThread(
                    new Runnable()
                    {
                        @Override
                        public void run() {
                            on_ready(res);
                        }
                    }
                );
                GetAPIHelper().res = res;
            }
            catch (Exception ex)
            {
                on_fail();
            }
        }
    }

    public Thread th;
    public void send(String req, String payload)
    {
        NetOp nop = new NetOp();
        nop.req = req;
        nop.payload = payload;

        th = new Thread(nop);
        th.start();
    }

    public void send (String req, String payload, Boolean params)
    {
        Params = params;
        send(req, payload);
    }

    public void sendNoParams(String req, String payload)
    {
        send(req, payload, false);
    }
    public Boolean Ready = false;
    public void on_ready()
    {
        on_ready(res);
    }


    public void SendStop()
    {
        Ready = false;
        Send();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        go_Ready();
    }

    public void go_Ready()
    {
        if(Ready) {
            on_ready();
        }
    }

    public void SendStop(String req, String payload)
    {
        Ready = false;
        send(req, payload);
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        go_Ready();
    }

    public void SendStop(String req, String payload, Boolean params)
    {
        Ready = false;
        send(req, payload, params);
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        go_Ready();
    }

    public void SendNoParamsStop(String req, String payload)
    {
        Ready = false;
        sendNoParams(req, payload);
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        go_Ready();
    }

    public void Send()
    {

    }
}


