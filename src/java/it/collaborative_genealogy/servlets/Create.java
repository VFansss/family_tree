/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.collaborative_genealogy.servlets;

import it.collaborative_genealogy.Database;
import it.collaborative_genealogy.User;
import it.collaborative_genealogy.exception.NotAllowed;
import it.collaborative_genealogy.tree.GenealogicalTree;
import it.collaborative_genealogy.tree.TreeNode;
import it.collaborative_genealogy.util.DataUtil;
import it.collaborative_genealogy.util.FreeMarker;
import it.collaborative_genealogy.util.Message;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Gianluca
 */
public class Create extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * Costruzione della pagina per la creazione di un profilo o per l'invito di un utente
     * 
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        
        HttpSession session = request.getSession(false);

        if(session!=null){
            
            
            Map<String, Object> data = new HashMap<>();

            User user_logged = (User)session.getAttribute("user_logged");

            // Recupero dell'utente corrente: non c'è il controllo sull'esistenza dell'utente, viene raccolta l'eccezione
            User user_current;
            TreeNode user_current_node;
            
            if (request.getParameter("id") != null){
                user_current_node = ((GenealogicalTree)session.getAttribute("family_tree")).getUserById((String)request.getParameter("id"));
                user_current = user_current_node.getUser();
                data.put("relative_grade",  user_current_node.getLabel());

            } else {
                user_current = user_logged;
            }

            String action = request.getRequestURI().substring(request.getContextPath().length()+1);

            data.put("action", action);
            data.put("script", action);
            data.put("user_logged", user_logged);
            data.put("user_current", user_current);
            //Codifica del messaggio di errore sulla base del codice inviato
            data.put("message", new Message(request.getParameter("msg"), true));
            FreeMarker.process(action+".html", data, response, getServletContext());

        } else {
            response.sendRedirect("login?msg=log");
        }
           
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * 
     * Gestione della creazione degli utenti e gli invii degli inviti
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            HttpSession session = request.getSession(false);
            //Controllo. Si tratta di una richiesta AJAX?
            boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
            // Se è attiva una sessiona
            if(session!=null) {
                
                
                // Recupera l'utente loggato
                User user_logged = (User)session.getAttribute("user_logged");
                // Recupera l'azione da svolgere
                String action = request.getRequestURI().substring(request.getContextPath().length()+1); // create o invite
                // Recupera i dati del nuovo utente
                String name = request.getParameter("name");
                String surname = request.getParameter("surname");
                String gender = request.getParameter("gender");
                String birthdate = request.getParameter("birthdate");
                String birthplace = request.getParameter("birthplace");
                
                String relationship = request.getParameter("relationship");
                Message check;
                    if(relationship.equals("")){
                        check = new Message("fld", true);
                    }else{
                        // Verifica i dati dell'utente
                        check = DataUtil.checkData(name, surname, gender, birthdate, birthplace);
                        // Se tutti i dati sono corretti
                        if(!check.isError()){
                            // Recupero dell'utente al quale bisogna aggiungere il nuovo parente
                            TreeNode user_current_node = ((GenealogicalTree)session.getAttribute("family_tree")).getUserById((String)request.getParameter("relative"));
                            User user_current = user_current_node.getUser();
                            // Gestione dati dell'utente
                            Map<String, Object> data = new HashMap<>();  
                            String user_id = User.createUniqueUserId(10);
                            data.put("id", user_id);
                            data.put("name", name);
                            data.put("surname", surname);
                            data.put("gender", gender);
                            data.put("birthplace", birthplace);
                            
                            try {
                                Date sqlDate = DataUtil.stringToDate(birthdate, "dd/MM/yyyy");
                                data.put("birthdate", DataUtil.dateToString(sqlDate));
                            } catch (ParseException ex) {
                                check = new Message("date_2", true);
                            }
                            // Se bisogna creare un nuovo utente
                            if(action.equals("create")){
                                
                                String biography = request.getParameter("biography");
                                data.put("biography", biography);
                                try {

                                    // Inserimento dati nel db
                                    Database.insertRecord("user", data); 
                                    // Recupero dell'utente appena creato
                                    User user_added = User.getUserById(user_id);
                                    // Imposta legame di parentela tra i due utenti convolti
                                    user_current.setRelative(user_added, relationship);
                                    // Dopo aver aggiungo il nuovo parente, bisogna fare il refresh dll'albero genealogico di tutti i parenti loggati in quel momento
                                    user_logged.sendRefreshAck();

                                } catch (SQLException ex) {
                                    check = new Message("srv", true); // Server error
                                } catch (NotAllowed ex) {
                                    check = new Message("no_all", true); // Not allowed
                                }

                            }else if(action.equals("invite")){
                                // Se bisogna invitare un altro utente a iscriversi
                                
                                    String email = request.getParameter("email");
                                    check = DataUtil.checkEmail(email);
                                    if(!check.isError()){
                                        data.put("email", email);
                                        data.put("biography", "");
                                        try {
                                            // Inserimento dati nel db
                                            Database.insertRecord("user", data); 
                                            // Recupero dell'utente appena creato
                                            User user_added = User.getUserById(user_id);
                                            // Imposta legame di parentela tra i due utenti convolti
                                            user_current.sendRequest(user_added, relationship);
                                            check = new Message("inv", false); // User invited
                                        } catch (SQLException ex) {
                                            check = new Message("srv", true); // Server error
                                        } catch (NotAllowed ex) {
                                            check = new Message("no_all", true); // Not allowed
                                        }
                                    }
                                    
                            } else {
                                check = new Message("tmp", true); // Tampered data
                            }
                            
                            
                            
                            
                        } 
                        
                        // Se la servlet è stata chiamata con ajax
                        if (ajax) {
                            // Definisce la risposta alla chiamata ajax
                            response.setContentType("text/plain");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(check.toJSON());       

                        // Se ci sono verificati degli errori
                        } else if(check.isError()){
                            // Mostra messaggio di errore
                            response.sendRedirect(action + "?msg="+check.getCode());

                        }else{
                            // Torna alla pagina del profilo
                            response.sendRedirect("profile");
                        }
                    }
                } else {
                    if(ajax){
                        // Definisce la risposta alla chiamata ajax
                        response.setContentType("text/plain");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(new Message("log", true).toJSON());       
                    }else{        
                        response.sendRedirect("login?msg=log");
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
