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
        
        boolean logged = false;
        
        //Se non è stato effettuato il login...
        if(session!=null) { 
            
            logged = true;             
            data.put("user_logged", (User)session.getAttribute("user_logged"));
        }
        
        Map<String, String> to_search = new HashMap<String, String>();
        
        //Bisogna recuperare i dati dalle form:
        //Se la richiesta giunge dalla searchbar
        if(request.getParameter("source").equals("searchbar")){
            
            String string_input = request.getParameter("search").trim();
            
            String[] input = string_input.split(" ");
            
            //String name = input[0];
            String surname = "";
            
            for (int i=1; i<input.length; i++){
                surname = surname + input[i] + " ";
            }
            
            to_search.put("name", input[0]);
            to_search.put("surname", surname);
            
        } else if (request.getParameter("source").equals("filters")) {
            
            to_search.put("name", request.getParameter("name").trim());
            to_search.put("surname", request.getParameter("surname").trim());
            to_search.put("birthplace", request.getParameter("birthplace"));
            to_search.put("birthdate", request.getParameter("birthdate"));
        }
        
        UserList results = search(to_search);
        
        data.put("logged", logged);
        data.put("results", results);
        //data.put("searching", input);     
        
        FreeMarker.process("search.html",data, response, getServletContext());
        
    }
    
    
    protected static UserList search(Map<String, String> input){
        UserList result = new UserList();
        
        String condition_string = "";
        for(Map.Entry<String, String> entry : input.entrySet()){
            if(!(entry.getValue().isEmpty())){
                if(!condition_string.equals("")){
                    condition_string = condition_string+" AND ";
                }
                condition_string = condition_string+entry.getKey()+"='"+entry.getValue()+"'";
            }
        }
        
        try (ResultSet record = Database.selectRecord("user", condition_string)){
            if(record != null)
                while(record.next()){
                    result.add(new User(record));  
                }
        } catch (SQLException ex){
            
        }
           
        return result;
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
