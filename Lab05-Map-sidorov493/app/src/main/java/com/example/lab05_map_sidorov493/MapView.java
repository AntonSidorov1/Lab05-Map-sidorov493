package com.example.lab05_map_sidorov493;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;

public class MapView extends SurfaceView {

    ArrayList<Tile> tiles = new ArrayList<>();
    Activity ctx;

    public Tile GetTile(int x, int y, int scale) {

        for (int i = 0; i < tiles.size(); i++) {
            Tile t = tiles.get(i);
            if (t.x == x && t.y == y && t.scale == scale) {
                //TilesDB.GetDB(ctx).insert(t);
                if(t.bmp == null && MapHelper.api)
                    tiles.set(i, new Tile(x, y, scale, ctx));
                t = tiles.get(i);
                return t;
            }
        }
        Tile nt = new Tile(x, y, scale, ctx);
        if(nt.bmp != null) {
            tiles.add(nt);
            if (MapHelper.api) {
                TilesDB.GetDB(ctx).insert(nt);
            }
        }
        return nt;
    }

    float last_x, last_y;

    public int current_level_index = 0;

    public int[] levels = new int[]{16, 8, 4, 2, 1};
    int[] x_tiles = new int[]{54, 108, 216, 432, 864};
    int[] y_tiles = new int[]{27, 54, 108, 216, 432};

    int tile_width = 100;
    int tile_height = 100;

    float offset_x = 0.0f;
    float offset_y = 0.0f;

    Paint p;
    int width, height;


    public MapView(Context context) {
        super(context);
        SetMap(context);
    }

    public MapView(Activity context) {
        super(context);
        SetMap(context);
    }


    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SetMap(context);
    }

    public MapView(Activity context, AttributeSet attrs) {
        super(context, attrs);
        SetMap(context);
    }

    public void SetScale(int level, int value)
    {
        current_level_index = level;
        ScaleValue_Index = value;
        invalidate();
    }

    public void SetScale(ScaleDB db)
    {
        db.GetScale(this);
    }

    public void SetScale()
    {
        SetScale(ScaleDB.GetDB(ctx));
    }

    float[] scales;
    public void SetMap(Context ctx)
    {
        try {

            this.ctx = (Activity) ctx;
        }
        catch (Exception ex)
        {

        }

        p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.RED);

        setWillNotDraw(true);

        scales = MapHelper.ScaleValues();
        //invalidate();
    }

    Boolean draw = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int act = event.getAction();
        switch (act)
        {
            case MotionEvent.ACTION_DOWN:
            {
                last_x = event.getX();
                last_y = event.getY();
                draw = true;
                return  true;
            }
            case MotionEvent.ACTION_MOVE:
            {
                draw = false;
                float x = event.getX();
                float y = event.getY();

                float dx = x - last_x;
                float dy = y - last_y;

                offset_x += dx;
                offset_y += dy;
                invalidate();

                last_x = x;
                last_y = y;
                return true;
            }
            case MotionEvent.ACTION_UP:
            {
                if(draw)
                {
                    draw = false;
                    invalidate();
                }
                return true;
            }
        }


        return false;
    }

    boolean rect_interects_rect(
            int ax0, int ay0, int ax1, int ay1,
            int bx0, int by0, int bx1, int by1)
    {
        if(!rect_interects_rect_X(ax0, ax1, bx0, bx1)) return  false;
        if(!rect_interects_rect_Y(ay0, ay1, by0, by1)) return  false;
        return true;
    }

    boolean rect_interects_rect_X(
            int ax0, int ax1,
            int bx0, int bx1)
    {
        if(ax1 < bx0) return  false;
        if(ax0 > bx1) return  false;
        return true;
    }

    boolean rect_interects_rect_Y(
            int ay0, int ay1,
            int by0, int by1)
    {
        if(ay1 < by0) return  false;
        if(ay0 > by1) return  false;
        return true;
    }


    public int ScaleValue_Index = -1;


    public void ErrorDraw()
    {

    }

    float aMinesToB(float a, int b, float mines)
    {
        int a1 = (int)a;
        int mines1 = (int)mines;
        while (a1 > b + mines1)
        {
            a -= mines;
            a1 = (int)a;
        }
        return a1;
    }

    float aPlusToB(float a, int b, float plus)
    {
        int a1 = (int)a;
        int plus1 = (int)plus;
        while (-a1 > b - plus1)
        {
            a += plus1;
            a1 = (int)a;
        }
        return a1;
    }

    @Override
    public void invalidate() {
        setWillNotDraw(false);
        super.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        try {

            ApiHelper helper = new ApiHelper(ctx);
            helper.SendNoParamsStop(MapHelper.GetUrl(ctx) + "raster", "");
            MapHelper.api = helper.Ready;

            width = canvas.getWidth();
            height = canvas.getHeight();

            float canvas_scale;
            try {
                canvas_scale = scales[ScaleValue_Index];
            } catch (Exception ex) {
                canvas_scale = 0.0f;
            }
            float canvas_width = canvas_scale, canvas_height = canvas_scale;
            try {
                if (canvas_height == 0.0f || canvas_width == 0.0f)
                    throw new Exception();
                canvas.scale(canvas_width, canvas_height);
            } catch (Exception ex) {
                ScaleValue_Index = MapHelper.DefaultScale();
                canvas_scale = scales[ScaleValue_Index];
                canvas_width = canvas_scale;
                canvas_height = canvas_scale;
                canvas.scale(canvas_width, canvas_height);
            } finally {
            }
            width = canvas.getWidth();
            height = canvas.getHeight();
            canvas.drawColor(Color.WHITE);
        /*if(!helper.Ready)
        {
            ErrorDraw();
            return;
        }*/
            int screan_x0 = 0;
            int screan_y0 = 0;
            int screan_x1 = width - 1;
            int screan_y1 = height - 1;

            int scale = levels[current_level_index];

            int w = x_tiles[current_level_index];
            int h = y_tiles[current_level_index];
            
            int xView = (int)offset_x, yView = (int)offset_y;


            offset_x = aMinesToB(offset_x, screan_x0, 400.0f);
            offset_y = aMinesToB(offset_y, screan_y0, 400.0f);

            offset_x = aPlusToB(offset_x, w*tile_width, 400.0f);
            offset_y = aPlusToB(offset_y, h*tile_height, 400.0f);


            for (int y = 0; y < h; y++) {
                int y0 = y * tile_height + (int) offset_y;
                int y1 = y0 + tile_height;
                if (y1 < screan_y0) {
                    y = (screan_y0 - (int) offset_y) / tile_height;
                    y--;
                    continue;
                }
                if (y0 > screan_y1)
                    break;
                if (!rect_interects_rect_Y(screan_y0, screan_y1, y0, y1))
                    continue;
                for (int x = 0; x < w; x++) {
                    int x0 = x * tile_width + (int) offset_x;
                    int x1 = x0 + tile_width;
                    if (x1 < screan_x0) {
                        x = (screan_x0 - (int) offset_x) / tile_width;
                        x--;
                        continue;
                    }

                    if (x0 > screan_x1)
                        break;

                    if (!rect_interects_rect(screan_x0, screan_y0, screan_x1, screan_y1, x0, y0, x1, y1))
                        continue;

                    Tile t = GetTile(x, y, scale);
                    if (t.bmp != null)
                        canvas.drawBitmap(t.bmp, x0, y0, p);

                    canvas.drawRect(x0, y0, x1, y1, p);
                    canvas.drawText(String.valueOf(scale) + ", " + String.valueOf(x) +
                            ", " + String.valueOf(y), (x0 + x1) / 2, (y0 + y1) / 2, p);

                }
            }
        }
        catch(Exception ex)
        {

        }
        ScaleDB.GetDB(ctx).UpdateScale(this);
    }
}
