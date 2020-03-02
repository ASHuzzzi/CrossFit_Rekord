package ru.lizzzi.crossfit_rekord.backend;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.items.ScheduleWeekly;
import ru.lizzzi.crossfit_rekord.items.WodItem;
import ru.lizzzi.crossfit_rekord.utils.Utils;
import ru.lizzzi.crossfit_rekord.interfaces.BackendResponseCallback;
import ru.lizzzi.crossfit_rekord.items.WorkoutResultItem;

public class BackendApi {

    //Поля таблицы exercises
    private final String TABLE_EXERCISES_NAME = "exercises";
    private final String TABLE_EXERCISES_CORE = "core";
    public final String TABLE_EXERCISES_DATE_SESSION = "date_session";
    public final String TABLE_EXERCISES_POSTWORKOUT = "postworkout";
    public final String TABLE_EXERCISES_SC = "Sc";
    public final String TABLE_EXERCISES_RX = "Rx";
    public final String TABLE_EXERCISES_RX_PLUS = "Rxplus";
    public final String TABLE_EXERCISES_SKILL = "skill";
    public final String TABLE_EXERCISES_WARMUP = "warmup";
    public final String TABLE_EXERCISES_WOD = "wod";

    //Поля таблицы notification
    private final String TABLE_NOTIFICATION_NAME = "notification";
    public final String TABLE_NOTIFICATION_CODE_NOTE = "codeNote";
    public final String TABLE_NOTIFICATION_DATE_NOTE = "dateNote";
    public final String TABLE_NOTIFICATION_HEADER = "header";
    public final String TABLE_NOTIFICATION_NUMBER_NOTE = "notification";
    public final String TABLE_NOTIFICATION_TEXT = "text";

    //Поля таблицы schedule
    private final String TABLE_SCHEDULE_NAME = "schedule";
    public final String TABLE_SCHEDULE_GYM = "gym";
    public final String TABLE_SCHEDULE_DAY_OF_WEEK = "day_of_week";
    public final String TABLE_SCHEDULE_DESCRIPTION = "description";
    public final String TABLE_SCHEDULE_START_TIME = "start_time";
    public final String TABLE_SCHEDULE_TYPE = "type";

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
    public final String TABLE_RESULTS_USER_NAME = "Name";
    public final String TABLE_RESULTS_SURNAME = "surname";
    public final String TABLE_RESULTS_SKILL = "skill";
    public final String TABLE_RESULTS_USER_ID = "ownerId";
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

    public void getScheduleWeekly(String selectedGym,
                                  final BackendResponseCallback<ScheduleWeekly> callback) {
        //Пока никак не обрабатываю ошибки от сервера. Т.е. если данные есть, то строю список
        //если данных нет, то список не строится и выскакивает стандартная заглушка.
        String whereClause = TABLE_SCHEDULE_GYM + " = '" + selectedGym + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setSortBy(TABLE_SCHEDULE_DAY_OF_WEEK);
        queryBuilder.setSortBy(TABLE_SCHEDULE_START_TIME);
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setPageSize(100);
        Backendless.Data.of(TABLE_SCHEDULE_NAME).find(queryBuilder, new BackendlessCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> response) {
                if ((response != null) && !response.isEmpty()) {
                    ScheduleWeekly scheduleWeekly = new Utils().splitLoadedSchedule(response);
                    callback.handleSuccess(scheduleWeekly);
                } else {
                    callback.handleFault(null);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                callback.handleFault(fault);
            }
        });
    }

    public void loadingExerciseWorkout(String selectedDay,
                                       final BackendResponseCallback<WodItem> callback) {
        String whereClause = TABLE_EXERCISES_DATE_SESSION + " = '" + selectedDay + "'" ;
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setPageSize(100);
        Backendless.Data.of(TABLE_EXERCISES_NAME).find(queryBuilder, new BackendlessCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> response) {
                if ((response != null) && !response.isEmpty()) {
                    WodItem wod = new Utils().getExercise(response.get(0));
                    callback.handleSuccess(wod);
                } else {
                    callback.handleFault(null);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                callback.handleFault(fault);
            }
        });
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
                                     WorkoutResultItem workoutResult) {
        String tableName = TABLE_RESULTS_NAME;
        String dateSession = TABLE_RESULTS_DATE_SESSION;
        String ownerID = TABLE_RESULTS_USER_ID;
        String name = TABLE_RESULTS_USER_NAME;
        String surname = TABLE_RESULTS_SURNAME;
        String skill = TABLE_RESULTS_SKILL;
        String wodLevel = TABLE_RESULTS_WOD_LEVEL;
        String wodResult = TABLE_RESULTS_WOD_RESULT;
        String whereClause;
        switch (action) {
            case ACTION_SAVE:
                Map<String, String> itemForSave = new HashMap<>();
                itemForSave.put(dateSession, selectedDateSession);
                itemForSave.put(ownerID, workoutResult.getUserId());
                itemForSave.put(name, workoutResult.getName());
                itemForSave.put(surname, workoutResult.getSurname());
                itemForSave.put(skill, workoutResult.getSkillResult());
                itemForSave.put(wodLevel, workoutResult.getWodLevel());
                itemForSave.put(wodResult, workoutResult.getWodResult());
                try {
                    Backendless.Persistence.of(tableName).save(itemForSave);
                    return true;
                } catch ( BackendlessException exception ) {
                    // failed, to get the error code, call exception.getFault().getCode()
                }
                break;
            case ACTION_DELETE:
                whereClause =
                        dateSession + " = '" + selectedDateSession +
                        "' and " +
                        ownerID + " = '" + workoutResult.getUserId() + "'";
                try {
                    Backendless.Persistence.of(tableName).remove(whereClause);
                    return true;
                } catch (BackendlessException exception) {
                    // failed, to get the error code, call exception.getFault().getCode()
                }
                break;
            case ACTION_UPLOAD:
                Map<String, Object> itemForUpdate = new HashMap<>();
                itemForUpdate.put(name,workoutResult.getName());
                itemForUpdate.put(surname, workoutResult.getSurname());
                itemForUpdate.put(skill, workoutResult.getSkillResult());
                itemForUpdate.put(wodLevel, workoutResult.getWodLevel());
                itemForUpdate.put(wodResult, workoutResult.getWodLevel());
                whereClause =
                        dateSession + " = '" + selectedDateSession +
                        "' and " +
                        ownerID + " = '" + workoutResult.getUserId() + "'";
                try {
                    Backendless.Persistence.of(tableName).update(whereClause, itemForUpdate);
                    return true;
                } catch (BackendlessException exception) {
                    // failed, to get the error code, call exception.getFault().getCode()
                }
                break;
        }
        return false;
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