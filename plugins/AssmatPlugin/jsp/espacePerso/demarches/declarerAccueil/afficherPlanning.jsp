<%@page import="fr.cg44.plugin.assmat.util.DemarcheUtil"%>
<%@page import="com.jalios.io.IOUtil"%>
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
    ProfilASSMAT profilAM = ProfilManager.getInstance().getProfilASSMAT(loggedMember);
    if (profilAM == null) {
      return;
    }


    URL url = new URL(Channel.getChannel().getProperty("plugin.assmatplugin.swagger.demarches.url") + "/assmats/" + profilAM.getNum_agrement() + "/planning/pdf");
    HttpURLConnection connection = IOUtil.openConnection(url, false, true, "GET");
    InputStream input = IOUtil.getInputStream(connection);
    
    response.setHeader("Content-Disposition", "attachment; filename=planning_"+ profilAM.getNum_agrement() + ".pdf"); 
    out.clearBuffer();
    out.clear();
    
    int i;   
    while ((i=input.read()) >= 0) {
     out.write(i);   
    }                  
    input.close();
    
  %>


