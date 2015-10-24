package Servlet.Templating;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import freemarker.template.Configuration;
import freemarker.template.Template;

import Classes.User;
import Classes.UserBuilder;
import Classes.FreeMarker;

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        //COSTRUZIONE DATA MODEL
        Map<String, Object> data = new HashMap<String, Object>();
        
        //Gestione sessione
        HttpSession session=request.getSession(false);  
        
        //Se non è stata generata la sessione
        if(session==null){
            request.setAttribute("msn", "Please log in to see this page"); //Da migliorare il sistema su Login.java
            request.getRequestDispatcher("login").include(request, response);
//            PrintWriter out = response.getWriter();
//            out.println("NON SEI LOGGATO");
//            return;
        }
        
        String logged_id = (String)session.getAttribute("id");
        User loggeduser = UserBuilder.getUserById(logged_id);
        
        User currentuser;
        
        if (request.getParameter("id")!=null){
            currentuser = UserBuilder.getUserById(request.getParameter("id"));
        } else {
            currentuser = loggeduser;
        }        
        //Lista fratelli
        List<User> siblings = new LinkedList<User>();
        
        siblings.add(UserBuilder.legolas);
        siblings.add(UserBuilder.gimli);
        siblings.add(UserBuilder.boromir);
        
        //Lista figli
        List<User> children = new LinkedList<User>();
        
        children.add(UserBuilder.eldarion);
        
        /*********NAVIGAZIONE*************/
        
        //Si recupera la lista degli utenti visitati
        List<User> navigation = (List<User>)session.getAttribute("navigation");
        
        //Se si sta visitando il proprio profilo, si svuota la lista
        if(currentuser.getId().equals(logged_id)){
            navigation.clear();
        
        } else {
            //Se altrimenti non si sta visualizzando il proprio profilo, si deve accorciare la lista
            int i = 0;
            for(User visited: navigation){
                if (visited.getId().equals(request.getParameter("id"))){
                    break;
                }
                i++;
            }
            navigation = navigation.subList(0,i--);
            navigation.add(currentuser);
        }
        
        session.setAttribute("navigation", navigation);
        
        
        // Inserimento utenti nel data-model
        data.put("siblings", siblings);
        data.put("navigation", navigation);
        data.put("children", children);

        data.put("loggeduser", loggeduser);
        data.put("currentuser", currentuser);
        
        data.put("spouse", UserBuilder.arwen);
        data.put("father", UserBuilder.arathorn);
        data.put("mother", UserBuilder.gilraen);
                
        FreeMarker.process("profile.html",data, response, getServletContext());
        
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
