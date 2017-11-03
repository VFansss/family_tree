package it.univaq.ingweb.collaborative.servlets;



import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import it.univaq.ingweb.collaborative.Database;
import it.univaq.ingweb.collaborative.User;
import it.univaq.ingweb.collaborative.util.FreeMarker;
import it.univaq.ingweb.collaborative.util.Message;
import it.univaq.ingweb.collaborative.util.Utility;

/**
 *
 * @author Marco
 */
public class Login extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8914037846153606085L;
	
	final static Logger log = Logger.getLogger(Login.class);
    /**
     * Caricamento pagina di login
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        log.info("Start login GET");
        try{
            HttpSession session = request.getSession(false);  
            // Se non è stata generata la sessione
            if(session == null){

                Map<String, Object> data = new HashMap<>();

                String idUser = request.getParameter("code");
                if(idUser != null){
                    data.put("user_id", idUser);
                    data.put("email", User.getUserById(idUser).getEmail());
                }
                
                /* Gestione azione */
                    // Recupera l'azione da svolgere (login o signup)
                    String action = (String) request.getAttribute("action");
                    // Se l'azione non è stata definita o non è valida, impostala come l'azione di login
                    if(action == null || (action.equals("login") && action.equals("signup"))) action = "login";
                    // Inserisci l'azione nel data-model
                    data.put("action", action);

                //Codifica del messaggio di errore sulla base del codice inviato
                data.put("message", new Message(request.getParameter("msg"), true));

                data.put("script", "login");

                FreeMarker.process("login.html", data, response, getServletContext());
            }else{
                // Altrimenti vai alla pagina dell'utente loggato
                response.sendRedirect("profile");
            }
        }catch(Exception e){
            log.error("Errore durante il caricamento della pagina di login",e);
        }
        
        log.info("End login GET");
        
    }

    /**
     * Gestione login dell'utente
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("Start login POST");
        try{
            
            //Recupera l'email dell'utente
            String email = request.getParameter("email");
            //Recupera la password dell'utente
            String password = request.getParameter("password");

            // Controllo. Si tratta di una richiesta AJAX?
            boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
            boolean quickSignup = false;
            String msg = null;
            boolean error = true;

            if(email.equals("") && password.equals("")){
                msg = "fld"; // All fields required

            }else{

                // Recupera l'utente
                User user = User.getUserByEmail(email);
                
                log.debug("Utente recuperato " + user);
                // Se l'utente non esiste
                if(user == null){
                    msg = "usr_1";
                }else{
                    try{
                        // Se l'utente non ha una password
                        if(user.getPassword() == null){
                            //Recupera l'id inviato dal form
                            String newIdUser = request.getParameter("user_id");
                            // Controllo di autenticazione: dal form viene anche inviato l'id dell'utente invitato, 
                            //      quindi se l'id nel form corrisponde all'id dell'utente inviato, quest'ultimo è autenticato.
                            if(newIdUser != null && newIdUser.equals(user.getId())){
                                // Imposta nuova password
                                Map<String, Object> data = new HashMap<>();
                                data.put("password", Utility.crypt(password));
                                Database.updateRecord("user", data, "id = '" + user.getId() + "'");
                                // Apri la sessione
                                // Prepara l'utente ad essere loggato (gestione della variabili si sessione)
                                user.initSession(request.getSession());
                                error = false;
                            }else{
                                msg = "tmp";
                            }
                            quickSignup = true;

                        }

                    } catch (SQLException ex) {
                        msg = "srv";
                        log.error("errore di connesione al db", ex);
                    }

                    if(!quickSignup){

                        // Se la password dell'utente è sbagliata
                        if(!user.checkPassword(password)){
                            msg = "psw";

                        }else{
                            // Prepara l'utente ad essere loggato (gestione della variabili di sessione)
                            user.initSession(request.getSession());
                            error = false;
                        }
                    }
                    
                }

            }
            // Se si è verificato un errore
            if(error){
                // Se la servlet è stata chiamata con ajax
                if (ajax) {
                    // Definisci il messaggio per ricavarne la descrizione completa
                    Message message = new Message(msg, true);
                    // Definisce la risposta alla chiamata ajax
                    response.setContentType("text/plain");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(message.getMsg());       
                } else {
                    // Torna alla pagine di login con messaggio di errore
                    response.sendRedirect("login?msg=" + URLEncoder.encode(msg, "UTF-8"));
                }
            }else{
                if (ajax) {
                    // Handle ajax response.
                    response.setContentType("text/plain");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("");       
                }else{
                    // Handle regular response
                    response.sendRedirect("profile");
                }
            }

        }catch(Exception e){
            log.error("Errore durante il login",e);
        }
        
        log.error("end login POST");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet per la gestione del login";
    }

}
