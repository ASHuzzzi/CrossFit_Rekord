package ru.lizzzi.crossfit_rekord.utils;

import ru.lizzzi.crossfit_rekord.R;

//класс для выбора заднего фона элементов в расписании
public class BackgroundDrawable {

    public int getBackgroundDrawable(String workoutType) {

        int backgroundColor = R.drawable.table_item_crossfit;

        if (!workoutType.equals("CrossFit")) {
            switch (workoutType) {
                case "On-Ramp":
                    backgroundColor = R.drawable.table_item_onramp;
                    break;
                case "Open Gym":
                    backgroundColor = R.drawable.table_item_opengym;
                    break;
                case "Stretching":
                    backgroundColor= R.drawable.table_item_stretching;
                    break;
                case "CrossFit Kids":
                    backgroundColor = R.drawable.table_item_crossfitkids;
                    break;
                case "CrossFit/TRX":
                    backgroundColor = R.drawable.table_item_crossfit_and_trx;
                    break;
                case "Gymnastics/Defence":
                    backgroundColor = R.drawable.table_item_gymnastics_and_defence;
                    break;
                case "CrossFit/Lady class":
                    backgroundColor = R.drawable.table_item_crossfit_and_ladyclass;
                    break;
                case "Weightlifting":
                    backgroundColor = R.drawable.table_item_weighlifting;
                    break;
                case "Endurance":
                    backgroundColor = R.drawable.table_item_endurance;
                    break;
                case "bjj":
                    backgroundColor = R.drawable.table_item_bjj;
                    break;
                case "bjj kids":
                    backgroundColor = R.drawable.table_item_bjj_kids;
                    break;
                case "Muay Thai kids":
                    backgroundColor = R.drawable.table_item_muaythai_kids;
                    break;
                case "Muay Thai":
                    backgroundColor = R.drawable.table_item_muaythai;
                    break;
                case "TRX":
                    backgroundColor = R.drawable.table_item_trx;
                    break;
                case "Lady class":
                    backgroundColor = R.drawable.table_item_ladyclass;
                    break;
                case "FBB+Power":
                    backgroundColor = R.drawable.table_item_fbb_power;
                    break;
                case "MMA":
                    backgroundColor = R.drawable.table_item_mma;
                    break;
            }
        }
        return  backgroundColor;
    }
}
