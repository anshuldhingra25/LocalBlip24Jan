package com.ordiacreativeorg.localblip.util;

import android.util.Log;

import com.ordiacreativeorg.localblip.model.Category;
import com.ordiacreativeorg.localblip.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dmytrobohachevskyy on 9/26/15.
 *
 * Common util for validation user input
 */
public final class CommonUtil {

    /**
     * Validate email address
     *
     * @return true if @param email is valid email address,
     * otherwise false
     */
    static public boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Validate first name
     *
     * @param firstName Users first name
     * @return true if name is valid, else false
     */
    public static boolean validateFirstName( String firstName ) {
        Pattern p = Pattern.compile("[A-Z0-9.,_%+-]+", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(firstName);
        return m.matches();
    }

    /**
     * Validate last name
     *
     * @param lastName Users lastname
     * @return true if name is valid, else false
     */
    public static boolean validateLastName( String lastName ) {
        return !lastName.matches( "[a-zA-z]+([ '-][a-zA-Z]+)*" );
    }

    /**
     * This method conver list of category to string
     *
     * @param arr - array of category object
     * @return - string category
     */
    public static String getCategoryString(List<Category> arr) {
        String res = "";

        Log.d( "CATEGORY COUNT", String.valueOf( arr.size() ) );
        for (Category category : arr) {
            res += category.getCategoryName() + " ";
        }

        return res;
    }


    public static ArrayList<String> getAllLocationListAsString(List<Location> list) {
        if ( null == list ) {
            Log.e( "LOCATION", "is null" );
            return null;
        }


        ArrayList<String> res = new ArrayList<>();
        for (Location location : list) {
            res.add(location.getAddress1());
        }

        return res;
    }
}
