package it.univaq.ingweb.collaborative.servlets;

import it.univaq.ingweb.collaborative.Database;
import it.univaq.ingweb.collaborative.User;
import it.univaq.ingweb.collaborative.exception.NotAllowedException;
import it.univaq.ingweb.collaborative.tree.GenealogicalTree;
import it.univaq.ingweb.collaborative.tree.TreeNode;
import it.univaq.ingweb.collaborative.util.DateUtility;
import it.univaq.ingweb.collaborative.util.Utility;
import it.univaq.ingweb.collaborative.util.FreeMarker;
import it.univaq.ingweb.collaborative.util.Message;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Marco
 */
public class Invite extends HttpServlet {

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

        if (session != null) {

            Map<String, Object> data = new HashMap<>();

            User userLogged = (User) session.getAttribute("user_logged");

            // Recupero dell'utente corrente: non c'è il controllo sull'esistenza dell'utente, viene raccolta l'eccezione
            User userCurrent;
            TreeNode userCurrentNode;

            if (request.getParameter("id") != null) {
                userCurrentNode = ((GenealogicalTree) session.getAttribute("family_tree")).getUserById((String) request.getParameter("id"));
                userCurrent = userCurrentNode.getUser();
                data.put("relative_grade", userCurrentNode.getLabel());

            } else {
                userCurrent = userLogged;
            }

            data.put("action", "invite");
            data.put("script", "invite");
            data.put("user_logged", userLogged);
            data.put("user_current", userCurrent);
            //Codifica del messaggio di errore sulla base del codice inviato
            data.put("message", new Message(request.getParameter("msg"), true));
            FreeMarker.process("invite.html", data, response, getServletContext());

        } else {
            response.sendRedirect("login?msg=log");
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Message check;

        HttpSession session = request.getSession(false);
        
        //Controllo. Si tratta di una richiesta AJAX?
        boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        // Se è attiva una sessiona
        if (session != null) {
            // Recupera l'utente loggato
            User user_logged = (User) session.getAttribute("user_logged");

            String email = request.getParameter("email");
            String name = request.getParameter("name");
            String surname = request.getParameter("surname");
            String gender = request.getParameter("gender");
            String birthdate = request.getParameter("birthdate");
            String birthplace = request.getParameter("birthplace");
            String relationship = request.getParameter("relationship");
            
            if (relationship.equals("")) {
                check = new Message("fld", true);
            } else {
                // Verifica i dati dell'utente
                check = Utility.checkData(name, surname, gender, birthdate, birthplace);
                // Se tutti i dati sono corretti
                if (!check.isError()) {
                    
                    // Controlla l'email
                    check = Utility.checkEmail(email);
                    if (!check.isError()) {
                        // Recupero dell'utente al quale bisogna aggiungere il nuovo parente
                        TreeNode userCurrentNode = ((GenealogicalTree) session.getAttribute("family_tree")).getUserById(request.getParameter("relative"));
                        User userCurrent = userCurrentNode.getUser();
                        // Gestione dati dell'utente
                        Map<String, Object> data = new HashMap<>();
                        String user_id = User.createUniqueUserId(10);
                        data.put("id", user_id);
                        data.put("email", email);
                        data.put("name", name);
                        data.put("surname", surname);
                        data.put("gender", gender);
                        data.put("birthplace", birthplace);
                        data.put("biography", "");

                        try {
                            Date sqlDate = DateUtility.stringToDate(birthdate);
                            data.put("birthdate", DateUtility.dateToString(sqlDate));
                        } catch (ParseException ex) { }

                        try {
                            // Inserimento dati nel db
                            Database.insertRecord("user", data);
                            // Recupero dell'utente appena creato
                            User user_added = User.getUserById(user_id);
                            // Imposta legame di parentela tra i due utenti convolti
                            userCurrent.sendRequest(user_added, relationship);
                            check = new Message("inv", false); // User invited
                        } catch (SQLException ex) {
                            check = new Message("srv", true); // Server error
                        } catch (NotAllowedException ex) {
                            check = new Message(ex.getMessage(), true); // Not allowed
                        }
                    }
                }
            }
            // Se la servlet è stata chiamata con ajax
            if (ajax) {
                // Definisce la risposta alla chiamata ajax
                response.setContentType("text/plain");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(check.toJSON());

                // Se ci sono verificati degli errori
            } else if (check.isError()) {
                // Mostra messaggio di errore
                response.sendRedirect("invite?msg=" + check.getCode());

            } else {
                // Torna alla pagina del profilo
                response.sendRedirect("profile?msg=inv");
            }

        } else {

            if (ajax) {
                // Definisce la risposta alla chiamata ajax
                response.setContentType("text/plain");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(new Message("log", true).toJSON());
            } else {
                response.sendRedirect("login?msg=log");
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
        return "Servlet per la gestione degli inviti a registrarsi a Collaborative Genealogy";
    }
}
