<%@page import="fr.cg44.plugin.assmat.util.DemarcheUtil"%>
<%@page import="com.jalios.io.IOUtil"%>
<%@page import="io.swagger.client.api.JasperControllerApi"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="io.swagger.client.model.DeclarationAccueilDTO"%>
<%@page import="io.swagger.client.ApiException"%>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ page contentType="application/pdf; charset=UTF-8"%>

<%
  response.reset();   
  response.setContentType("application/octet-stream");
%>
  
<%
    Integer idDeclaration = Integer.parseInt(request.getParameter("idDeclaration"));

    ProfilASSMAT profilAM = ProfilManager.getInstance().getProfilASSMAT(loggedMember);
    if (profilAM == null) {
      return;
    }

    // Manager du web service   
    JasperControllerApi jasperControllerApi = AssmatUtil.getJasperControllerApi();

    try {
      // Récupère la déclaration
      DeclarationAccueilDTO declaration = DemarcheUtil.getDeclarationAccueilById(idDeclaration);

      // Vérifie qu'il s'agit bien d'une déclaration de cette assmat
      if (declaration != null && declaration.getNumDossier() == profilAM.getNum_agrement()) {

        // Web service KO
        //List<byte[]> bytePdf = jasperControllerApi.getPdfDeclarationUsingGET(idDeclaration);

        URL url = new URL(Channel.getChannel().getProperty("plugin.assmatplugin.swagger.demarches.url") + "/declarations/" + declaration.getIdDeclaration() + "/pdf");
        HttpURLConnection connection = IOUtil.openConnection(url, false, true, "GET");
        InputStream input = IOUtil.getInputStream(connection);
        
        response.setHeader("Content-Disposition", "attachment; filename=declaration_"+ declaration.getIdDeclaration() + ".pdf"); 
        out.clearBuffer();
        out.clear();
        
        int i;   
	      while ((i=input.read()) >= 0) {
	        out.write(i);   
	      }  
        
        
        input.close();
        

      }

    } catch (ApiException e) {
      logger.error(e);
    }
  %>


