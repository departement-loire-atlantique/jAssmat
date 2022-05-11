<% request.setAttribute("ContentType", "application/json; charset=UTF-8"); 
%><%@page import="fr.cg44.plugin.corporateidentity.tools.ASDUtil"%><%
%><%@page import="com.jalios.jcms.analytics.*"%><%
%><%@ include file="/jcore/doInitPage.jsp" %><%
%><%@ page import="com.jalios.jcms.handler.QueryHandler" %><%
%><%@ page import="fr.cg44.plugin.tools.comparator.NameAutoCompleteComparator" %><%
String txtSearch = getUntrustedStringParameter("text", "");
String citySearch = getUntrustedStringParameter("cityCode", "");

logger.warn("adresse : "+txtSearch);
logger.warn("ville : "+v);


String adresse = "toto";
String idCommune = "";
String codeInsee = "";
String cityName = "";

/*
QueryHandler qh = new QueryHandler();
qh.setText(txtSearch);
qh.setSearchedFields(new String[]{com.jalios.jcms.search.LucenePublicationSearchEngine.TITLE_FIELD});
qh.setCheckPstatus(true);
qh.setSort("relevance");
qh.setTypes(new String[]{"City"});
qh.setExactType(true);


if(Util.notEmpty(qh.getResultSet())) {
  City city = (City) Util.getFirst(qh.getResultSet());
  idCommune = city.getTitle();
  codeInsee = city.getCityCode() + "";
  cityName = city.getTitle();
}
*/
%>{
    "adresse":"<%= adresse %>",
}