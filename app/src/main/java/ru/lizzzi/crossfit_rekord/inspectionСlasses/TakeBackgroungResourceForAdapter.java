package ru.lizzzi.crossfit_rekord.inspectionСlasses;

import ru.lizzzi.crossfit_rekord.R;

//класс для выбора заднего фона элементов в расписании
public class TakeBackgroungResourceForAdapter {

    public int takeBackgroungResourceForAdapter(String stType){
        int iResource = R.drawable.table_item_crossfit;

        if (!stType.equals("CrossFit")){
            switch (stType){
                case "On-Ramp":
                    iResource = R.drawable.table_item_onramp;
                    break;
                case "Open Gym":
                    iResource = R.drawable.table_item_opengym;
                    break;
                case "Stretching":
                    iResource= R.drawable.table_item_stretching;
                    break;
                case "CrossFit Kids":
                    iResource = R.drawable.table_item_crossfitkids;
                    break;
                case "CrossFit/TRX":
                    iResource = R.drawable.table_item_crossfit_and_trx;
                    break;
                case "Gymnastics/Defence":
                    iResource = R.drawable.table_item_gymnastics_and_defence;
                    break;
                case "CrossFit/Lady class":
                    iResource = R.drawable.table_item_crossfit_and_ladyclass;
                    break;
                case "Weightlifting":
                    iResource = R.drawable.table_item_weighlifting;
                    break;
                case "Endurance":
                    iResource = R.drawable.table_item_endurance;
                    break;
                case "bjj":
                    iResource = R.drawable.table_item_bjj;
                    break;
                case "bjj kids":
                    iResource = R.drawable.table_item_bjj;
                    break;
                case "Muay Thai kids":
                    iResource = R.drawable.table_item_muaythai;
                    break;
                case "Muay Thai":
                    iResource = R.drawable.table_item_muaythai;
                    break;
                case "TRX":
                    iResource = R.drawable.table_item_trx;
                    break;
                case "Lady class":
                    iResource = R.drawable.table_item_ladyclass;
                    break;
            }
        }
        return  iResource;
    }
}
