package ru.lizzzi.crossfit_rekord.inspectionСlasses;

public class ConstructorLinks {

    public String constructorLinks (int iSelectDay, String stStartTime, String stTypesItem){

        switch (stTypesItem){

            case "Open Gym":
                stTypesItem = "Open%20Gym";
                break;


            case "CrossFit Kids":
                stTypesItem = "CF%20kids";
                break;

            case "CrossFit/TRX":
                stTypesItem = "TRX";
                break;

            case "CrossFit/Lady class":
                stTypesItem = "%Lady%20class";
                break;
        }


        String stURL = "http://crossfitrekord.webox.beget.tech/rec.php?day=";

        String stResult = stURL
                + String.valueOf(iSelectDay)
                + "&time="
                + stStartTime
                + "%20"
                + stTypesItem;

        return stResult;
    }
}
