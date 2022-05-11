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
  %>
  
<%@ include file='/types/AbstractCollection/doIncludePortletCollection.jsp'%>
<jalios:include target="OLD_BROWSER"/>
<%= AccessibilityLinks.createAccessibilityBlock(AccessibilityLinks.NAVIGATION_ID,AccessibilityLinks.SEARCH_ID, AccessibilityLinks.CONTENT_ID, AccessibilityLinks.FOOTER_ID)%>
<%= getPortlet(bufferMap,"header") %>
<%= getPortlet(bufferMap,"menu") %>


<div class="portail portail-accueil-editorial">
<div class="row-fluid">
        <div class="span6 block-spacer">
          <%= getPortlets(bufferMap, "parents", "")%>
        </div>
        <div class="span6 block-spacer">
          <%= getPortlets(bufferMap, "assistantes", "")%>
        </div>
      </div>
      
      <div class="row-fluid">
        <div class="span4 block-spacer">
          <%= getPortlets(bufferMap, "agenda", "")%>
        </div>
        <div class="span8 block-spacer">
          <%= getPortlets(bufferMap, "zoom", "")%>
        </div>
     </div>  </div>