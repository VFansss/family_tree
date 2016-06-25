package it.univaq.ingweb.collaborative.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Marco
 */
public class FreeMarker {
    final static Logger log = Logger.getLogger(FreeMarker.class);
    /**
     * 
     * @param data              dati da inserire nel template          
     * @param path_template     pathname del template da caricare
     * @param response          
     * @param context
     * @throws IOException
     */
    public static void process(String path_template, Map data, HttpServletResponse response, ServletContext context) throws IOException{
        log.info("Start templating");
        response.setContentType("text/html;charset=UTF-8");        
        // Configurazione freemarker
        Configuration cfg = new Configuration();
        
        cfg.setDefaultEncoding("UTF-8");
            
        cfg.setServletContextForTemplateLoading(context, "/template");

        Template template = cfg.getTemplate(path_template);
        
        PrintWriter out = response.getWriter();
        
        try{
            template.process(data, out);
            
        } catch (TemplateException ex) {     
            
        } finally{
            out.flush();
            out.close(); 
        }
        
        
        log.info("End templating");
    }

    
    
}
