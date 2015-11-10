/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.collaborative_genealogy.servlets;

import it.collaborative_genealogy.User;
import it.collaborative_genealogy.Request;
import it.collaborative_genealogy.tree.GenealogicalTree;
import it.collaborative_genealogy.tree.TreeNode;
import java.io.IOException;
import java.io.PrintWriter;
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
public class ShowRequests extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try{
            
            HttpSession session = request.getSession(false);
            
            if(session!=null){
                
                Map<String, Object> data = new HashMap<String, Object>();
                
                // Recupero dell'utente loggato
                User user_logged = (User)session.getAttribute("user_logged");
                
                List<Request> requests = new LinkedList<Request>();
                
                try{
                    ResultSet record = user_logged.getRequest();
                    while (record.next()){
                        requests.add(new Request(record));
                    }
                } catch (SQLException ex) {
                    requests = null;
                }
                
                data.put("requests", requests);
                data.put("user_logged", user_logged);
                
            } else {
                // Vai alla pagina di login e mostra messaggio di errore
                response.sendRedirect("login?msn=" + URLEncoder.encode("log", "UTF-8"));
            }
            
        } catch (Exception ex) {
            response.sendRedirect("error");
        }
        
    }

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
        processRequest(request, response);
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
