package co.camaleon.moneymanager.utils;

import android.util.Log;

import java.lang.String;import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

	public static String setDateFormat(String date, boolean withTime, String dateFormat){
		if(date == null || date.isEmpty()) return "";
		if(withTime && date.length() == 10){
			date += " 00:00:00";
		}

		String str = removeTimeZone(date);

		String strData = null;
		TimeZone tzUTC = TimeZone.getTimeZone(ConstantsUtils.TIME_ZONE);

		String entrada = withTime ? "yyyy-MM-dd HH:mm:ss": "yyyy-MM-dd";
		SimpleDateFormat formatoEntrada = new SimpleDateFormat(entrada, Locale.US);
		formatoEntrada.setTimeZone(tzUTC);

		String format;
		if(dateFormat == null) {
			format = withTime ? "EEEE, yyyy/MMMM/dd HH:mm" : "EEEE, yyyy/MMMM/dd";
		}else{
			format = dateFormat;
		}

		SimpleDateFormat formatoSaida = new SimpleDateFormat(format);

		try {
			strData = formatoSaida.format(formatoEntrada.parse(str));
			strData = strData.substring(0,1).toUpperCase() + strData.substring(1);
		} catch (ParseException e) {
			Log.e("Error parser data", Log.getStackTraceString(e));
		}
		return strData;
	}

	public static String getDateFormated(int year, int month, int day){
		StringBuilder dateStr = new StringBuilder();
		dateStr.
				append(year).
				append("-").
				append(Utils.fillCeros(month + 1, 2)).
				append("-").
				append(Utils.fillCeros(day, 2));

		return dateStr.toString();
	}

	public static String getDateTimeFormated(int year, int month, int day, int hour, int minute){
		StringBuilder dateStr = new StringBuilder();
		dateStr.
				append(year).
				append("-").
				append(Utils.fillCeros(month + 1, 2)).
				append("-").
				append(Utils.fillCeros(day, 2)).
				append(" ").
				append(Utils.fillCeros(hour, 2)).
				append(":").
				append(Utils.fillCeros(minute, 2)).
				append(":00");

		return dateStr.toString();
	}

	public static String removeTimeZone(String data){
		// busca na string e remove o padrão " (+ ou -)dddd" Ex: " +3580"
		return data.replaceFirst("(\\s[+|-]\\d{4})", "");
	}


}
