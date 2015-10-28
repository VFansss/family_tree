/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import Class.Database;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Marco
 */
@WebServlet(name = "ContextListener", urlPatterns = {"/ContextListener"})
public class ContextInitializer implements ServletContextListener {
    private Database db;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            db = new Database();
            sce.getServletContext().setAttribute("connect", db.connect());
        } catch (ServletException ex) {
            Logger.getLogger(ContextInitializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        db.close();
    }

}
