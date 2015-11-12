/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.collaborative_genealogy.servlets;

import it.collaborative_genealogy.User;
import it.collaborative_genealogy.exception.NotAllowed;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Marco
 */
public class RemoveRelative extends HttpServlet {

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if(session!=null){
            try {
                //Recupero utente loggato
                User user_logged = (User)session.getAttribute("user_logged");
                String relationship = request.getParameter("type");
                
                // Prima di eliminare il parente, bisogna fare il refresh dll'albero genealogico dei parenti loggati in quel momento
                user_logged.sendRefreshAck();
                
                switch(relationship){
                    
                    case "mother":      user_logged.removeParent("female");                                     break;
                    case "father":      user_logged.removeParent("male");                                       break;
                    
                    case "daughter":
                    case "son":         user_logged.removeChild(User.getUserById(request.getParameter("id")));  break;
                    
                    case "wife": 
                    case "husband":     user_logged.removeSpouse();                                             break;
                        
                    default: throw new NotAllowed();
                }

                response.sendRedirect("profile");
            } catch (NotAllowed | SQLException ex) {
                Logger.getLogger(RemoveRelative.class.getName()).log(Level.SEVERE, null, ex);
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
    }// </editor-fold>

}
