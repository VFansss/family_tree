/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.script;

import classes.DataUtil;
import classes.Database;
import classes.Message;
import classes.User;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

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
            response.sendRedirect("login?msg=log");
                
        }else{

            Message msg; 
            switch (action) {
                case "data":
                    msg = changeData(request);
                    break;
                case "email":
                    msg = changeEmail(request);
                    break;
                case "password":
                    msg = changePassword(request);
                    break;
                case "avatar":
                    msg = changeAvatar(request, this);
                    break;
                default: msg = new Message("Something is wrong", true);
            }
            
            if(msg.isError()){
                response.sendRedirect("settings?msg=" + msg.getMessage() + "&action=" + action + "&type=error");
            }else{
                response.sendRedirect("settings?msg=" + msg.getMessage() + "&action=" + action + "&type=check");
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
        String biography = (String)request.getParameter("biography");
        
        // Conversione della data di nascita in un tipo compatibile al database
        
        
        String msg;
        boolean error = true;
        // Se non sono stati compilati tutti i dati
        if(name.equals("") || surname.equals("") || gender == null || birthdate.equals("")  || birthplace.equals("")){
            msg = "All fields are required";

        // Se la data di nascita non è valida
        }else {
            
            Message check;
        
            // Controllo del nome
            check = DataUtil.checkName(name);
            if(!check.isError()) {

                // Controllo del cognome
                check = DataUtil.checkName(surname);
                if(!check.isError()) {

                    // Controllo del sesso
                    check = DataUtil.checkGender(gender);
                    if(!check.isError()) {

                        // Controllo della città di nascita
                        check = DataUtil.checkBirthplace(birthplace);
                        if(!check.isError()) {

                            // Controllo della data di nascita
                            check = DataUtil.checkBirthdate(birthdate);
            }}}}
            
            if(check.isError()){
                msg = check.getMessage();
                
            }else{
            
                Map<String, Object> data = new HashMap<>();
            
                data.put("name", name);
                data.put("surname", surname);
                Date sqlDate = DataUtil.stringToDate(birthdate, "dd/MM/yyyy");
                data.put("birthdate", DataUtil.dateToString(sqlDate));
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

                user_logged.setBiography(biography);

                // Aggiornamento dati dell'utente
                if(!user_logged.setData(data)) {
                    msg = "Something is wrong";
                }else{
                    msg = "Data changed";
                    error = false;
                }
            }
            
            
        }
             
        return new Message(msg, error);
        
    }
    
    public static Message changeEmail(HttpServletRequest request){
        // Recupero dei dati
        String current_email = (String)request.getParameter("current_email");
        String new_email = (String)request.getParameter("new_email");
        String confirm_email = (String)request.getParameter("confirm_email");
        
        String msg;
        boolean error = true;
        
        // Se non sono stati compilati tutti i dati
        if(current_email.equals("") || new_email.equals("") || confirm_email.equals("")){
            msg = "All fields are required";
            
        // Se l'email corrente è sbagliata
        }else if(!user_logged.getEmail().equals(current_email)){
            msg =  "Current email is not valid";
        
        // Se la conferma dell'email non corrisponde
        }else if(!confirm_email.equals(new_email)){
            msg =  "Confirm email is not valid";
            
        }else{
            // Se l'email non è valida
            Message check = DataUtil.checkEmail(new_email);
            if(check.isError()){
                msg = check.getMessage();
                
            }else{
                // Aggiorna email utente
                boolean result = user_logged.setEmail(new_email);
                if(!result) {
                    msg =  "Something is wrong";
                }else{
                    msg =  "Email changed";
                    error = false;
                }
            }
        }
        
        // Ritorna il messaggio da visualizzare
        return new Message(msg, error);
    }
    
    public static Message changePassword(HttpServletRequest request){
        // Recupero dei dati
        String current_password = (String)request.getParameter("current_password");
        String new_password = (String)request.getParameter("new_password");
        String confirm_password = (String)request.getParameter("confirm_password");
        
        String msg;
        boolean error = true;
        
        // Se non sono stati compilati tutti i dati
        if(current_password.equals("") || new_password.equals("") || confirm_password.equals("")){
            msg = "All fields are required";
            
        // Se l'password corrente è sbagliata
        }else if(!user_logged.checkPassword(current_password)){
            msg =  "Current passwrod is not valid";
        
        // Se la conferma dell'password non corrisponde
        }else if(!new_password.equals(confirm_password)){
            msg =  "Confirm passwrod is not valid";
            
        }else{
            
            // Se la password non è nel formato giusto
            Message check = DataUtil.checkEmail(new_password);
            if(check.isError()){
                msg = check.getMessage();
            }else{
               // Aggiorna email utente
                boolean result = user_logged.setPassword(confirm_password);
                if(!result){
                    msg =  "Something is wrong";
                } else{
                    msg =  "Password changed";
                    error = false;
                }     
               
            }
            
                
            
        }
        
        // Ritorna il messaggio da visualizzare
        return new Message(msg, error);
    }
    
    public static Message changeAvatar(HttpServletRequest request, SettingsS aThis){
        String msg = "";
        boolean error = true;
        //process only if its multipart content
        if(ServletFileUpload.isMultipartContent(request)){
            try {
                List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
               
                for(FileItem item : multiparts){
                    if(!item.isFormField()){
                        if(item.getName().equals("")){
                            msg = "Please, select a photo";
                        }else{
                            String name = user_logged.getId() + ".jpg";
                            item.write( new File(aThis.getServletContext().getRealPath("/template/profile/").replace("build\\", "") + File.separator + name));
                            msg = "Photo Uploaded Successfully";
                            error = false;
                        }
                        
                    }
                }
            
               //File uploaded successfully
               
            } catch (Exception ex) {
               msg = "File Uploaded Failed";
            }         
          
        }else{
            msg = "Something is wrong";
        }
     

        return new Message(msg, error);
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
