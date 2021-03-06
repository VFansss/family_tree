package it.univaq.ingweb.collaborative.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.univaq.ingweb.collaborative.util.Utility;

/**
 *
 * @author Marco
 */
public class ImageView extends HttpServlet {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -8212731602885051850L;

	/**
     * Gestione caricamento immagine
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String code = request.getParameter("code");
        String idUser = request.getParameter("id");
        if(code == null){
            response.sendRedirect("image-view?id=" + idUser + "&code=" + Utility.generateCode(5));
        }else{
            ServletContext cntx= getServletContext();
            // Recupera l'id dell'utente
            
            String filename = cntx.getRealPath("/template/profile/" + idUser + ".jpg");

            String mime = cntx.getMimeType(filename);
            if (mime == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            }else{
                response.setContentType(mime);
                File file = new File(filename);
                if(!file.exists()) file = new File(cntx.getRealPath("/template/images/default-avatar.jpg"));
                response.setContentLength((int)file.length());

                FileInputStream in = new FileInputStream(file);
                OutputStream out = response.getOutputStream();

                // Copy the contents of the file to the output stream
                byte[] buf = new byte[1024];
                int count;
                while ((count = in.read(buf)) >= 0) {
                    out.write(buf, 0, count);
                }
                out.close();
                in.close();
            }
        }
        
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet per la visualizzazione delle immagini del profilo";
    }

}
