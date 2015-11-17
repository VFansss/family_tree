/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.collaborative_genealogy.servlets;

import it.collaborative_genealogy.Database;
import it.collaborative_genealogy.User;
import it.collaborative_genealogy.util.DataUtil;
import it.collaborative_genealogy.util.FreeMarker;
import it.collaborative_genealogy.util.Message;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.ResultSet;
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
public class Login extends HttpServlet {
    
    /**
     * Caricamento pagina di login
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);  
        //Se non è stata generata la sessione
//        if(session == null){
            
            Map<String, Object> data = new HashMap<>();

            try{
                String user_id = request.getParameter("code");
                data.put("user_id", user_id);
                data.put("email", User.getUserById(user_id).getEmail());
            }catch(NullPointerException ex){}

            /* Gestione azione */
                // Recupera l'azione da svolgere (login o signup)
                String action = (String) request.getAttribute("action");
                // Se l'azione non è stata definita o non è valida, impostala come l'azione di login
                if(action == null || (action.equals("login") && action.equals("signup"))) action = "login";
                // Inserisci l'azione nel data-model
                data.put("action", action);

            //Codifica del messaggio di errore sulla base del codice inviato
            data.put("message", new Message(request.getParameter("msg"), true));

            data.put("login_script", "");

            FreeMarker.process("login.html", data, response, getServletContext());
//        }else{
//            // Altrimenti vai alla pagina dell'utente loggato
//            response.sendRedirect("profile");
//        }
        
    }

    /**
     * Gestione login dell'utente
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Recupera l'email dell'utente
        String email = request.getParameter("email");
        //Recupera la password dell'utente
        String password = request.getParameter("password");
        
        //Controllo. Si tratta di una richiesta AJAX?
        boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        boolean quick_signup = false;
        String msg = null;
        boolean error = true;
        
        if(email.equals("") && password.equals("")){
            msg = "fld"; // All fields required
            
        }else{
            
            // Recupera l'utente
            User user_to_log = User.getUserByEmail(email);
            // Se l'utente non esiste
            if(user_to_log == null){
                msg = "usr_1";
            }else{
                try {
                    ResultSet record = Database.selectRecord("user", "email = '" + user_to_log.getEmail() + "'");
                    if(record.next()){
                        // Se l'utente non ha una password
                        if(record.getString("password") == null){
                            //Recupera l'id inviato dal form
                            String new_user_id = request.getParameter("user_id");
                            // Controllo di autenticazione: dal form viene anche inviato l'id dell'utente invitato, 
                            //      quindi se l'id nel form corrisponde all'id dell'utente inviato, quest'ultimo è autenticato.
                            if(new_user_id != null && new_user_id.equals(user_to_log.getId())){

                                Map<String, Object> data = new HashMap<>();
                                data.put("password", DataUtil.crypt(password));
                                Database.updateRecord("user", data, "id = '" + user_to_log.getId() + "'");
                                // Prepara l'utente ad essere loggato (gestione della variabili si sessione)
                                user_to_log.prepareToLog(request);
                                error = false;
                            }else{
                                msg = "tmp";
                            }
                            quick_signup = true;

                        }
                    }
                } catch (SQLException ex) {
                    msg = "srv";
                }
                
                if(!quick_signup){
                
                    // Se la password dell'utente è sbagliata
                    if(!user_to_log.checkPassword(password)){
                        msg = "psw";

                    }else{
                        // Prepara l'utente ad essere loggato (gestione della variabili si sessione)
                        user_to_log.prepareToLog(request);
                        error = false;
                    }
                }
                if(error){
                    
                }
            }
            
        }
        // Se si è verificato un errore
        if(error){
            // Se la servlet è stata chiamata con ajax
            if (ajax) {
                // Definisci il messaggio per ricavarne la descrizione completa
                Message message = new Message(msg, true);
                // Definisce la risposta alla chiamata ajax
                response.setContentType("text/plain");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(message.getMsg());       
            } else {
                // Torna alla pagine di login con messaggio di errore
                response.sendRedirect("login?msg=" + URLEncoder.encode(msg, "UTF-8"));
            }
        }else{
            if (ajax) {
                // Handle ajax response.
                response.setContentType("text/plain");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("");       
            }else{
                // Handle regular response
                response.sendRedirect("profile");
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
