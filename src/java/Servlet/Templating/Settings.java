/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet.Templating;

import Class.Database;
import Class.FreeMarker;
import Class.User;
import Class.UserList;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Marco
 */
public class Settings extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ClassNotFoundException {
        response.setContentType("text/html;charset=UTF-8");
  
        //Gestione sessione
        HttpSession session = request.getSession(false);  
        
        //Se non è stata generata la sessione
        if(session != null){
            
            Database db = new Database("collaborative_genealogy");
            
            if(db.connect("admin", "admin")) {
            
                User user_logged = User.getUserById((String)session.getAttribute("id"));
                
                /* Primo form per il cambio dei dati*/
                
                    Map<String, Object> data = new HashMap<>();

                    List<String[]> first_form = new ArrayList();

                    String[] name =         {"name",        "text",     "Name",         user_logged.getName()                   };
                    String[] surname =      {"surname",     "text",     "Surname",      user_logged.getSurname()                };
                    String[] gender =       {"gender",      "text",     "Gender",       user_logged.getGender()                 };
                    String[] birthdate =    {"birthplace",  "text",     "Birthplace",   user_logged.getBirthdate().toString()   };
                    String[] birthplace =   {"birthplace",  "text",     "Birthplace",   user_logged.getBirthplace()             };
                    
                    first_form.add(name);
                    first_form.add(surname);
                    first_form.add(gender);
                    first_form.add(birthdate);
                    first_form.add(birthplace);

                    data.put("first_form", first_form);
                
                /* Secondo form per il cambio dell'email*/
                    List<String[]> second_form = new ArrayList();

                    String[] email =        {"email",           "Email",    "Email"         , user_logged.getEmail()};
                    String[] new_email =    {"new_email",       "Email",    "New Email"     , ""};
                    String[] repeat_email = {"repeat_email",    "Email",    "Repeat Email"  , ""};

                    second_form.add(email);
                    second_form.add(new_email);
                    second_form.add(repeat_email);

                    data.put("second_form", second_form);
                    
                /* Terzo form per il cambio della password*/
                    List<String[]> third_form = new ArrayList();

                    String[] password =             {"password",        "password", "Password"          };
                    String[] new_password =         {"new_password",    "password", "New Password"      };
                    String[] repeat_password =      {"repeat_password", "password", "Repeat Password"   };

                    third_form.add(password);
                    third_form.add(new_password);
                    third_form.add(repeat_password);

                    data.put("third_form", third_form);
                
                data.put("user_logged", user_logged);

                String msn = request.getParameter("msn");
                data.put("msn", msn);
                
                
                FreeMarker.process("settings.html",data, response, getServletContext());
                
            }
            
        }else{
            // Vai alla pagina di login e mostra messaggio di errore
            response.sendRedirect("login?msn=" + URLEncoder.encode("Please log in to see this page", "UTF-8"));

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
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
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
