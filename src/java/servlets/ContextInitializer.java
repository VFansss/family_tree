/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import classes.Database;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author Marco
 */
@WebServlet(name = "ContextListener", urlPatterns = {"/ContextListener"})
public class ContextInitializer implements ServletContextListener {

    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Database.connect();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Database.close();
    }

}
