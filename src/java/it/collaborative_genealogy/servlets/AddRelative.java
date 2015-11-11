/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.collaborative_genealogy.servlets;

import it.collaborative_genealogy.User;
import it.collaborative_genealogy.exception.NotAllowed;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Gianluca
 */
public class AddRelative extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    

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
        
        HttpSession session = request.getSession(false);
        
        if(session!=null){
            
            //Recupero utente loggato
            User user_logged = (User)session.getAttribute("user_logged");
            
            User user_sender = User.getUserById(request.getParameter("user_sender"));
            User user_receiver = User.getUserById(request.getParameter("user_receiver"));
            String relationship = (String)request.getParameter("relationship");
            
            try{
                
                user_sender.sendRequest(user_receiver, relationship);
                
                response.sendRedirect("profile?id=" + user_sender.getId() + "&msg=oksnd");
                
            } catch(SQLException ex){
                
                PrintWriter out = response.getWriter();
                out.println("Si è verificato un errore con il database");
            } catch(NotAllowed ex){
                
                PrintWriter out = response.getWriter();
                out.println("Errore: impossibile aggiungere questo utente");
            }
        }
    }


}
