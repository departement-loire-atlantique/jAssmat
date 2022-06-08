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
  %>
  
<%@ include file='/types/AbstractCollection/doIncludePortletCollection.jsp'%>
<jalios:include target="OLD_BROWSER"/>
<%= AccessibilityLinks.createAccessibilityBlock(AccessibilityLinks.NAVIGATION_ID,AccessibilityLinks.SEARCH_ID, AccessibilityLinks.CONTENT_ID, AccessibilityLinks.FOOTER_ID)%>


<%= getPortlet(bufferMap,"header") %>

<div class="container">

	<div class="">
		<div class="block-spacer">
			<%=getPortlet(bufferMap, "breadcrumb")%>
		</div>
	</div>
	<% String alertContent=getPortlet(bufferMap, "alert");
	if(Util.notEmpty(alertContent)){ %>
		<div class="alerts-container"><%-- PQF des alertes--%>
			<%=alertContent%>
		</div>
	<% } %>
	
	<div class="bandeauAM">
	 <%@ include file='/plugins/CorporateIdentityPlugin/jsp/headstall/model1.jspf' %>
	</div>
	
	<div class="">
		<div class="row-fluid">
			<div class="span3" id="<%= AccessibilityLinks.CONTENT_ID%>">
				<%=getPortlets(bufferMap, "left", "")%>
			</div>
	     		<div class="span9 rightContent">
				<%=getPortlets(bufferMap, "right", "")%>
			</div>
		</div>
	</div>

</div>

<%= getPortlet(bufferMap,"footer") %>