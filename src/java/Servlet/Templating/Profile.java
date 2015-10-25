package Servlet.Templating;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Class.FreeMarker;
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        //Gestione sessione
        HttpSession session = request.getSession(false);  
        
        //Se non è stata generata la sessione
        if(session==null){
            // Vai alla pagina di login e mostra messaggio di errore
            response.sendRedirect("login?msn=" + URLEncoder.encode("Please log in to see this page", "UTF-8"));

        }
        
        // Recupero dell'utente loggato
        User user_logged = User.getUserById((String)session.getAttribute("id"));
        
        // Recupero dell'utente corrente
        User user_current;
        if (request.getParameter("id") != null){
            user_current = User.getUserById(request.getParameter("id"));
        } else {
            user_current = user_logged;
        }        
        
        
        
        /* Recupero dei parenti dell'utente corrente */
        
            // Recupero di padre, madre e coniuge
            User father = user_current.getFather();
            User mother = user_current.getMother();
            User spouse = user_current.getSpouse();
            
            // Recupero dei fratelli
            UserList siblings = new UserList();
            siblings.addAll(user_current.getSiblings());

            // Recupero dei figli
            UserList children = new UserList();
            children.addAll(user_current.getChildren());

        /* Inserimento dei parenti nel data-model */
            
            data.put("user_logged", user_logged);
            data.put("user_current", user_current);
            
            data.put("siblings", siblings);
            data.put("children", children);

            data.put("spouse", spouse);
            data.put("father", father);
            data.put("mother", mother);
        
        /* Gestione breadcrumb */
        
            // Recupero del breadcrumb
            UserList breadcrumb = (UserList)session.getAttribute("navigation");

            Iterator iter = breadcrumb.iterator();
            boolean remove = false;
            while(iter.hasNext()){
                User user = (User)iter.next();
                if(!remove){
                    // Se l'utente corrente è uguale a quello nella lista
                    if(user.getId().equals(user_current.getId())){
                        // Elimina tutti gli utenti successivi
                        remove = true;
                    }
                }else{
                    iter.remove();
                }

            }
//          
            breadcrumb.add(user_current);
        
        // Inserimento del nuovo breadcrumb nella variabile di sessione
        session.setAttribute("navigation", breadcrumb);
        // Inserimento del breadcrumb nel data-model
        data.put("navigation", breadcrumb);
        // Caricamento del template
        FreeMarker.process("profile.html",data, response, getServletContext());
//        
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
