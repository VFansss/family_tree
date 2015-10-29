/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author Marco
 */
public class Function {
    
    public static java.sql.Date stringToDate(String birthdate){
        // Conversione della data di nascita da String a Date
            try {
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date myDate;
                myDate = formatter.parse(birthdate);
                java.sql.Date sqlDate = new java.sql.Date(myDate.getTime());
                return sqlDate;
            } catch (ParseException ex) {
                // Se il parse della data non va a buon fine, significa che la data non è nel formato giusto
                return null;
            }
    }
}
