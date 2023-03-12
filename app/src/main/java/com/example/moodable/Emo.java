package com.example.moodable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

// The class should be final to limit who can extend it.
public class Emo {

    // Fields should be final, this looks like POJO class, it should be immutable, to avoid issues
    // when it's used from multiple threads.
    public String date;
    public String emo;
    public Emo (String date, String emo){
        this.date = date;
        this.emo = emo;
    }

}
