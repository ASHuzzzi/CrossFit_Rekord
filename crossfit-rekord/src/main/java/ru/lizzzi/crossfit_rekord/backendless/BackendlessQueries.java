package ru.lizzzi.crossfit_rekord.backendless;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessException;
import com.backendless.persistence.DataQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackendlessQueries {

    //Поля таблицы exercises
    private final String TABLE_EXERCISES_NAME = "exercises";
    private final String TABLE_EXERCISES_CORE = "core";
    private final String TABLE_EXERCISES_DATE_SESSION = "date_session";
    private final String TABLE_EXERCISES_POSTWORKOUT = "postworkout";
    private final String TABLE_EXERCISES_SC = "Sc";
    private final String TABLE_EXERCISES_RX = "Rx";
    private final String TABLE_EXERCISES_RX_PLUS = "Rxplus";
    private final String TABLE_EXERCISES_SKILL = "skill";
    private final String TABLE_EXERCISES_WARMUP = "warmup";
    private final String TABLE_EXERCISES_WOD = "wod";

    //Поля таблицы notification
    private final String TABLE_NOTIFICATION_NAME = "notification";
    private final String TABLE_NOTIFICATION_CODE_NOTE = "codeNote";
    private final String TABLE_NOTIFICATION_DATE_NOTE = "dateNote";
    private final String TABLE_NOTIFICATION_HEADER = "header";
    private final String TABLE_NOTIFICATION_NUMBER_NOTE = "notification";
    private final String TABLE_NOTIFICATION_TEXT = "text";

    //Поля таблицы schedule
    private final String TABLE_SCHEDULE_NAME = "schedule";
    private final String TABLE_SCHEDULE_GYM = "gym";
    private final String TABLE_SCHEDULE_DAY_OF_WEEK = "day_of_week";
    private final String TABLE_SCHEDULE_DESCRIPTION = "description";
    private final String TABLE_SCHEDULE_START_TIME = "start_time";
    private final String TABLE_SCHEDULE_TYPE = "type";

    //Поля таблицы recording
    private final String TABLE_RECORDING_NAME = "recording";
    private final String TABLE_RECORDING_CARD_NUMBER = "cardNumber";
    private final String TABLE_RECORDING_DATA = "Data";
    private final String TABLE_RECORDING_SURNAME = "surname";
    private final String TABLE_RECORDING_TIME = "time";
    private final String TABLE_RECORDING_USER_NAME = "username";
    private final String TABLE_RECORDING_OWNER_ID = "ownerId";
    private final String TABLE_RECORDING_OBJECT_ID = "objectId";

    //Поля таблицы results
    private final String TABLE_RESULTS_NAME = "results";
    public final String TABLE_RESULTS_DATE_SESSION = "date_session";
    private final String TABLE_RESULTS_USER_NAME = "Name";
    private final String TABLE_RESULTS_SURNAME = "surname";
    public final String TABLE_RESULTS_SKILL = "skill";
    private final String TABLE_RESULTS_USER_ID = "ownerId";
    public final String TABLE_RESULTS_WOD_LEVEL = "wod_level";
    public final String TABLE_RESULTS_WOD_RESULT = "wod_result";

    //Поля таблицы Users
    private final String TABLE_USERS_NAME = "Users";
    private final String TABLE_USERS_CARD_NUMBER = "cardNumber";
    private final String TABLE_USERS_EMAIL = "email";
    private final String TABLE_USERS_USER_NAME = "name";
    private final String TABLE_USERS_PASSWORD = "password";
    private final String TABLE_USERS_PHONE_NUBMER = "phoneNumber";
    private final String TABLE_USERS_SURNAME = "surname";

    private final int ACTION_SAVE = 2;
    private final int ACTION_DELETE = 3;
    private final int ACTION_UPLOAD = 4;

    public List<Map> loadingCalendarWod(String userID, long startDate, long endDate) {
        try {
            String whereClause =
                    TABLE_RESULTS_USER_ID + " = '" + userID +
                    "' and " +
                    TABLE_RESULTS_DATE_SESSION + " >= '" + startDate +
                    "' and " +
                    TABLE_RESULTS_DATE_SESSION + " <= '" + endDate + "'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(100);
            return Backendless.Data.of(TABLE_RESULTS_NAME).find(queryBuilder);
        } catch (BackendlessException exception) {
            return null;
        }
    }

    public List<Map> loadingSchedule(String selectedGym) {
        //Пока никак не обрабатываю ошибки от сервера. Т.е. если данные есть, то строю список
        //если данных нет, то список не строится и выскакивает стандартная заглушка.
        try {
            String whereClause = TABLE_SCHEDULE_GYM + " = '" + selectedGym + "'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setSortBy(TABLE_SCHEDULE_DAY_OF_WEEK);
            queryBuilder.setSortBy(TABLE_SCHEDULE_START_TIME);
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(100);
            return Backendless.Data.of(TABLE_SCHEDULE_NAME).find(queryBuilder);
        } catch (BackendlessException exception) {
            return null;
        }
    }

    public List<Map> loadingExerciseWorkout(String selectedDay) {
        try {
            String whereClause = TABLE_EXERCISES_DATE_SESSION + " = '" + selectedDay + "'" ;
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(100);
            return Backendless.Data.of(TABLE_EXERCISES_NAME).find(queryBuilder);
        } catch (BackendlessException exception) {
            return null;
        }
    }

    public List<Map> loadingWorkoutResults(String selectedDay) {
        try {
            String whereClause = TABLE_RESULTS_DATE_SESSION + " = '" + selectedDay + "'" ;
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(100);
            return Backendless.Data.of(TABLE_RESULTS_NAME).find(queryBuilder);
        } catch (BackendlessException exception) {
            return null;
        }
    }

    public Map<String, String> authUser(String email, String password) {
        try {
            BackendlessUser backendlessUser = Backendless.UserService.login(email, password);
            Map<String, String> user = new HashMap<>();
            user.put("userID", backendlessUser.getObjectId());
            user.put("name", backendlessUser.getProperty("name").toString());
            user.put("surname",
                    (backendlessUser.getProperty("surname") != null)
                            ? backendlessUser.getProperty("surname").toString()
                            : "");
            user.put("password", password);
            user.put("email", backendlessUser.getProperty("email").toString());
            user.put("phoneNumber",
                    backendlessUser.getProperty("phoneNumber") != null
                            ? backendlessUser.getProperty("phoneNumber").toString()
                            : "");
            user.put("cardNumber",
                    (backendlessUser.getProperty("cardNumber") != null)
                            ? backendlessUser.getProperty("cardNumber").toString()
                            : "");
            return user;
        } catch (BackendlessException exception) {
            return null;
        }
    }

    public boolean saveUserData(String email,
                                String password,
                                String name,
                                String surname,
                                String phone) {
        try {
            BackendlessUser backendlessUser = Backendless.UserService.login(email, password);
            backendlessUser.setProperty(TABLE_USERS_USER_NAME, name);
            backendlessUser.setProperty(TABLE_USERS_SURNAME, surname);
            backendlessUser.setProperty(TABLE_USERS_PHONE_NUBMER, phone);
            Backendless.UserService.update(backendlessUser);
            return true;
        } catch (BackendlessException exception) {
            // login failed, to get the error code, call exception.getFault().getCode()
            return false;
        }
    }

    public boolean changePassword(String email, String oldPassword, String newPassword) {
        try {
            BackendlessUser user = Backendless.UserService.login(email, oldPassword);
            user.setProperty(TABLE_USERS_PASSWORD, newPassword);
            Backendless.UserService.update(user);
            return true;
        } catch (BackendlessException exception) {
            // login failed, to get the error code, call exception.getFault().getCode()
            return false;
        }
    }

    public boolean changeEmail(String oldEmail, String newEmail, String password) {
        try {
            BackendlessUser backendlessUser = Backendless.UserService.login(oldEmail, password);
            backendlessUser.setEmail(newEmail);
            Backendless.UserService.update(backendlessUser);
            return true;
        } catch (BackendlessException exception) {
            // login failed, to get the error code, call exception.getFault().getCode()
            return false;
        }
    }

    public boolean setWorkoutDetails(int action,
                                     String selectedDateSession,
                                     String userId,
                                     String userName,
                                     String userSurname,
                                     String userSkill,
                                     String userWoDLevel,
                                     String userWodResult) {
        String tableName = TABLE_RESULTS_NAME;
        String dateSession = TABLE_RESULTS_DATE_SESSION;
        String ownerID = TABLE_RESULTS_USER_ID;
        String name = TABLE_RESULTS_USER_NAME;
        String surname = TABLE_RESULTS_SURNAME;
        String skill = TABLE_RESULTS_SKILL;
        String wodLevel = TABLE_RESULTS_WOD_LEVEL;
        String wodResult = TABLE_RESULTS_WOD_RESULT;
        boolean resultOfQuery = false;
        String whereClause;
        switch (action) {
            case ACTION_SAVE:
                Map<String, String> itemForSave = new HashMap<>();
                itemForSave.put(dateSession, selectedDateSession);
                itemForSave.put(ownerID, userId);
                itemForSave.put(name, userName);
                itemForSave.put(surname, userSurname);
                itemForSave.put(skill, userSkill);
                itemForSave.put(wodLevel, userWoDLevel);
                itemForSave.put(wodResult, userWodResult);
                try {
                    Backendless.Persistence.of(tableName).save(itemForSave);
                    resultOfQuery = true;
                } catch ( BackendlessException exception ) {
                    // failed, to get the error code, call exception.getFault().getCode()
                }
                break;
            case ACTION_DELETE:
                whereClause =
                        dateSession + " = '" + selectedDateSession +
                        "' and " +
                        ownerID + " = '" + userId + "'";
                try {
                    Backendless.Persistence.of(tableName).remove(whereClause);
                    resultOfQuery = true;
                } catch (BackendlessException exception) {
                    // failed, to get the error code, call exception.getFault().getCode()
                }
                break;
            case ACTION_UPLOAD:
                Map<String, Object> itemForUpdate = new HashMap<>();
                itemForUpdate.put(name, userName);
                itemForUpdate.put(surname, userSurname);
                itemForUpdate.put(skill, userSkill);
                itemForUpdate.put(wodLevel, userWoDLevel);
                itemForUpdate.put(wodResult, userWodResult);
                whereClause =
                        dateSession + " = '" + selectedDateSession +
                        "' and " +
                        ownerID + " = '" + userId + "'";
                try {
                    Backendless.Persistence.of(tableName).update(whereClause, itemForUpdate);
                    resultOfQuery = true;
                } catch (BackendlessException exception) {
                    // failed, to get the error code, call exception.getFault().getCode()
                }
                break;
        }
        return resultOfQuery;
    }

    public List<Map> downloadNotifications(String dateLastCheck, String timeNow) {
        try {
            String whereClause =
                    TABLE_NOTIFICATION_DATE_NOTE + " > '" + dateLastCheck +
                    "' and " +
                    TABLE_NOTIFICATION_DATE_NOTE + " < '" + timeNow + "'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(100);
            queryBuilder.setSortBy(TABLE_NOTIFICATION_DATE_NOTE);
            return Backendless.Data.of(TABLE_NOTIFICATION_NAME).find(queryBuilder);
        } catch (BackendlessException exception) {
            return null;
        }
    }

    public String userRegistration(String userName, String userSurname, String email, String password) {
        try {
            BackendlessUser backendlessUser = new BackendlessUser();
            backendlessUser.setEmail(email);
            backendlessUser.setPassword(password);
            backendlessUser.setProperty(TABLE_USERS_USER_NAME, userName);
            backendlessUser.setProperty(TABLE_USERS_SURNAME, userSurname);
            backendlessUser = Backendless.UserService.register(backendlessUser);
            return backendlessUser.getObjectId();
        } catch (BackendlessException exception) {
            return null;
        }
    }

    public boolean recoverPassword(String email) {
        try {
            Backendless.UserService.restorePassword(email);
            return true;
        } catch (BackendlessException exception) {
            return false;
        }
    }
}