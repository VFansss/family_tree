/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet.Templating;

import Class.Database;
import Class.FreeMarker;
import Class.User;
import Class.UserList;
import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Gianluca
 */
public class Search extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        //Gestione sessione
        HttpSession session=request.getSession(false);
        
        User user_logged = null;
        boolean is_logged;
        
        //Se non è stato effettuato il login...
        if(session==null){

            is_logged = false;         
        //Se è stato effettuato il login...
        } else { 
            
            String logged_id = (String)session.getAttribute("user_logged");
            
            is_logged = true;            
        }
        
        boolean connect = (boolean) this.getServletContext().getAttribute("connect");
        
        if(connect){
            UserList results = search("marco");
        } else {
            //Gestione "impossibile eseguire la ricerca"
        }
        
        FreeMarker.process("search.html",data, response, getServletContext());
        
    }
    
    protected UserList search(String condition){
        
        return new UserList();
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
