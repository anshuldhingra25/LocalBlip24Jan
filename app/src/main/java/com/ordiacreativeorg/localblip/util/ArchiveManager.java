package com.ordiacreativeorg.localblip.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ordiacreativeorg.localblip.model.Blip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/15/2015
 */
public class ArchiveManager {
    private static SharedPreferences getPreferences(Context context){
        return context.getSharedPreferences("com.ordiacreative.localblip.archive", Context.MODE_PRIVATE);
    }

    public static void getSession(Context context, HashMap<String,String> sessionInfo){
        sessionInfo.clear();
        String id = getPreferences(context).getString("session_id", "");
        String email = getPreferences(context).getString("session_email", "");
        String apiKey = getPreferences(context).getString("session_apikey", "");
        if (!email.trim().isEmpty() && !apiKey.trim().isEmpty()) {
            sessionInfo.put("id", id);
            sessionInfo.put("email", email);
            sessionInfo.put("apikey", apiKey);
        }
    }

    public static void saveSession(Context context, String email, String apiKey, int id){
        getPreferences(context).edit().putString("session_id", String.valueOf(id)).commit();
        getPreferences(context).edit().putString("session_email", email).commit();
        getPreferences(context).edit().putString("session_apikey", apiKey).commit();
    }

    public static void removeSession(Context context){
        getPreferences(context).edit().remove("session_id").commit();
        getPreferences(context).edit().remove("session_email").commit();
        getPreferences(context).edit().remove("session_apikey").commit();
    }

    public static void archiveNewBlips(Context context, String memberId, List<Blip> blips){
        String blipsIds = "";
        for (Blip blip : blips) {
            blipsIds += blip.getCouponId() + ";";
        }
        if (blipsIds.length() > 0) blipsIds = blipsIds.substring(0, blipsIds.length() - 1);
        getPreferences(context).edit().putString("anb_" + memberId, blipsIds).commit();
    }

    public static ArrayList<String> getArchivedNewBlips(Context context, String memberId){
        String blips = getPreferences(context).getString("anb_" + memberId, "");
        ArrayList<String> sendersList = new ArrayList<>();
        sendersList.addAll(Arrays.asList(blips.split(";")));
        return sendersList;
    }

    public static void archiveBlipAlerts(Context context, String memberId, List<String> conversations){
        String blipAlertsIds = "";
        for (String conversation : conversations) {
            blipAlertsIds += conversation + ";";
        }
        if (blipAlertsIds.length() > 0) blipAlertsIds = blipAlertsIds.substring(0, blipAlertsIds.length() - 1);
        getPreferences(context).edit().putString("anb_" + memberId, blipAlertsIds).commit();
    }

    public static ArrayList<String> getArchivedBlipAlerts(Context context, String memberId){
        String blips = getPreferences(context).getString("anb_" + memberId, "");
        ArrayList<String> sendersList = new ArrayList<>();
        sendersList.addAll(Arrays.asList(blips.split(";")));
        return sendersList;
    }

    public static void archiveMessages(Context context, String memberId, List<String> conversations){
        String messagesIds = "";
        for (String conversation : conversations) {
            messagesIds += conversation + ";";
        }
        if (messagesIds.length() > 0) messagesIds = messagesIds.substring(0, messagesIds.length() - 1);
        getPreferences(context).edit().putString("anb_" + memberId, messagesIds).commit();
    }

    public static ArrayList<String> getArchivedMessages(Context context, String memberId){
        String blips = getPreferences(context).getString("anb_" + memberId, "");
        ArrayList<String> sendersList = new ArrayList<>();
        sendersList.addAll(Arrays.asList(blips.split(";")));
        return sendersList;
    }
    public static void saveFirst(Context context, String s){
        getPreferences(context).edit().putString("session_first", s).commit();
    }

    public static void removeFirst(Context context){
        getPreferences(context).edit().remove("session_first").commit();

    }
}
