<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="fr.cg44.plugin.assmat.PointAssmat"%>
<%@page import="fr.cg44.plugin.assmat.managers.AssmatSearchDAO"%>
<%@page import="fr.cg44.plugin.assmat.beans.AssmatSearch"%>
<%@ page contentType="text/html; charset=UTF-8"%><%
%><%@ include file='/jcore/doInitPage.jsp'%>
<%@ include file='/jcore/portal/doPortletParams.jsp'  
%><%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%@page import="java.util.TreeSet"%>
<%@page import="java.util.Set"%>
<%@page import="generated.ProfilASSMAT"%>
<%@page import="com.jalios.util.Util"%>
<%
//Set<ProfilASSMAT> listeProfilAM = (Set<ProfilASSMAT>) request.getSession().getAttribute("listeProfilAM");
jcmsContext.addJavaScript("plugins/ToolsPlugin/js/facets/tooltipCategories.js");
jcmsContext.addCSSHeader("plugins/EServicePlugin/css/types/AbstractPortletSkinable/titleBarDottedTitleElementBorderBot.css");
jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/headstall/model1.css");
jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/AbstractPortletSkinable/titleBar.css");
jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/common.css");
jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/FestivalCard/festivalCardQueryDisplay.css");


jcmsContext.addCSSHeader("plugins/AssmatPlugin/css/plugin.css");
jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");
// JSP Custom qui ajoute ou supprime du panier une structure PH
Set<Publication> listeProfil = (Set<Publication>) request.getSession().getAttribute("listeProfilAMSelection");
if(Util.notEmpty(listeProfil)){
PointAssmat pointAssmat = null;
ProfilASSMAT profilAM = null;
ProfilManager profilMgr = ProfilManager.getInstance();

//Set<ProfilASSMAT> listeProfilAM = (Set<ProfilASSMAT>) request.getSession().getAttribute("listeProfilAM");



if(Util.notEmpty(listeProfil)){
  session.setAttribute("urlSelection", ServletUtil.getUrl(request));
  session.setAttribute("isSelection", "true"); 
  %>
  

  
  <div class="containerBandeau">
  <div class="nbSelect">
  <%
  String[] parameters = new String[]{listeProfil.size()+""};
  %>
  <p><trsb:glp key="NB-AM-SELECTIONNEE" parameter="<%= parameters %>" /></p>
  </div>
  <div class="clearSelect">
   <jalios:include jsp='plugins/AssmatPlugin/jsp/recherche/selection/doCleanSelection.jsp' />
  </div>
  
  </div>
  
  <jalios:pager name="assmatPager"  declare='true' pageSize="10"
  action="init" size="<%=listeProfil.size()%>" />
  
  
  <jalios:foreach collection="<%=listeProfil%>" name="itPub" type="Publication"
            max='<%=assmatPager.getPageSize()%>'
            skip='<%=assmatPager.getStart()%>'>
            <%
            profilAM = (ProfilASSMAT) itPub;
  AssmatSearch assmatSearch =Util.getFirst(AssmatSearchDAO.getAssmatSearchByIdMembre(profilAM.getAuthorId()));
  boolean isDomicile= assmatSearch.getIsDomicile();
  pointAssmat = new PointAssmat(String.valueOf(profilAM.getLatitudeAssmat()), String.valueOf(profilAM.getLongitudeAssmat()), channel.getProperty("jcmsplugin.assmatplugin.recherche.am.result.recap.avec.dispo.popup.color"), "",isDomicile);
  if(Util.notEmpty(profilAM)){
    request.setAttribute("assmatSearch", assmatSearch);
    request.setAttribute("point", pointAssmat);
  
    %> 
    <%jcmsContext.setTemplateUsage(TypeTemplateEntry.USAGE_DISPLAY_QUERY);  %>
    <jalios:include pub="<%= profilAM%>"/>
 <% request.removeAttribute("assmatSearch");  
 request.removeAttribute("point");
  } 
    %>        
            
  </jalios:foreach>
  <jalios:pager name="assmatPager"  template="corporate"/>

<jalios:javascript>
jQuery( ".removeAM-selection" ).click(function() {
  location.reload();
});

</jalios:javascript>
<%
}
}else{
  
  
  %>
   <div class="containerBandeau">
  <div class="nbSelect">
 <p><trsb:glp key="SELECTION-AM_VIDE" /></p>
  </div>
  
  
  </div>
  
  
  <%
  
  
  
}
%>
