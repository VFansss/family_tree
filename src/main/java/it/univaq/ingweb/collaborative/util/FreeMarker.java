package it.univaq.ingweb.collaborative.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 *
 * @author Marco
 */
public class FreeMarker {
    private final static Logger log = Logger.getLogger(FreeMarker.class);
    
    private static Configuration cfg;
    
    static {
    	cfg = new Configuration(Configuration.VERSION_2_3_24);
    	cfg.setDefaultEncoding("UTF-8");
        
    }
    /**
     * 
     * @param data              dati da inserire nel template          
     * @param pathTemplate     pathname del template da caricare
     * @param response          
     * @param context
     * @throws IOException
     */
    public static void process(String pathTemplate, Map<String, Object> data, HttpServletResponse response, ServletContext context) throws IOException{
        log.info("Start templating");
        response.setContentType("text/html;charset=UTF-8");        
        // Configurazione freemarker        
        
        cfg.setServletContextForTemplateLoading(context, "/template");

        Template template = cfg.getTemplate(pathTemplate);
        
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
