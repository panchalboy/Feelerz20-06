package com.jassimalmunaikh.feelerz;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Predicate;

public class Utilities {
    private static final Utilities ourInstance = new Utilities();

    public static Utilities getInstance() {
        return ourInstance;
    }

    private Utilities() {
    }

    void HideKeyboard(Activity activity)
    {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    void ShowKeyboard(Activity activity)
    {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.showSoftInput(view, 0);

    }



    String GetAge(String dateOfBirth)
    {
        if(dateOfBirth.isEmpty()) return "";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dob = LocalDate.parse(dateOfBirth,formatter);
        Period diff = Period.between(dob,LocalDate.now());

        return new Integer(diff.getYears()).toString();
    }


    public String[] RemoveEmptyStrings(String[] data)
    {
        List<String> list = new ArrayList<String>(Arrays.asList(data));
        list.removeIf(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return s.isEmpty();
            }
        });
        data = list.toArray(new String[0]);
        return data;
    }


   public String GetFromattedDate()
    {
        SimpleDateFormat date =  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        return date.format(new Date());
    }

    public String updateFCMToken(String token) {

        String latest = UserDataCache.GetInstance().latestFCMToken;

        if(latest.isEmpty()) {
            UserDataCache.GetInstance().latestFCMToken = token;
            UserDataCache.GetInstance().currentFCMToken = token;
            return token;
        }
        if(latest.equals(token)) {
            UserDataCache.GetInstance().currentFCMToken = token;
            return token;
        }
        else {
            UserDataCache.GetInstance().currentFCMToken =  latest;
           return latest;
        }
    }
}
