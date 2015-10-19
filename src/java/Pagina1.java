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
/**
 *
 * @author Gianluca
 */
@WebServlet(urlPatterns = {"/urlpagina1"})
public class Pagina1 extends HttpServlet {

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
        
        // Build the data-model
        Map<String, Object> data = new HashMap<String, Object>();        

        User loggeduser = new User("Gianluca", "Filippone", "ABC123", "Pescara", "17/12/1993", "Bio di prova!", "gianluca.jpg");
        User currentuser = new User("Aragorn", "Granpasso", "DSC213", "Gondor", "1/03/2931", "Per frodo!", "aragorn.jpg");
        User spouse = new User("Arwen", "Undomiel", "ASH359", "Valinor", "15/06/241", "Preferirei dividere una sola vita con te che affrontare tutte le ere di questo mondo da sola.", "arwen.jpg");
        User father = new User("Arathorn II", "Dunendain", "DGS830", "Gondor", "10/03/2900", "Mio figlio sembra Gesù Cristo.", "arathorn.jpg");
        User mother = new User("Gilraen", "Dunendain", "ASH359", "Gondor", "15/06/2907", "Ho dato la speranza ai Dúnedain, non ne ho conservata per me.", "gilraen.jpg");
        User child = new User("Eldarion", "Dunendain", "HSB302", "Gondor", "25/12/0", "Il cantante degli Aerosmith è mio nonno, ma la mamma non lo sa.", "eld.jpg");

        
        data.put("loggeduser", currentuser);
        data.put("currentuser", currentuser);
        data.put("spouse", spouse);
        data.put("father", father);
        data.put("mother", mother);
        data.put("child", child);
        
        
        // Configurazione freemarker
        Configuration cfg = new Configuration();
        
        cfg.setDefaultEncoding("UTF-8");
            
        cfg.setServletContextForTemplateLoading(getServletContext(), "/template");

        Template template = cfg.getTemplate("profile.html");

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
