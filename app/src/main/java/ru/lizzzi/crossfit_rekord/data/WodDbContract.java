package ru.lizzzi.crossfit_rekord.data;

import android.provider.BaseColumns;


public class WodDbContract {
    private WodDbContract(){

    }

    public static final class DBWod implements BaseColumns{
        public final static String TABLE_NAME = "Wod";

        public final static String Column_quantity = "quantity";
        public final static String Column_exercise = "exercise";
        public final static String Column_weight = "weight";
        public final static String Column_wodkey = "wodkey";

    }

    public static final class DBCaptionWod implements BaseColumns{
        public final static String TABLE_NAME = "CaptionWod";

        public final static String Column_Day = "Day";
        public final static String Column_Month = "Month";
        public final static String Column_Year = "Year";
        public final static String Column_CaptionWod = "CaptionWod";
        public final static String Column_Time = "Time";
        public final static String Column_Level = "Level";
        public final static String Column_WodKey = "WodKey";
        public final static String Column_Result = "Result";


    }
}
