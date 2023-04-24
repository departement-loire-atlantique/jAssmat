<%@ page contentType="text/html; charset=UTF-8" %><%
%><%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%><%
%><%@ include file='/jcore/doInitPage.jspf' %><%@ include file='/jcore/portal/doPortletParams.jspf'  %><% 
  PortalJspCollection box = (PortalJspCollection) portlet;
  ServletUtil.backupAttribute(pageContext , "ShowChildPortalElement");
%><%@ include file='/types/AbstractCollection/doIncludePortletCollection.jspf'%>
<% 
ServletUtil.restoreAttribute(pageContext , "ShowChildPortalElement");

//Drag and Drop required a custom class wrapper 'dnd-container' and the wrapper ID. They also REQUIRED a DOM ID
String dndCSS = (isLogged && loggedMember.canWorkOn(box)) ? "ID_"+box.getId()+" dnd-container" : "";

// Retrieve Portlet's buffer for PortletSelection
String selection = getPortlet(bufferMap,"selection");
%>

<%= getPortlet(bufferMap,"header") %>

<main role="main" id="content">

<jalios:include target="SOCLE_ALERTE"/>

    <section class="ds44-container-large">
    
        <ds:titleNoImage title="<%= Util.notEmpty(box.getPageTitle(userLang)) ? box.getPageTitle(userLang) : box.getTitle(userLang) %>" breadcrumb="true"></ds:titleNoImage>
        
        <div class="ds44-inner-container">
            <div class="grid-12-small-1">
                <div class="col-7 ds44-mt2">
                    <%= getPortlet(bufferMap,"left") %>
                </div>
                
                <%-- Colonne de droite (affichée systématiquement même si portlets vides) --%>
                <div class="col-1 grid-offset"></div>
                <aside class="col-4 asideCards ds44-mt2">
                    <jalios:if predicate='<%= Util.notEmpty(getPortlets(bufferMap, "right")) %>'>
                        <jalios:foreach name="itBuffer" array='<%= getPortlets(bufferMap, "right") %>' type="String">
                        
                            <%= itBuffer %>
                             
                            <jalios:if predicate='<%= itCounter < getPortlets(bufferMap, "right").length %>'>
                                <div class="ds44-mb3"></div>
                            </jalios:if>
                            
                        </jalios:foreach>
                    </jalios:if>
                </aside>
            </div>
        </div>
    </section>
    
    <footer role="contentinfo">
	    <%= getPortlet(bufferMap,"footer") %>
	    <%= getPortlet(bufferMap,"footerNavigation") %>
	    <p id="backToTop" class="ds44-posRi ds44-hide-mobile ds44-btn-fixed ds44-js-button-sticky" data-is-delayed="true">
	        <a class="ds44-icoLink ds44-icoLink--footer" href="#top"><i class="icon icon-arrow-up icon--sizeXL" aria-hidden="true"></i><span class="ds44-icoTxtWrapper"><%= glp("jcmsplugin.socle.hautDepage")%></span></a>
	    </p>
	</footer>

</main>