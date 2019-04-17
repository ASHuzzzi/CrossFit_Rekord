package ru.lizzzi.crossfit_rekord.backendless;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

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

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_CARDNUMBER = "cardNumber";
    private static final String APP_PREFERENCES_EMAIL = "Email";
    private static final String APP_PREFERENCES_PASSWORD = "Password";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private static final String APP_PREFERENCES_USERNAME = "Username";
    private static final String APP_PREFERENCES_USERSURNAME = "Usersurname";
    private static final String APP_PREFERENCES_PHONE = "Phone";
    private SharedPreferences sharedPreferences;

    public void onCreate(){
        MultiDex.install(getApplicationContext());
        super.onCreate();
        BackendlessQueries.context = getApplicationContext();
    }

    private static Context getAppContext(){
        return BackendlessQueries.context;
    }

    public List<Map> loadCalendarWod(String objectID, String startDay, String nowDay) {
        try {
            String userID = getAppContext().getResources().getString(R.string.bTableResultsUserID);
            String dateSession = getAppContext().getResources().getString(R.string.bTableResultsDateSession);
            String tableName = getAppContext().getResources().getString(R.string.bTableResultsName);
            String whereClause =
                    userID + " = '" + objectID + "' and " + dateSession + " >= '" +
                    startDay + "' and " + dateSession + " <= '" + nowDay + "'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(100);
            return Backendless.Data.of(tableName).find(queryBuilder);
        } catch (BackendlessException exception) {
            return null;
        }
    }

    public List<Map> loadAllTable(String selectGym) {
        //Пока никак не обрабатываю ошибки от сервера. Т.е. если данные есть, то строю список
        //если данных нет, то список не строится и выскакивает стандартная заглушка.
        try {
            String gym = getAppContext().getResources().getString(R.string.bTableScheduleGym);
            String dayOfWeek = getAppContext().getResources().getString(R.string.bTableScheduleDayOfWeek);
            String startTime = getAppContext().getResources().getString(R.string.bTableScheduleStartTime);
            String tableName = getAppContext().getResources().getString(R.string.bTableScheduleName);
            String whereClause = gym + " = '" + selectGym + "'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setSortBy(dayOfWeek);
            queryBuilder.setSortBy(startTime);
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(100);
            return Backendless.Data.of(tableName).find(queryBuilder);
        } catch (BackendlessException exception) {
            return null;
        }
    }

    public List<Map> loadWorkoutDetails(String typeQuery,
                                        String tableName,
                                        String selecteDay,
                                        String userId) {
        try {
            String dateSession = getAppContext().getResources().getString(R.string.bTableResultsDateSession);
            String userID= getAppContext().getResources().getString(R.string.bTableResultsUserID);
            String whereClause;
            if (typeQuery.equals("all")) {
                whereClause = dateSession + " = '" + selecteDay + "'" ;
            } else {
                whereClause =
                        dateSession + " = '" + selecteDay + "' and " + userID + " = '" + userId + "'" ;
            }
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(100);
            return Backendless.Data.of(tableName).find(queryBuilder);
        } catch (BackendlessException exception) {
            return null;
        }
    }

    public boolean authUser(String email, String password) {
        try {
            Backendless.UserService.login(email, password);
            BackendlessUser user = Backendless.UserService.CurrentUser();
            sharedPreferences = getAppContext().getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(APP_PREFERENCES_OBJECTID, user.getObjectId());
            editor.putString(APP_PREFERENCES_CARDNUMBER, String.valueOf(user.getProperty("cardNumber")));
            editor.putString(APP_PREFERENCES_USERNAME, String.valueOf(user.getProperty("name")));
            editor.putString(APP_PREFERENCES_USERSURNAME, String.valueOf(user.getProperty("surname")));
            editor.putString(APP_PREFERENCES_PASSWORD, password);
            editor.putString(APP_PREFERENCES_EMAIL, String.valueOf(user.getProperty("email")));
            editor.putString(APP_PREFERENCES_PHONE, String.valueOf(user.getProperty("phoneNumber")));
            editor.apply();
            return true;
        } catch (BackendlessException exception) {
            return false;
        }
    }

    public boolean saveUserData(String email,
                                String password,
                                String name,
                                String surname,
                                String phone) {
        try {
            BackendlessUser user = Backendless.UserService.login(email, password);
            String userName = getAppContext().getResources().getString(R.string.bTableUsersNameT);
            String userSurname = getAppContext().getResources().getString(R.string.bTableUsersSurname);
            String userPhoneNumber = getAppContext().getResources().getString(R.string.bTableUsersPhoneNumber);
            user.setProperty(userName, name);
            user.setProperty(userSurname, surname);
            user.setProperty(userPhoneNumber, phone);
            try {
                Backendless.UserService.update(user);
                sharedPreferences = getAppContext().getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(APP_PREFERENCES_USERNAME, name);
                editor.putString(APP_PREFERENCES_USERSURNAME, surname);
                editor.putString(APP_PREFERENCES_PHONE, phone);
                editor.apply();
                return true;
            } catch (BackendlessException exception) {
                // update failed, to get the error code, call exception.getFault().getCode()
                return false;
            }
        } catch (BackendlessException exception) {
            // login failed, to get the error code, call exception.getFault().getCode()
            return false;
        }
    }

    public boolean saveUserRegData(String email, String oldPassword, String newPassword) {
        try {
            BackendlessUser user = Backendless.UserService.login(email, oldPassword);
            String userPassword = getAppContext().getResources().getString(R.string.bTableUsersPassword);
            user.setProperty(userPassword, newPassword);
            try {
                Backendless.UserService.update(user);
                sharedPreferences = getAppContext().getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(APP_PREFERENCES_PASSWORD, newPassword);
                editor.apply();
                return true;
            } catch (BackendlessException exception) {
                // update failed, to get the error code, call exception.getFault().getCode()
                return false;
            }
        } catch (BackendlessException exception) {
            // login failed, to get the error code, call exception.getFault().getCode()
            return false;
        }
    }

    public boolean saveChangeEmail(String oldEmail, String newEmail, String password) {
        try {
            BackendlessUser backendlessUser = Backendless.UserService.login(oldEmail, password);
            backendlessUser.setEmail(newEmail);
            try {
                Backendless.UserService.update(backendlessUser);
                sharedPreferences = getAppContext().getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(APP_PREFERENCES_EMAIL, newEmail);
                editor.apply();
                return true;
            } catch (BackendlessException exception) {
                // update failed, to get the error code, call exception.getFault().getCode()
                return false;
            }
        } catch (BackendlessException exception) {
            // login failed, to get the error code, call exception.getFault().getCode()
            return false;
        }
    }

    public boolean saveEditWorkoutDetails(int loaderId,
                                          String userDateSession,
                                          String userId,
                                          String userName,
                                          String userSurname,
                                          String userSkill,
                                          String userWoDLevel,
                                          String userWodResult) {
        String tableName = getAppContext().getResources().getString(R.string.bTableResultsName);
        String dateSession = getAppContext().getResources().getString(R.string.bTableResultsDateSession);
        String ownerID = getAppContext().getResources().getString(R.string.bTableRecordingOwnerId);
        String name = getAppContext().getResources().getString(R.string.bTableResultsNameT);
        String surname = getAppContext().getResources().getString(R.string.bTableResultsSurname);
        String skill = getAppContext().getResources().getString(R.string.bTableResultsSkill);
        String wodLevel = getAppContext().getResources().getString(R.string.bTableResultsWodLevel);
        String wodResult = getAppContext().getResources().getString(R.string.bTableResultsWodResult);
        boolean resultQuery = false;
        String whereClause;
        switch (loaderId) {
            case 2:
                HashMap<String, String> record = new HashMap<>();
                record.put(dateSession, userDateSession);
                record.put(ownerID, userId);
                record.put(name, userName);
                record.put(surname, userSurname);
                record.put(skill, userSkill);
                record.put(wodLevel, userWoDLevel);
                record.put(wodResult, userWodResult);
                try {
                    Backendless.Persistence.of(tableName).save(record);
                    resultQuery = true;
                } catch ( BackendlessException exception ) {
                    // failed, to get the error code, call exception.getFault().getCode()
                }
                break;
            case 3:
                whereClause = dateSession + " = '" + userDateSession +
                        "' and " + ownerID + " = '" + userId + "'";
                try {
                    Backendless.Persistence.of(tableName).remove(whereClause);
                    resultQuery = true;
                } catch (BackendlessException exception) {
                    // failed, to get the error code, call exception.getFault().getCode()
                }
                break;
            case 4:
                Map<String, Object> record2 = new HashMap<>();
                record2.put(name, userName);
                record2.put(surname, userSurname);
                record2.put(skill, userSkill);
                record2.put(wodLevel, userWoDLevel);
                record2.put(wodResult, userWodResult);
                whereClause = dateSession + " = '" + userDateSession +
                        "' and " + ownerID + " = '" + userId + "'";
                try {
                    Backendless.Persistence.of(tableName).update(whereClause, record2);
                    resultQuery = true;
                } catch (BackendlessException exception) {
                    // failed, to get the error code, call exception.getFault().getCode()
                }
                break;
        }
        return resultQuery;
    }

    public List<Map> loadNotification (String dateLastCheck, String timeNow) {
        try {
            String dateNote = getAppContext().getResources().getString(R.string.bTableNotificationDateNote);
            String tableName = getAppContext().getResources().getString(R.string.bTableNotificationName);
            String whereClause = dateNote + " > '" + dateLastCheck + "' and " +
                    dateNote + " < '" + timeNow + "'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(100);
            queryBuilder.setSortBy(dateNote);
            return Backendless.Data.of(tableName).find(queryBuilder);
        } catch (BackendlessException exception) {
            return null;
        }
    }

    public boolean regUser(String userName, String email, String password) {
        try {
            BackendlessUser backendlessUser = new BackendlessUser();
            backendlessUser.setEmail(email);
            backendlessUser.setPassword(password);
            backendlessUser.setProperty("name", userName);
            backendlessUser = Backendless.UserService.register(backendlessUser);

            sharedPreferences = getAppContext().getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(APP_PREFERENCES_USERNAME, userName);
            editor.putString(APP_PREFERENCES_EMAIL, email);
            editor.putString(APP_PREFERENCES_PASSWORD, password);
            editor.putString(APP_PREFERENCES_OBJECTID, backendlessUser.getObjectId());
            editor.apply();
            return true;
        } catch (BackendlessException exception) {
            return false;
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