package it.collaborative_genealogy.servlets;

import it.collaborative_genealogy.util.FreeMarker;
import it.collaborative_genealogy.tree.GenealogicalTree;
import it.collaborative_genealogy.tree.NodeList;
import it.collaborative_genealogy.tree.TreeNode;
import it.collaborative_genealogy.User;
import it.collaborative_genealogy.util.Message;
import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Gianluca
 */
public class Profile extends HttpServlet {

    /**
     * Carica la pagina del profilo
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> data = new HashMap<>();
    
        try{    
            
            //Gestione sessione
            HttpSession session = request.getSession(false);  
            
            //Se è stata generata la sessione
            if(session != null){
                GenealogicalTree family_tree = (GenealogicalTree)session.getAttribute("family_tree");
                // Recupero dell'utente loggato
                User user_logged = (User)session.getAttribute("user_logged");
                // Verifica se bisogna fare il refresh dell'albero genealogico presente in cache
                user_logged.checkFamilyTreeCache(session);
                // Recupero dell'utente corrente: non c'è il controllo sull'esistenza dell'utente, viene raccolta l'eccezione
                User user_current;
                TreeNode user_current_node;
                String relative_grade = null;
                if (request.getParameter("id") != null){
                    user_current_node = family_tree.getUserById((String)request.getParameter("id"));
                    user_current = user_current_node.getUser();
                    relative_grade = user_current_node.getLabel();
                } else {
                    user_current = user_logged;
                    relative_grade = "You";
                }

                

                /* Recupero dei parenti dell'utente corrente */

                // Recupero del padre
                TreeNode father = null;
                try {
                    father = family_tree.getUser(user_current.getRelative("father"));
                } catch (SQLException ex) { }

                // Recupero della madre
                TreeNode mother = null;
                try {
                    mother = family_tree.getUser(user_current.getRelative("mother"));
                } catch (SQLException ex) { }

                // Recupero del coniuge
                TreeNode spouse = null;
                try {
                    spouse = family_tree.getUser(user_current.getRelative("spouse"));
                } catch (SQLException ex) { }

                // Recupero dei fratelli
                NodeList siblings = null;
                try {
                    siblings = family_tree.getUsers(user_current.getSiblings());
                } catch (SQLException ex) { }

                // Recupero dei figli
                NodeList children = null;
                try {
                    children = family_tree.getUsers(user_current.getChildren());
                } catch (SQLException ex) { }

                /* Inserimento dei parenti nel data-model */

                data.put("user_logged", user_logged);
                data.put("user_current", user_current);
                data.put("relative_grade", relative_grade);

                data.put("siblings", siblings);
                data.put("children", children);

                data.put("spouse", spouse);
                data.put("father", father);
                data.put("mother", mother);

                /* Gestione breadcrumb */

                // Recupero del breadcrumb
                NodeList breadcrumb = (NodeList)session.getAttribute("breadcrumb");
                if(user_current.equals(user_logged)){
                    breadcrumb.clear();

                }else{

                    Iterator iter = breadcrumb.iterator();
                    boolean remove = false;
                    while(iter.hasNext()){
                        TreeNode node = (TreeNode)iter.next();
                        if(!remove){
                            // Se l'utente corrente è uguale a quello nella lista
                            if(node.getUser().getId().equals(user_current.getId())){
                                // Elimina tutti gli utenti successivi
                                iter.remove();
                                remove = true;
                            }
                        }else{
                            iter.remove();
                        }
                    }
                }


                breadcrumb.add(family_tree.getUser(user_current));

                // Se bisogna ripulire la breadcrumb
                if(request.getParameter("clear") != null && request.getParameter("clear").equals("true")){
                    breadcrumb.cleaner();
                }

                // Inserimento del nuovo breadcrumb nella variabile di sessione
                session.setAttribute("breadcrumb", breadcrumb);
                // Inserimento del breadcrumb nel data-model
                data.put("breadcrumb", breadcrumb);
                data.put("active_button", "profile");     
                
                // Controllo messaggio
                Message message = new Message(request.getParameter("msg"), false);
                data.put("message", message);
                          
                // Controllo richieste in arrivo
                int request_count = 0;
                try { 
                    ResultSet record = user_logged.getRequests();
                    while(record.next()){
                        request_count++;
                    }
                } catch (SQLException ex) {
                    request_count = 0;
                } finally {
                    data.put("request", request_count);
                }
                
                
                // Caricamento del template
                FreeMarker.process("profile.html",data, response, getServletContext());

            }else{
                // Vai alla pagina di login e mostra messaggio di errore
                response.sendRedirect("login?msg=" + URLEncoder.encode("log", "UTF-8"));
            }

        } catch (Exception e){
            response.sendRedirect("error");
        }
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
