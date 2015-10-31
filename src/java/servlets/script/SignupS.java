/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.script;

import classes.Database;
import classes.DataUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classes.DataUtil;
import java.net.URLEncoder;

/**
 *
 * @author Alex
 */
public class SignupS extends HttpServlet {

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
    
    //Oggetto ausiliario DataUtil. Utilizzato per ricevere riuscita delle chiamate
    //di funzione dei metodi check_*
    //Controlla Javadoc DataUtil
    DataUtil reply = new DataUtil();
    
    //
    //Check sul campo 'mail'
    //
    reply = DataUtil.check_mail(request.getParameter("email"));
    if(!reply.success){
        
    response.sendRedirect("signup?msn=" + URLEncoder.encode(reply.message, "UTF-8"));
    return;}
    
    //
    //Check sul campo 'nome'
    //
    reply = DataUtil.check_name(request.getParameter("name"));
    if(!reply.success){
        
    response.sendRedirect("signup?msn=" + URLEncoder.encode(reply.message, "UTF-8"));
    return;}
    
    //
    //Check sul campo 'cognome'
    //
    reply = DataUtil.check_name(request.getParameter("surname"));
    if(!reply.success){
        
    response.sendRedirect("signup?msn=" + URLEncoder.encode(reply.message, "UTF-8"));
    return;}
    
        
        
    //Tutti i campi sono considerati 'ok'
    //TODO: Si procede alla scrittura sul DB
    
    response.sendRedirect("login?msn=" + URLEncoder.encode("signup_done", "UTF-8"));

//END OF METHOD
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
