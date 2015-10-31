/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets_asdsda.Script;

import classes_asdsa.Database;
import classes_asdsa.DataUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classes_asdsa.DataUtil;
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
        
    String name = request.getParameter("name");
    
    name = name.trim();
    name = DataUtil.internalTrim(name);
    name = DataUtil.capitalizeEachWord(name);
            
    //Check alphanumeric
    if(!DataUtil.isAlphanumeric(name)){
        //NOT Alphanumeric
        
        //LANCIO ERRORE
    }
    
    //Check lunghezza anomala
    //Nome 'anomalo': meno di 2 caratteri, piu di 50
    if(DataUtil.anormalLength(name,2,50)){
        
        //NOME STRANO
        
        //LANCIO ERRORE
    }
    

    
    response.sendRedirect("login");
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
