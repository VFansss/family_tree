/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import classes.Database;
import classes.User;
import classes.util.DataUtil;
import classes.util.FreeMarker;
import classes.util.Message;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Marco
 */
public class Signup extends HttpServlet {

    /**
     * Caricamento pagina di signup
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("action", "signup");
        request.getRequestDispatcher("login").forward(request, response);
    }

    /**
     * Gestione della registrazione di un 
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email        = request.getParameter("email").trim();
        String password     = request.getParameter("password").trim();
        String name         = DataUtil.spaceTrim(request.getParameter("name"));
        String surname      = DataUtil.spaceTrim(request.getParameter("surname"));
        String gender       = request.getParameter("gender").trim();
        String birthplace   = DataUtil.spaceTrim(request.getParameter("birthplace"));
        String birthdate    = request.getParameter("birthdate").trim();

        Message check;

        // Se non sono stati compilati tutti i campi
        if(email.equals("") || password.equals("") || name.equals("") || surname.equals("") || gender == null || birthdate.equals("")  || birthplace.equals("")){
            check = new Message("fld", true);
        }else{

            // Controllo dell'email
            check = DataUtil.checkEmail(email);
            if(!check.isError()) {

                // Controllo della password
                check = DataUtil.checkPassword(password);
                if(!check.isError()) {

                    // Controllo del nome
                    check = DataUtil.checkName(name, "name");
                    if(!check.isError()) {

                        // Controllo del cognome
                        check = DataUtil.checkName(surname, "surname");
                        if(!check.isError()) {

                            // Controllo del sesso
                            check = DataUtil.checkGender(gender);
                            if(!check.isError()) {

                                // Controllo della città di nascita
                                check = DataUtil.checkBirthplace(birthplace);
                                if(!check.isError()) {

                                    // Controllo della data di nascita
                                    check = DataUtil.checkBirthdate(birthdate);
        }}}}}}}

        // Se è stato riscontrato un errore, 
        if(check.isError()){
            // Vai alla pagina di signup mostrando l'errore
            response.sendRedirect("signup?msg=" + URLEncoder.encode(check.getCode(), "UTF-8"));

        }else{

            Map<String, Object> data = new HashMap<>();

            // Genera l'id dell'utente
            data.put("id", User.createUniqueUserId(10));

            data.put("email", email);
            data.put("password", DataUtil.crypt(password));
            data.put("name", name);
            data.put("surname", surname);
            data.put("gender", gender);
            data.put("birthplace", birthplace);
            Date sqlDate = DataUtil.stringToDate(birthdate, "dd/MM/yyyy");
            data.put("birthdate", DataUtil.dateToString(sqlDate));

            try {
                Database.insertRecord("user", data);
                // Creo l'oggetto riservato all'utente
                User new_user = new User(name, surname, email, gender, sqlDate, birthplace, null);
                // Prepara l'utente ad essere loggato (gestione della variabili si sessione)
                new_user.prepareToLog(request);
                // Reindirizzamento alla pagina del profilo dell'utente
                response.sendRedirect("profile");
            } catch (SQLException ex) {
                response.sendRedirect("signup?msg=Error");
            }

        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
