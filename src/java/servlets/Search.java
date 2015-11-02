/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import classes.Database;
import classes.util.FreeMarker;
import classes.User;
import classes.UserList;
import classes.tree.GenealogicalTree;
import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        
        Map<String, Object> data = new HashMap<>();
        
        //Gestione sessione
        HttpSession session=request.getSession(false);
                
        //Se è stato effettuato il login...
        if(session!=null) { 
            data.put("family_tree", (GenealogicalTree)session.getAttribute("family_tree"));
            data.put("user_logged", (User)session.getAttribute("user_logged"));
        } 
        
        Map<String, String> values = new HashMap<>();
        values.put("name", "");
        values.put("surname", "");
        values.put("birthplace", "");
        values.put("birthdate", "");
        
        //Bisogna recuperare i dati dalle form:
        //Se la richiesta giunge dalla searchbar...
        String source = request.getParameter("source");
        if(source!=null && source.equals("searchbar")){
            
            String string_input = request.getParameter("search").trim();
            
            //...esegue la ricerca sulla stringa in input e mette i risultati in data
            data.put("results", search(string_input));  
            data.put("searching", string_input);
            
        //Altrimenti se la richiesta giunge dal form...
        } else if (source!=null && request.getParameter("source").equals("filters")) {
            
            Map<String, String> to_search = new HashMap<>();

            to_search.put("name", request.getParameter("name").trim());
            to_search.put("surname", request.getParameter("surname").trim());
            to_search.put("birthplace", request.getParameter("birthplace"));
            to_search.put("birthdate", request.getParameter("birthdate"));
            
            values=to_search;
            
            //...esegue la ricerca sulla map e mette i risultati direttamente in data
            data.put("results", search(to_search));
            
            //Si riempie poi la mappa per lasciare nella nuova pagina le informazioni cercate
            
        }
        
        
        data.put("values", values);        
        FreeMarker.process("search.html",data, response, getServletContext());
        
    }
    
    protected static UserList search(String input){
        String[] parameters = input.split(" ");
        Map<String, String> search_map = new HashMap<>();
        UserList result = new UserList();
        
        switch(parameters.length){
            case 1:
                //Ricerca per solo nome o solo cognome:
                //Solo nome
                search_map.put("name", parameters[0]);
                result.addAll(search(search_map));
                //Solo cognome
                search_map.remove("name");
                search_map.put("surname", parameters[0]);
                result.addAll(search(search_map));
                break;
            case 2:
                //Si cerca nome-cognome, cognome-nome, nome-nome e cognome-cognome
                //Nome-cognome
                search_map.put("name", parameters[0]);
                search_map.put("surname", parameters[1]);
                result.addAll(search(search_map));
                //Cognome-nome
                search_map.put("name", parameters[1]);
                search_map.put("surname", parameters[0]);
                result.addAll(search(search_map));
                //Nome-nome
                search_map.remove("surname");
                search_map.put("name", parameters[0]+" "+parameters[1]);
                result.addAll(search(search_map));
                //Cognome-cognome
                search_map.remove("name");
                search_map.put("surname", parameters[0]+" "+parameters[1]);
                result.addAll(search(search_map));
                break;
            case 3:
                //Si cerca nome-cognome-cognome, nome-nome-cognome, cognome-cognome-nome e cognome-nome-nome
                //Nome-cognome-cognome
                search_map.put("name", parameters[0]);
                search_map.put("surname", parameters[1]+" "+parameters[2]);
                result.addAll(search(search_map));
                //nome-nome-cognome
                search_map.put("name", parameters[0]+" "+parameters[1]);
                search_map.put("surname", parameters[2]);
                result.addAll(search(search_map));
                //cognome-cognome-nome
                search_map.put("surname", parameters[0]+" "+parameters[1]);
                search_map.put("name", parameters[2]);
                result.addAll(search(search_map));
                //Cognome-nome-nome
                search_map.put("surname", parameters[0]);
                search_map.put("name", parameters[1]+" "+parameters[2]);
                result.addAll(search(search_map));
                break;
            case 4:
                //Si cerca nome-nome-cognome-cognome e cognome-cognome-nome-nome
                //nome-nome-cognome-cognome
                search_map.put("name", parameters[0]+" "+parameters[1]);
                search_map.put("surname", parameters[2]+" "+parameters[3]);
                result.addAll(search(search_map));
                //cognome-cognome-nome-nome
                search_map.put("surname", parameters[0]+" "+parameters[1]);
                search_map.put("name", parameters[2]+" "+parameters[3]);
                result.addAll(search(search_map));
                break;
        }
        
        return result;
    }

    protected static UserList search(Map<String, String> input){
        UserList result = new UserList();
        
        String condition_string = "";
        for(Map.Entry<String, String> entry : input.entrySet()){
            if(!(entry.getValue().isEmpty())){
                if(!condition_string.equals("")){
                    condition_string += " AND ";
                }
                condition_string += entry.getKey() + " COLLATE UTF8_GENERAL_CI LIKE '%" + entry.getValue()+"%'";
            }
        }
        //condition_string = condition_string+" COLLATE LATIN1_SWEDISH_CI";
        
        try {
            ResultSet record = Database.selectRecord("user", condition_string);
            while(record.next()){
                result.add(new User(record));  
            }
        } catch (SQLException ex) {
            Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
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
