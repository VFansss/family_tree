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

import freemarker.template.Configuration;
import freemarker.template.Template;

import Classes.User;
import Classes.UserBuilder;
/**
 *
 * @author Gianluca
 */
@WebServlet(urlPatterns = {"/profile"})
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
        
        //Tutti gli utenti:
        //User loggeduser = new User("Gianluca", "Filippone", "ABC123", "Pescara", "17/12/1993", "Bio di prova!", "gianluca.jpg");
        
        //Lista fratelli
        List<User> siblings = new LinkedList<User>();
        
        siblings.add(UserBuilder.legolas);
        siblings.add(UserBuilder.gimli);
        siblings.add(UserBuilder.boromir);
        
        //Lista utenti precedentemente visualizzati
        List<User> navigation = new LinkedList<User>();
        
        navigation.add(UserBuilder.arathorn);
        navigation.add(UserBuilder.gilraen);
        navigation.add(UserBuilder.boromir);
        navigation.add(UserBuilder.aragorn);
        
        // Inserimento utenti nel data-model
        data.put("siblings", siblings);
        data.put("navigation", navigation);
        
        data.put("loggeduser", UserBuilder.aragorn);
        data.put("currentuser", UserBuilder.getUserById(request.getParameter("id")));
        data.put("spouse", UserBuilder.arwen);
        data.put("father", UserBuilder.arathorn);
        data.put("mother", UserBuilder.gilraen);
        data.put("child", UserBuilder.eldarion);
        
        
        // Configurazione freemarker
        Configuration cfg = new Configuration();
        
        cfg.setDefaultEncoding("UTF-8");
            
        cfg.setServletContextForTemplateLoading(getServletContext(), "/template");

        Template template = cfg.getTemplate("profile.html");
        //Template template = cfg.getTemplate("profile2.html");
        
        PrintWriter out = response.getWriter();
        try{
            template.process(data, out);
        }
        catch (Exception e){
            out.println("ERRORE PROCESSING TEMPLATE");
        }
        out.flush();
        
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
