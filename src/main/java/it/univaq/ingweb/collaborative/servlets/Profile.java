package it.univaq.ingweb.collaborative.servlets;

import it.univaq.ingweb.collaborative.util.FreeMarker;
import it.univaq.ingweb.collaborative.tree.GenealogicalTree;
import it.univaq.ingweb.collaborative.tree.NodeList;
import it.univaq.ingweb.collaborative.tree.TreeNode;
import it.univaq.ingweb.collaborative.User;
import it.univaq.ingweb.collaborative.util.Message;
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
import org.apache.log4j.Logger;

/**
 *
 * @author Gianluca
 */
public class Profile extends HttpServlet {
    final static Logger log = Logger.getLogger(Profile.class);
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
        log.info("Start profile GET");
        try{    
            
            //Gestione sessione
            HttpSession session = request.getSession(false);  
            
            //Se è stata generata la sessione
            if(session != null){
                GenealogicalTree familyTree = (GenealogicalTree)session.getAttribute("family_tree");
                // Recupero dell'utente loggato
                User userLogged = (User)session.getAttribute("user_logged");
                log.debug("Utente loggato: " + userLogged);
                // Verifica se bisogna fare il refresh dell'albero genealogico presente in cache
                boolean refresh = userLogged.checkFamilyTreeCache(session);
                if(!refresh){
                
                    // Recupero dell'utente corrente: non c'è il controllo sull'esistenza dell'utente, viene raccolta l'eccezione
                    User userCurrent;
                    TreeNode userCurrentNode;
                    String relativeGrade;
                    if (request.getParameter("id") != null){
                        userCurrentNode = familyTree.getUserById((String)request.getParameter("id"));
                        userCurrent = userCurrentNode.getUser();
                        relativeGrade = userCurrentNode.getLabel();
                    } else {
                        userCurrent = userLogged;
                        relativeGrade = "You";
                    }

                    /* Recupero dei parenti dell'utente corrente */
                    log.debug("Utente corrente: " + userCurrent);
                    
                    // Recupero del padre
                    TreeNode father = null;
                    try {
                        father = familyTree.getUser(userCurrent.getRelative("father"));
                    } catch (SQLException ex) { }
                    log.debug("Padre: " + father);
                    
                    // Recupero della madre
                    TreeNode mother = null;
                    try {
                        mother = familyTree.getUser(userCurrent.getRelative("mother"));
                    } catch (SQLException ex) { }
                    log.debug("Madre: " + mother);
                    
                    // Recupero del coniuge
                    TreeNode spouse = null;
                    try {
                        spouse = familyTree.getUser(userCurrent.getRelative("spouse"));
                    } catch (SQLException ex) { }
                    log.debug("Coniuge: " + spouse);
                    
                    // Recupero dei fratelli
                    NodeList siblings = null;
                    try {
                        siblings = familyTree.getUsers(userCurrent.getSiblings());
                    } catch (SQLException ex) { }
                    log.debug("Fratelli: " + siblings);
                    
                    // Recupero dei figli
                    NodeList children = null;
                    try {
                        children = familyTree.getUsers(userCurrent.getChildren());
                    } catch (SQLException ex) { }
                    log.debug("Figli: " + children);
                    
                    /* Inserimento dei parenti nel data-model */

                    data.put("user_logged", userLogged);
                    data.put("user_current", userCurrent);
                    data.put("relative_grade", relativeGrade);

                    data.put("siblings", siblings);
                    data.put("children", children);

                    data.put("spouse", spouse);
                    data.put("father", father);
                    data.put("mother", mother);

                    /* Gestione breadcrumb */

                    // Recupero del breadcrumb
                    NodeList breadcrumb = (NodeList)session.getAttribute("breadcrumb");
                    if(userCurrent.equals(userLogged)){
                        breadcrumb.clear();

                    }else{

                        Iterator iter = breadcrumb.iterator();
                        boolean remove = false;
                        while(iter.hasNext()){
                            TreeNode node = (TreeNode)iter.next();
                            if(!remove){
                                // Se l'utente corrente è uguale a quello nella lista
                                if(node.getUser().getId().equals(userCurrent.getId())){
                                    // Elimina tutti gli utenti successivi
                                    iter.remove();
                                    remove = true;
                                }
                            }else{
                                iter.remove();
                            }
                        }
                    }


                    breadcrumb.add(familyTree.getUser(userCurrent));

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
                    int requestCount = 0;
                    try { 
                        ResultSet record = userLogged.getRequests();
                        while(record.next()){
                            requestCount++;
                        }
                    } catch (SQLException ex) {
                        requestCount = 0;
                    } finally {
                        data.put("request", requestCount);
                    }


                    // Caricamento del template
                    FreeMarker.process("profile.html",data, response, getServletContext());
                }else{
                    StringBuffer requestURL = request.getRequestURL();
                    if (request.getQueryString() != null) {
                        requestURL.append("?").append(request.getQueryString());
                    }
                    String completeURL = requestURL.toString();
                    // Vai alla pagina di login e mostra messaggio di errore
                    response.sendRedirect(completeURL);
                }
            }else{
                
                // Vai alla pagina di login e mostra messaggio di errore
                response.sendRedirect("login?msg=" + URLEncoder.encode("log", "UTF-8"));
            }

        } catch (Exception e){
            log.error("Errore durante il caricamento della pagina del profilo", e);
            response.sendRedirect("error");
            
        }
        log.info("End profile GET");
    }


    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet della pagina del profilo";
    }

}
