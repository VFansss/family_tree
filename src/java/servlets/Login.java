/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import classes.Database;
import classes.User;
import classes.util.FreeMarker;
import classes.util.Message;
import java.io.IOException;
import java.net.URLEncoder;
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
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        if(Database.isConnected()){
            HttpSession session = request.getSession(false);  
            //Se non è stata generata la sessione
            if(session == null){
                Map<String, Object> data = new HashMap<>();
                // Recupera l'azione da svolgere (login o signup)
                String action = request.getParameter("action");
                // Se l'azione non è stata definita o non è valida, impostala come l'azione di login
                if(action == null || (action.equals("login") && action.equals("signup"))) action = "login";
                // Inserisci l'azione nel data-model
                data.put("action", action);
                //Codifica del messaggio di errore sulla base del codice inviato
                data.put("message", new Message(request.getParameter("msg"), true));

                FreeMarker.process("login.html",data, response, getServletContext());
            }else{
                // Altrimenti vai alla pagina dell'utente loggato
                response.sendRedirect("profile");
            }
        }
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Recupera l'email dell'utente
        String email = request.getParameter("email");
        //Recupera la password dell'utente
        String password = request.getParameter("password");
        
        String msg = null;
        boolean error = true;
        if(email.equals("") && password.equals("")){
            msg = "fld"; // All fields required
        }else{
            
            // Recupera l'utente
            User user_to_log = User.getUserByEmail(email);

            // Se l'utente non esiste
            if(user_to_log == null){
                msg = "usr";

            // Se la password dell'utente è sbagliata
            }else if(!user_to_log.checkPassword(password)){
                msg = "psw";

            }else{
                // Prepara l'utente ad essere loggato (gestione della variabili si sessione)
                user_to_log.prepareToLog(request);
                response.sendRedirect("profile");
                error = false;
            }
        
        }
        
        if(error){
            // Torna alla pagine di login con messaggio di errore
            response.sendRedirect("login?msg=" + URLEncoder.encode(msg, "UTF-8"));
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
