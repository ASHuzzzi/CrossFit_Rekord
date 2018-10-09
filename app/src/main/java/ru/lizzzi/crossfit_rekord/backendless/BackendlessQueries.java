package ru.lizzzi.crossfit_rekord.backendless;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessException;
import com.backendless.persistence.DataQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;

public class BackendlessQueries extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public void onCreate(){
        super.onCreate();
        BackendlessQueries.context = getApplicationContext();
    }

    public static Context getAppContext(){
        return BackendlessQueries.context;
    }

    public List<Map> loadCalendarWod(String objectID, String startDay, String nowDay){
        try
        {
            String stUserID = getAppContext().getResources().getString(R.string.bTableResultsUserID);
            String stDateSession = getAppContext().getResources().getString(R.string.bTableResultsDateSession);
            String stTableName = getAppContext().getResources().getString(R.string.bTableResultsName);
            String whereClause = stUserID + " = '" + objectID + "' and " + stDateSession + " >= '" +
                    startDay + "' and " + stDateSession + " <= '" + nowDay + "'";

            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(100);

            return Backendless.Data.of(stTableName).find(queryBuilder);
        }catch( BackendlessException exception )
        {
            return null;
        }

    }
    public List<Map> loadPeople(int iLoaderId, String dateSelect, String timeSelect, String userName, String userId){

        try {
            String stData = getAppContext().getResources().getString(R.string.bTableRecordingData);
            String stTime = getAppContext().getResources().getString(R.string.bTableRecordingTime);
            String stUserame = getAppContext().getResources().getString(R.string.bTableRecordingUsername);
            String stOwnerId = getAppContext().getResources().getString(R.string.bTableRecordingOwnerId);
            String stObjectId = getAppContext().getResources().getString(R.string.bTableRecordingObjectId);
            String stTableName = getAppContext().getResources().getString(R.string.bTableRecordingName);

            HashMap<String, String> record = new HashMap<>();
            if (iLoaderId == 2) {
                record.put(stData, dateSelect);
                record.put(stTime, timeSelect);
                record.put(stUserame, userName);
                record.put(stOwnerId, userId);
                Backendless.Persistence.of(stTableName).save(record);
            }

            if (iLoaderId == 3) {
                record.put(stObjectId, userId);
                Backendless.Persistence.of(stTableName).remove(record);
            }

            String whereClause =
                    stData + " = '" + dateSelect + "' and " + stTime + " = '" + timeSelect + "'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(20);
            return Backendless.Data.of(stTableName).find(queryBuilder);
        }catch( BackendlessException exception )
        {
            return null;
        }

    }

    public List<Map> loadAllTable(){

        //Пока никак не обрабатываю ошибки от сервера. Т.е. если данные есть, то строю список
        //если данных нет, то список не строится и выскакивает стандартная заглушка.

        try
        {
            String stDayOfWeek = getAppContext().getResources().getString(R.string.bTableScheduleDayOfWeek);
            String stStartTime = getAppContext().getResources().getString(R.string.bTableScheduleStartTime);
            String stTableName = getAppContext().getResources().getString(R.string.bTableScheduleName);

            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setSortBy(stDayOfWeek);
            queryBuilder.setSortBy(stStartTime);
            queryBuilder.setPageSize(100);
            return Backendless.Data.of(stTableName).find(queryBuilder);
        }catch( BackendlessException exception )
        {
            return null;
        }
    }

    public List<Map> loadWorkoutDetails(String typeQuery, String tableName, String selecteDay, String userId){

        try
        {
            String stDateSession = getAppContext().getResources().getString(R.string.bTableResultsDateSession);
            String stUserID= getAppContext().getResources().getString(R.string.bTableResultsUserID);

            String whereClause;
            if (typeQuery.equals("all")){
                whereClause = stDateSession + " = '" + selecteDay + "'" ;
            }else {
                whereClause = stDateSession + " = '" + selecteDay + "' and " + stUserID + " = '" + userId + "'" ;
            }

            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(100);
            return Backendless.Data.of(tableName).find(queryBuilder);
        }catch( BackendlessException exception )
        {
            return null;
        }

    }

    public BackendlessUser authUser(String stcardNumber, String stPassword){
        BackendlessUser user = null;

        try
        {
            Backendless.UserService.login(stcardNumber, stPassword);
            user = Backendless.UserService.CurrentUser();
        }
        catch( BackendlessException exception )
        {

        }

        return user;
    }

    public boolean saveUserData(String objectid, String cardNumber, String name, String surname, String e_mail, String phone){

        try
        {
            //логин и пас это номер карты
            BackendlessUser user = Backendless.UserService.login(cardNumber, cardNumber);

            String stName = getAppContext().getResources().getString(R.string.bTableUsersNameT);
            String stSurname = getAppContext().getResources().getString(R.string.bTableUsersSurname);
            String stPhoneNumber = getAppContext().getResources().getString(R.string.bTableUsersPhoneNumber);

            user.setProperty(stName, name);
            user.setProperty(stSurname, surname);
            user.setEmail(e_mail);
            user.setProperty(stPhoneNumber, phone);
            try
            {

                Backendless.UserService.update(user);
                return true;

            }
            catch( BackendlessException exception )
            {
                // update failed, to get the error code, call exception.getFault().getCode()
                return false;
            }

        }
        catch( BackendlessException exception )
        {
            // login failed, to get the error code, call exception.getFault().getCode()
            return false;
        }



    }

    public boolean saveEditWorkoutDetails(int iLoaderId, String dateSession, String userId,
                                            String userName, String userSkill, String userWoDLevel,
                                            String userWodResult){

        String stTableName = getAppContext().getResources().getString(R.string.bTableResultsName);
        String stDateSession = getAppContext().getResources().getString(R.string.bTableResultsDateSession);
        String stUserID = getAppContext().getResources().getString(R.string.bTableResultsUserID);
        String stName = getAppContext().getResources().getString(R.string.bTableResultsNameT);
        String stSkill = getAppContext().getResources().getString(R.string.bTableResultsSkill);
        String stWodLevel = getAppContext().getResources().getString(R.string.bTableResultsWodLevel);
        String stWodResult = getAppContext().getResources().getString(R.string.bTableResultsWodResult);

        boolean resultQueries = false;

        HashMap<String, String> record = new HashMap<>();
        String whereClause;

        switch (iLoaderId){
            case 2:
                record.put(stDateSession, dateSession);
                record.put(stUserID, userId);
                record.put(stName, userName);
                record.put(stSkill, userSkill);
                record.put(stWodLevel, userWoDLevel);
                record.put(stWodResult, userWodResult);

                try
                {
                    Backendless.Persistence.of(stTableName).save(record);
                    resultQueries = true;
                }
                catch( BackendlessException exception )
                {
                    // failed, to get the error code, call exception.getFault().getCode()
                }
                break;

            case 3:
                whereClause =
                        stDateSession + " = '" + dateSession + "' and " + stUserID + " = '" + userId + "'";

                try
                {
                    Backendless.Persistence.of(stTableName).remove(whereClause);
                    resultQueries = true;
                }
                catch( BackendlessException exception )
                {
                    // failed, to get the error code, call exception.getFault().getCode()
                }
                break;

            case 4:
                Map<String, Object> record2 = new HashMap<>();
                record2.put(stName, userName);
                record2.put(stSkill, userSkill);
                record2.put(stWodLevel, userWoDLevel);
                record2.put(stWodResult, userWodResult);
                whereClause =
                        stDateSession + " = '" + dateSession + "' and " + stUserID + " = '" + userId + "'";

                try
                {
                    Backendless.Persistence.of(stTableName).update(whereClause, record2);
                    resultQueries = true;
                }
                catch( BackendlessException exception )
                {
                    // failed, to get the error code, call exception.getFault().getCode()
                }
                break;
        }

        return resultQueries;
    }

    public List<Map> loadNotification (String datelastcheck){

        try
        {
            String stDateNote = getAppContext().getResources().getString(R.string.bTableNotificationDateNote);
            String stTableName = getAppContext().getResources().getString(R.string.bTableNotificationName);
            String whereClause = stDateNote + " > '" + datelastcheck + "'";

            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(100);
            queryBuilder.setSortBy(stDateNote);

            return Backendless.Data.of(stTableName).find(queryBuilder);
        }
        catch( BackendlessException exception )
        {
            return null;
        }

    }
}
