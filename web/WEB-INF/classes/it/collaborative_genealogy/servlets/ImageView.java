/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.collaborative_genealogy.servlets;

import it.collaborative_genealogy.util.DataUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Marco
 */
public class ImageView extends HttpServlet {
    
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
        String user_id = request.getParameter("id");
        if(code == null){
            response.sendRedirect("image-view?id=" + user_id + "&code=" + DataUtil.generateCode(5));
        }else{
            ServletContext cntx= getServletContext();
            // Recupera l'id dell'utente
            
            String filename = cntx.getRealPath("/template/profile/" + user_id + ".jpg");

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
                int count = 0;
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
        return "Short description";
    }

}
