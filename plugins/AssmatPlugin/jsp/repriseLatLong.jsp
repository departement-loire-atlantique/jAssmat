<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@ include file='/jcore/doInitPage.jsp' %>

<%
if(!isAdmin) {
  sendForbidden(request, response);
  return;
}
%>

<%
logger.info("Début de la reprise lat lont");
Set<ProfilASSMAT> setProfil = channel.getAllDataSet(ProfilASSMAT.class);
for(ProfilASSMAT itProfil : setProfil){
  ProfilManager.getInstance().UpdateLontLatProfilAssmat(itProfil);
}
logger.info("Fin de la reprise lat lont");
%>