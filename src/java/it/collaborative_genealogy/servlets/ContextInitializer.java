package it.collaborative_genealogy.servlets;


import it.collaborative_genealogy.Database;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
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
        
        try {
            Database.connect();
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(ContextInitializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            Database.close();
        } catch (SQLException ex) {
            Logger.getLogger(ContextInitializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
