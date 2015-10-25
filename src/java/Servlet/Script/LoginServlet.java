/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet.Script;

import Class.Database;
import Class.User;
import Class.UserList;
import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gianluca
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

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
        
        PrintWriter out = response.getWriter();
        
        // Connessione al database
        Database db = new Database("collaborative_genealogy");
        if(!db.connect("admin", "admin")){
            out.println("ERRORE CONNESSIONE A DATABASE");
            return;
        }

        //Recupera l'email dell'utente
        String email = request.getParameter("email");
        //Recupera la password dell'utente
        String password = request.getParameter("password");
        
        // Recupera l'utente
        User user = User.getUserByEmail(email);

        // Se l'utente non esiste
        if(user == null){
            // Torna alla pagine di login con messaggio di errore
            response.sendRedirect("login?msn=" + URLEncoder.encode("User does not exist", "UTF-8"));

        // Se la password dell'utente è sbagliata
        }else if(!user.checkPassword(password)){
            // Torna alla pagine di login con messaggio di errore
            response.sendRedirect("login?msn=" + URLEncoder.encode("Password is not valid", "UTF-8"));

        }else{
            // Altrimenti, fai il login dell'utente
            HttpSession session = request.getSession();
            session.setAttribute("id", user.getId());
            session.setAttribute("navigation", new UserList());
            response.sendRedirect("profile?id=" + user.getId());
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
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
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
