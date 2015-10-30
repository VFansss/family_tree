/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet.Script;

import Class.Database;
import Class.Function;
import Class.User;
import java.io.IOException;
import java.sql.Date;
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
public class Settings extends HttpServlet {
    private static HttpSession session;
    private static User user_logged;
    
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
        String action = (String)request.getParameter("action");
        
        session = request.getSession(false);  
        user_logged = (User)session.getAttribute("user_logged");
        
        // Se non è loggato nessun utente;
        if(!(action == null || (action.equals("data") || action.equals("email") || action.equals("password") || action.equals("avatar")))){
            // Vai alla pagina delle impostazioni
            response.sendRedirect("settings");
        }else if(user_logged == null){
                response.sendRedirect("login?msn=log");
                
        }else{

            String result; 
            switch (action) {
                case "data":
                    result = changeData(request);
                    break;
                case "email":
                    result = changeEmail(request);
                    break;
                case "password":
                    result = changePassword(request);
                    break;
                case "avatar":
                    result = changeAvatar(request);
                    break;
                default: result = "Something is wrong";
            }
            
            response.sendRedirect("settings?result=" + result + "&action=" + action);

        }
        
    }
    
    public static String changeData(HttpServletRequest request){
        
        String name = (String)request.getParameter("name");
        String surname = (String)request.getParameter("surname");
        String gender = (String)request.getParameter("gender");
        String birthdate = (String)request.getParameter("birthdate");
        String birthplace = (String)request.getParameter("birthplace");
        
        Date sqlDate = Function.stringToDate(birthdate);
        
        boolean resu1 =user_logged.getName().equals(name);
        boolean resu2 =user_logged.getSurname().equals(surname);
        boolean resu3 =user_logged.getGender().equals(gender.toLowerCase());
        boolean resu4 =user_logged.getBirthdate().equals(sqlDate);
        boolean resu5 =user_logged.getBirthplace().equals(birthplace);
      
        
        if(name.equals("") || surname.equals("") || gender.equals("") || birthdate.equals("") || birthplace.equals("")){
            
            return "All fields are required";
        
        // Se non è stato modificato nessun campo
        }else if(user_logged.getName().equals(name) && user_logged.getSurname().equals(surname) && user_logged.getGender().equals(
                    gender) && user_logged.getBirthdate().equals(birthdate) && user_logged.getBirthplace().equals(birthplace)){
            
            
           ;
            return "No data to change";

        // Se il sesso non è valido
        }else if(!(gender.toLowerCase().equals("male") || gender.toLowerCase().equals("female"))){
            
            return "You can be only male or female";
                
        }else{
            
            
            
            if(sqlDate == null){ 
                
                return "Birthdate is not valid";
            
            }else{
                
                Map<String, Object> data = new HashMap<>();
                data.put("name", name);
                data.put("surname", surname);
                data.put("birthdate", sqlDate);
                data.put("birthplace", birthplace);

                if(!user_logged.getGender().equals(gender)){
                    /*
                        Se si cambia il sesso, l'utente deve essere scollegato dal proprio albero genealogico 
                    */
                    data.put("gender", gender);
                    user_logged.removeFather();
                    user_logged.removeMother();
                    user_logged.removeSpouse();
                }

                boolean result = Database.updateRecord("user", data, "id = '" + user_logged.getId() + "'");
                if(!result) return "Something is wrong";
                
                // Aggiornamento dell'utente
                session.setAttribute("user_logged", User.getUserById(user_logged.getId()));
                return "";
            }
            
        }
                
        
        
    }
    
    public static String changeEmail(HttpServletRequest request){
        String current_email = (String)request.getAttribute("current_email");
        String new_email = (String)request.getAttribute("new_email");
        String repeat_email = (String)request.getAttribute("repeat_email");
        
        return "";
    }
    
    public static String changePassword(HttpServletRequest request){
    
        return "";
    }
    
    public static String changeAvatar(HttpServletRequest request){
    
        return "";
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
