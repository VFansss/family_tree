package it.collaborative_genealogy.servlets;

import it.collaborative_genealogy.User;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Marco
 */
public class RemoveRelative extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if(session!=null){
            try {
                //Recupero utente loggato
                User user_logged = (User)session.getAttribute("user_logged");
                String relationship = request.getParameter("type");
                
                switch(relationship){
                    
                    case "mother":      user_logged.removeParent("female");                                     break;
                    case "father":      user_logged.removeParent("male");                                       break;
                    
                    case "daughter":
                    case "son":         user_logged.removeChild(User.getUserById(request.getParameter("id")));  break;
                    
                    case "wife": 
                    case "husband":     user_logged.removeSpouse();                                             break;
                    
                }

                response.sendRedirect("profile");
            } catch (SQLException ex) {
                response.sendRedirect("profile?msg=srv");
            }
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
       
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet per la gestione della rimozione di un parente dal proprio albero genealogico";
    }

}
