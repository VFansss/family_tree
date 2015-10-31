/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.script;

import classes.Database;
import classes.Function;
import classes.Message;
import classes.User;
import java.io.IOException;
import java.sql.Date;
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
public class SettingsS extends HttpServlet {
    private static HttpSession session;
    private static User user_logged;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String action = (String)request.getParameter("action");
        
        session = request.getSession(false);  
        user_logged = (User)session.getAttribute("user_logged");
        
        // Se non è loggato nessun utente;
        if(!(action == null || (action.equals("data") || action.equals("email") || action.equals("password") || action.equals("avatar")))){
            // Vai alla pagina delle impostazioni
            response.sendRedirect("settings");
        }else if(user_logged == null){
            response.sendRedirect("login?msn=log");
                
        }else{

            Message msn; 
            switch (action) {
                case "data":
                    msn = changeData(request);
                    break;
                case "email":
                    msn = changeEmail(request);
                    break;
                case "password":
                    msn = changePassword(request);
                    break;
                case "avatar":
                    msn = changeAvatar(request);
                    break;
                default: msn = new Message("Something is wrong", false);
            }
            
            if(msn.isError()){
                response.sendRedirect("settings?msn=" + msn.getMessage() + "&action=" + action + "&type=error");
            }else{
                response.sendRedirect("settings?msn=" + msn.getMessage() + "&action=" + action + "&type=check");
            }
            

        }
        
    }
    
    public static Message changeData(HttpServletRequest request){
        // Recupero dei dati
        String name = (String)request.getParameter("name");
        String surname = (String)request.getParameter("surname");
        String gender = (String)request.getParameter("gender");
        String birthdate = (String)request.getParameter("birthdate");
        String birthplace = (String)request.getParameter("birthplace");
        
        // Conversione della data di nascita in un tipo compatibile al database
        Date sqlDate = Function.stringToDate(birthdate, "dd/MM/yyyy");
        
        String msn;
        boolean flag = false;
        // Se non sono stati compilati tutti i dati
        if(name.equals("") || surname.equals("") || gender == null || birthdate.equals("")  || birthplace.equals("")){
            msn = "All fields are required";
        
        // Se non è stato modificato nessun campo
        }else if(user_logged.getName().equals(name) && user_logged.getSurname().equals(surname) && user_logged.getGender().equals(
                    gender) && user_logged.getBirthdate().equals(sqlDate) && user_logged.getBirthplace().equals(birthplace)){
            msn = "No data to change";

        // Se il sesso non è valido
        }else if(!(gender.toLowerCase().equals("male") || gender.toLowerCase().equals("female"))){
            msn = "You can be only male or female";
            
        // Se la data di nascita non è valida
        }else if(sqlDate == null){    
            msn = "Birthdate is not valid";
            
        }else{
            
            Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("surname", surname);
            data.put("birthdate", Function.dateToString(sqlDate));
            data.put("birthplace", birthplace);

            if(!user_logged.getGender().equals(gender)){
                /*
                    Se si cambia il sesso, l'utente deve essere scollegato dal proprio albero genealogico 
                */
                data.put("gender", gender);
                user_logged.removeFather();
                user_logged.removeMother();
                user_logged.removeSpouse();
            }
            
            
            // Aggiornamento dati dell'utente
            if(!user_logged.setData(data)) {
                msn = "Something is wrong";
            }else{
                msn = "Data changed";
                flag = true;
            }
            
        }
             
        return new Message(msn, flag);
        
    }
    
    public static Message changeEmail(HttpServletRequest request){
        // Recupero dei dati
        String current_email = (String)request.getParameter("current_email");
        String new_email = (String)request.getParameter("new_email");
        String confirm_email = (String)request.getParameter("confirm_email");
        
        String msn;
        boolean flag = false;
        
        // Se non sono stati compilati tutti i dati
        if(current_email.equals("") || new_email.equals("") || confirm_email.equals("")){
            msn = "All fields are required";
            
        // Se l'email corrente è sbagliata
        }else if(!user_logged.getEmail().equals(current_email)){
            msn =  "Current email is not valid";
        
        // Se la conferma dell'email non corrisponde
        }else if(!confirm_email.equals(new_email)){
            msn =  "Confirm email is not valid";
            
        }else{
            
            // Aggiorna email utente
            boolean result = user_logged.setEmail(new_email);
            if(!result) msn =  "Something is wrong";
                
            msn =  "Email changed";
            flag = true;
        }
        
        // Ritorna il messaggio da visualizzare
        return new Message(msn, flag);
    }
    
    public static Message changePassword(HttpServletRequest request){
        // Recupero dei dati
        String current_password = (String)request.getParameter("current_password");
        String new_password = (String)request.getParameter("new_password");
        String confirm_password = (String)request.getParameter("confirm_password");
        
        String msn;
        boolean flag = false;
        
        // Se non sono stati compilati tutti i dati
        if(current_password.equals("") || new_password.equals("") || confirm_password.equals("")){
            msn = "All fields are required";
            
        // Se l'email corrente è sbagliata
        }else if(!user_logged.checkPassword(current_password)){
            msn =  "Current passwrod is not valid";
        
        // Se la conferma dell'email non corrisponde
        }else if(!new_password.equals(confirm_password)){
            msn =  "Confirm passwrod is not valid";
            
        }else{
            
            // Aggiorna email utente
            boolean result = user_logged.setPassword(confirm_password);
            if(!result) msn =  "Something is wrong";
                
            msn =  "Password changed";
            flag = true;
        }
        
        // Ritorna il messaggio da visualizzare
        return new Message(msn, flag);
    }
    
    public static Message changeAvatar(HttpServletRequest request){
        String msn = "";
        boolean flag = false;
        
        return new Message(msn, flag);
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
