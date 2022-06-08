<% request.setAttribute("ContentType", "application/json; charset=UTF-8"); 
%><%@page import="fr.cg44.plugin.corporateidentity.tools.ASDUtil"%><%
%><%@page import="com.jalios.jcms.analytics.*"%><%
%><%@ include file="/jcore/doInitPage.jsp" %><%
%><%@ page import="com.jalios.jcms.handler.QueryHandler" %><%
%><%@ page import="fr.cg44.plugin.tools.comparator.NameAutoCompleteComparator" %><%
String txtSearch = getUntrustedStringParameter("text", "");

//0011596: Ignorer les st et ste dans la recherche d'une commune 
if(Util.notEmpty(txtSearch) && ( txtSearch.toLowerCase().startsWith("st ") || txtSearch.toLowerCase().startsWith("st-") || txtSearch.toLowerCase().startsWith("ste ") || txtSearch.toLowerCase().startsWith("ste-")  ) ) {
 txtSearch = txtSearch.replaceAll("(?i)st ", "saint ");
 txtSearch = txtSearch.replaceAll("(?i)st-", "saint-");
 txtSearch = txtSearch.replaceAll("(?i)ste ", "sainte ");
 txtSearch = txtSearch.replaceAll("(?i)ste-", "sainte-");
}

String idCommune = "";
String codeInsee = "";
String cityName = "";
String cityLong = "";
String cityLat = "";

QueryHandler qh = new QueryHandler();
qh.setText(txtSearch);
qh.setSearchedFields(new String[]{com.jalios.jcms.search.LucenePublicationSearchEngine.TITLE_FIELD, "zipCode","codesPostaux"});
qh.setCheckPstatus(true);
qh.setSort("relevance");
qh.setTypes(new String[]{"City"});
qh.setExactType(true);


if(Util.notEmpty(qh.getResultSet())) {
  City city = (City) Util.getFirst(qh.getResultSet());
  idCommune = city.getId();
  codeInsee = city.getCityCode() + "";
  cityName = city.getTitle();
  cityLong = city.getExtraData("extra.City.plugin.tools.geolocation.longitude");
  cityLat = city.getExtraData("extra.City.plugin.tools.geolocation.latitude");
  
}

%>{
    "idCommune":"<%= idCommune %>",
    "codeInsee":"<%= codeInsee %>",
    "cityName":"<%= cityName %>",
    "cityLong":"<%= cityLong %>",
    "cityLat":"<%= cityLat %>"
}