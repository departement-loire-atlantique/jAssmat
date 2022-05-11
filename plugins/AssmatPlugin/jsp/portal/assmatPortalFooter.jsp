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
		    <div class="navigation">
		     <%= getPortlet(bufferMap,"navigationFooter") %>
		    </div>    
		    <div class="logo">
			 <%= getPortlet(bufferMap,"logoFooter2") %>
		     <%= getPortlet(bufferMap,"logoFooter") %>
		    </div>
		    <%
		    if(showSignature){
		    %><div class="signature hidden-phone"><%
		       %><img src="plugins/EServicePlugin/images/un_service_du_depla.png" alt="<%=glp("cg44.plugin.eserviceplugin.portal.footer.signature-image")%>"/><%
		    %></div><%
		    }
		    %>
		    <div class="socialNetwork<%=liseretOnPhone?" liseret":""%>"><%
		    	%><%= getPortlet(bufferMap,"socialNetwork") %>
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