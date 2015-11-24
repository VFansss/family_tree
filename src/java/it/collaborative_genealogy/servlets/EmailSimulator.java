package it.collaborative_genealogy.servlets;

import it.collaborative_genealogy.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Marco
 */
public class EmailSimulator extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        String email = request.getParameter("email");
        User user = User.getUserByEmail(email);
        
        String action = (String)request.getParameter("action");
       
        HttpSession session=request.getSession();  
        
        User user_logged = (User)session.getAttribute("user_logged");
        
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Email simulator</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Email Simulator</h1>");
            
            if(action.equals("invite")){
                out.println("<p>"+user_logged.getName()+" "+user_logged.getSurname()+" has sent you a request to join to Collaborative Genealogy</p>");
                out.println("<a target='_blank' href='login?code="+user.getId()+"'>Click here to sign up</a>"); 
            } else if(action.equals("request")){
                out.println("<p>"+user_logged.getName()+" "+user_logged.getSurname()+" added you as "+request.getParameter("relationship")+"</p>");
                out.println("<a target='_blank' href='requests'>Click here to accept the request</a>"); 
            } else {
                out.println("<p>Else<p>");
            }
            
            out.println("</body>");
            out.println("</html>");
            out.close();
        }
        session.invalidate();
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
        return "Servlet di servizio per la simulazione dell'invio delle email per le richieste";
    }// </editor-fold>

}
