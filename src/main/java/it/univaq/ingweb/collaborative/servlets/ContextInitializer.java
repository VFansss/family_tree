package it.univaq.ingweb.collaborative.servlets;


import it.univaq.ingweb.collaborative.Database;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebServlet;
import org.apache.log4j.Logger;

/**
 *
 * @author Marco
 */
@WebServlet(name = "ContextListener", urlPatterns = {"/ContextListener"})
public class ContextInitializer implements ServletContextListener {

    final static Logger log = Logger.getLogger(ContextInitializer.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        try {
            Database.connect();
        } catch (NamingException | SQLException ex) {
            log.error("Errore durante la connesione al db", ex);
        }
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            Database.close();
        } catch (SQLException ex) {
            log.error("Errore durante la chiusura del db", ex);
        }
    }

}
