package ru.lizzzi.crossfit_rekord.backendless;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessException;
import com.backendless.persistence.DataQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackendlessQueries {


    public List<Map> loadCalendarWod(String objectID, String startDay, String nowDay){
        try
        {
            String whereClause = "userID = '" + objectID + "' and date_session >= '" +
                    startDay + "' and date_session <= '" + nowDay + "'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(100);

            return Backendless.Data.of("results").find(queryBuilder);
        }catch( BackendlessException exception )
        {
            return null;
        }

    }
    public List<Map> loadPeople(int iLoaderId, String dateSelect, String timeSelect, String userName, String userId){

        try {
            String table_name = "recording";
            HashMap<String, String> record = new HashMap<>();
            if (iLoaderId == 2) {
                record.put("data", dateSelect);
                record.put("time", timeSelect);
                record.put("username", userName);
                record.put("ownerId", userId);
                Backendless.Persistence.of(table_name).save(record);
            }

            if (iLoaderId == 3) {
                record.put("objectId", userId);
                Backendless.Persistence.of(table_name).remove(record);
            }

            String whereClause = "data = '" + dateSelect + "' and time = '" + timeSelect + "'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(20);
            return Backendless.Data.of(table_name).find(queryBuilder);
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
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setSortBy("day_of_week");
            queryBuilder.setSortBy("start_time");
            queryBuilder.setPageSize(100);
            return Backendless.Data.of("schedule").find(queryBuilder);
        }catch( BackendlessException exception )
        {
            return null;
        }
    }

    public List<Map> loadWorkoutDetails(String typeQuery, String tableName, String selecteDay, String userId){
        String whereClause;
        if (typeQuery.equals("all")){
            whereClause = "date_session = '" + selecteDay + "'" ;
        }else {
            whereClause = "date_session = '" + selecteDay + "' and userID = '" + userId + "'" ;
        }

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setPageSize(100);
        return Backendless.Data.of(tableName).find(queryBuilder);
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

    public boolean saveUserData(String objectid, String cardNumber, String name, String surname, String email, String phone){
        BackendlessUser user = new BackendlessUser();
        boolean result = false;

        try
        {
            //логин и пас это номер карты
            user = Backendless.UserService.login(cardNumber, cardNumber);
        }
        catch( BackendlessException exception )
        {
            // login failed, to get the error code, call exception.getFault().getCode()
        }

        try
        {
            user.setProperty( "name", name);
            user.setProperty("surname", surname);
            user.setEmail(email);
            user.setProperty("phoneNumber", phone);
            Backendless.UserService.update(user);
            result = true;

        }
        catch( BackendlessException exception )
        {
            // update failed, to get the error code, call exception.getFault().getCode()
        }
        return result;
    }

    public boolean saveEditWorkoutDetails(int iLoaderId, String dateSession, String userId,
                                            String userName, String userSkill, String userWoDLevel,
                                            String userWodResult){

        boolean resultQueries = false;
        String table_name = "results";
        HashMap<String, String> record = new HashMap<>();
        if (iLoaderId == 2) {
            record.put("date_session", dateSession);
            record.put("userID", userId);
            record.put("Name", userName);
            record.put("skill", userSkill);
            record.put("wod_level", userWoDLevel);
            record.put("wod_result", userWodResult);

            try
            {
                Backendless.Persistence.of(table_name).save(record);
                resultQueries = true;
            }
            catch( BackendlessException exception )
            {
                // failed, to get the error code, call exception.getFault().getCode()
            }
        }

        if (iLoaderId == 3) {
            String whereClause = "date_session = '" + dateSession + "' and userID = '" + userId + "'";

            try
            {
                Backendless.Persistence.of(table_name).remove(whereClause);
                resultQueries = true;
            }
            catch( BackendlessException exception )
            {
                // failed, to get the error code, call exception.getFault().getCode()
            }
        }

        if (iLoaderId == 4 ){
            Map<String, Object> record2 = new HashMap<>();
            record2.put("Name", userName);
            record2.put("skill", userSkill);
            record2.put("wod_level", userWoDLevel);
            record2.put("wod_result", userWodResult);
            String whereClause = "date_session = '" + dateSession + "' and userID = '" + userId + "'";

            try
            {
                Backendless.Persistence.of(table_name).update(whereClause, record2);
                resultQueries = true;
            }
            catch( BackendlessException exception )
            {
                // failed, to get the error code, call exception.getFault().getCode()
            }
        }
        return resultQueries;
    }

    public List<Map> loadNotification (String datelastcheck){
        List<Map> data;
        String whereClause = "dateNote > '" + datelastcheck + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setPageSize(100);
        queryBuilder.setSortBy("dateNote");
        data = Backendless.Data.of("notification").find(queryBuilder);

        return data;
    }
}

//TODO Перевести все название полей таблиц в  строковые ресурсы.
