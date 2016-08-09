package it.univaq.ingweb.collaborative.util;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.commons.validator.DateValidator;

/**
 *
 * @author Marco
 */
public class DateUtility {
    
    public static final String DATE_FORMAT_DEFAULT = "dd/MM/yyyy";
    /**
     * Converte una data da String a Date
     * @param date      data da convertire
     * @param format    formato di date
     * @return          data nell formato yyyy-MM-dd
     * @throws java.text.ParseException
     */
    public static Date stringToDate(String date, String format) throws ParseException{
        DateFormat formatter = new SimpleDateFormat(format);
        java.util.Date myDate;

        myDate = formatter.parse(date);
        Date sqlDate = new Date(myDate.getTime());
        return sqlDate;
        
    }
    
    /**
     * Converte una data da String a Date con formato "dd/MM/yyyy"
     * @param date      data da convertire
     * @return          data nell formato yyyy-MM-dd
     * @throws java.text.ParseException
     */
    public static Date stringToDate(String date) throws ParseException{
        return stringToDate(date, DATE_FORMAT_DEFAULT);
    }
    
    /**
     * Controlla il formato della data (dd/MM/yyyy)
     * @param date  data da verificare
     * @return      true se la data è nel formato corretto, false altrimenti
     */
    public static boolean validateDateFormat(String date){
        return DateValidator.getInstance().isValid(date, "dd/MM/yyyy", false);
    }
    
    /**
     * Controlla se la data rientra nel range stabilito (01/01/1900 - oggi)
     * @param date  data da verificare
     * @return      true se la data è corretta, false altrimenti
     */
    public static boolean validateDateRange(Date date){
        
        // Calcola la data corrente
        java.util.Date currentDate = new java.util.Date();
        // Calcola la data minima 01/01/1900)
        Calendar cal = new GregorianCalendar(1900, 01, 01);
        java.util.Date minDate = cal.getTime();
        
        // Calcola i secondi della data da validare
        long dateSecond = date.getTime();
        // Calcola i secondi della data attuale
        long currentSecond = currentDate.getTime();
        // Calcola i secondi della data minima
        long minSecond = minDate.getTime();

        return !(dateSecond >= currentSecond || dateSecond <= minSecond);
    }
    
    /**
     * Converte una data da Date a String
     * @param date  data da convertire
     * @return      stringa nel formato yyyy-MM-dd
     */
    public static String dateToString(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
