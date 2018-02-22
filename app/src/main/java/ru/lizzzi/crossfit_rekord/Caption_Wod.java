package ru.lizzzi.crossfit_rekord;

/**
 * Created by Liza on 24.11.2017.
 */

public class Caption_Wod {
    public String day;
    public String month;
    public String caption_wod;
    public String level;
    public String result;

    Caption_Wod(String _day, String _month, String _caption_wod, String _level, String _result){
        day = _day;
        month = _month;
        caption_wod = _caption_wod;
        level = _level;
        result = _result;
    }
}
