<%@ page contentType="text/html; charset=UTF-8"%> 
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ include file='/jcore/portal/doPortletParams.jsp'%>
<% 
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

<div class="portail">


<%= getPortlet(bufferMap,"header") %>

	<main id="main_content" tabindex="-1"> 
		<div class="container">
			<% String alertContent=getPortlet(bufferMap, "alert");
			if(Util.notEmpty(alertContent)){
			%>
			<div class="alerts-container"><%-- PQF des alertes--%>
				<%=alertContent%>
			</div><%
			}%>
			<div class="block-spacer">
				<%=getPortlet(bufferMap, "breadcrumb")%>
			</div>
		
			<div class="row-fluid"><%
				String portletsRight=getPortlets(bufferMap, "right", "");
			
				%>
				<div class="span4 block-spacer">
					<%=getPortlets(bufferMap, "left", "")%>
				</div><%
				if(Util.notEmpty(portletsRight)){
	     		%>
	      <div class="span8 block-spacer rightContent">
					<%=portletsRight%>
				</div><%
				}%>
			</div>
		</div>
	</main>
	<%= getPortlet(bufferMap,"footer") %>
</div>