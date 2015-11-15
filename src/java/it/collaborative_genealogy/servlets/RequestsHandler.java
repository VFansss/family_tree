/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.collaborative_genealogy.servlets;

import it.collaborative_genealogy.User;
import it.collaborative_genealogy.Request;
import it.collaborative_genealogy.exception.NotAllowed;
import it.collaborative_genealogy.util.FreeMarker;
import it.collaborative_genealogy.util.Message;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Gianluca
 */
public class RequestsHandler extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  
        try{
            
            HttpSession session = request.getSession(false);
            
            if(session!=null){
                 // Recupero dell'utente loggato
                User user_logged = (User)session.getAttribute("user_logged");
                
                // Recupero la lista delle richieste
                List<Request> requests = new LinkedList<>();
                try{
                    ResultSet record = user_logged.getRequests();
                    while (record.next()){
                        requests.add(new Request(record));
                    }
                } catch (SQLException ex) {
                    
                }
                
                // Se ci sono richieste da mostrare
                if(!requests.isEmpty()){
                    
                    Map<String, Object> data = new HashMap<>();
                    data.put("user_logged", user_logged);
                    data.put("message", new Message(request.getParameter("msg"), true));
                    data.put("requests", requests);

                    FreeMarker.process("requests.html", data, response, getServletContext());
                }else{
                    // Altrimenti, vai alla pagina del profilo
                    response.sendRedirect("profile");
                    
                }
                
                
            } else {
                // Vai alla pagina di login e mostra messaggio di errore
                response.sendRedirect("login?msn=" + URLEncoder.encode("log", "UTF-8"));
            }
            
        } catch (Exception ex) {
            response.sendRedirect("error");
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
    
            
        HttpSession session = request.getSession(false);

        if(session!=null){
            Message message;
            
            // Recupero dell'utente loggato
            User user_logged = (User)session.getAttribute("user_logged");
            
            String accept = request.getParameter("accept");
            String decline = request.getParameter("decline");
            
            // Se si deve accettare la richiesta di parantela
            if(accept != null){
                
                // Recupero dell'utente che ha inviato la richeista
                User sender = User.getUserById(accept);
                try{
                    user_logged.acceptRequest(sender);
                    // Dopo aver aggiungo il nuovo parente, bisogna fare il refresh dll'albero genealogico di tutti i parenti loggati in quel momento
                    user_logged.sendRefreshAck();
                     message = new Message("acc", false); // Request accepted
                } catch (NotAllowed ex){
                    message = new Message("no_all", true); // Not allowed
                } catch (SQLException ex){
                    message = new Message("srv", true); // Server error
                }
                
            // Se si deve rifiutare la richiesta di parantela
            }else if(decline != null){
                // Recupero dell'utente che ha inviato la richeista
                User sender = User.getUserById(decline);
                try{
                    user_logged.declineRequest(sender);
                    message = new Message("dec", false); // Request declined
                } catch (SQLException ex){
                    message = new Message("srv", true); // Server error
                }
            
            }else{
                // Dati corrotti
                message = new Message("tmp", true); // Tampered data
            }
            
            // Torna all apagina delle richieste
            response.sendRedirect("requests?msn=" + URLEncoder.encode(message.getCode(), "UTF-8"));
        } else {
            // Vai alla pagina di login e mostra messaggio di errore
            response.sendRedirect("login?msn=" + URLEncoder.encode("log", "UTF-8"));
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
