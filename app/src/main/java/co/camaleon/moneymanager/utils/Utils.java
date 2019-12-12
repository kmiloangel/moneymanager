package co.camaleon.moneymanager.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.NumberFormat;

import co.camaleon.moneymanager.R;
import co.camaleon.moneymanager.fragments.NewMovimientoFragment;

/**
 * Created by Usuario on 14/04/2015.
 */
public class Utils {
    public static String setPriceFormat(float price){
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(price);
    }

    public static String getStringWhenEmpty(String str){
        return str == null || str.isEmpty() ? ConstantsUtils.NOT_DEFINED_NAME : str;
    }

    public static String fillCeros(int num, int length){
        return String.format("%0" + length + "d", num);
    }

    public static String implode(String glue, String[] strArray){
        String ret = "";
        for(int i=0;i<strArray.length;i++)
        {
            ret += (i == strArray.length - 1) ? strArray[i] : strArray[i] + glue;
        }
        return ret;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void goToPreviousFragment(Activity activity, FragmentManager fm){
        Utils.hideSoftKeyboard(activity);

        fm.popBackStack();
    }
}
