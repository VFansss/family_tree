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
import java.io.IOException;
import java.io.PrintWriter;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try{
            HttpSession session = request.getSession(false);

            if(session!=null){
                
                Map<String, Object> data = new HashMap<String, Object>();
                
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
                
                FreeMarker.process(action+".html", data, response, getServletContext());
                
            } else {
                response.sendRedirect("login?msg=log");
            }
            
        } catch (Exception e){
            response.sendRedirect("error");
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
        
        try {
            HttpSession session = request.getSession(false);
            
            if(session!=null) {
                
                Map<String, Object> data = new HashMap<String, Object>();

                User user_logged = (User)session.getAttribute("user_logged");
                
                String action = request.getRequestURI().substring(request.getContextPath().length()+1);
                
                switch(action){
                    case "create":
                        // Recupero dell'utente corrente (è l'utente al quale bisogna aggiungere il nuovo profilo come parente)
                        TreeNode user_current_node = ((GenealogicalTree)session.getAttribute("family_tree")).getUserById((String)request.getParameter("relative"));
                        User user_current = user_current_node.getUser();
                        
                        //Recupero dati e creazione nuovo utente:
                        Map<String, Object> new_user_data = new HashMap<String, Object>();                        
                        
                        String name = request.getParameter("name");
                        String surname = request.getParameter("surname");
                        String gender = request.getParameter("gender");
                        String birthdate = request.getParameter("birthdate");
                        String birthplace = request.getParameter("birthplace");
                        String relationship = request.getParameter("relationship");
                        
                        if(name==null || surname==null || gender==null || birthdate==null || birthplace==null || relationship==null){
                           response.sendRedirect("error?msg=err");
                        }
                        
                        if(gender!="male" || gender!="female"){
                           response.sendRedirect("error?msg=err");
                        }
                        
                        String user_id = User.createUniqueUserId(10);
                        data.put("id", user_id);
                        
                        new_user_data.put("name", name);
                        new_user_data.put("surname", surname);
                        new_user_data.put("gender", gender);
                        new_user_data.put("birthplace", birthplace);
                        
                        Date sqlDate = null;
                        try {
                            sqlDate = DataUtil.stringToDate(birthdate, "dd/MM/yyyy");
                            data.put("birthdate", DataUtil.dateToString(sqlDate));
                        } catch (ParseException ex) {
                            //response.sendRedirect("error?msg=err");
                        }
                        
                        try{
                           Database.insertRecord("user", data); 
                        } catch (SQLException e){
                            //response.sendRedirect("error?msg=dboninsert");
                        }
                        
                        User user_added = User.getUserById("user_id");

                        try{
                            user_current.sendRequest(user_added, relationship);
                        } catch (NotAllowed | SQLException e){
                            //response.sendRedirect("error?msg=sendingRequest");
                        } catch (NullPointerException e){
                            //response.sendRedirect("error?msg=nullpointer");
                        }
                    
                        // Dopo aver aggiungo il nuovo parente, bisogna fare il refresh dll'albero genealogico di tutti i parenti loggati in quel momento
                        user_logged.sendRefreshAck();
                        session.setAttribute("family_tree", user_logged.getFamilyTree());
                        break;
                    case "invite":
                        break;
                } //switch
                
                response.sendRedirect("profile?id=user_id");
                                
            } else {
                response.sendRedirect("login?msg=log");
            }
            
            
        } catch (Exception e){
            response.sendRedirect("error?msg=maintry");
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
