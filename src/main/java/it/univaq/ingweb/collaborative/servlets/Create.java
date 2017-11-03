package it.univaq.ingweb.collaborative.servlets;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import it.univaq.ingweb.collaborative.Database;
import it.univaq.ingweb.collaborative.User;
import it.univaq.ingweb.collaborative.exception.NotAllowedException;
import it.univaq.ingweb.collaborative.tree.GenealogicalTree;
import it.univaq.ingweb.collaborative.tree.TreeNode;
import it.univaq.ingweb.collaborative.util.DateUtility;
import it.univaq.ingweb.collaborative.util.FreeMarker;
import it.univaq.ingweb.collaborative.util.Message;
import it.univaq.ingweb.collaborative.util.Utility;

/**
 *
 * @author Gianluca
 */
public class Create extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1066536126970866033L;

	/**
     * Handles the HTTP <code>GET</code> method.
     *
     * Costruzione della pagina per la creazione di un profilo o per l'invito di
     * un utente
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

            Map<String, Object> data = new HashMap<String, Object>();

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

            data.put("action", "create");
            data.put("script", "create");
            data.put("user_logged", userLogged);
            data.put("user_current", userCurrent);
            //Codifica del messaggio di errore sulla base del codice inviato
            data.put("message", new Message(request.getParameter("msg"), true));
            FreeMarker.process("create.html", data, response, getServletContext());

        } else {
            response.sendRedirect("login?msg=log");
        }

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * Gestione della creazione degli utenti e gli invii degli inviti
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Message check = new Message("dt_ok", false);
        // Recupera l'azione da svolgere
        String action = request.getRequestURI().substring(request.getContextPath().length() + 1); // create o invite

        HttpSession session = request.getSession(false);
        //Controllo. Si tratta di una richiesta AJAX?
        boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        // Se è attiva una sessiona
        if (session != null) {
            // Recupera l'utente loggato
            User userLogged = (User) session.getAttribute("user_logged");
            String name = "", surname = "", gender = "", birthdate = "", birthplace = "", biography = "", relationship = "", relative = "";

            if (ServletFileUpload.isMultipartContent(request)) {
                FileItem avatar = null;
                try {
                    List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

                    for (FileItem item : multiparts) {
                        if (!item.isFormField()) {
                            avatar = item;
                        } else {
                            String field = item.getFieldName();
                            switch (field) {
                                case "name":            name = item.getString();            break;
                                case "surname":         surname = item.getString();         break;
                                case "gender":          gender = item.getString();          break;
                                case "birthdate":       birthdate = item.getString();       break;
                                case "birthplace":      birthplace = item.getString();      break;
                                case "biography":       biography = item.getString();       break;
                                case "relationship":    relationship = item.getString();    break;
                                case "relative":        relative = item.getString();        break;
                            }
                        }
                    }

                } catch (Exception ex) {
                    check = new Message("pho_err", true); // Photo Uploaded Failed
                }

                if (!check.isError()) {

                    if (relationship.equals("")) {
                        check = new Message("fld", true);
                    } else {
                        // Verifica i dati dell'utente
                        check = Utility.checkData(name, surname, gender, birthdate, birthplace);
                        // Se tutti i dati sono corretti
                        if (!check.isError()) {
                            // Recupero dell'utente al quale bisogna aggiungere il nuovo parente
                            TreeNode userCurrentNode = ((GenealogicalTree) session.getAttribute("family_tree")).getUserById(relative);
                            User user_current = userCurrentNode.getUser();
                            // Gestione dati dell'utente
                            Map<String, Object> data = new HashMap<>();
                            String idUser = User.createUniqueUserId(10);
                            data.put("id", idUser);
                            data.put("name", name);
                            data.put("surname", surname);
                            data.put("gender", gender);
                            data.put("birthplace", birthplace);
                            data.put("biography", biography);

                            try {
                                Date sqlDate = DateUtility.stringToDate(birthdate);
                                data.put("birthdate", DateUtility.dateToString(sqlDate));
                            } catch (ParseException ex) {
                                check = new Message("date_2", true);
                            }

                            try {
                                // Inserimento dati nel db
                                Database.insertRecord("user", data);
                                // Recupero dell'utente appena creato
                                User user_added = User.getUserById(idUser);
                                // Imposta legame di parentela tra i due utenti convolti
                                user_current.setRelative(user_added, relationship);
                                // Dopo aver aggiungo il nuovo parente, bisogna fare il refresh dll'albero genealogico di tutti i parenti loggati in quel momento
                                userLogged.sendRefreshAck();
                                if (avatar != null && !avatar.getName().equals("")) {
                                    avatar.write(new File(this.getServletContext().getRealPath("/template/profile/").replace("build\\", "") + File.separator + idUser + ".jpg"));
                                    check = new Message("pho_ok", false); // Photo Uploaded Successfully
                                }
                            } catch (SQLException ex) {
                                check = new Message("srv", true); // Server error
                            } catch (NotAllowedException ex) {
                                check = new Message(ex.getMessage(), true); // Not allowed
                            } catch (Exception ex) {
                                check = new Message("pho_err", true); // Not allowed
                            }

                        }

                        // Se la servlet è stata chiamata con ajax
                        if (ajax) {
                            // Definisce la risposta alla chiamata ajax
                            response.setContentType("text/plain");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(check.toJSON());

                            // Se ci sono verificati degli errori
                        }
                    }
                } else {
                    check = new Message("tmp", true);
                }

            }

            if (!ajax && check.isError()) {
                // Mostra messaggio di errore
                response.sendRedirect(action + "?msg=" + check.getCode());

            } else {
                // Torna alla pagina del profilo
                response.sendRedirect("profile");
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
        return "Servlet \"Create\", gestisce la creazione di un nuovo proofilo base per un parente";
    }

}
