<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file='/jcore/doInitPage.jsp' 
%><%@ page import="fr.cg44.plugin.tools.AccessibilityLinks"  
%><%@ include file='/jcore/portal/doPortletParams.jsp'  
%><%@ include file='/types/AbstractCollection/doIncludePortletCollection.jsp'
%><% 
	jcmsContext.addCSSHeader("plugins/EServicePlugin/css/portal/commonPortalFooter.css");
boolean printView = (request.getAttribute("printView") != null) || (hasParameter("printView")); 
if(!printView){
	boolean liseretOnPhone="true".equals(channel.getProperty("cg44.plugin.eserviceplugin.portal.footer..liseret.show-on-phone"));
	boolean showSignature="true".equals(channel.getProperty("cg44.plugin.eserviceplugin.portal.footer.signature-image.show"));
%>	
<footer>
  <div class="container">
	  <div class="row">
	  	<div class="span12" id="<%=AccessibilityLinks.FOOTER_ID%>" tabindex="-1">
			  
			    <div class="logo">
				 <%= getPortlet(bufferMap,"logoFooter2") %>
			     <%= getPortlet(bufferMap,"logoFooter") %>
			    </div>
			    <div class="texte">
			    <%
			    String idFormu = channel.getProperty("jcmsplugin.assmatplugin.formulaire.contact.assmat");
			    %>
			    	<span class="texte1"><%= glp("jcmsplugin.assmatplugin.socle.footer.msg.1") %></span> <%= glp("jcmsplugin.assmatplugin.socle.footer.msg.2", channel.getPublication(idFormu).getDisplayUrl(userLocale),channel.getProperty("jcmsplugin.assmatplugin.telephone-support") ) %>
			    </div>
		    <div class="liseret"></div><%
		    %>
		  </div>
	  </div>
	  <div class="row">
	  	<div class="span12">    
		    <%= getPortlet(bufferMap,"planSite") %>
	    </div>
	  </div>
  </div>
</footer><%
}else{%>
	<footer>
		<div class="container">
			<div class="row spacer">
				<div class="span12 "><%
					Publication publication = (Publication) request.getAttribute("publication");
					String targetUrl; 
					if(publication!=null && !portal.equals(publication)){
			  			targetUrl = JcmsUtil.getDisplayUrl(publication, userLocale);
			  		}else{
						targetUrl = JcmsUtil.getDisplayUrl(currentCategory, userLocale);
				  	}
				%>
				<%= channel.getUrl()+targetUrl
				%></div>
			</div>	
		</div>
	</footer>
<%}%>