package ru.lizzzi.crossfit_rekord.backendless;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackendlessQueries {

    public List<Map> loadCalendarWod(){
        List<Map> data;
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(100);
        data = Backendless.Data.of("exercises").find(queryBuilder);

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

    public List<Map> loadWorkoutDetails(String tableName, String selecteDay){
        String whereClause = "date_session = '" + selecteDay + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setPageSize(100);
        return Backendless.Data.of(tableName).find(queryBuilder);
    }
}
