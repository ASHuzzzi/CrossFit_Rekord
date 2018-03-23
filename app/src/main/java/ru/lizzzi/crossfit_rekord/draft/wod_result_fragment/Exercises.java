package ru.lizzzi.crossfit_rekord.draft.wod_result_fragment;

/**
 * Created by Liza on 22.11.2017.
 */

public class Exercises {
    public String quantity;
    public String exercise;
    public String weight;
    String wodkey;

    Exercises(String _quantity, String _exercise, String _weight, String _wodkey){
        quantity= _quantity;
        exercise= _exercise;
        weight= _weight;
        wodkey = _wodkey;
    }

}
