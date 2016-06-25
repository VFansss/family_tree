package it.univaq.ingweb.collaborative.servlets;

import it.univaq.ingweb.collaborative.util.FreeMarker;
import it.univaq.ingweb.collaborative.util.Message;
import it.univaq.ingweb.collaborative.User;
import it.univaq.ingweb.collaborative.util.Utility;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.ParseException;
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
public class Settings extends HttpServlet {
    private static HttpSession session;
    private static User userLogged;
    
    /**
     * Caricamento della pagina delle impostazioni
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        //Gestione sessione
        session = request.getSession(false);  
        
        //Se non è stata generata la sessione
        if(session != null){     
            
            userLogged = (User)session.getAttribute("user_logged");
            
            String as = userLogged.getBirthdate().toString();
            Map<String, Object> data = new HashMap<>();

            data.put("user_logged", userLogged);

            data.put("action",request.getParameter("action"));
            
        
            String msg = request.getParameter("msg");
            boolean error = false;
            data.put("message", new Message(msg, error));
            
            data.put("active_button", "settings");
            data.put("script", "settings");
            FreeMarker.process("settings.html",data, response, getServletContext());
            
        }else{
            // Vai alla pagina di login e mostra messaggio di errore
            response.sendRedirect("login?msg=" + URLEncoder.encode("Please log in to see this page", "UTF-8"));

        }
    }

    /**
     * Gestione del cambio dei dati dell'
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = (String)request.getParameter("action");

        session = request.getSession(false);  
        userLogged = (User)session.getAttribute("user_logged");
        
        //Controllo. Si tratta di una richiesta AJAX?
        boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        
        // Se non è loggato nessun utente;
        if(!(action == null || (action.equals("data") || action.equals("email") || action.equals("password") || action.equals("avatar")))){
            // Vai alla pagina delle impostazioni
            response.sendRedirect("settings");
        }else if(userLogged == null){
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
                default: msg = new Message("tmp", true);
            }
            if(ajax){
                // Definisce la risposta alla chiamata ajax
                response.setContentType("text/plain");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(msg.toJSON()); 
            }else{
                if(msg.isError()){
                    response.sendRedirect("settings?msg=" + URLEncoder.encode(msg.getCode(), "UTF-8") + "&action=" + action + "&type=error");
                }else{
                    response.sendRedirect("settings?msg=" + URLEncoder.encode(msg.getCode(), "UTF-8") + "&action=" + action + "&type=check");
                }
            }
            
        }
    }

    private static Message changeData(HttpServletRequest request){
        // Recupero dei dati
        String name         = Utility.spaceTrim(request.getParameter("name"));
        String surname      = Utility.spaceTrim(request.getParameter("surname"));
        String gender       = request.getParameter("gender").trim();
        String birthplace   = Utility.spaceTrim(request.getParameter("birthplace"));
        String birthdate    = request.getParameter("birthdate").trim();
        String biography    = Utility.spaceTrim(request.getParameter("biography"));
        
        
        Message check = Utility.checkData(name, surname, gender, birthdate, birthplace);
        try {
            if(!check.isError()){
                userLogged.setData(name, surname, gender, birthdate, birthplace, biography);
            }
            
        } catch (SQLException ex) {
            check = new Message("srv", true); // Server error
        } catch (ParseException ex) {
             check = new Message("date_2", true); // The date is not valid
        }
        
        // Se non ci sono errori 
        if(!check.isError()){
            check = new Message("dt_ok", false); // Data changed
        }

        return check;
    }
    
    private static Message changeEmail(HttpServletRequest request){
        // Recupero dei dati
        String current_email = (String)request.getParameter("current_email");
        String new_email = (String)request.getParameter("new_email");
        String confirm_email = (String)request.getParameter("confirm_email");
        
        String msg;
        boolean error = true;
        
        // Se non sono stati compilati tutti i dati
        if(current_email.equals("") || new_email.equals("") || confirm_email.equals("")){
            msg = "fld"; // All fields are required
            
        // Se l'email corrente è sbagliata
        }else if(!userLogged.getEmail().equals(current_email)){
            msg =  "eml_1"; // Current email is not valid
        
        // Se la conferma dell'email non corrisponde
        }else if(!confirm_email.equals(new_email)){
            
            msg =  "eml_2"; // Confirm email is not valid
            
        }else{
            // Se l'email non è valida
            Message check = Utility.checkEmail(new_email);
            if(check.isError()){
                msg = check.getCode();
                
            }else{
                try {
                    // Aggiorna email utente
                    userLogged.setEmail(new_email);
                    msg =  "eml_ok"; // Email changed
                    error = false;
                } catch (SQLException ex) {
                    msg =  "srv"; // Server error
                }
            }
        }
        
        // Ritorna il messaggio da visualizzare
        return new Message(msg, error);
    }
    
    private static Message changePassword(HttpServletRequest request){
        // Recupero dei dati
        String current_password = (String)request.getParameter("current_password");
        String new_password = (String)request.getParameter("new_password");
        String confirm_password = (String)request.getParameter("confirm_password");
        
        String msg;
        boolean error = true;
        
        // Se non sono stati compilati tutti i dati
        if(current_password.equals("") || new_password.equals("") || confirm_password.equals("")){
            msg = "fld";
            
        // Se la password corrente è sbagliata
        }else if(!userLogged.checkPassword(current_password)){
            msg =  "psd_1"; // Current passwrod is not valid
        
        // Se la conferma della password non corrisponde
        }else if(!new_password.equals(confirm_password)){
            msg =  "psd_2"; // Confirm passwrod is not valid
            
        }else{
            
            // Se la password non è nel formato giusto
            Message check = Utility.checkPassword(new_password);
            if(check.isError()){
                msg = check.getCode();
            }else{
                try {
                    // Aggiorna email utente
                    userLogged.setPassword(confirm_password);
                    msg =  "psd_ok"; // Password changed
                    error = false;
                } catch (SQLException ex) {
                    msg =  "srv"; // Server error
                }
            }
        }
        
        // Ritorna il messaggio da visualizzare
        return new Message(msg, error);
    }
    
    private static Message changeAvatar(HttpServletRequest request, Settings aThis){
        String msg = "";
        boolean error = true;
        //process only if its multipart content
        if(ServletFileUpload.isMultipartContent(request)){
            try {
                List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
               
                for(FileItem item : multiparts){
                    if(!item.isFormField()){
                        if(item.getName().equals("")){
                            msg = "pho_slt"; // Please, select a photo
                        }else{
                            String name = userLogged.getId() + ".jpg";
                            item.write( new File(aThis.getServletContext().getRealPath("/template/profile/").replace("build\\", "") + File.separator + name));
                            msg = "pho_ok"; // Photo Uploaded Successfully
                            error = false;
                        }
                        
                    }
                }
                
            } catch (Exception ex) {
               msg = "pho_err"; // Photo Uploaded Failed
            }         
          
        }else{
            msg = "tmp";
        }
     

        return new Message(msg, error);
    }
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet per le impostazioni";
    }
}
