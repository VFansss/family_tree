/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.templating;

import classes.Database;
import classes.FreeMarker;
import classes.User;
import classes.UserList;
import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.*;
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
        
        //User user_logged = null;
        boolean logged = false;
        
        //Se non è stato effettuato il login...
        if(session!=null) { 
            
            logged = true;             
            data.put("user_logged", (User)session.getAttribute("user_logged"));
        }
        
        
        String input = request.getParameter("search");
        String[] parts = null;
        
        if (input!=null) parts = input.split(" ");
        
        UserList results = search(parts);
        
        data.put("logged", logged);
        data.put("results", results);
        data.put("searching", input);
        
        FreeMarker.process("search.html",data, response, getServletContext());
        
    }
    
    protected static UserList search(String[] conditions){
        UserList results = new UserList();
        try {        
            if(conditions != null){ 
                for(int i=0; i<conditions.length; i++){
                    try (ResultSet record = Database.selectRecord("user", "name='"+conditions[i]+"'")) {
                        while(record.next()){
                            results.add(new User(record));  
                        }
                    }catch (SQLException ex){
                        if(i-1<conditions.length) continue;
                        else throw ex;
                    }
                }
            }
        } catch (SQLException ex) {
            results = null;
        } finally {
            return results;
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
