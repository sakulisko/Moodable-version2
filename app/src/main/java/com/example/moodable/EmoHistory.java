package com.example.moodable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class EmoHistory extends AppCompatActivity {
    Button backToMonthButton;
    Button clearButton;
    ScrollView scrollView;
    LinearLayout linearLayout;

    Button averageButton;

    public List<Emo> emos = new ArrayList<>();

    @SuppressLint({"MissingInflatedId", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emo_history);

        //po kliknuti back se presmeruje do stranky mesice/kalendare
        backToMonthButton = findViewById(R.id.backToMonthButton);
        backToMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmoHistory.this, MonthPage.class);
                startActivity(intent);
            }
        });

        //po kliknuti clear smaze vsechny dat v souboru
        clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear(view);
                Intent intent = new Intent(EmoHistory.this, EmoHistory.class);
                startActivity(intent);
            }
        });

        //nacte a ulozi do seznamu objektu Emo
        emos = read();
        //Toast.makeText(getApplicationContext(), emos.get(0).date, Toast.LENGTH_LONG).show();

        //tridi sestupne seznam pomoci Comparatoru
        Collections.sort(emos, new AveragePerMonth().new emoSorter());
        Collections.reverse(emos);

        //po kliknuti se presmeruje do stranky prumeru emoci kazdeho mesice
        averageButton = findViewById(R.id.averageButton);
        averageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmoHistory.this, AveragePerMonth.class);
                startActivity(intent);
            }
        });

        //behem iteraci pridame emoji do stranky historie
        scrollView = findViewById(R.id.scrowView);
        linearLayout = findViewById(R.id.linearLayout);
        for (int i=0; i< emos.size(); i++) {
            TextView dateText = new TextView(this);
            String date = emos.get(i).date;
            dateText.setText(date);
            linearLayout.addView(dateText);
            ImageView emoji = new ImageView(this);
            emoji.setId(i);
            switch (emos.get(i).emo){
                case "smile":
                    emoji.setImageResource(R.drawable.smile);
                    break;
                case "love":
                    emoji.setImageResource(R.drawable.love);
                    break;
                case "sad":
                    emoji.setImageResource(R.drawable.sad);
                    break;
                case "angry":
                    emoji.setImageResource(R.drawable.angry);
                    break;
            }
            emoji.setScaleType(ImageView.ScaleType.CENTER_CROP);
            emoji.setBackgroundColor(00000000);
            emoji.setLayoutParams(new ViewGroup.LayoutParams((int) convertPixelsToDp(1170f, emoji.getContext()), (int) convertPixelsToDp(1200f, emoji.getContext())));
            linearLayout.addView(emoji);
        }
    }
    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    public List<Emo> read(){
        Log.e("File","read.start");
        String filename = "emoData";
        BufferedReader reader = null;
        SimpleDateFormat format=new SimpleDateFormat("dd-mm-yyyy");
        try  {
            //vytvoreni instance objektu FileInputStream
            FileInputStream fis = openFileInput(filename);
            //pres InputStreamReader vytvorime objekt BufferedReader
            reader = new BufferedReader(new InputStreamReader(fis));
            List<Emo> emos = new ArrayList<>();
            String line = "";
            List<String> linesDate = new ArrayList<>();
            List<String> lines = new ArrayList<>();
            //nacteni po radku. Skonci kdyz uz neni dalsi data
            while ((line=reader.readLine())!=null){
                String[] date_emo = line.split(": ");
                String newDate = date_emo[0];
                if (linesDate.contains(newDate)) {
                    replace(lines, line);
                }
                else {
                    linesDate.add(date_emo[0]);
                    lines.add(line);
                    emos.add(new Emo(date_emo[0], date_emo[1]));
                }
            }
            return emos;
        }catch (IOException e){
            Log.e("File",e.getMessage());
        } finally {
            Log.e("File","read.finally");
            //vypneme stream
            try {
                if(reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private void replace(List<String> lines, String newLine) {
        for (int i=0; i<lines.size(); i++) {
            if (lines.get(i).contains(newLine.split(": ")[0])) {
                lines.set(i, newLine);
                break;
            }
        }
    }
    public void clear(View v){
        String filename = "emoData";
        String fileContents = "";
        BufferedWriter writer = null;
        //algoritmus mazani dat: vynahradit prazdnym stringem misto obsah souboru
        try  {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(fileContents);
        }catch (IOException e){
            Log.e("File",e.getMessage());
        }finally {
            try {
                if(writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
