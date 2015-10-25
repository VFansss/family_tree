/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet.Templating;

import Class.Database;
import Class.FreeMarker;
import Class.User;
import Class.UserList;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Marco
 */
public class Settings extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ClassNotFoundException {
        response.setContentType("text/html;charset=UTF-8");
        
        
        
        
        
        //Gestione sessione
        HttpSession session = request.getSession(false);  
        
        //Se non è stata generata la sessione
        if(session==null){
            // Vai alla pagina di login e mostra messaggio di errore
            response.sendRedirect("login?msn=" + URLEncoder.encode("Please log in to see this page", "UTF-8"));

        }else{
        
            Database db = new Database("collaborative_genealogy");
            
            if(!db.connect("admin", "admin")) return;
            
            User user_logged = User.getUserById("C");

            Map<String, Object> data = new HashMap<>();

            List<String[]> fields = new ArrayList();

            String[] name =         {"name",        "text",     "Name",         user_logged.getName()};
            String[] surname =      {"surname",     "text",     "Surname",      user_logged.getSurname()};
            String[] gender =       {"gender",      "text",     "Gender",       user_logged.getGender()};
            String[] birthdate =    {"birthplace",  "text",     "Birthplace",   user_logged.getBirthdate().toString()};
            String[] birthplace =   {"birthplace",  "text",     "Birthplace",   user_logged.getBirthplace()};
            String[] email =        {"email",       "email",    "E-mail",       user_logged.getEmail()};
            String[] password =     {"password",    "password", "Password",     ""};

            fields.add(name);
            fields.add(surname);
            fields.add(gender);
            fields.add(birthdate);
            fields.add(birthplace);
            fields.add(email);
            fields.add(password);

            data.put("fields", fields);
            
            data.put("user_logged", user_logged);
            
            String msn = request.getParameter("msn");
            data.put("msn", msn);
           
            
            FreeMarker.process("settings.html",data, response, getServletContext());
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
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
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
