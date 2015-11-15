/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.collaborative_genealogy.servlets;

import it.collaborative_genealogy.Database;
import it.collaborative_genealogy.util.FreeMarker;
import it.collaborative_genealogy.User;
import it.collaborative_genealogy.UserList;
import it.collaborative_genealogy.tree.GenealogicalTree;
import it.collaborative_genealogy.tree.TreeNode;
import it.collaborative_genealogy.util.DataUtil;
import it.collaborative_genealogy.util.Message;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Gianluca
 */
public class Search extends HttpServlet {
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserList results = new UserList();
        Map<String, Object> data = new HashMap<>();
        Map<String, String> input_filter = new HashMap<>();
        //Gestione sessione
        HttpSession session=request.getSession(false);
        Message check = new Message(null, false);
        
        //Se è stato effettuato il login...
        if(session!=null) { 
            // Verifica se l'albero genealogico nella cache è aggiornato
            User user_logged = (User)session.getAttribute("user_logged");
            user_logged.checkFamilyTreeCache(session);
            data.put("family_tree", (GenealogicalTree)session.getAttribute("family_tree"));
            data.put("user_logged", (User)session.getAttribute("user_logged"));
        } 
        
        // Se la ricerca è stata effettuata dalla search-bar
        if(request.getParameter("search-bar-button") != null){
            
            /* Ricerca dalla search bar */
            input_filter.put("name", "");
            input_filter.put("surname", "");
            input_filter.put("birthplace", "");
            input_filter.put("birthdate", "");
             
            String input = request.getParameter("search-bar-input");
            
            // Se la stringa da cercare è alfanumerica
            if(!DataUtil.isAlphanumeric(input, true)){
                check = new Message("alp", false);
            }else{
                // Cerca la stringa
                results = search(input); 
            }
            data.put("searching", input);
            
        }else{
            
            /* Ricerca dal form dei filtri */
            // Recupero del nome
            String name = DataUtil.spaceTrim(request.getParameter("name"));
            // Recupero del cognome
            String surname = DataUtil.spaceTrim(request.getParameter("surname"));
            input_filter.put("name", name);
            input_filter.put("surname", surname);
            
            // Inizializzazione della data e luogo di nascita
            String birthplace = "";
            String birthdate = "";
            // Se c'è una sessiona attiva
            if(session != null){
                // Recupera la data e il luogo di nascita
                birthplace = DataUtil.spaceTrim(request.getParameter("birthplace"));
                birthdate = request.getParameter("birthdate").trim();
                // Se è stata inserita una data di nascita
                if(!birthdate.equals("")){
                    try {
                        // Prova a convertire la data di nascita in Date e inserisci il risultato del data-model
                        input_filter.put("birthdate", DataUtil.stringToDate(birthdate, "dd/MM/yyyy").toString());
                    } catch (ParseException ex) {}
                }else{
                    input_filter.put("birthdate", "");
                }     
                // Inserisci il luogo di nascita nel data-model
                input_filter.put("birthplace", birthplace);
                
                String relative = request.getParameter("relative");
                if(relative != null) data.put("selected_relative", relative); 
            }
            
            // Controllo del nome
            if(!DataUtil.isAlphanumeric(name, true)) {
                check = new Message("name_1", true); // The name must be alphanumeric

            // Controllo del cognome
            }else if(!DataUtil.isAlphanumeric(surname, true)) {
                check = new Message("surname_1", true); // The surname must be alphanumeric

            // Se c'è una sessione attiva
            }else if(session != null){
                // Controllo della città di nascita
                check = DataUtil.checkBirthplace(birthplace);
                if(!check.isError()) {
                    // Se è stata inserita una data di nascita
                    if(!birthdate.equals("")){
                        // Controllo della data di nascita
                        check = DataUtil.checkBirthdate(birthdate);
                    }
                }  
            }
           
            
            // Se non sono stati trovati errori
            if(!check.isError()){
                // Esegui la ricerca
                results = search(input_filter); 
            }
            
            // Se c'è una sessione attiva
            if(session != null){
                // Inserirsci la data di nascita del data-model
                input_filter.put("birthdate", request.getParameter("birthdate").trim());
            }
            
            //--------------------------------------------
            //         GESTIONE AGGIUNTA PARENTI
            //--------------------------------------------
            if(request.getParameter("add_to")!=null){
                String add_to_id = request.getParameter("add_to");
            
                User add_to = User.getUserById(add_to_id);
            
                data.put("add_to", add_to);
            }
            
        }
        
        // Se è stato riscontrato qualche errore
        if(check.isError()){
            // Messaggio di errore
            data.put("message", check); 
        }else{
            // Se non è stato trovato qualche utente
            if(results.isEmpty()){
                data.put("message", new Message("usr_3", true)); // No users found
            }else{
                // Mostra risultati
                data.put("results", results); 
            }
        }
        
        // Inserisci i campi compilati nel data-model
        data.put("values", input_filter);
        
        // Genera il data-model
        FreeMarker.process("search.html",data, response, getServletContext());
    }

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
        
        Map<String, Object> data = new HashMap<>();
        Map<String, String> input_filter = new HashMap<>();
        input_filter.put("name", "");
        input_filter.put("surname", "");
        input_filter.put("birthplace", "");
        input_filter.put("birthdate", "");
        data.put("values", input_filter); 
        
        
        //Gestione pagina di arrivo per inserimento nuovo parente
        HttpSession session = request.getSession(false);
        
        if(session!=null){            
            
            User user_logged = (User)session.getAttribute("user_logged");
            
            //Gestione aggiunta parente
            if(request.getParameter("to")!=null){
                
                try{
                    TreeNode user_current_node = ((GenealogicalTree)session.getAttribute("family_tree")).getUserById((String)request.getParameter("to"));
                    User user_current = user_current_node.getUser();
                    String relative_grade = user_current_node.getLabel();
                    
                    data.put("add_to", user_current);
                    
                } catch (NullPointerException ex){

                    data.put("error", "manipulated_url");
                }
                
            }
            
            data.put("user_logged", user_logged);
            
        }
        
        FreeMarker.process("search.html",data, response, getServletContext());
    }

    protected static UserList search(String input){
        UserList result = new UserList();
        String condition = "(CONCAT(name, ' ', surname) COLLATE UTF8_GENERAL_CI LIKE '%" + input + "%' "
                      + "OR CONCAT(surname, ' ', name) COLLATE UTF8_GENERAL_CI LIKE '%" + input + "%')"
                      // Includi gli utenti non verificati ma escludi quelli invitati che non hanno ancora fatto la registrazione
                    + "AND ((email IS NOT NULL AND password IS NOT NULL) OR (email IS NULL AND password IS NULL))";
        
        try {
            ResultSet record = Database.selectRecord("user", condition);
            while(record.next()){
                result.add(new User(record));  
            }
        } catch (SQLException ex) {
            Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }

    protected static UserList search(Map<String, String> input){
        UserList result = new UserList();
        
        String condition_string = "";
        for(Map.Entry<String, String> entry : input.entrySet()){
            
            
                if(!entry.getValue().equals("")){
                    if(!condition_string.equals("")){
                        condition_string += " AND ";
                    }
                    if(!entry.getKey().equals("birthdate")){
                        condition_string += entry.getKey() + " COLLATE UTF8_GENERAL_CI LIKE '%" + entry.getValue()+"%'";
                    }else{
                        condition_string += entry.getKey() + "='" + entry.getValue() + "'";
                    }
                }     
        }
        
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
