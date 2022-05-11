<%@ page contentType="text/html; charset=UTF-8" 
%><%@ page import="fr.cg44.plugin.tools.AccessibilityLinks"  
%><%@ include file='/jcore/doInitPage.jsp' 
%><%@ include file='/jcore/portal/doPortletParams.jsp'  
%><% 
	PortalJspCollection box = (PortalJspCollection) portlet;
  ServletUtil.backupAttribute(pageContext , "ShowChildPortalElement");
//Drag and Drop required a custom class wrapper 'dnd-container' and the wrapper ID. They also REQUIRED a DOM ID
 String dndCSS = (isLogged && loggedMember.canWorkOn(box)) ? "ID_"+box.getId()+" dnd-container" : ""; 
 jcmsContext.addCSSHeader("plugins/EServicePlugin/css/portal/portal.css");
 
 jcmsContext.addJavaScript("plugins/ToolsPlugin/js/facets/tooltipCategories.js");
   jcmsContext.addCSSHeader("plugins/EServicePlugin/css/types/AbstractPortletSkinable/titleBarDottedTitleElementBorderBot.css");
   jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/headstall/model1.css");
   jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/AbstractPortletSkinable/titleBar.css");
   jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/common.css");
   
   jcmsContext.addCSSHeader("plugins/AssmatPlugin/css/plugin.css");
   jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");
   String idRelais = channel.getProperty("$jcmsplugin.assmatplugin.portlet.relais.id");
   
   String idPortalSearch = channel.getProperty("jcmsplugin.assmatplugin.portal.recherche.am");
   Publication portalSearch = channel.getPublication(idPortalSearch);
  %>
  
<%@ include file='/types/AbstractCollection/doIncludePortletCollection.jsp'%>
<jalios:include target="OLD_BROWSER"/>
<%= AccessibilityLinks.createAccessibilityBlock(AccessibilityLinks.NAVIGATION_ID,AccessibilityLinks.SEARCH_ID, AccessibilityLinks.CONTENT_ID, AccessibilityLinks.FOOTER_ID)%>
<%= getPortlet(bufferMap,"header") %>
<%-- <%= getPortlet(bufferMap,"menu") %> --%>
<div class="container festival-card-box-display-container" style="border-bottom: none; padding: 0;">




<% String alertContent=getPortlet(bufferMap, "alert");
if(Util.notEmpty(alertContent)){ %>
	<div class="alerts-container"><%-- PQF des alertes--%>
		<%=alertContent%>
	</div>
<% } %>
<div class="mainContent contentRecherche">
<div class="block-spacer">
  <%=getPortlet(bufferMap, "breadcrumb")%>
</div>
<%
 
 String urlRetour =(String) session.getAttribute("urlRecherche");
 if(Util.isEmpty(urlRetour)){
 urlRetour = portalSearch.getDisplayUrl(userLocale);
 }
 
 %> 
<div class="row-fluid bandeauRetourSearch">
          <div class="span12">
            <ul class="btnNavHz">
            
              <li><a href="<%=urlRetour %>" class="btnNoirType btnRetourRecherche">
            
              <trsb:glp  key="PROFIL-ASSMAT-LIBELLE-RETOUR-RECHERCHE-HTML" >
              Retour à la recherche
              </trsb:glp>
        
               </a></li>
             
            </ul>
          </div>
        </div>

<%@ include file='/plugins/AssmatPlugin/jsp/recherche/header/bandeauSelection.jspf' %>


        
	<div class="row-fluid">
		<div class="span8" id="<%= AccessibilityLinks.CONTENT_ID%>">
			<%=getPortlets(bufferMap, "left", "")%>
		</div>
    <div class="span4 rightContent">
			<jalios:include id='<%=idRelais %>' />
		</div>
	</div>
</div>

</div>

<div>
<%= getPortlet(bufferMap,"footer") %>
</div>
