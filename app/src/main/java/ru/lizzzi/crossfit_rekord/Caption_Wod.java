package ru.lizzzi.crossfit_rekord;

/**
 * Created by Liza on 24.11.2017.
 */

public class Caption_Wod {
    String day;
    String month;
    String caption_wod;
    String level;
    String result;

    Caption_Wod(String _day, String _month, String _caption_wod, String _level, String _result){
        day = _day;
        month = _month;
        caption_wod = _caption_wod;
        level = _level;
        result = _result;
    }
}
