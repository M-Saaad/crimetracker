package com.example.crimetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    private static final String DATA_LOGIN = "status_login",
            DATA_AS = "as";

    private static SharedPreferences getSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setDataAs(Context context, String data){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(DATA_AS,data);
        editor.apply();
    }

    public static String getDataAs(Context context){
        return getSharedPreferences(context).getString(DATA_AS,"");
    }

    public static void setDataLogin(Context context, boolean status){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(DATA_LOGIN,status);
        editor.apply();
    }

    public static boolean getDataLogin(Context context){
        return getSharedPreferences(context).getBoolean(DATA_LOGIN,false);
    }

    public static void setUserProfileData(Context context, String name, String email, String contact_no, String password, String emergency_number_1, String emergency_number_2){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("Name", name);
        editor.putString("Email", email);
        editor.putString("Password", password);
        editor.putString("Contact Number", contact_no);
        editor.putString("Emergency Number 1", emergency_number_1);
        editor.putString("Emergency Number 2", emergency_number_2);
        editor.apply();
    }

    public static String[] getUserProfileData(Context context){
        return new String[]{
            getSharedPreferences(context).getString("Name", ""),
            getSharedPreferences(context).getString("Email", ""),
            getSharedPreferences(context).getString("Password", ""),
            getSharedPreferences(context).getString("Contact Number", ""),
            getSharedPreferences(context).getString("Emergency Number 1", ""),
            getSharedPreferences(context).getString("Emergency Number 2", "")
        };
    }

    public static void clearData(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(DATA_AS);
        editor.remove(DATA_LOGIN);
        editor.apply();
    }

}
