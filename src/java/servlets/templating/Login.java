/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.templating;

import classes.Database;
import classes.FreeMarker;
import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Marco
 */
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
        //Gestione sessione
        HttpSession session = request.getSession(false);  
        if(Database.isConnected()){
            //Se non è stata generata la sessione
            if(session == null){
                Map<String, Object> data = new HashMap<>();

                data.put("action", "login");

                //Codifica del messaggio di errore sulla base del codice inviato
                String msn = request.getParameter("msn");
                if (msn!=null){
                    switch(msn){
                        case "log":
                            msn = "Please log in to see this page";
                            break;
                        case "usr":
                            msn = "User does not exist";
                            break;
                        case "psw":
                            msn = "Incorrect password";
                            break;
                        case "signup_done":
                            msn = "Registrazione effettuata correttamente!";
                            break;
                        default:
                            msn=null;
                    }
                }

                data.put("msn", msn);

                FreeMarker.process("login.html",data, response, getServletContext());
            }else{
                // Altrimenti vai alla pagina dell'utente loggato
                response.sendRedirect("profile");
            }
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
