package com.example.moodable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class AveragePerMonth extends AppCompatActivity {
    List<Emo> emos = new ArrayList<>();
    Button backButton;
    ScrollView scrollView;
    LinearLayout linearLayout;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.average_per_month);

        //nacteni a ulozeni dat emoci
        emos = read();
        Collections.sort(emos, new AveragePerMonth().new emoSorter());

        List<int[]> averageSumOfEmoPerMonth = getAverageSumOfEmoPerMonth(emos);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AveragePerMonth.this, EmoHistory.class);
                startActivity(intent);
            }
        });

        linearLayout = findViewById(R.id.linearLayout);

        //zobrazeni prumernou emoci na strance
        for (int[] month_year_averageSum : averageSumOfEmoPerMonth) {
            TextView date = new TextView(this);
            //date.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            date.setText("Date: " + month_year_averageSum[0] + "-" + month_year_averageSum[1]);
            date.setGravity(Gravity.CENTER);
            //date.setText("hello");
            linearLayout.addView(date);

            ImageView emoji = new ImageView(this);
            switch (month_year_averageSum[2]) {
                case 1:
                    emoji.setImageResource(R.drawable.smile);
                    break;
                case 2:
                    emoji.setImageResource(R.drawable.love);
                    break;
                case 3:
                    emoji.setImageResource(R.drawable.sad);
                    break;
                case 4:
                    emoji.setImageResource(R.drawable.angry);
                    break;
            }
            emoji.setScaleType(ImageView.ScaleType.CENTER_CROP);
            emoji.setBackgroundColor(00000000);
            emoji.setLayoutParams(new ViewGroup.LayoutParams((int) EmoHistory.convertPixelsToDp(1170f, emoji.getContext()), (int) EmoHistory.convertPixelsToDp(1200f, emoji.getContext())));
            linearLayout.addView(emoji);
        }
    }
    public List<Emo> read(){
        Log.e("File","read.start");
        String filename = "emoData";
        BufferedReader reader = null;
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
        }finally {
            Log.e("File","read.finally");
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
    private static List<int[]> getAverageSumOfEmoPerMonth(List<Emo> emolist) {
        List<int[]> sumsPerMonth = new ArrayList<>();
        //date[0] + "-" + date[1] + "-" + + date[2] + ": " + "smile\n"
        int previousMonth = Integer.parseInt(emolist.get(0).date.split("-")[1]);
        int previousYear = Integer.parseInt(emolist.get(0).date.split("-")[2].split(":")[0]);
        int currentEmoSum = 0;
        int currentMonthSum = 0;
        for (int i=0; i<emolist.size(); i++) {

            Emo emo = emolist.get(i);
            String[] dateInArray = emo.date.split("-");
            int currentMonth = Integer.parseInt(dateInArray[1]);
            String currentEmo = emo.emo;
            int currentYear = Integer.parseInt(dateInArray[2]);
            switch (currentEmo) {
                case "smile":
                    currentEmoSum += 1;
                    break;
                case "love":
                    currentEmoSum += 2;
                    break;
                case "sad":
                    currentEmoSum += 3;
                    break;
                case "angry":
                    currentEmoSum += 4;
                    break;
            }
            if (emolist.size()==1) {
                int[] emoSum = new int[] {currentMonth, currentYear , currentEmoSum};
                sumsPerMonth.add(emoSum);
                break;
            }
            if (i == emolist.size()-1 || ((previousMonth != currentMonth || previousYear != currentYear)&& currentEmoSum > 0)) {
                double average = currentEmoSum / currentMonthSum;
                if (previousYear != currentYear) {
                    previousYear = currentYear;
                }
                int[] thisMonthYear = new int[]{previousMonth, previousYear, (int)Math.round(average)};
                sumsPerMonth.add(thisMonthYear);
                currentEmoSum = 0;
                currentMonthSum = 0;
                previousMonth = currentMonth;
                previousYear = currentYear;
            }

            currentMonthSum++;
        }
        return sumsPerMonth;
    }
    class emoSorter implements Comparator<Emo> {
        @Override
        public int compare(Emo emo1, Emo emo2) {
            Integer res = null;
            SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
            try {
                System.out.println(emo1.date + "---" + emo2.date);
                Date date1 = format.parse(emo1.date);
                Date date2 = format.parse(emo2.date);
                return date1.compareTo(date2);
            } catch (ParseException e) {
                System.out.println("cannot parse");
            }
            return res.intValue();
        }
    }
}
