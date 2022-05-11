<%@page import="fr.cg44.plugin.assmat.util.DemarcheUtil"%>
<%@page import="io.swagger.client.ApiException"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.cg44.plugin.assmat.selector.RelaisMamSelectorIDSolis"%>
<%@page import="fr.cg44.plugin.assmat.comparator.DeclarationAccueilDateModifComparator"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.administrabletexteplugin.tag.TrsbGlp"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="io.swagger.client.model.AccueilDTO"%>
<%@page import="io.swagger.client.model.DeclarationAccueilDTO"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ include file='/jcore/portal/doPortletParams.jsp' %>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>

<%
PortletJsp box = (PortletJsp) portlet;
%>
<%
ProfilManager profilMngr = ProfilManager.getInstance();
ProfilASSMAT profil = (ProfilASSMAT) profilMngr.getProfilASSMAT(channel.getCurrentLoggedMember());

if(Util.isEmpty(profil)){
  sendForbidden(request, response);
  return;
}

SolisManager solisMgr = SolisManager.getInstance();
AssmatSolis assmatSolis = Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(profil.getNum_agrement()));

// 0011633: Déclaration d'accueil : activation du pilote 
Boolean isGroupAuthorized = false;

String groupAuthorizedId = channel.getProperty("plugin.assmatplugin.declaration.pilote.group.id");
if(Util.notEmpty(groupAuthorizedId)) {
  Group groupAuthorized = channel.getGroup(groupAuthorizedId) ;
  if(groupAuthorized != null && loggedMember.belongsToGroup(groupAuthorized) ) {
    isGroupAuthorized = true;
  }
}

%>



<%

// Lien pour la déclaration d'accueil (terminer une déclaration ou modifier une déclaration)
Publication declarationAccueilPortlet = channel.getPublication(channel.getProperty("plugin.assmatplugin.portlet.declararer-accueil.id"));
String lienDeclarationAccueil = "";
if(declarationAccueilPortlet != null) {
  lienDeclarationAccueil = declarationAccueilPortlet.getDisplayUrl(userLocale);
}

Publication AccueilEspacePersoPortlet = channel.getPublication(channel.getProperty("jcmsplugin.assmatplugin.socle.portail.param.id"));
String lienRetourModal = AccueilEspacePersoPortlet.getDisplayUrl(userLocale);

//Lien pour la déclaration de fin d'accueil
Publication declarationFinAccueilPortlet = channel.getPublication(channel.getProperty("plugin.assmatplugin.portlet.declararer-fin-accueil.id"));
String lienDeclarationFinAccueil = "";
if(declarationFinAccueilPortlet != null) {
  lienDeclarationFinAccueil = declarationFinAccueilPortlet.getDisplayUrl(userLocale);
}

// Format d'affichage de la fate
DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("dd/MM/YYYY");
SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

// Service swarger pour répérer les déclarations
Set<AccueilDTO> declarationBrouillonList = null; 
Set<AccueilDTO> declarationEnCoursList = null;
boolean isSwaggerOk = true;
try {	
	
	//liste des declaration à l'état brouillon
	declarationBrouillonList = new TreeSet<AccueilDTO>(new DeclarationAccueilDateModifComparator());
	declarationBrouillonList.addAll(DemarcheUtil.getListAccueils(profil.getNum_agrement(), "brouillon"));
	
	//liste des declaration à l'état en cours
	declarationEnCoursList = new TreeSet<AccueilDTO>(new DeclarationAccueilDateModifComparator());
	declarationEnCoursList.addAll(DemarcheUtil.getListAccueils(profil.getNum_agrement(), "en cours"));
} catch(ApiException e) {
  logger.warn("Web service swagger indisponible", e);
  isSwaggerOk = false;
}

%>


<div class="headstall container-fluid demarche">
  
  <!--  Titre de la page -->
  <div class="row-fluid">    
    <div class="span12 label">
      <div class="row-fluid title">
        <div class="headerEspacePerso">
          <h1><trsb:glp key="ASS-ACC-TITRE1-HTML" /></h1>
        </div>        
      </div>
    </div>   
  </div> 
  <!-- FIN titre de la page -->
  
  
  <!-- Corps de la page -->
  <div class="row-fluid">
	  <div class="span12 label">
	  
	  
	     <!-- présentation de l’espace -->
	     <p class="titleAccueilPerso"><trsb:glp key="ACCUEIL-PROFIL-INTRO"></trsb:glp></p>
	     
	     
	     <!-- zone réservée à l’affichage d’une information spécifique -->
	     <div class="alert alert-block fade in alert-cg alert-info">
			      <p><trsb:glp key="ACCUEIL-PROFIL_HTML"></trsb:glp></p>
			 </div>
			 
			 <!-- 3.3.2.2 Date de renouvellement de l'agrément -  -->
			 <% 
			    Boolean hasRenouvellementDom = false;
		      Date agrementRenouvellementDomDate = assmatSolis.getDateProchainRenouvellement();	
		      if(Util.notEmpty(agrementRenouvellementDomDate) && Util.notEmpty(assmatSolis.getExerceDomicile()) && assmatSolis.getExerceDomicile()) {
		        // Ajoute 2 mois à la date de renouvellement
		        GregorianCalendar calendar = new GregorianCalendar();
		        calendar.setTime(new Date());
		        calendar.add(Calendar.MONTH, 2);
		        if(calendar.getTime().after(agrementRenouvellementDomDate)) {
		          hasRenouvellementDom = true;
		        }
	        }
		      
		      Boolean hasRenouvellementMam = false;
	        Date agrementRenouvellementMamDate = assmatSolis.getDateProchainRenouvellementMam(); 
	        if(Util.notEmpty(agrementRenouvellementMamDate) && Util.notEmpty(assmatSolis.getExerceMam()) && assmatSolis.getExerceMam()) {
	          // Ajoute 2 mois à la date de renouvellement
	          GregorianCalendar calendar = new GregorianCalendar();
	          calendar.setTime(new Date());
	          calendar.add(Calendar.MONTH, 2);
	          if(calendar.getTime().after(agrementRenouvellementMamDate)) {
	            hasRenouvellementMam = true;
	          }
          }
			 %>
			 <jalios:if predicate='<%= hasRenouvellementDom || hasRenouvellementMam %>'>
			   <div class="alert alert-block fade in alert-cg alert-warning">
				   <%-- Renouvellement Domicile --%>
				   <jalios:if predicate="<%= hasRenouvellementDom %>">
					   <% String agrementRenouvellementDomDateString = simpleDateFormat.format(agrementRenouvellementDomDate); %>				 
			       <p><trsb:glp key="ASS-ACC-ME-PER-DOM-HTML" parameter='<%= new String[]{agrementRenouvellementDomDateString} %>'></trsb:glp></p>		     
		       </jalios:if>
		       <%-- Renouvellement MAM --%>
		       <jalios:if predicate="<%= hasRenouvellementMam %>">
	           <% String agrementRenouvellementMamDateString = simpleDateFormat.format(agrementRenouvellementMamDate); %>
	           <p><trsb:glp key="ASS-ACC-ME-PER-MAM-HTML" parameter='<%= new String[]{agrementRenouvellementMamDateString} %>'></trsb:glp></p>
	         </jalios:if>
         </div>
       </jalios:if>
			 
			 
			 <jalios:if predicate="<%= isGroupAuthorized && isSwaggerOk %>">
			 			 
				 <!-- 2.3.2.2  Bloc des démarches enregistrées en brouillon -->  
         <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/accueilsBrouillon.jspf'%>
         
         <%-- 2.3.2.3 Bloc des accueils en cours --%>
         <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/accueilsEnCours.jspf'%>
					 					      
	     </jalios:if>
	     
	     
	     <%--   BLOCK SWAGGER KO --%>
	     
	     <jalios:if predicate="<%= !isSwaggerOk %>">
	       <p><trsb:glp key="SWAGGER-RESP-ERR" /></p>
	     </jalios:if>
	     
	     
	     <%--   BLOCK MES INTERLOCUTEURS --%>
	      <%
            Set<Place> setPlace = new TreeSet<Place>();
            if(assmatSolis.getIdUa() != null) {
              Set<Place> setPlaceUA =(Set<Place>) JcmsUtil.applyDataSelector(channel.getAllDataSet(Place.class), new RelaisMamSelectorIDSolis(assmatSolis.getIdUa().replace(" ", "_")));           
              setPlace.addAll(setPlaceUA);
            }
            
            if(assmatSolis.getIdRam() != null) {
              Set<Place> setPlaceDOM =(Set<Place>) JcmsUtil.applyDataSelector(channel.getAllDataSet(Place.class), new RelaisMamSelectorIDSolis("RAM_"+assmatSolis.getIdRam()));           
              setPlace.addAll(setPlaceDOM);
            }
            
            if(assmatSolis.getIdRamMam() != null) {
              Set<Place> setPlaceMAM =(Set<Place>) JcmsUtil.applyDataSelector(channel.getAllDataSet(Place.class), new RelaisMamSelectorIDSolis("RAM_"+assmatSolis.getIdRamMam()));           
              setPlace.addAll(setPlaceMAM);
            }
            
            
           
           %>
           
           <jalios:if predicate="<%= Util.notEmpty(setPlace)  %>">
           
             
             
             
             <div class="cadreType cadreBleu prepEntretien" style="width: 50%; margin-top: 35px; border: none;">
             
                <h2 style="border-bottom: 2px dotted black"><trsb:glp key="ASS-ACC-INT-TITRE1-HTML" /></h2>
                    
                    
                    <jalios:foreach collection="<%= setPlace %>" name="itPlace" type="Place">
                      
     
                          <div class='blocAdresse <%= itPlace.getSolisId().startsWith("UA") ? "uaAdresse" : ""  %>'>
                            <div>
                              <strong><%=itPlace.getTitle() %></strong><br />
                              
                              <jalios:wiki><%= AssmatUtil.replaceCharSpaceBR(itPlace.getStreet()) %></jalios:wiki>
                                             
                              <%=itPlace.getZipCode() %>  <%= itPlace.getCity() %><br />
                            </div>
                           <jalios:if predicate="<%=Util.notEmpty(itPlace.getPhones()) %>">
                              <div><span class="bold">Tél :</span>
                                <jalios:foreach name="itTel" type="String" array="<%= itPlace.getPhones()%>">
                                  <jalios:if predicate="<%= itCounter != 1 %>">
                                   -
                                  </jalios:if>                      
                                  <%= itTel %>                      
                                </jalios:foreach>
                              </div>
                            </jalios:if>
                           <jalios:if predicate="<%=Util.notEmpty(itPlace.getMails()) %>">
                            
                             <jalios:foreach name="itMail" type="String" array="<%= itPlace.getMails()%>">
                             <%
                             String mail = itMail;
                             Category cat = channel.getCategory(channel.getProperty("jcmsplugin.assmatplugin.formulaire.contact.ram"));
                         if(Util.notEmpty(cat)){
                         String lien = cat.getDisplayUrl(userLocale) + "?mailRAM=" + encodeForHTML(mail) + "&titleRAM=" + encodeForURL(itPlace.getTitle()); 
                             %>
                             <u><a class="gras" href="<%=lien %>">Courriel</a></u>
                             <%} %>
                             </jalios:foreach>
                             
                          </jalios:if>
                           <jalios:if predicate="<%=Util.notEmpty(itPlace.getWebsites()) %>">
                               <div>
                                 <jalios:foreach name="itSite" type="String" array="<%= itPlace.getWebsites()%>">
                                    <u><a class="gras" target='_blank' href="<%=itSite%>">Site internet</a></u>
                                 </jalios:foreach>
                               </div>
                            </jalios:if>
                          </div>  
                          
                          <jalios:if predicate="<%= setPlace.size() != itCounter %>">
                              <div style="border-bottom: 2px dotted black; margin-top: 10px;"> </div>
                          </jalios:if>
                          
                     </jalios:foreach>                      
                       
                </div>
                   
                   
                
           
           </jalios:if>
	     
	     
	     
	     
	  </div>
  </div>
  <!-- FIN corps de la page -->
    

</div>

