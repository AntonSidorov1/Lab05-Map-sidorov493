package com.example.lab05_map_sidorov493;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {
    Button exit;
    EditText domain, port;
    TextView info;
    MapView mv;
    Spinner scales, levels;
    EditText TextChash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        exit = findViewById(R.id.buttonExit);
        domain = findViewById(R.id.DomainNameText);
        port = findViewById(R.id.PortText);
        domain.setText(MapHelper.DomainUrl);
        port.setText(MapHelper.PortUrl);

        info = findViewById(R.id.AppInfo1);
        info.setText(MapHelper.AppInfoWidthURL(this));
        mv = MapHelper.mv;

        levels = findViewById(R.id.spinnerLevels);
        ArrayList<String> levaelTexts = new ArrayList<>();
        int[] levelValues = mv.levels;
        for(int i = 0; i < levelValues.length; i++)
        {
            levaelTexts.add(String.valueOf(levelValues[i]));
        }
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, levaelTexts);
        levels.setAdapter(levelAdapter);
        levels.setSelection(mv.current_level_index);

        scales = findViewById(R.id.scales);
        float []scaleValues = mv.scales;
        ArrayList<String> scaleTexts = new ArrayList<>();
        for(int i = 0; i < scaleValues.length; i++)
        {
            scaleTexts.add(String.valueOf(scaleValues[i]));
        }
        ArrayAdapter<String> scaleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scaleTexts);
        scales.setAdapter(scaleAdapter);
        scales.setSelection(mv.ScaleValue_Index);

        TextChash = findViewById(R.id.TextCash);
        TextChash.setText(MapHelper.GetTime());
    }


    public  void  DomainClear_Click(View v)
    {
        domain.setText("");
    }

    public void DomainStart_Click(View v)
    {
        domain.setText(MapHelper.DomainUrl);
    }

    public void PortStart_Click(View v)
    {
        port.setText(MapHelper.PortUrl);
    }

    public  void  PortClear_Click(View v)
    {
        port.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id)
        {
            case R.id.exit: {

                View v = exit;
                Exit1_Click(v);
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

    public void Exit1_Click(View v)
    {
        MapHelper.exit = true;
        Exit_Click(v);
    }

    public void Exit_Click(View v)
    {
        MapHelper.ChangeTime(TextChash.getText().toString());
        mv.current_level_index = levels.getSelectedItemPosition();
        mv.ScaleValue_Index = scales.getSelectedItemPosition();
        //GrapsParams.NowGraph = graphs.GetGraph();
        MapHelper.DomainUrl = domain.getText().toString();
        MapHelper.PortUrl = port.getText().toString();

        Intent i = getIntent();
        finish();
    }

    public void ClearCash_Click(View v)
    {
        TilesDB.GetDB(this).ClearCash();
    }
}