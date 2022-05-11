<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.PointAssmat"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.cg44.plugin.tools.modal.ModalGenerator"%><%
%><%@ include file='/jcore/doInitPage.jsp' %><%
%><% ProfilASSMAT obj = (ProfilASSMAT) request.getAttribute(PortalManager.PORTAL_PUBLICATION); 
%>
<%
Member mbr = obj.getAuthor();
SolisManager solisMgr=  SolisManager.getInstance();
PointAssmat point=(PointAssmat) request.getAttribute("pointAssmat");
%><%
%><% 
SimpleDateFormat formater = null;

Date aujourdhui = new Date();

formater = new SimpleDateFormat("dd/MM/yyyy");

jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/infoWindowGoogleMaps.css");
jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");%><%

if(Util.notEmpty(obj) && Util.notEmpty(point) && Util.notEmpty(mbr)){ 

    %>
    
    <%
    ProfilManager profilMgr = ProfilManager.getInstance();
    Boolean hasDispo = profilMgr.hasDispo(obj.getAuthor()); 
    Boolean showContactDispo = !obj.getAfficherContactUniquementSiD() || hasDispo;
    Date dateModifDispo = profilMgr.getDateModifDispo(obj.getAuthor());
    %>
    
    <h2><a href="<%=obj.getDisplayUrl(userLocale)%>"><%=mbr.getFullName() %></a></h2>

    <jalios:cache timeout='1440' id='<%= "info_window_map_"+obj.getRowId()+"_"+point.isDomicile() %>'>
        <%  AssmatSolis assmat = Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(obj.getNum_agrement()));    %>
        <jalios:if predicate="<%= Util.notEmpty(assmat) %>">
	        <jalios:if predicate="<%= point.isDomicile() %>">
	            <p><%=assmat.getAdresseDomicile() %> 
	              <jalios:if predicate="<%= Util.notEmpty(assmat.getComplementAdresse()) %>">
	                   <br /><%= assmat.getComplementAdresse() %>
	               </jalios:if>
	            </p>
	            <p><%=assmat.getCpDomicile() %> <%=assmat.getCommuneDomicile() %> </p>
	       </jalios:if>
	       
	       <jalios:if predicate="<%= !point.isDomicile() %>">
	            <jalios:if predicate="<%= Util.notEmpty(assmat.getNomMam()) %>">
	                <p><%=assmat.getNomMam() %> </p>
	            </jalios:if>
	            
	            <jalios:if predicate="<%= Util.notEmpty(assmat.getAdresseMam()) %>">
	                <p><%=assmat.getAdresseMam() %> </p>
	            </jalios:if>
	            
	
	           <jalios:if predicate="<%= Util.notEmpty(assmat.getCpMam()) %>">
	                <p><%= assmat.getCpMam() %> <%=assmat.getCommuneMam() %></p>
	            </jalios:if>
	       </jalios:if>
       </jalios:if>
    </jalios:cache>
    
              <jalios:if predicate="<%= showContactDispo  %>">
    
					           <%if("true".equals(obj.getVisbiliteTelephoneFixe()) && Util.notEmpty(obj.getTelephoneFixe()) ){ %>
					            <p>Tel fixe : <%=obj.getTelephoneFixe() %> </p>
					           <%} %>
					           
					           <%if("true".equals(obj.getVisibiliteTelephonePortable()) &&Util.notEmpty(mbr.getMobile()) ){ %>
					            <p>Tel mobile : <%=mbr.getMobile() %> </p>
					           <%} %>
					         
					            <!--  Courriel -->
					            <%if(AssmatUtil.getBooleanFromString(obj.getVisibiliteAdresseEmail())){ %>
					              <jalios:if predicate="<%= Util.notEmpty(mbr.getEmail()) %>"><%
					                  Category cat = channel.getCategory(channel.getProperty("jcmsplugin.assmatplugin.formulaire.contact.am"));
					                  if(Util.notEmpty(cat)){
					                    String lien = cat.getDisplayUrl(userLocale) + "?idMAM=" + mbr.getId();
					                    %>                    
					                          
					                     <div class="courrielPH"><%
					                        %><b> <u><a href="<%= lien %>">Courriel</a></u></b>
					                     </div>
					                     
					                   <%}%>
					              </jalios:if>  
					            <%} %> 				            					            
					      </jalios:if>
         
       
       
       <p style="margin-top: 5px;"><%=point.getInfoPoint() %></p>
       
       <div class="link bold">
          <a style="padding-left: 0px;" href="<%=obj.getDisplayUrl(userLocale)%>"><img src="plugins/ToolsPlugin/images/bullet.jpg" style="margin-right: 10px;" />En savoir plus</a>
       </div>
       
       
       <%
        Date maj = obj.getMdate();
        if(dateModifDispo != null && dateModifDispo.after(maj)) {
          maj = dateModifDispo;
        }
       %>
       
       <p>Mise à jour : <%= formater.format(maj) %></p>
       
        <% request.setAttribute("dataId", obj.getId()); %>
       
       
      
        <jalios:include jsp='plugins/AssmatPlugin/jsp/recherche/selection/doProcessSelectionPopUp.jsp' />
  

       
    <%}%>