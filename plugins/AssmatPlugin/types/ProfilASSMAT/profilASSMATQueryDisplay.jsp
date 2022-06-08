<%@page import="fr.trsb.cd44.solis.manager.QuartierDAO"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="fr.cg44.plugin.assmat.beans.AssmatSearch"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.PointAssmat"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.cg44.plugin.tools.ToolsUtil"%>
<%@page import="fr.cg44.plugin.tools.modal.ModalGenerator"%>
<%@ taglib prefix="trsb"
  uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%><%
%><%@ page contentType="text/html; charset=UTF-8"%><%
%><%@ include file='/jcore/doInitPage.jsp'%><%

ProfilASSMAT assmat = (ProfilASSMAT) request.getAttribute(PortalManager.PORTAL_PUBLICATION);
SolisManager solisMgr = SolisManager.getInstance();

ProfilASSMAT obj = assmat;
Member mbr = obj.getAuthor(); 

ProfilManager profilMgr = ProfilManager.getInstance();

Date dateModifDispo = profilMgr.getDateModifDispo(mbr);

NumberFormat nf = new DecimalFormat("0.#");
if(Util.notEmpty(assmat.getLatitudeAssmat()) && Util.notEmpty(assmat.getLongitudeAssmat()))

  jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/FestivalCard/festivalCardQueryDisplay.css");
  SimpleDateFormat formater = null;
  formater = new SimpleDateFormat("dd/MM/yyyy");
  
  AssmatSearch assmatSearch = (AssmatSearch)request.getAttribute("assmatSearch");
  PointAssmat pointUser =(PointAssmat) request.getAttribute("userLocation");
  session.setAttribute("userLocation", pointUser);
  PointAssmat pointAssmat =(PointAssmat) request.getAttribute("point");
  
  boolean isDomicile = pointAssmat.isDomicile();
  double distance=-1;
if(Util.notEmpty(pointAssmat) && Util.notEmpty(pointUser)){
   distance= AssmatUtil.getDistance((double)pointAssmat.getLatitude(),(double) pointAssmat.getLongitude(),(double) pointUser.getLatitude(), (double)pointUser.getLongitude()); 
}
if(pointAssmat.getLatitude() == 0 && pointAssmat.getLongitude() == 0) {
  distance = -1;
}
%><%@ include file='/plugins/CorporateIdentityPlugin/jsp/style/getBackgroundStyle.jspf' %><%
%>

<style>
.imgStarGreen{
display:none;
}

</style>

<div class="amcontainer">
 <div class="row-fluid">
<div class="span1"></div>
<div class="span11">
    <div class="header headerAssmatSearch">  
      <h2> 
      <%=mbr.getFullName() %>
      </h2>
     
<!--Mdate -->
      <div class="thematics villePH" <%= backgroundStyle %>><p><%
          
          Date maj = assmat.getMdate();
		      if(dateModifDispo != null && dateModifDispo.after(maj)) {
		        maj = dateModifDispo;
		      }
      
              %>Mise à jour le <%= formater.format(maj) %><%
        %></p></div>
        
        <div class="distance list">
        
          <jalios:if predicate="<%= (Util.notEmpty(pointAssmat) && distance != -1) || (44109 == assmatSearch.getCommune() && (Util.notEmpty(assmatSearch.getIdMicroQuartier()))) %>">
            <img style="vertical-align: top;" src='plugins/AssmatPlugin/img/target-with-circle.png'/>      
          </jalios:if>
          
          <div style="display: inline-block; line-height: normal;">
          
	          <jalios:if predicate='<%= 44109 == assmatSearch.getCommune() && (Util.notEmpty(assmatSearch.getIdMicroQuartier()))%>'>	          
		          <span class="quartier">Quartier :		          	            
                 <%= QuartierDAO.getLibQuartier(QuartierDAO.getIdQuartier(assmatSearch.getIdMicroQuartier())) %>                 
                 <% String libMicroQuartier = QuartierDAO.getLibMicroQuartier(assmatSearch.getIdMicroQuartier()); %>
                 <jalios:if predicate="<%= Util.notEmpty(libMicroQuartier) %>">
                    (<%= libMicroQuartier %>)
                 </jalios:if>                         		                      		                     
		          </span>
	          </jalios:if>
        
		        <%if(Util.notEmpty(pointAssmat) && distance != -1){ %>
		        
		          <jalios:if predicate='<%= Util.notEmpty(request.getParameter("adresse")) %>'>        
		            <div class="">à <%=nf.format(distance) %> km de l'adresse recherchée</div>
		          </jalios:if>
		          
		          <jalios:if predicate='<%= Util.isEmpty(request.getParameter("adresse")) %>'>
		             <div class="">à <%=nf.format(distance) %> km de votre localisation</div>
		          </jalios:if>
		            
		        <%} %>
         </div>
        </div>
        
  </div>  
  </div>
  </div>
  <div class="row-fluid content infoAM">
   <div class="span1"  ></div>
   
   

    <div class="infoPH span5"  >
       
<!--    Adresse -->

<jalios:cache timeout='1440' id='<%= "query_map_assmat_"+obj.getRowId()+"_"+isDomicile %>'>
    <%
    AssmatSolis assmatSolis = Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(assmat.getNum_agrement()));
    %>
    <jalios:if predicate="<%= Util.notEmpty(assmatSolis) %>">
<jalios:if predicate="<%= assmatSolis.getExerceDomicile() != null && assmatSolis.getExerceDomicile() && isDomicile %>">
    
    <jalios:if predicate="<%= Util.notEmpty(assmatSolis.getAdresseDomicile()) %>">
         <div>
            <%= assmatSolis.getAdresseDomicile() %>
         </div>
         <jalios:if predicate="<%= Util.notEmpty(assmatSolis.getComplementAdresse()) %>">
	         <div>
	            <%= assmatSolis.getComplementAdresse() %>
	         </div>
         </jalios:if>
    </jalios:if>
     
    
     
<!--    Code postale + city -->
  
    <jalios:if predicate="<%= Util.notEmpty(assmatSolis.getCpDomicile()) %>"><%
          %><div><p><%
                %><%= assmatSolis.getCpDomicile() %> <%=assmatSolis.getCommuneDomicile() %></p>
          </div><%
     %></jalios:if>   
   </jalios:if>
   
   
   
   <jalios:if predicate="<%= assmatSolis.getExerceMam() != null && assmatSolis.getExerceMam() && !isDomicile %>">
    <jalios:if predicate="<%= Util.notEmpty(assmatSolis.getNomMam()) %>"><%
          %><div><%
                %><%= assmatSolis.getNomMam() %>
          </div><%
     %></jalios:if>
        
    <jalios:if predicate="<%= Util.notEmpty(assmatSolis.getAdresseMam()) %>"><%
          %><div><%
                %><%= assmatSolis.getAdresseMam() %>
          </div><%
     %></jalios:if>
     
    
     
<!--    Code postale + city -->
  
    <jalios:if predicate="<%= Util.notEmpty(assmatSolis.getCpMam()) %>"><%
          %><div><p><%
                %><%= assmatSolis.getCpMam() %> <%=assmatSolis.getCommuneMam() %></p>
          </div><%
     %></jalios:if>   
   </jalios:if>
</jalios:if>   
</jalios:cache>

    
    <%    
    Boolean hasDispo = profilMgr.hasDispo(assmat.getAuthor());
    // Si l'assmat a des dispo ou accepte d'etre contacter meme sans dispo
    Boolean showContactDispo = !assmat.getAfficherContactUniquementSiD() || hasDispo;
    %>

      <jalios:if predicate="<%= showContactDispo  %>">
    
						<!--  tel fixe -->
						<%if(AssmatUtil.getBooleanFromString(assmat.getVisbiliteTelephoneFixe())){ %>
						    <jalios:if predicate="<%= Util.notEmpty(assmat.getTelephoneFixe()) %>"><%
						          %><div><%
						                %><b>Tél fixe :</b> <%=assmat.getTelephoneFixe() %>
						          </div><%
						     %></jalios:if>   
						          <%} %>
						<!--  Tel mobile -->
						
						<jalios:if predicate="<%= Util.notEmpty(assmat.getAuthor()) %>">
						<%if(AssmatUtil.getBooleanFromString(assmat.getVisibiliteTelephonePortable())){ %>
						    <jalios:if predicate="<%= Util.notEmpty(assmat.getAuthor().getMobile()) %>"><%
						          %><div><b>Tél portable:</b> <%=assmat.getAuthor().getMobile() %>  </div><%
						     %></jalios:if>  
						      <%} %>
						<!--  Courriel -->
						<%if(AssmatUtil.getBooleanFromString(assmat.getVisibiliteAdresseEmail())){ %>
						<jalios:if predicate="<%= Util.notEmpty(assmat.getAuthor().getEmail()) %>"><%
						Category cat = channel.getCategory(channel.getProperty("jcmsplugin.assmatplugin.formulaire.contact.am"));
						if(Util.notEmpty(cat)){
						      String lien = cat.getDisplayUrl(userLocale) + "?idMAM=" + assmat.getAuthor().getId();
						    
						          %>
						
						          
						    <div class="courrielPH"><%
						                %><b> <u><a href="<%=lien %>">Courriel</a></u></b>
						          </div><%}
						     %></jalios:if>  
						      <%} %>  
						</jalios:if>  
						      
					</jalios:if>	      
						      
     <br> <p><%=pointAssmat.getInfoPoint() %></p> 
      <%if(!pointAssmat.isDomicile()){ %>
              <p class="typeExercice mam"><trsb:glp key="PROFIL-ASSMAT-CONTENT-COORD-EXERCE-MAM" /></p>
              <%} %>
      
	      <div class="wysiwyg classic lienEnsavoirPlus">
	 <ul>
	 <% String idPortalRecherche = channel.getProperty("jcmsplugin.assmatplugin.portal.recherche.am"); %>
	 
	  <li><a class="bold" href="<%=assmat.getDisplayUrl(userLocale)%>?portal=<%=idPortalRecherche%>">En savoir plus</a></li>
	 </ul>
	</div>
      
<!--  Site internet -->


       </div>
      
      
       <jalios:if predicate="<%= Util.notEmpty(assmat.getLatitudeAssmat()) && Util.notEmpty(assmat.getLongitudeAssmat()) %>">
       <div class="span6">
       
       
       
        <% request.setAttribute("dataId", assmat.getId()); %>
       
        <jalios:include jsp='plugins/AssmatPlugin/jsp/recherche/selection/doProcessSelection.jsp' />
   
          <jalios:if predicate="<%= pointAssmat.getLatitude() != 0 && pointAssmat.getLongitude() != 0 %>">
			       <div class="carto">
			       
			       
			       
			                    <% request.setAttribute("gMapsType", "small"); %>
			                    <%-- include file='/plugins/AssmatPlugin/jsp/recherche/map/mapAssmatDisplay.jsp'--%>
			                  <% request.removeAttribute("gMapsType"); %>
			                  <% request.removeAttribute("listPoint"); %>
			      
			           </div>
			     </jalios:if>
       </div>
       </jalios:if>
</div>
</div>