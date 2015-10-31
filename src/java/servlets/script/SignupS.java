/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.script;

import classes.Database;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classes.DataUtil;
import classes.Message;
import classes.User;
import classes.tree.NodeList;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

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
    
    String email        = request.getParameter("email").trim();
    String password     = request.getParameter("password").trim();
    String name         = DataUtil.spaceTrim(request.getParameter("name"));
    String surname      = DataUtil.spaceTrim(request.getParameter("surname"));
    String gender       = request.getParameter("gender").trim();
    String birthplace   = DataUtil.spaceTrim(request.getParameter("birthplace"));
    String birthdate    = request.getParameter("birthdate").trim();
    
    Message check;
    
    // Se non sono stati compilati tutti i campi
    if(email.equals("") || password.equals("") || name.equals("") || surname.equals("") || gender == null || birthdate.equals("")  || birthplace.equals("")){
        check = new Message("All field required", true);
    }else{
        
        // Controllo dell'email
        check = DataUtil.checkEmail(email);
        if(!check.isError()) {
             
            // Controllo della password
            check = DataUtil.checkPassword(password);
            if(!check.isError()) {

                // Controllo del nome
                check = DataUtil.checkName(name);
                if(!check.isError()) {

                    // Controllo del cognome
                    check = DataUtil.checkName(surname);
                    if(!check.isError()) {
            
                        // Controllo del sesso
                        check = DataUtil.checkGender(gender);
                        if(!check.isError()) {
        
                            // Controllo della città di nascita
                            check = DataUtil.checkBirthplace(birthplace);
                            if(!check.isError()) {
            
                                // Controllo della data di nascita
                                check = DataUtil.checkBirthdate(birthdate);

        }}}}}}}
        
        // Se è stato riscontrato un errore, 
        if(check.isError()){
//            // Vai alla pagina di signup mostrando l'errore
            response.sendRedirect("signup?msg=" + URLEncoder.encode(check.getMessage(), "UTF-8"));
           
        }else{
            
            Map<String, Object> data = new HashMap<>();
            
            // Genera l'id dell'utente
            data.put("id", User.createUniqueUserId(10));
            
            data.put("email", email);
            data.put("password", DataUtil.crypt(password));
            data.put("name", name);
            data.put("surname", surname);
            data.put("gender", gender);
            data.put("birthplace", birthplace);
            Date sqlDate = DataUtil.stringToDate(birthdate, "dd/MM/yyyy");
            data.put("birthdate", DataUtil.dateToString(sqlDate));
            
            boolean result = Database.insertRecord("user", data);
            if(!result) {
                response.sendRedirect("signup?msg=Error");
            }else{
                // Prelevo l'utente appena creato
                User new_user = User.getUserByEmail(email);
                // Altrimenti, fai il login dell'utente
                HttpSession session = request.getSession();
                // Inserimento dell'utente in una variabile di sessione
                session.setAttribute("user_logged", new_user);
                // Inizializzazione della breadcrumb dell'utente
                session.setAttribute("breadcrumb", new NodeList());

                // Se l'utente si è registrato sotto invito, allora avrà già un suo albero genealogico
                session.setAttribute("family_tree", new_user.getFamilyTree());
                // Reindirizzamento alla pagina del profilo dell'utente
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
