/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.collaborative_genealogy.servlets;

import it.collaborative_genealogy.User;
import it.collaborative_genealogy.tree.GenealogicalTree;
import it.collaborative_genealogy.tree.TreeNode;
import it.collaborative_genealogy.util.FreeMarker;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
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
                response.sendRedirect("login?msn=log");
            }
            
        } catch (Exception e){
            response.sendRedirect("error");
        }    }

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
