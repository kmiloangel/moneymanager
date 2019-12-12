package co.camaleon.moneymanager.utils;

import android.widget.TextView;

import java.util.regex.Pattern;

/**
 * Created by Usuario on 20/04/2015.
 */
public class FormValidation {
    public static final int VALIDATE_EMPTY      = 1;
    public static final int VALIDATE_NUMERIC    = 2;
    public static final int VALIDATE_EMAIL      = 3;

    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private String str;

    public static boolean validate(int type, TextView tv, String error_message){
        String str = tv.getText().toString().trim();
        tv.setError(null);

        boolean error;

        switch (type){
            case FormValidation.VALIDATE_EMPTY: error = validateEmpty(str); break;
            case FormValidation.VALIDATE_NUMERIC: error = validateNumber(str); break;
            case FormValidation.VALIDATE_EMAIL: error = validateEmail(str); break;
            default: error = false;
        }

        if(!error){
            tv.setSelectAllOnFocus(true);
            tv.clearFocus();
            tv.requestFocus();
            tv.setError(error_message);
        }

        return error;
    }

    private static boolean validateEmpty(String str){
        return str != null && !str.isEmpty();
    }

    private static boolean validateNumber(String str){
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    private static boolean validateEmail(String str){
        return Pattern.matches(FormValidation.EMAIL_REGEX, str);
    }
}
