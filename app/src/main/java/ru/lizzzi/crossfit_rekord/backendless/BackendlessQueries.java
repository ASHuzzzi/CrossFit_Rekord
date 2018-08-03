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
        List<Map> data;
        String whereClause = "userID = '" + objectID + "' and date_session >= '" +
                startDay + "' and date_session <= '" + nowDay + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setPageSize(100);
        data = Backendless.Data.of("results").find(queryBuilder);

        return data;
    }
    public List<Map> loadPeople(int iLoaderId, String dateSelect, String timeSelect, String userName, String userId){
        String table_name = "recording";
        HashMap<String, String> record = new HashMap<>();
        if (iLoaderId == 2) {
            record.put("data", dateSelect);
            record.put("time", timeSelect);
            record.put("username", userName);
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
    }

    public List<Map> loadTable(int iNumberOfDayWeek){
        String whereClause = "day_of_week = " + iNumberOfDayWeek;
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setSortBy("start_time");
        queryBuilder.setPageSize(20);
        return Backendless.Data.of("schedule").find(queryBuilder);
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
        Backendless.UserService.login(stcardNumber, stPassword);
        return Backendless.UserService.CurrentUser();
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

    public List<Map> saveEditWorkoutDetails(int iLoaderId, String dateSession, String userId,
                                            String userName, String userSkill, String userWoDLevel,
                                            String userWodResult){

        /*String whereClause = "date_session = '" + selecteDay + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setPageSize(100);
        return Backendless.Data.of(tableName).find(queryBuilder);*/

        String table_name = "results";
        HashMap<String, String> record = new HashMap<>();
        if (iLoaderId == 2) {
            record.put("date_session", dateSession);
            record.put("userID", userId);
            record.put("Name", userName);
            record.put("skill", userSkill);
            record.put("wod_level", userWoDLevel);
            record.put("wod_result", userWodResult);
            Backendless.Persistence.of(table_name).save(record);
        }

        if (iLoaderId == 3) {
            /*record.put("date_session", dateSession);
            record.put("userID", userId);*/
            String whereClause = "date_session = '" + dateSession + "' and userID = '" + userId + "'";
            Backendless.Persistence.of(table_name).remove(whereClause);
        }

        if (iLoaderId == 4 ){
            Map<String, Object> record2 = new HashMap<>();
            /*record.put("date_session", dateSession);
            record.put("userID", userId);*/
            record2.put("Name", userName);
            record2.put("skill", userSkill);
            record2.put("wod_level", userWoDLevel);
            record2.put("wod_result", userWodResult);
            String whereClause = "date_session = '" + dateSession + "' and userID = '" + userId + "'";
            Backendless.Persistence.of(table_name).update(whereClause, record2);
        }
        return null;
    }
}

//TODO Перевести все название полей таблиц в  строковые ресурсы.
