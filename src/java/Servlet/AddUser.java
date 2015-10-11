/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import Class.Database;
import Class.User;
import Class.UserList;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Marco
 */
public class AddUser extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.lang.ClassNotFoundException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ClassNotFoundException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Database.setOut(out);
            Database db = new Database("collaborative_genealogy");
            db.connect("admin", "admin");
            
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html><html><head><title>Servlet Test</title></head><body>");
            
            Map<String, Object> data = new HashMap<>();
            Map<String, String> action = new HashMap<>();
            
            data.put(           "name",   request.getParameter("name")        );
            data.put(        "surname",   request.getParameter("surname")     );
            data.put(          "email",   request.getParameter("email")       );
            data.put(         "gender",   request.getParameter("gender")      );
            data.put(      "birthdate",   request.getParameter("birthdate")   );
            data.put(     "birthplace",   request.getParameter("birthplace")  );
            
            action.put(       "father",   request.getParameter("father")      );
            action.put(       "mother",   request.getParameter("mother")      );
            action.put(       "spouse",   request.getParameter("spouse")      );
            
            User u1 = User.getUserById("AAA");   
  
            u1.getUnlabeledTree().analize();
//            out.println(User.create(data, action));

            out.close();
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
            Logger.getLogger(AddUser.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(AddUser.class.getName()).log(Level.SEVERE, null, ex);
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
