package it.univaq.ingweb.collaborative.servlets;

import java.io.IOException;
import java.sql.ResultSet;
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

import it.univaq.ingweb.collaborative.Database;
import it.univaq.ingweb.collaborative.User;
import it.univaq.ingweb.collaborative.UserList;
import it.univaq.ingweb.collaborative.tree.GenealogicalTree;
import it.univaq.ingweb.collaborative.tree.TreeNode;
import it.univaq.ingweb.collaborative.util.DateUtility;
import it.univaq.ingweb.collaborative.util.FreeMarker;
import it.univaq.ingweb.collaborative.util.Message;
import it.univaq.ingweb.collaborative.util.Utility;
/**
 *
 * @author Gianluca
 */
public class Search extends HttpServlet {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -3589338998027595527L;

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
        Map<String, String> inputFilter = new HashMap<>();
        //Gestione sessione
        HttpSession session=request.getSession(false);
        Message check = new Message(null, false);
        
        //Se è stato effettuato il login...
        if(session!=null) { 
            // Verifica se l'albero genealogico nella cache è aggiornato
            User userLogged = (User)session.getAttribute("user_logged");
            userLogged.checkFamilyTreeCache(session);
            data.put("family_tree", (GenealogicalTree)session.getAttribute("family_tree"));
            data.put("user_logged", (User)session.getAttribute("user_logged"));
        } 
        
        // Se la ricerca è stata effettuata dalla search-bar
        if(request.getParameter("search-bar-button") != null){
            
            /* Ricerca dalla search bar */
            inputFilter.put("name", "");
            inputFilter.put("surname", "");
            inputFilter.put("birthplace", "");
            inputFilter.put("birthdate", "");
             
            String input = request.getParameter("search-bar-input");
            if(!input.equals("")){
                // Se la stringa da cercare è alfanumerica
                if(!Utility.isAlphanumeric(input, true)){
                    check = new Message("alp", false);
                }else{
                    // Cerca la stringa
                    results = search(input); 
                }
                data.put("searching", input);
            }else{
                response.sendRedirect("search");
            }
            
            
            
        }else{
            
            /* Ricerca dal form dei filtri */
            // Recupero del nome
            String name = Utility.spaceTrim(request.getParameter("name"));
            // Recupero del cognome
            String surname = Utility.spaceTrim(request.getParameter("surname"));
            inputFilter.put("name", name);
            inputFilter.put("surname", surname);
            
            // Inizializzazione della data e luogo di nascita
            String birthplace = "";
            String birthdate = "";
            // Se c'è una sessiona attiva
            if(session != null){
                // Recupera la data e il luogo di nascita
                birthplace = Utility.spaceTrim(request.getParameter("birthplace"));
                birthdate = request.getParameter("birthdate").trim();
                // Se è stata inserita una data di nascita
                if(!birthdate.equals("")){
                    try {
                        // Prova a convertire la data di nascita in Date e inserisci il risultato del data-model
                        inputFilter.put("birthdate", DateUtility.stringToDate(birthdate).toString());
                    } catch (ParseException ex) {}
                }else{
                    inputFilter.put("birthdate", "");
                }     
                // Inserisci il luogo di nascita nel data-model
                inputFilter.put("birthplace", birthplace);
                
                String relative = request.getParameter("relative");
                if(relative != null) data.put("selected_relative", relative); 
            }
            
            // Controllo del nome
            if(!Utility.isAlphanumeric(name, true)) {
                check = new Message("name_1", true); // The name must be alphanumeric

            // Controllo del cognome
            }else if(!Utility.isAlphanumeric(surname, true)) {
                check = new Message("surname_1", true); // The surname must be alphanumeric

            // Se c'è una sessione attiva
            }else if(session != null){
                // Controllo della città di nascita
                check = Utility.checkBirthplace(birthplace);
                if(!check.isError()) {
                    // Se è stata inserita una data di nascita
                    if(!birthdate.equals("")){
                        // Controllo della data di nascita
                        check = Utility.checkBirthdate(birthdate);
                    }
                }  
            }
           
            
            // Se non sono stati trovati errori
            if(!check.isError()){
                // Esegui la ricerca
                results = search(inputFilter); 
            }
            
            // Se c'è una sessione attiva
            if(session != null){
                // Inserirsci la data di nascita del data-model
                inputFilter.put("birthdate", request.getParameter("birthdate").trim());
            }
            
            //--------------------------------------------
            //         GESTIONE AGGIUNTA PARENTI
            //--------------------------------------------
            if(request.getParameter("add_to")!=null){
                String addToId = request.getParameter("add_to");
            
                User addTo = User.getUserById(addToId);
            
                data.put("add_to", addTo);
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
        data.put("values", inputFilter);
        
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
        Map<String, String> inputFilter = new HashMap<>();
        inputFilter.put("name", "");
        inputFilter.put("surname", "");
        inputFilter.put("birthplace", "");
        inputFilter.put("birthdate", "");
        data.put("values", inputFilter); 
        
        
        //Gestione pagina di arrivo per inserimento nuovo parente
        HttpSession session = request.getSession(false);
        
        if(session!=null){            
            
            User userLogged = (User)session.getAttribute("user_logged");
            
            //Gestione aggiunta parente
            if(request.getParameter("to")!=null){
                
                try{
                    TreeNode userCurrentNode = ((GenealogicalTree)session.getAttribute("family_tree")).getUserById((String)request.getParameter("to"));
                    User userCurrent = userCurrentNode.getUser();
                   
                    data.put("add_to", userCurrent);
                    
                } catch (NullPointerException ex){

                    data.put("error", "manipulated_url");
                }
                
            }
            
            data.put("user_logged", userLogged);
            
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
        
        String conditionString = "";
        for(Map.Entry<String, String> entry : input.entrySet()){
            
            
                if(!entry.getValue().equals("")){
                    if(!conditionString.equals("")){
                        conditionString += " AND ";
                    }
                    if(!entry.getKey().equals("birthdate")){
                        conditionString += entry.getKey() + " COLLATE UTF8_GENERAL_CI LIKE '%" + entry.getValue()+"%'";
                    }else{
                        conditionString += entry.getKey() + "='" + entry.getValue() + "'";
                    }
                }     
        }
        
        try {
            ResultSet record = Database.selectRecord("user", conditionString);
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
        return "Servlet per la ricerca degli utenti e per la ricerca degli utenti da aggiungere";
    }

}
