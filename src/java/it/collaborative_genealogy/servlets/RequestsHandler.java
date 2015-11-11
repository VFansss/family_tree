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
                
                Map<String, Object> data = new HashMap<>();
                // Recupero dell'utente loggato
                User user_logged = (User)session.getAttribute("user_logged");
                
                // GESTIONE ACCETTAZIONE RICHIESTE
                String req = request.getParameter("accept_from");
                if(req!=null){
                    User sender = User.getUserById(req);
                    try{
                        user_logged.acceptRequest(sender);
                        
                        data.put("message", "Request accepted");
                        // Dopo aver aggiungo il nuovo parente, bisogna fare il refresh dll'albero genealogico di tutti i parenti loggati in quel momento
                        user_logged.sendRefreshAck();
                    } catch (NotAllowed ex){
                        
                        data.put("message", "You are not allowed to accept this request");
                    } catch (SQLException ex){
                        
                        data.put("message", "An error occurred, please retry");
                    }
                }
                
                // GESTIONE RIFIUTO RICHIESTE
                req = request.getParameter("decline_from");
                if(req!=null){
                    User sender = User.getUserById(req);
                    try{
                        user_logged.declineRequest(sender);
                        data.put("message", "Request declined");
                    } catch (SQLException ex){
                        
                        data.put("message", "An error occurred, please retry");
                    }
                }
                
               
                List<Request> requests = new LinkedList<>();
                
                try{
                    ResultSet record = user_logged.getRequests();
                    while (record.next()){
                        requests.add(new Request(record));
                    }
                } catch (SQLException ex) {
                    requests = null;
                }
                
                data.put("requests", requests);
                data.put("user_logged", user_logged);
                
                FreeMarker.process("requests.html", data, response, getServletContext());
                
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
