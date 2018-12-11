package ru.lizzzi.crossfit_rekord.data;

import android.provider.BaseColumns;

public class MyResultDBContract {

    public MyResultDBContract(){

    }

    public static final class dbMyResult implements BaseColumns{

        public final static String TABLE_NAME = "myResult";

        public final static String columnExercise = "Exercise";
        public final static String columnResult = "Result";
    }
}

