package ru.lizzzi.crossfit_rekord.inspectionСlasses;

import ru.lizzzi.crossfit_rekord.R;

//класс для выбора заднего фона элементов в расписании
public class TakeBackgroungResourceForAdapter {

    public int takeBackgroungResourceForAdapter(String stType){
        int iResource = R.drawable.table_item_crossfit;

        if (stType.equals("On-Ramp")){
            iResource = R.drawable.table_item_onramp;
        }
        if (stType.equals("Open Gym")){
            iResource = R.drawable.table_item_opengym;
        }
        if (stType.equals("Stretching")){
            iResource= R.drawable.table_item_stretching;
        }
        if (stType.equals("CrossFit Kids")){
            iResource = R.drawable.table_item_crossfitkids;
        }
        if (stType.equals("CrossFit/TRX")){
            iResource = R.drawable.table_item_crossfit_and_trx;
        }
        if (stType.equals("Gymnastics/Defence")){
            iResource = R.drawable.table_item_gymnastics_and_defence;

        }if (stType.equals("CrossFit/Lady class")){
            iResource = R.drawable.table_item_crossfit_and_ladyclass;
        }
        if (stType.equals("Weightlifting")){
            iResource = R.drawable.table_item_weighlifting;
        }
        if (stType.equals("Endurance")){
            iResource = R.drawable.table_item_endurance;
        }

        return  iResource;

    }
}
