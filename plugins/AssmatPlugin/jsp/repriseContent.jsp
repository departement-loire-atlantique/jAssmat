<%@page import="fr.cg44.plugin.assmat.datacontroller.DispoASSMATDataController"%>
<%@page import="fr.cg44.plugin.assmat.datacontroller.ProfilASSMATDataController"%>
<%@ include file='/jcore/doInitPage.jsp' %>

<%
if(!isAdmin) {
  sendForbidden(request, response);
  return;
}
%>

<%
logger.info("Début de la reprise content");
Set<ProfilASSMAT> setProfil = channel.getAllDataSet(ProfilASSMAT.class);
for(ProfilASSMAT profil : setProfil){
	ProfilASSMATDataController controller = new ProfilASSMATDataController();
	controller.createAssmatSearch(profil);
}

Set<Disponibilite> setDispo = channel.getAllDataSet(Disponibilite.class);
for(Disponibilite dispo : setDispo){
	DispoASSMATDataController controller = new DispoASSMATDataController();
    controller.createDispoSearch(dispo);
}
logger.info("Fin de la reprise content");
%>