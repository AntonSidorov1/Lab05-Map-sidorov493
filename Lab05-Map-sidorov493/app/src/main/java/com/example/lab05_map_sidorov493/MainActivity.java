package com.example.lab05_map_sidorov493;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button exit;
    TextView info;
    MapView mv;
    Boolean exiting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapHelper.exit = false;
        exit = findViewById(R.id.CloseProgram);
        info = findViewById(R.id.AppInfo);
        info.setText(MapHelper.AppInfoWidthURL(this));

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Exit_Click(view);
            }
        });

        MapHelper.ChangeTime(this);
        mv = findViewById(R.id.map);
        mv.SetScale();
       // mv.invalidate();
    }

    public void ZoomOut_Click(View v)
    {
        if(mv.current_level_index == 0) return;
        mv.offset_x += mv.width / 2.0f;
        mv.offset_y += mv.height / 2.0f;
        mv.offset_x /= 2.0f;
        mv.offset_y /= 2.0f;
        mv.current_level_index--;
        mv.invalidate();
    }

    public void ZoomIn_Click(View v)
    {

        if(mv.current_level_index == mv.levels.length - 1) return;
        mv.offset_x *= 2.0f;
        mv.offset_y *= 2.0f;
        mv.offset_x -= mv.width / 2.0f;
        mv.offset_y -= mv.height / 2.0f;
        mv.current_level_index++;
        mv.invalidate();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id)
        {
            case R.id.exit: {

                View v = exit;
                Exit_Click(v);
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void Exit_Click(View v)
    {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);

        bld.setPositiveButton("Нет",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel(); // Закрываем диалоговое окно
                        MapHelper.exit = false;
                    }
                });
        bld.setNegativeButton("Да", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish(); // Закрываем Activity
            }
        });
        AlertDialog dlg = bld.create();
        dlg.setTitle("Выход из приложения");
        dlg.setMessage("Уважаемый пользователь \n" +
                "Вы действительно хотите выйти из программы \n" +
                "Вы, также, можете запустить программу снова \n" +
                "С уважением и любовью, Создатель программы, Сидоров Антон Дмитриевич");
        dlg.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        MapHelper.ChangeTimeInDB(this);
        UrlStorege.GetDB(this).UpdateUrl();
        info.setText(MapHelper.AppInfoWidthURL(this));
        mv.invalidate();
        MapHelper.exit = false;
    }


    public void Settings_Click(View v)
    {
        MapHelper.mv = mv;
        Intent i = new Intent(this, Settings.class);
        startActivityForResult(i, 200);
    }

}