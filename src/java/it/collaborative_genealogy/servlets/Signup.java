package it.collaborative_genealogy.servlets;

import it.collaborative_genealogy.Database;
import it.collaborative_genealogy.User;
import it.collaborative_genealogy.util.DataUtil;
import it.collaborative_genealogy.util.Message;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Alessio
 */
public class Signup extends HttpServlet {

    /**
     * Caricamento pagina di signup
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("action", "signup");
        request.getRequestDispatcher("login").forward(request, response);
    }

    /**
     * Gestione della registrazione di un utente
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email        = request.getParameter("email").trim();
        String password     = request.getParameter("password").trim();
        String name         = DataUtil.spaceTrim(request.getParameter("name"));
        String surname      = DataUtil.spaceTrim(request.getParameter("surname"));
        String gender       = request.getParameter("gender").trim();
        String birthplace   = DataUtil.spaceTrim(request.getParameter("birthplace"));
        String birthdate    = request.getParameter("birthdate").trim();

        Message check;
        
        
        //Controllo. Si tratta di una richiesta AJAX?
        boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        // Se non sono stati compilati tutti i campi
        if(email.equals("") || password.equals("") || name.equals("") || surname.equals("") || gender == null || birthdate.equals("")  || birthplace.equals("")){
            check = new Message("fld", true);
        }else{

            // Controllo dell'email
            check = DataUtil.checkEmail(email);
            if(!check.isError()) {

                // Controllo della password
                check = DataUtil.checkPassword(password);
                if(!check.isError()) {

                    // Controllo di dati
                    check = DataUtil.checkData(name, surname, gender, birthdate, birthplace);

        }}}

        // Se è stato riscontrato un errore, 
        if(!check.isError()){
            
            Map<String, Object> data = new HashMap<>();

            // Genera l'id dell'utente
            String user_id = User.createUniqueUserId(10);
            data.put("id", user_id);

            data.put("email", email);
            data.put("password", DataUtil.crypt(password));
            data.put("name", name);
            data.put("surname", surname);
            data.put("gender", gender);
            data.put("birthplace", birthplace);
            data.put("biography", "");
            
            Date sqlDate = null;
            try {
                sqlDate = DataUtil.stringToDate(birthdate, "dd/MM/yyyy");
                data.put("birthdate", DataUtil.dateToString(sqlDate));
            } catch (ParseException ex) { }
            

            try {
                Database.insertRecord("user", data);
                // Creo l'oggetto riservato all'utente
                User new_user = new User(user_id, name, surname, email, gender, sqlDate, birthplace, "");
                // Prepara l'utente ad essere loggato (gestione della variabili si sessione)
                new_user.initSession(request.getSession(false));
                // Reindirizzamento alla pagina del profilo dell'utente
                
                
                if (ajax) {
                    // Handle ajax response.  
                    response.setContentType("text/plain");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("");
                }
                else{
                    // Handle regular response
                    response.sendRedirect("profile");
                }
                
                
            } catch (SQLException ex) {
                response.sendRedirect("signup?msg=Error");
            }

        }else{
            
            if (ajax) {
                // Handle ajax response.
                response.setContentType("text/plain");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(check.getMsg());
            } else{
                // Handle regular response
                // Vai alla pagina di signup mostrando l'errore
                response.sendRedirect("signup?msg=" + URLEncoder.encode(check.getCode(), "UTF-8"));
            }
            
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet per la gestione della registrazione";
    }

}
