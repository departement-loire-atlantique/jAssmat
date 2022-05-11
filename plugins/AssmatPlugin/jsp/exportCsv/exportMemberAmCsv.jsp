<%@page import="fr.cg44.plugin.assmat.util.ExportAssmat"%><%
%><%@ page contentType="text/html; charset=UTF-8" %><%
  response.setHeader("Content-Disposition", "attachment; filename=profilAM.csv"); 
  request.setAttribute("ContentType", "text/csv; charset=" + channel.getProperty("csv.charset"));
%><%@ include file='/jcore/doInitPage.jsp' %><%

if( !loggedMember.isWorkAdmin() ){
  sendForbidden(request, response);
  return;
}
%><%

Set<ProfilASSMAT> profilSet = channel.getAllPublicationSet(ProfilASSMAT.class, channel.getDefaultAdmin());
out.clear();

ExportAssmat.exportCsvAM(profilSet, out);

%>