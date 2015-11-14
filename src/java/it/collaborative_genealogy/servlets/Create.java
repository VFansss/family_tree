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
            String relative_grade = null;
            if (request.getParameter("id") != null){
                user_current_node = ((GenealogicalTree)session.getAttribute("family_tree")).getUserById((String)request.getParameter("id"));
                user_current = user_current_node.getUser();
                relative_grade = user_current_node.getLabel();
                data.put("relative_grade", relative_grade);

            } else {
                user_current = user_logged;
            }

            String action = request.getRequestURI().substring(request.getContextPath().length()+1);

            data.put("action", action);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
            HttpSession session = request.getSession(false);
            
            if(session!=null) {

                User user_logged = (User)session.getAttribute("user_logged");
                
                String action = request.getRequestURI().substring(request.getContextPath().length()+1);
                
                switch(action){
                    case "create":
                        
                        String name = request.getParameter("name");
                        String surname = request.getParameter("surname");
                        String gender = request.getParameter("gender");
                        String birthdate = request.getParameter("birthdate");
                        String birthplace = request.getParameter("birthplace");
                        String biography = request.getParameter("biography");
                        String relationship = request.getParameter("relationship");
                        Message check;
                        if(relationship.equals("")){
                            check = new Message("fld", true);
                        }else{

                            check = DataUtil.checkData(name, surname, gender, birthdate, birthplace);

                            if(!check.isError()){
                                // Recupero dell'utente corrente (è l'utente al quale bisogna aggiungere il nuovo profilo come parente)
                                TreeNode user_current_node = ((GenealogicalTree)session.getAttribute("family_tree")).getUserById((String)request.getParameter("relative"));
                                User user_current = user_current_node.getUser();

                                Map<String, Object> new_user_data = new HashMap<>();  

                                String user_id = User.createUniqueUserId(10);
                                new_user_data.put("id", user_id);

                                new_user_data.put("name", name);
                                new_user_data.put("surname", surname);
                                new_user_data.put("gender", gender);
                                new_user_data.put("birthplace", birthplace);
                                new_user_data.put("biography", biography);
                                Date sqlDate = null;
                                try {

                                    sqlDate = DataUtil.stringToDate(birthdate, "dd/MM/yyyy");
                                    new_user_data.put("birthdate", DataUtil.dateToString(sqlDate));
                                    Database.insertRecord("user", new_user_data); 
                                    User user_added = User.getUserById(user_id);
                                    user_current.setRelative(user_added, relationship);
                                    // Dopo aver aggiungo il nuovo parente, bisogna fare il refresh dll'albero genealogico di tutti i parenti loggati in quel momento
                                    user_logged.sendRefreshAck();
                                    session.setAttribute("family_tree", user_logged.getFamilyTree());

                                } catch (ParseException ex) {
                                    check = new Message("date_2", true);
                                } catch (SQLException ex) {
                                    check = new Message("srv", true);
                                } catch (NotAllowed ex) {
                                    check = new Message("no_all", true);
                                }

                            }
                        }
                        
                        if(check.isError()){
                            response.sendRedirect("create?msg="+check.getCode());
                        }else{
                            response.sendRedirect("profile");
                        }
                        break;
                        
                    case "invite":
                        break;
                } //switch
                
                
                                
            } else {
                response.sendRedirect("login?msg=log");
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
