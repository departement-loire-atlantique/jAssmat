<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
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
 jcmsContext.addCSSHeader("plugins/AssmatPlugin/css/plugin.css"); 
  %>
  
<%@ include file='/types/AbstractCollection/doIncludePortletCollection.jsp'%>
<jalios:include target="OLD_BROWSER"/>
<%= AccessibilityLinks.createAccessibilityBlock(AccessibilityLinks.NAVIGATION_ID,AccessibilityLinks.SEARCH_ID, AccessibilityLinks.CONTENT_ID, AccessibilityLinks.FOOTER_ID)%>
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
      <%@ include file='/plugins/CorporateIdentityPlugin/jsp/headstall/model1.jspf' %>
      <div class="row-fluid">
        <div class="span8 block-spacer" id="<%= AccessibilityLinks.CONTENT_ID%>">
          <%=getPortlets(bufferMap, "left", "")%>
        </div>
        
        
        <%
        String idCategRam = channel.getProperty("plugin.assmatplugin.categ.ram.id");
        String idCategAsso = channel.getProperty("plugin.assmatplugin.categ.asso.id");
        
        Category categRam = channel.getCategory(idCategRam);
        Category categAsso = channel.getCategory(idCategAsso);
        
        boolean isRam = AssmatUtil.isMemberRAM(loggedMember);
        boolean isAsso = AssmatUtil.isMemberASSO(loggedMember);
        
        boolean authorized = false;
        String typeIdentification = "";
        
        // RAM       
        if(currentCategory.equals(categRam) || (Util.notEmpty(categRam.getChildrenSet()) && categRam.getChildrenSet().contains(currentCategory)) ) {
          typeIdentification = "RAM";
        }
        
        if(isRam && currentCategory.equals(categRam)){
          authorized = true;
        }
        if(Util.notEmpty(categRam.getChildrenSet())){
	        if(isRam && categRam.getChildrenSet().contains(currentCategory)){
	          authorized = true;
	        }
        }
        
        if(currentCategory.equals(categAsso) || (Util.notEmpty(categAsso.getChildrenSet()) && categAsso.getChildrenSet().contains(currentCategory)) ) {
          typeIdentification = "ASSO";
        }
    
        // Association Syndicat
        if(isAsso && currentCategory.equals(categAsso)){
          authorized = true;
        }
        if(Util.notEmpty(categAsso.getChildrenSet())){
          if(isAsso && categAsso.getChildrenSet().contains(currentCategory)){
            authorized = true;          
          }
        }
        %>
      
        <%if(authorized){ %>
        <div class="span4  block-spacer" >
         <div class="span12 " style="margin-left: 0px;">
          <%=getPortlet(bufferMap, "profil")%>      
        </div>
            <div class="span12  " style="margin-left: 0px;">
            <%=getPortlet(bufferMap, "interlocuteurs")%>
        </div>
        </div>
        
        <%}else {
          session.setAttribute("identification", typeIdentification);
          sendForbidden(request, response); 
        }
        %>
        
        
        
      </div>
    </div>
  </main>
  <%= getPortlet(bufferMap,"footer") %>
</div>