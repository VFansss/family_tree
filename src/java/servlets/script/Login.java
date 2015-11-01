/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.script;

import classes.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URLEncoder;

/**
 *
 * @author Gianluca
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class Login extends HttpServlet {

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
 
        //Recupera l'email dell'utente
        String email = request.getParameter("email");
        //Recupera la password dell'utente
        String password = request.getParameter("password");

        // Recupera l'utente
        User user_to_log = User.getUserByEmail(email);

        // Se l'utente non esiste
        if(user_to_log == null){
            // Torna alla pagine di login con messaggio di errore
            response.sendRedirect("login?msg=" + URLEncoder.encode("usr", "UTF-8"));

        // Se la password dell'utente è sbagliata
        }else if(!user_to_log.checkPassword(password)){
            // Torna alla pagine di login con messaggio di errore
            response.sendRedirect("login?msg=" + URLEncoder.encode("psw", "UTF-8"));

        }else{
            // Prepara l'utente ad essere loggato (gestione della variabili si sessione)
            user_to_log.prepareToLog(request);
            response.sendRedirect("profile");
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
