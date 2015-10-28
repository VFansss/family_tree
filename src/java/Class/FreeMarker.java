/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

import Servlet.Templating.Signup;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Marco
 */
public class FreeMarker {
    
    /**
     * 
     * @param data              dati da inserire nel template          
     * @param path_template     pathname del template da caricare
     * @param response          
     * @param servlet_context
     * @throws IOException
     */
    public static void process(String path_template, Map data, HttpServletResponse response, ServletContext servlet_context) throws IOException{
        
        response.setContentType("text/html;charset=UTF-8");        
        // Configurazione freemarker
        Configuration cfg = new Configuration();
        
        cfg.setDefaultEncoding("UTF-8");
            
        cfg.setServletContextForTemplateLoading(servlet_context, "/template");

        Template template = cfg.getTemplate(path_template);
        
        PrintWriter out = response.getWriter();
            
        try{
            template.process(data, out);
            
        } catch (TemplateException ex) {     
            Logger.getLogger(Signup.class.getName()).log(Level.SEVERE, null, ex);
            
        } finally{
            out.flush();
            out.close(); 
        }

    }

    
    
}
