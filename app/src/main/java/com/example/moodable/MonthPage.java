package com.example.moodable;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MonthPage extends AppCompatActivity {
    CalendarView calendarView;
    Button historyButton;
    ImageButton emoji1;
    ImageButton emoji2;
    ImageButton emoji3;
    ImageButton emoji4;
    ProgressBar progressBar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //int[] date = new int[3];

        super.onCreate(savedInstanceState);

        // zpracovavani datum: 54-74
        Date currentDate = new Date();

        SimpleDateFormat dateFormat = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        }
        System.out.println(dateFormat.format(currentDate));

        String[] dateStr = dateFormat.format(currentDate).split("-");

        int[] date = {Integer.parseInt(dateStr[2]), Integer.parseInt(dateStr[1]), Integer.parseInt(dateStr[0])};
        int[] currentDateArr = date;
        long[] currentTimeStamp = {Long.MAX_VALUE};
        long[] chosedTimeStamp = {Long.MAX_VALUE};
        try {
            currentTimeStamp[0] = convertToTimeStamp(currentDateArr[2], currentDateArr[1], currentDateArr[0]);
            chosedTimeStamp[0] = currentTimeStamp[0];
        } catch (ParseException e) {
            System.out.println("cannot parse");
        }

        //kalendar view a udalosti: 76-98
        setContentView(R.layout.activity_current_month_page);
        calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                //Toast.makeText(getApplicationContext(), year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
                try {
                    chosedTimeStamp[0] = convertToTimeStamp(year, month+1, day);
                    if (currentTimeStamp[0] < chosedTimeStamp[0]) {
                        Toast.makeText(getApplicationContext(), "You cannot chose future date", Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (ParseException e) {
                    System.out.println(e.getMessage());
                }
                date[0] = day;
                date[1] = month+1;
                date[2] = year;
                //Toast.makeText(getApplicationContext(), "You chose " + date[0] + "-" + date[1] + "-" + date[2], Toast.LENGTH_LONG).show();
            }
        });

        //ruzna emoji a udalosti: 100-184
        emoji1 = findViewById(R.id.emoji1);
        emoji1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentTimeStamp[0] > chosedTimeStamp[0]) {
                    Toast.makeText(getApplicationContext(), "You were happy on" + date[0] + "-" + date[1] + "-" + date[2], Toast.LENGTH_LONG).show();

                }
                else if (currentTimeStamp[0] == chosedTimeStamp[0]){
                    Toast.makeText(getApplicationContext(), "You are happy today!", Toast.LENGTH_LONG).show();

                }
                else {
                    return;
                }

                String content = date[0] + "-" + date[1] + "-" + + date[2] + ": " + "smile\n";
                write(content);

            }
        });

        emoji2 = findViewById(R.id.emoji2);
        emoji2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentTimeStamp[0] > chosedTimeStamp[0]) {
                    Toast.makeText(getApplicationContext(), "You were in love on" + date[0] + "-" + date[1] + "-" + date[2], Toast.LENGTH_LONG).show();

                }
                else if (currentTimeStamp[0] == chosedTimeStamp[0]){
                    Toast.makeText(getApplicationContext(), "You are in love today!", Toast.LENGTH_LONG).show();

                }
                else {
                    return;
                }

                String content = date[0] + "-" + date[1] + "-" + + date[2] + ": " + "love\n";
                write(content);
            }
        });

        emoji3 = findViewById(R.id.emoji3);
        emoji3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentTimeStamp[0] > chosedTimeStamp[0]) {
                    Toast.makeText(getApplicationContext(), "You were angry on" + date[0] + "-" + date[1] + "-" + date[2], Toast.LENGTH_LONG).show();

                }
                else if (currentTimeStamp[0] == chosedTimeStamp[0]){
                    Toast.makeText(getApplicationContext(), "You are angry today!", Toast.LENGTH_LONG).show();

                }
                else {
                    return;
                }

                String content = date[0] + "-" + date[1] + "-" + + date[2] + ": " + "angry\n";
                write(content);
            }
        });

        emoji4 = findViewById(R.id.emoji4);
        emoji4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentTimeStamp[0] > chosedTimeStamp[0]) {
                    Toast.makeText(getApplicationContext(), "You were sad on" + date[0] + "-" + date[1] + "-" + date[2], Toast.LENGTH_LONG).show();

                }
                else if (currentTimeStamp[0] == chosedTimeStamp[0]){
                    Toast.makeText(getApplicationContext(), "You are sad today!", Toast.LENGTH_LONG).show();

                }
                else {
                    return;
                }

                String content = date[0] + "-" + date[1] + "-" + + date[2] + ": " + "sad\n";
                write(content);
            }
        });

        progressBar = findViewById(R.id.progressBar);
        historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // progress bar je viditelny, kdyz uzivatel klikne na historyButton
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MonthPage.this, EmoHistory.class);
                startActivity(intent);
            }
        });

    }
    private long convertToTimeStamp(int year, int month, int day) throws ParseException {
        String time = year + "-" + month + "-" + day;
        //String time="2018-1-9";
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        //nastavi casovy format ve stringu, ktery chceme nacist
        Date date = format.parse(time);
        //prevede do timestamp
        Long timestamp=date.getTime();
        return timestamp;
    }
    //zapsani do souboru
    public void write(String fileContents){
        //zalozeni souboru
        String filename = "emoData";
        //funkce skonci, kdyz kontext, ktery chceme zapsat, je prazdny
        if (fileContents.isEmpty()) {
            Log.e("File","FileContents.isEmpty()");
            return;
        }
        BufferedWriter writer = null;
        try  {
            //vytvoreni instance objektu FileOutputStream
            FileOutputStream fos = openFileOutput(filename, Context.MODE_APPEND);
            //pres OutputStreamWriter vytvorime objekt BufferedWriter
            writer = new BufferedWriter(new OutputStreamWriter(fos));
            //pres objekt BufferedWriter zapiseme kontext do souboru
            writer.write(fileContents);
        }catch (IOException e){
            Log.e("File",e.getMessage());
        }finally {
            try {
                //vzdy rucne vypneme stream write, je jedno jestli nastala exception
                if(writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}