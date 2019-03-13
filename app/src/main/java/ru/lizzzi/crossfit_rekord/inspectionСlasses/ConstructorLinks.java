package ru.lizzzi.crossfit_rekord.inspectionСlasses;

//класс создает ссылку для переадресации на сайт
public class ConstructorLinks {

    public String constructorLinks (int iSelectGym, int iSelectDay, String stStartTime, String stTypesItem){

        if (!stTypesItem.equals("CrossFit")){
            switch (stTypesItem){

                case "Open Gym":
                    stTypesItem = "Open%20Gym";
                    break;

                case "CrossFit Kids":
                    stTypesItem = "CF%20kids";
                    break;

                case "Lady class":
                    stTypesItem = "Lady%20Class";
                    break;

                case "bjj kids":
                    stTypesItem = "bjj%20kids";
                    break;

                case "Muay Thai kids":
                    stTypesItem = "Muay%20Thai%20kids";
                    break;

                case "Muay Thai":
                    stTypesItem = "Muay%20Thai";
            }
        }

        String stSelectGym;
        if (iSelectGym == 2){
            stSelectGym = "2";
        }else {
            stSelectGym = "";
        }

        String stURL = "http://i.crossfitrekord.ru/rec";

        return stURL
                + stSelectGym
                + ".php?day="
                + String.valueOf(iSelectDay)
                + "&time="
                + stStartTime
                + "%20"
                + stTypesItem;
    }
}
