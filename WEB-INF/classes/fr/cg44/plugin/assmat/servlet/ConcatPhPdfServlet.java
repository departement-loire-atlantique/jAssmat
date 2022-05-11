package fr.cg44.plugin.assmat.servlet;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.jalios.jcms.Publication;
import com.jalios.util.Util;

import fr.cg44.plugin.assmat.PointAssmat;
import fr.cg44.plugin.assmat.beans.AssmatSearch;
import fr.cg44.plugin.assmat.pdf.GeneratePdf;


public class ConcatPhPdfServlet extends HttpServlet {

  private static final long serialVersionUID = 1352440229245589912L;

  private static final Logger LOGGER = Logger.getLogger(ConcatPhPdfServlet.class);

  /**
   * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
   */
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
  }

  /**
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    concatPdf(request, response, "GET");
  }

  /**
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    concatPdf(request, response, "POST");
  }

  /**
   * Performs the action: generate a PDF from a GET or POST.
   * 
   * @param request
   *          the Servlets request object
   * @param response
   *          the Servlets request object
   * @param methodGetPost
   *          the method that was used in the form
   * @throws ServletException
   * @throws IOException
   */
  public void concatPdf(HttpServletRequest request, HttpServletResponse response, String methodGetPost) throws ServletException, IOException {
    try {

      String type = Util.getString(request.getParameter("type"), "");      

      ByteArrayOutputStream baosContent = new ByteArrayOutputStream();
      if(Util.notEmpty(type)){

        HttpSession session = request.getSession();

        if (type.equals("detail")) {       	
          // Récupère les 4 groupes triés correspondants au filtre de disponibilités de la recherche                   
          TreeMap<AssmatSearch,PointAssmat> assmatPoints = (TreeMap<AssmatSearch,PointAssmat>) session.getAttribute("assmatPoints");        
          GeneratePdf.traitementStructureSet(assmatPoints, baosContent, false, request);
          // Panier
        } else if (type.equals("list")) {
          Set<Publication> mapCommuneEtablissement = (Set<Publication>) request.getSession().getAttribute("listeProfilAMSelection");
          GeneratePdf.traitementStructureSet(mapCommuneEtablissement, baosContent, true);

        } else {
          LOGGER.warn("Structure PH : type de rapport PDF '" + type + "' non prevu.");
        }

        // setting the content type
        response.setContentType("application/pdf");
      }else{
        LOGGER.error("appel de la servelet sans parametre");
        response.setContentType("text/plain ");
        baosContent.write(1);
      }
      // setting some response headers
      response.setHeader("Expires", "0");
      response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
      response.setHeader("Pragma", "public");
      // the contentlength is needed for MSIE!!!
      response.setContentLength(baosContent.size());
      // write ByteArrayOutputStream to the ServletOutputStream
      ServletOutputStream out = response.getOutputStream();
      baosContent.writeTo(out);
      out.flush();

    } catch (Exception e2) {
      LOGGER.error("Erreur lors de la génération PDF", e2);
    }
  }

  /**
   * @see javax.servlet.GenericServlet#destroy()
   */
  public void destroy() {
  }

}