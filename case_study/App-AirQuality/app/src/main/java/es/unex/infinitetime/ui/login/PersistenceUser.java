package es.unex.infinitetime.ui.login;

import android.content.SharedPreferences;

public class PersistenceUser {

    private static PersistenceUser instance;
    private static final String KEY_USER_ID = "user_id";
    private static final long NO_USER = 1000000000;

    private long userId;
    private SharedPreferences preferences;

    public static PersistenceUser getInstance() {
        if(instance == null){
            instance = new PersistenceUser();
        }
        return instance;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = (int) userId;
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void saveUserId(){
        SharedPreferences.Editor editor =  preferences.edit();
        editor.putLong(KEY_USER_ID, userId);
        editor.apply();
    }

    public void deleteUserId(){
        setUserId(-1);
        SharedPreferences.Editor editor =  preferences.edit();
        editor.remove(KEY_USER_ID);
        editor.apply();
    }

    public void loadUserId(){
        userId = preferences.getLong(KEY_USER_ID, NO_USER);
        setUserId(userId);
    }

    public boolean hasValidUserId(){
        return userId != NO_USER;
    }
}
