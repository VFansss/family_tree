package Servlet.Templating;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Class.FreeMarker;
import Class.Tree.GenealogicalTree;
import Class.Tree.NodeList;
import Class.Tree.TreeNode;
import Class.User;
import Class.UserList;
import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.net.URLEncoder;



/**
 *
 * @author Gianluca
 */
public class Profile extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { response.setContentType("text/html;charset=UTF-8");
        
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        //Gestione sessione
        HttpSession session = request.getSession(false);  
        
        //Se è stata generata la sessione
        if(session != null){
            
            // Recupero dell'utente loggato
            User user_logged = (User)session.getAttribute("user_logged");
            
            // Recupero dell'utente corrente
            User user_current;
            TreeNode user_current_node;
            String relative_grade = null;
            if (request.getParameter("id") != null){
                user_current_node = ((GenealogicalTree)session.getAttribute("family_tree")).getUserById((String)request.getParameter("id"));
                user_current = user_current_node.getUser();
                relative_grade = user_current_node.getLabel();
            } else {
                user_current = user_logged;
            }
            
            // Se l'utente corrente esiste
            if(user_current != null){
                
                GenealogicalTree family_tree = (GenealogicalTree)session.getAttribute("family_tree");

                /* Recupero dei parenti dell'utente corrente */

                // Recupero di padre, madre e coniuge
                TreeNode father = family_tree.getUser(user_current.getFather());
                TreeNode mother = family_tree.getUser(user_current.getMother());
                TreeNode spouse = family_tree.getUser(user_current.getSpouse());

                // Recupero dei fratelli
                NodeList siblings = family_tree.getUsers(user_current.getSiblings());

                // Recupero dei figli
                NodeList children = family_tree.getUsers(user_current.getChildren());

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

                // Inserimento del nuovo breadcrumb nella variabile di sessione
                session.setAttribute("breadcrumb", breadcrumb);
                // Inserimento del breadcrumb nel data-model
                data.put("breadcrumb", breadcrumb);
                // Caricamento del template
                FreeMarker.process("profile.html",data, response, getServletContext());
                
            // Altrimenti, se l'utente corrente non esiste
            }else{
                // Vai alla pagina dell'utente loggato
                response.sendRedirect("profile");
            }
            
            
        }else{
            // Vai alla pagina di login e mostra messaggio di errore
            response.sendRedirect("login?msn=" + URLEncoder.encode("log", "UTF-8"));
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
