<%@ page contentType="text/html; charset=UTF-8" 
%><%@ page import="fr.cg44.plugin.tools.AccessibilityLinks"  
%><%@ include file='/jcore/doInitPage.jsp' 
%><%@ include file='/jcore/portal/doPortletParams.jsp'  
%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<% 
PortletJspCollection box = (PortletJspCollection) portlet;
  ServletUtil.backupAttribute(pageContext , "ShowChildPortalElement");
//Drag and Drop required a custom class wrapper 'dnd-container' and the wrapper ID. They also REQUIRED a DOM ID
 String dndCSS = (isLogged && loggedMember.canWorkOn(box)) ? "ID_"+box.getId()+" dnd-container" : ""; 
 jcmsContext.addCSSHeader("plugins/EServicePlugin/css/portal/portal.css"); 
 jcmsContext.addCSSHeader("plugins/EServicePlugin/css/types/AbstractPortletSkinable/titleBarDottedTitleElementBorderBot.css");
 jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/headstall/model1.css");
 jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/AbstractPortletSkinable/titleBar.css");
 jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/common.css");
 
 jcmsContext.addCSSHeader("plugins/AssmatPlugin/css/plugin.css");
 %>
  
<%@ include file='/types/AbstractCollection/doIncludePortletCollection.jsp'%>
<jalios:include target="OLD_BROWSER"/>
<%= AccessibilityLinks.createAccessibilityBlock(AccessibilityLinks.NAVIGATION_ID,AccessibilityLinks.SEARCH_ID, AccessibilityLinks.CONTENT_ID,AccessibilityLinks.FOOTER_ID)%>
<div class="portail">
  

  <main id="main_content" tabindex="-1"> 
  <div class="container">
    <% String alertContent=getPortlet(bufferMap, "alert");
    if(Util.notEmpty(alertContent)){
    %>
      <div class="alerts-container"><%-- PQF des alertes--%>
        <%=alertContent%>
      </div><%
      }%>

      <div class="row-fluid">
        <div class="span12 PortletJsp" id="<%= AccessibilityLinks.CONTENT_ID%>">
          <div class="headstall container-fluid accueil">
           <div class="row-fluid title">
            <div class="span12 label">
            <h1><trsb:glp key="assmat.lbl.bienvenue.accueil"></trsb:glp></h1>
            </div>
          </div>
          <div class="row-fluid containerContent">
           <!-- COLONNE GAUCHE -->
            <div class="span5 offset1 description">
        
<!--              introduction -->
              <%=getPortlets(bufferMap, "introduction", "")%>
              
<!--              prevenir par mail/sms -->
              <%=getPortlets(bufferMap, "prevenir", "")%>
        
            </div>
           <!-- FIN COLONNE GAUCHE -->
          
           <!-- COLONNE DROITE -->
           <div class="span5 offset1">
             <%=getPortlets(bufferMap, "content", "")%>
          </div>
           <!-- FIN COLONNE DROITE -->
           </div>
          </div>
        </div>
      </div>
    </div>
  </main>

</div>