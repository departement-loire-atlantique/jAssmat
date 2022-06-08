<%@page import="fr.trsb.cd44.solis.manager.QuartierDAO"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil.SelectionEtatDispo"%>
<%@page import="fr.cg44.plugin.assmat.selector.RelaisMamSelectorIDSolis"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil.SelectionEtatDispo"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil.SelectionEtatDispo"%>
<%@page import="fr.cg44.plugin.tools.facetedsearch.exception.UnknowCityException"%>
<%@page import="fr.cg44.plugin.assmat.comparator.DisponibiliteComparator"%>
<%@page import="fr.cg44.plugin.assmat.selector.RelaisMamSelectorCommune"%>
<%@page import="fr.cg44.plugin.tools.facetedsearch.manager.CityManager"%>
<%@page import="fr.cg44.plugin.assmat.beans.AssmatSearch"%>
<%@page import="fr.cg44.plugin.assmat.SemainierUtil"%>
<%@page import="fr.cg44.plugin.assmat.Semainier"%>
<%@page import="java.lang.reflect.Field"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.PointAssmat"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.cg44.plugin.tools.ToolsUtil"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%@page import="fr.cg44.plugin.tools.modal.ModalGenerator"%><%
%><%@ page contentType="text/html; charset=UTF-8"%><%
%><%@ include file='/jcore/doInitPage.jsp'%>

<%
ProfilASSMAT profilAM = (ProfilASSMAT) request.getAttribute(PortalManager.PORTAL_PUBLICATION);
SolisManager solisMgr = SolisManager.getInstance();
AssmatSolis asmmatSolis =Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(profilAM.getNum_agrement()));
PointAssmat pointAssmat = new PointAssmat(String.valueOf(profilAM.getLatitudeAssmat()),String.valueOf(profilAM.getLongitudeAssmat()), "#AEC900", "Avec des disponibilités");

PointAssmat pointUser =(PointAssmat) session.getAttribute("userLocation");

NumberFormat nf = new DecimalFormat("0.#");

ProfilManager profilMgr = ProfilManager.getInstance();

Date dateModifDispo = profilMgr.getDateModifDispo(profilAM.getAuthor());

TreeMap<AssmatSearch,PointAssmat> assmatPoints =(TreeMap<AssmatSearch,PointAssmat>) session.getAttribute("assmatPoints");

//Si on vient de la recherche on retourne sur la recherche, sinon sur la selection
String urlRecherche =(String) session.getAttribute("urlRecherche");
String urlRetour = urlRecherche;

double distanceMam = -1;
double distanceDom = -1;

boolean hasQuartierMam = "44109".equals(asmmatSolis.getCodeInseeMam()) &&  Util.notEmpty(asmmatSolis.getIdMicroQuartierMam()) && asmmatSolis.getExerceMam();
boolean hasQuartierDom = "44109".equals(asmmatSolis.getCodeInsee()) &&  Util.notEmpty(asmmatSolis.getIdMicroQuartierDom()) && asmmatSolis.getExerceDomicile();


boolean isSelection = false;
if(Util.notEmpty(session.getAttribute("isSelection"))){
	isSelection = true;
  urlRetour =(String) session.getAttribute("urlSelection");
}

request.setAttribute("dataId", profilAM.getId());

ProfilASSMAT profilPrecedent = null;
ProfilASSMAT profilSuivant = null;

boolean isGood = false;
boolean isTerminer = false;

//0011253: Défilement des fiches dans ma sélection 
//Supprimer les boutons de défilement pour l'instant de fiche à fiche pour l'instant et prévoir une évolution

if(Util.notEmpty(assmatPoints) && !isSelection){

  if(Util.notEmpty(pointUser)) {
    if(Util.notEmpty(asmmatSolis.getLatitudeMam()) && Util.notEmpty(asmmatSolis.getLongitudeMam()) && asmmatSolis.getExerceMam()) {
      distanceMam = AssmatUtil.getDistance(asmmatSolis.getLatitudeMam().doubleValue(), asmmatSolis.getLongitudeMam().doubleValue(),(double) pointUser.getLatitude(), (double)pointUser.getLongitude()); 
    }
    if(Util.notEmpty(asmmatSolis.getLatitude()) && Util.notEmpty(asmmatSolis.getLongitude()) && asmmatSolis.getExerceDomicile()) {
      distanceDom = AssmatUtil.getDistance(asmmatSolis.getLatitude().doubleValue(), asmmatSolis.getLongitude().doubleValue(),(double) pointUser.getLatitude(), (double)pointUser.getLongitude());   
    }
  }
  
	for (Map.Entry<AssmatSearch, PointAssmat> entry : assmatPoints.entrySet())
	{
	  if(isGood && !isTerminer){
	    profilSuivant = profilMgr.getProfilASSMATbyAssmatSearch(entry.getKey());
	    isTerminer= true; 
	  }
	 if(!isTerminer){
	 if(profilAM==profilMgr.getProfilASSMATbyAssmatSearch(entry.getKey())){
	   isGood = true;
	  }
	 if(!isGood){
	   profilPrecedent = profilMgr.getProfilASSMATbyAssmatSearch(entry.getKey());
	 }
	 
	 }  
	}
}

String idPortalRecherche = channel.getProperty("jcmsplugin.assmatplugin.portal.recherche.am"); 
Publication portalRecherche = channel.getPublication(idPortalRecherche);

if(Util.isEmpty(urlRetour)){
    urlRetour = portalRecherche.getDisplayUrl(userLocale);
}

SimpleDateFormat formater = null;
formater = new SimpleDateFormat("dd/MM/yyyy");


jcmsContext.addCSSHeader("plugins/EServicePlugin/css/portal/portal.css");
Set<ProfilASSMAT> listeProfilAM = (Set<ProfilASSMAT>) request.getSession().getAttribute("listeProfilAMSelection");
PortalJspCollection portalSelection = (PortalJspCollection) channel.getPublication("$jcmsplugin.assmatplugin.portail.selection.recherche");
  jcmsContext.addJavaScript("plugins/ToolsPlugin/js/facets/tooltipCategories.js");
  jcmsContext.addCSSHeader("plugins/EServicePlugin/css/types/AbstractPortletSkinable/titleBarDottedTitleElementBorderBot.css");
  jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/headstall/model1.css");
  jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/AbstractPortletSkinable/titleBar.css");
  jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/common.css");
  
  jcmsContext.addCSSHeader("plugins/AssmatPlugin/css/plugin.css");
  jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");
%>

<%
	Boolean hasDispo = profilMgr.hasDispo(profilAM.getAuthor()); 
	Boolean showContactDispo = !profilAM.getAfficherContactUniquementSiD() || hasDispo;
%>

<style>

.breadCrumb{
display: none;
}

</style>
 <div class="fullDisplay AM">     
       <div style=" text-align: left;  " class="Outter PortletNavigate ID_local_56434 dnd-header PortletSkinable Inner  ">
<ul class="breadCrumb breadCrumbFullDisplay">

  <li itemprop="title">
  <a itemprop="url" href="index.jsp" title="Retour à l'accueil du site">
    <img src="s.gif" class="spr-home" alt="Retour à l'accueil du site">
  </a></li>
  
   <li itemprop="title">
    <a itemprop="url" class="" href="<%= portalRecherche.getDisplayUrl(userLocale) %>"><trsb:glp key="PROFIL-ASSMAT-LIBELLE-TITRE" /></a>
  </li>
  <li itemprop="title">
    <span class="bold"><%=asmmatSolis.getNomAssmat() %> <%=asmmatSolis.getPrenomAssmat() %> </span>
  </li>
  
</ul>
</div>
       
       
        <div class="row-fluid">
          <div class="span12">
            <ul class="btnNavHz">
            
              <li class="retour"><a href="<%=urlRetour %>" class="btnNoirType btnRetourRecherche">
              <%if(isSelection){%>
                 <trsb:glp  key="PROFIL-ASSMAT-LIBELLE-RETOUR-SELECTION-HTML" ></trsb:glp>
             
              <%}else{%>             
              <trsb:glp  key="PROFIL-ASSMAT-LIBELLE-RETOUR-RECHERCHE-HTML" ></trsb:glp>
              <%} %>
               </a></li>
              <%if(Util.notEmpty(profilPrecedent)){ %>
              <li class="precendent"><a href="<%=profilPrecedent.getDisplayUrl(userLocale) %>?portal=<%=idPortalRecherche%>" class="btnNoirType btnPrec"><trsb:glp  key="PROFIL-ASSMAT-LIBELLE-FICHE-PRECED-HTML" ></trsb:glp></a></li>
              <%} %>
                <%if(Util.notEmpty(profilSuivant)){ %>
              <li class="btnSuivant"><a href="<%=profilSuivant.getDisplayUrl(userLocale) %>?portal=<%=idPortalRecherche%>" class="btnNoirType btnSuiv"><trsb:glp  key="PROFIL-ASSMAT-LIBELLE-FICHE-SUIV-HTML" ></trsb:glp></a></li>
               <%} %>
            </ul>
          </div>
        </div>
        <!-- /boutons de navigation horizontale -->
        
        <div class="row-fluid">
          <div class="span12 label headerEspacePerso">
          
            <h1 style="display: inline-block;"><trsb:glp key="PROFIL-ASSMAT-LIBELLE-TITRE" /></h1>
            
            <div style="float: right; display: table; font-weight: 700; margin-top: 10px;" class="menu hidden-tablet hidden-phone hide-touch printHide">
              
              
              <div style="display: table-cell; width: 180px;line-height: normal;border-right: 2px black dotted;" width: 175px;" class="">
                <jalios:include jsp='plugins/AssmatPlugin/jsp/recherche/selection/doProcessSelection.jsp' />
              </div>
              
              
              <div style="display: table-cell; width: 130px; border-right: 2px black dotted; padding-left: 15px; line-height: normal;">
               <a style="width: 95px; display: table-cell;" class="afficheSelection" href='<%=portalSelection.getDisplayUrl(userLocale)%>'>Afficher ma sélection (<span class="nbAMpanier"><%if(Util.notEmpty(listeProfilAM)){%><%=listeProfilAM.size() %><%}else{%>0<%} %></span>)</a>
               <div style="display: table-cell; vertical-align: middle;">
                 <img alt="" src="plugins/AssmatPlugin/img/picto-star.png" />
               </div>
              </div>


              <div style="display: table-cell; vertical-align: middle; padding-left: 15px;">
               <%
                Set<ProfilASSMAT> profilMap = new HashSet<ProfilASSMAT>();
                profilMap.add(profilAM);
                session.setAttribute("listeProfilAMFull", profilMap);
               %>
               <a style="padding-right: 10px;" href="javascript:window.print()" title="<%= glp("plugin.tools.accessibily.newTabLabel") %>">Imprimer</a><img class="spr-picto-impression" alt="" src="s.gif" />  
              </div>


            </div>    
            
            
                   
          </div>
        </div>
        
        <!-- COLONNAGE -->
        <div class="row-fluid">
          
          <!-- COLONNE GAUCHE -->
          <div class="span5 colGauche">
            
            <div class="cadreType cadreVert">
              <h2><%=asmmatSolis.getNomAssmat() %> <%=asmmatSolis.getPrenomAssmat() %> </h2>
              <p class="exergue">
               <%
                Date maj = profilAM.getMdate();
				        if(dateModifDispo != null && dateModifDispo.after(maj)) {
				          maj = dateModifDispo;
				        }
				        %>
                <trsb:glp key="PROFIL-ASSMAT-CONTENT-COORD-MAJ" parameter="<%= new String[]{formater.format(maj)} %>" /> 
              </p>

              <div class="blocAdresse">
              <%if(Util.notEmpty(asmmatSolis.getAdresseMam()) && asmmatSolis.getExerceMam() ){ %>
                <p>
	                <%=asmmatSolis.getNomMam() %><br />
	                <%=asmmatSolis.getAdresseMam() %><br />
	                <%=asmmatSolis.getCpMam() %> <%=asmmatSolis.getCommuneMam() %>
<%-- 	                <jalios:if predicate='<%= hasQuartierMam %>'> --%>
<%--                     <p> <strong>Quartier :</strong> <%= QuartierDAO.getLibQuartier(QuartierDAO.getIdQuartier(asmmatSolis.getIdMicroQuartierMam())) %></p> --%>
<%--                    </jalios:if>                 --%>
                </p>
              <%} %>

							 <%if(Util.notEmpty(asmmatSolis.getAdresseDomicile())&& asmmatSolis.getExerceDomicile()  ){ %>
							        <%-- 0011460: Fiche détaillée d'une AM exerçant à domicile et en mam  --%>
							        <%if(Util.notEmpty(asmmatSolis.getAdresseMam()) && asmmatSolis.getExerceMam() ){ %>
							           <strong>Domicile</strong>
							        <%} %>
							        <p>
								        <%=asmmatSolis.getAdresseDomicile() %><br />
								        
								        <jalios:if predicate="<%= Util.notEmpty(asmmatSolis.getComplementAdresse()) %>">
							              <%= asmmatSolis.getComplementAdresse() %><br />
							          </jalios:if>
								        
								        <%=asmmatSolis.getCpDomicile() %>  <%=asmmatSolis.getCommuneDomicile() %>
								        
<%-- 								        <jalios:if predicate='<%= hasQuartierDom %>'> --%>
<%-- 								          <p> <strong>Quartier :</strong> <%= QuartierDAO.getLibQuartier(QuartierDAO.getIdQuartier(asmmatSolis.getIdMicroQuartierDom())) %></p> --%>
<%-- 								        </jalios:if> --%>
							        
							        </p>
							<%} %>
							
							
							<jalios:if predicate="<%= showContactDispo  %>">		
									<% Boolean hasPhone = false; %>
									<P>									
										<%if( AssmatUtil.getBooleanFromString(profilAM.getVisbiliteTelephoneFixe())){ %>
										 <%if(Util.notEmpty(profilAM.getTelephoneFixe())){ %>
										   <% hasPhone = true; %>
			                 <strong>Tél fixe :</strong> <%=profilAM.getTelephoneFixe() %><br />
			                <%}
										 }%>
										 
										 <%if( AssmatUtil.getBooleanFromString(profilAM.getVisibiliteTelephonePortable())){ %>
			                 <%if(Util.notEmpty(profilAM.getAuthor().getMobile())){ %>
			                   <% hasPhone = true; %>
			                   <strong>Tél portable :</strong> <%=profilAM.getAuthor().getMobile() %><br />
			                <%} 
										 }
			              %>
			              
			              <%if( Util.notEmpty(profilAM.getCreneauHorairesDappel()) && hasPhone ){ %>
		                      <%= profilAM.getCreneauHorairesDappel() %>
		                <%} %>                 
                 </P>
		              
               </jalios:if>
               
               <%
               String idCategMam = channel.getProperty("$plugin.assmatplugin.categ.relaiam");
               Category categRAM= channel.getCategory(idCategMam); 
               
               
               
               Set<Place> setPlace = new TreeSet<Place>();
                                          
               // Exrece Domicile
               if(asmmatSolis.getIdRam() != null) {
                 Set<Place> setPlaceDOM =(Set<Place>) JcmsUtil.applyDataSelector(channel.getAllDataSet(Place.class), new RelaisMamSelectorIDSolis("RAM_"+asmmatSolis.getIdRam()));           
                 setPlace.addAll(setPlaceDOM);
               }
               // Exrece MAM
               if(asmmatSolis.getIdRamMam() != null) {
                 Set<Place> setPlaceMAM =(Set<Place>) JcmsUtil.applyDataSelector(channel.getAllDataSet(Place.class), new RelaisMamSelectorIDSolis("RAM_"+asmmatSolis.getIdRamMam()));           
                 setPlace.addAll(setPlaceMAM);
               }
               %>
               
              </div>
                <!--  Courriel -->
            <%if(showContactDispo && AssmatUtil.getBooleanFromString(profilAM.getVisibiliteAdresseEmail())){ %>
             <jalios:if predicate="<%= Util.notEmpty(profilAM.getAuthor()) %>">
            <jalios:if predicate="<%= Util.notEmpty(profilAM.getAuthor().getEmail()) %>"><%
            Category cat = channel.getCategory(channel.getProperty("jcmsplugin.assmatplugin.formulaire.contact.am"));
            if(Util.notEmpty(cat)){
                  String lien = cat.getDisplayUrl(userLocale) + "?idMAM=" + profilAM.getAuthor().getId();
                
                      %>
            
                      
               <%
                            %><b> <u><a class="courrielFull" href="<%=lien %>">Courriel</a></u></b>
                     <%}
                 %></jalios:if> 
                 </jalios:if>  
                  <%} %>  
            
            <%if(Util.notEmpty(asmmatSolis.getExerceMam())){ %>
             <%if(asmmatSolis.getExerceMam()){ %>
              <p class="typeExercice mam">
              	<img class="pictoPrint" src="plugins/AssmatPlugin/img/picto-maison-assmat.png">
              	<trsb:glp key="PROFIL-ASSMAT-CONTENT-COORD-EXERCE-MAM" />
              </p>
              <%}} %>
                <%if(Util.notEmpty(asmmatSolis.getExerceDomicile())){ %>
                  <%if(asmmatSolis.getExerceDomicile()){ %>
              <p class="typeExercice aDom">
              	<img class="pictoPrint" src="plugins/AssmatPlugin/img/picto-domicile.png">
              	<trsb:glp key="PROFIL-ASSMAT-CONTENT-COORD-EXERCE-DOM" />
              </p>
            <%} }%>
            </div> <!-- /cadreType cadreVert -->
            
        <%
        
        List<Disponibilite> dispoList = profilMgr.getDisponibilitesList(profilAM.getAuthor(), null);
        
          
        if(Util.notEmpty(dispoList)){
          boolean hasInconnu = false;
          boolean hasNonDispo = false;                   
          Set<Disponibilite> dispoListImmFutur = new TreeSet<Disponibilite>(new DisponibiliteComparator());         
          for(Disponibilite itDispo : dispoList) {
            if((SelectionEtatDispo.IMMEDIATE.getValue().equalsIgnoreCase(itDispo.getEtatDispo())) || (SelectionEtatDispo.FUTURE.getValue().equalsIgnoreCase(itDispo.getEtatDispo())) ){
              dispoListImmFutur.add(itDispo);
            }else if(SelectionEtatDispo.INCONNU.getValue().equalsIgnoreCase(itDispo.getEtatDispo())) {
              hasInconnu = true;
            }else if(SelectionEtatDispo.NON_DISPO.getValue().equalsIgnoreCase(itDispo.getEtatDispo())) {
              hasNonDispo = true;
            }
          }

        %>
            
            <div class="blocStd disponibilites">
              <h2><trsb:glp key="PROFIL-ASSMAT-LIBELLE-DISPONIBILITE-HTML" ></trsb:glp></h2>
              <ul class="listeDispos">
              
              <jalios:select>
              
              <jalios:if predicate="<%= Util.notEmpty(dispoListImmFutur) %>">
              
              <%for(Disponibilite dispo : dispoListImmFutur){
              
                // Ne pas afficher les disponibilité spécifique. (celles qui ne peuvent être saisies)
                // 0011193: Fiche d'étaillée AM affichage tranche d'âge spécifique 
                if(!dispo.getSaisieDisponible()) {
                  continue;
                }
                
               String etatDispo = dispo.getEtatDispo(); 
              
        String lundi_avant_ecole1 =           "";
        String mardi_avant_ecole1 =           "";
        String mercredi_avant_ecole1 =          "";
        String jeudi_avant_ecole1 =           "";
        String vendredi_avant_ecole1 =          "";
        String samedi_avant_ecole1 =          "";
        String dimanche_avant_ecole1 =          "";
        String lundi_matin1 =               "";
        String mardi_matin1 =               "";
        String mercredi_matin1 =            "";
        String jeudi_matin1 =               "";
        String vendredi_matin1 =            "";
        String samedi_matin1 =              "";
        String dimanche_matin1 =            "";
        String lundi_midi1 =              "";
        String mardi_midi1 =              "";
        String mercredi_midi1 =             "";
        String jeudi_midi1 =              "";
        String vendredi_midi1 =             "";
        String samedi_midi1 =               "";
        String dimanche_midi1 =             "";
        String lundi_pm1 =                "";
        String mardi_pm1 =                "";
        String mercredi_pm1 =               "";
        String jeudi_pm1 =                "";
        String vendredi_pm1 =               "";
        String samedi_pm1 =               "";
        String dimanche_pm1 =               "";
        String lundi_apres_ecole1 =           "";
        String mardi_apres_ecole1 =           "";
        String mercredi_apres_ecole1 =          "";
        String jeudi_apres_ecole1 =           "";
        String vendredi_apres_ecole1 =          "";
        String samedi_apres_ecole1 =          "";
        String dimanche_apres_ecole1 =          "";
        String lundi_nuit1 =              "";
        String mardi_nuit1 =              "";
        String mercredi_nuit1 =             "";
        String jeudi_nuit1 =              "";
        String vendredi_nuit1 =             "";
        String samedi_nuit1 =               "";
        String dimanche_nuit1 =             "";
    
        SelectionEtatDispo etat =  AssmatUtil.SelectionEtatDispo.getEtatDispo(etatDispo);
        Semainier s = SemainierUtil.decoded(dispo.getSemainierPlaceDisponible());

        if(Util.notEmpty(s) && Util.notEmpty(etatDispo) && (SelectionEtatDispo.IMMEDIATE.equals(etat) || SelectionEtatDispo.FUTURE.equals(etat)) ){
          lundi_avant_ecole1 =          String.valueOf(s.isLundi_avant_ecole());
          mardi_avant_ecole1 =          String.valueOf(s.isMardi_avant_ecole());
          mercredi_avant_ecole1 =         String.valueOf(s.isMercredi_avant_ecole());
          jeudi_avant_ecole1 =          String.valueOf(s.isJeudi_avant_ecole());
          vendredi_avant_ecole1 =         String.valueOf(s.isVendredi_avant_ecole());
          samedi_avant_ecole1 =           String.valueOf(s.isSamedi_avant_ecole());
          dimanche_avant_ecole1 =         String.valueOf(s.isDimanche_avant_ecole());
          
          lundi_matin1 =              String.valueOf(s.isLundi_matin());
          mardi_matin1 =              String.valueOf(s.isMardi_matin());
          mercredi_matin1 =             String.valueOf(s.isMercredi_matin());
          jeudi_matin1 =              String.valueOf(s.isJeudi_matin());
          vendredi_matin1 =             String.valueOf(s.isVendredi_matin());
          samedi_matin1 =             String.valueOf(s.isSamedi_matin());
          dimanche_matin1 =             String.valueOf(s.isDimanche_matin());
          
          
          lundi_midi1 =               String.valueOf(s.isLundi_midi());
          mardi_midi1 =               String.valueOf(s.isMardi_midi());
          mercredi_midi1 =            String.valueOf(s.isMercredi_midi());
          jeudi_midi1 =               String.valueOf(s.isJeudi_midi());
          vendredi_midi1 =            String.valueOf(s.isVendredi_midi());
          samedi_midi1 =              String.valueOf(s.isSamedi_midi());
          dimanche_midi1 =            String.valueOf(s.isDimanche_midi());
          
          lundi_pm1 =               String.valueOf(s.isLundi_pm());
          mardi_pm1 =               String.valueOf(s.isMardi_pm());
          mercredi_pm1 =              String.valueOf(s.isMercredi_pm());
          jeudi_pm1 =               String.valueOf(s.isJeudi_pm());
          vendredi_pm1 =              String.valueOf(s.isVendredi_pm());
          samedi_pm1 =              String.valueOf(s.isSamedi_pm());
          dimanche_pm1 =              String.valueOf(s.isDimanche_pm());
          
          lundi_apres_ecole1 =          String.valueOf(s.isLundi_apres_ecole());
          mardi_apres_ecole1 =          String.valueOf(s.isMardi_apres_ecole());
          mercredi_apres_ecole1 =         String.valueOf(s.isMercredi_apres_ecole());
          jeudi_apres_ecole1 =          String.valueOf(s.isJeudi_apres_ecole());
          vendredi_apres_ecole1 =         String.valueOf(s.isVendredi_apres_ecole());
          samedi_apres_ecole1 =           String.valueOf(s.isSamedi_apres_ecole());
          dimanche_apres_ecole1 =         String.valueOf(s.isDimanche_apres_ecole());
          
          
          lundi_nuit1 =               String.valueOf(s.isLundi_nuit());
          mardi_nuit1 =               String.valueOf(s.isMardi_nuit());
          mercredi_nuit1 =            String.valueOf(s.isMercredi_nuit());
          jeudi_nuit1 =               String.valueOf(s.isJeudi_nuit());
          vendredi_nuit1 =            String.valueOf(s.isVendredi_nuit());
          samedi_nuit1 =              String.valueOf(s.isSamedi_nuit());
          dimanche_nuit1 =            String.valueOf(s.isDimanche_nuit());
          
          
          //Verification des creneau avant ecole
          if("true".equalsIgnoreCase(lundi_avant_ecole1)){
            lundi_avant_ecole1 ="creneauDispo";
          }else{
            lundi_avant_ecole1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(mardi_avant_ecole1)){
            mardi_avant_ecole1 ="creneauDispo";
          }else{
            mardi_avant_ecole1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(mercredi_avant_ecole1)){
            mercredi_avant_ecole1 ="creneauDispo";
          }else{
            mercredi_avant_ecole1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(jeudi_avant_ecole1)){
            jeudi_avant_ecole1 ="creneauDispo";
          }else{
            jeudi_avant_ecole1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(vendredi_avant_ecole1)){
            vendredi_avant_ecole1 ="creneauDispo";
          }else{
            vendredi_avant_ecole1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(samedi_avant_ecole1)){
            samedi_avant_ecole1 ="creneauDispo";
          }else{
            samedi_avant_ecole1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(dimanche_avant_ecole1)){
            dimanche_avant_ecole1 ="creneauDispo";
          }else{
            dimanche_avant_ecole1 ="creneauNonDispo";
          }
          
          
          
          
          //Verification des creneau matin
          if("true".equalsIgnoreCase(lundi_matin1)){
            lundi_matin1 ="creneauDispo";
          }else{
            lundi_matin1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(mardi_matin1)){
            mardi_matin1 ="creneauDispo";
          }else{
            mardi_matin1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(mercredi_matin1)){
            mercredi_matin1 ="creneauDispo";
          }else{
            mercredi_matin1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(jeudi_matin1)){
            jeudi_matin1 ="creneauDispo";
          }else{
           jeudi_matin1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(vendredi_matin1)){
            vendredi_matin1 ="creneauDispo";
          }else{
            vendredi_matin1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(samedi_matin1)){
            samedi_matin1 ="creneauDispo";
          }else{
            samedi_matin1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(dimanche_matin1)){
           dimanche_matin1 ="creneauDispo";
          }else{
            dimanche_matin1 ="creneauNonDispo";
          }
          
          
   
          
          if("true".equalsIgnoreCase(lundi_midi1)){
            lundi_midi1 ="creneauDispo";
          }else{
            lundi_midi1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(mardi_midi1)){
            mardi_midi1 ="creneauDispo";
          }else{
            mardi_midi1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(mercredi_midi1)){
            mercredi_midi1 ="creneauDispo";
          }else{
            mercredi_midi1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(jeudi_midi1)){
            jeudi_midi1 ="creneauDispo";
          }else{
            jeudi_midi1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(vendredi_midi1)){
            vendredi_midi1 ="creneauDispo";
          }else{
            vendredi_midi1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(samedi_midi1)){
            samedi_midi1 ="creneauDispo";
          }else{
            samedi_midi1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(dimanche_midi1)){
            dimanche_midi1 ="creneauDispo";
          }else{
            dimanche_midi1 ="creneauNonDispo";
          }
          
          
          

          
          if("true".equalsIgnoreCase(lundi_pm1)){
            lundi_pm1 ="creneauDispo";
          }else{
            lundi_pm1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(mardi_pm1)){
            mardi_pm1 ="creneauDispo";
          }else{
            mardi_pm1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(mercredi_pm1)){
            mercredi_pm1 ="creneauDispo";
          }else{
            mercredi_pm1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(jeudi_pm1)){
            jeudi_pm1 ="creneauDispo";
          }else{
            jeudi_pm1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(vendredi_pm1)){
            vendredi_pm1 ="creneauDispo";
          }else{
            vendredi_pm1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(samedi_pm1)){
            samedi_pm1 ="creneauDispo";
          }else{
            samedi_pm1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(dimanche_pm1)){
            dimanche_pm1 ="creneauDispo";
          }else{
            dimanche_pm1 ="creneauNonDispo";
          }
          
          
          if("true".equalsIgnoreCase(lundi_apres_ecole1)){
            lundi_apres_ecole1 ="creneauDispo";
          }else{
            lundi_apres_ecole1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(mardi_apres_ecole1)){
            mardi_apres_ecole1 ="creneauDispo";
          }else{
            mardi_apres_ecole1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(mercredi_apres_ecole1)){
            mercredi_apres_ecole1 ="creneauDispo";
          }else{
            mercredi_apres_ecole1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(jeudi_apres_ecole1)){
            jeudi_apres_ecole1 ="creneauDispo";
          }else{
            jeudi_apres_ecole1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(vendredi_apres_ecole1)){
            vendredi_apres_ecole1 ="creneauDispo";
          }else{
            vendredi_apres_ecole1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(samedi_apres_ecole1)){
            samedi_apres_ecole1 ="creneauDispo";
          }else{
            samedi_apres_ecole1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(dimanche_apres_ecole1)){
            dimanche_apres_ecole1 ="creneauDispo";
          }else{
            dimanche_apres_ecole1 ="creneauNonDispo";
          }
          

          
          if("true".equalsIgnoreCase(lundi_nuit1)){
            lundi_nuit1 ="creneauDispo";
          }else{
            lundi_nuit1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(mardi_nuit1)){
            mardi_nuit1 ="creneauDispo";
          }else{
            mardi_nuit1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(mercredi_nuit1)){
            mercredi_nuit1 ="creneauDispo";
          }else{
            mercredi_nuit1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(jeudi_nuit1)){
            jeudi_nuit1 ="creneauDispo";
          }else{
            jeudi_nuit1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(vendredi_nuit1)){
            vendredi_nuit1 ="creneauDispo";
          }else{
            vendredi_nuit1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(samedi_nuit1)){
            samedi_nuit1 ="creneauDispo";
          }else{
            samedi_nuit1 ="creneauNonDispo";
          }
          if("true".equalsIgnoreCase(dimanche_nuit1)){
            dimanche_nuit1 ="creneauDispo";
          }else{
            dimanche_nuit1 ="creneauNonDispo";
          }
          
          
        }
              %>
              
              
              
              
                <li>
                
                
                <%
                boolean isEmptySemainier = true;
                if(Util.notEmpty(s)){
                                          if (s.isLundi_avant_ecole() || s.isMardi_avant_ecole() || s.isMercredi_avant_ecole() || s.isJeudi_avant_ecole() || s.isVendredi_avant_ecole()
                                        || s.isSamedi_avant_ecole() || s.isDimanche_avant_ecole() ||

                                        s.isLundi_matin() || s.isMardi_matin() || s.isMercredi_matin() || s.isJeudi_matin() || s.isVendredi_matin() || s.isSamedi_matin()
                                        || s.isDimanche_matin() ||

                                        s.isLundi_midi() || s.isMardi_midi() || s.isMercredi_midi() || s.isJeudi_midi() || s.isVendredi_midi() || s.isSamedi_midi()
                                        || s.isDimanche_midi() ||

                                        s.isLundi_pm() || s.isMardi_pm() || s.isMercredi_pm() || s.isJeudi_pm() || s.isVendredi_pm() || s.isSamedi_pm() || s.isDimanche_pm() ||

                                        s.isLundi_apres_ecole() || s.isMardi_apres_ecole() || s.isMercredi_apres_ecole() || s.isJeudi_apres_ecole() || s.isVendredi_apres_ecole()
                                        || s.isSamedi_apres_ecole() || s.isDimanche_apres_ecole() ||

                                        s.isLundi_nuit() || s.isMardi_nuit() || s.isMercredi_nuit() || s.isJeudi_nuit() || s.isVendredi_nuit() || s.isSamedi_nuit()
                                        || s.isDimanche_nuit()) {
                                      isEmptySemainier = false;
                                    }
                                                            }%>
                
                <%if( Util.notEmpty(etatDispo) &&  SelectionEtatDispo.INCONNU.equals(etat)){ %>
                 <p class="crDispos">
                 <img class="crDisposPictoPrint" src="plugins/AssmatPlugin/img/icon-enfant.png">
                 <jalios:if predicate='<%= dispo.getAgrement().contains("dom-") %>'>                  
                    <span class="dispoInconnu">1 place de <%=dispo.getLibelle() %> à domicile</span><br>
                 </jalios:if>
                 <jalios:if predicate='<%= dispo.getAgrement().contains("mam-") %>'>                  
                    <span class="dispoInconnu">1 place de <%=dispo.getLibelle() %> en MAM</span><br>
                 </jalios:if>
                 <span class="noteDispoInconnu">Disponibilité non renseignée</span></p>
                 <%} %>
                
                 <%if( Util.notEmpty(etatDispo) && (SelectionEtatDispo.IMMEDIATE.equals(etat) || (SelectionEtatDispo.FUTURE.equals(etat) && new Date().after(dispo.getDateDispoPlaceFuture())  ) )){ %>
                  <p class="crDispos">  
                  <img class="crDisposPictoPrint" src="plugins/AssmatPlugin/img/icon-enfant.png">
                  <jalios:if predicate='<%= dispo.getAgrement().contains("dom-") %>'>
					            <trsb:glp key="PROFIL-ASSMAT-CONTENT-DISPONIBILITE-DISPO-DISPONIBLE-DOM" parameter='<%= new String[]{dispo.getLibelle()} %>' />  
				          </jalios:if>
				          <jalios:if predicate='<%= dispo.getAgrement().contains("mam-") %>'>
				              <trsb:glp key="PROFIL-ASSMAT-CONTENT-DISPONIBILITE-DISPO-DISPONIBLE-MAM" parameter='<%= new String[]{dispo.getLibelle()} %>' /> 
				          </jalios:if> </p>
                 <%} else if( Util.notEmpty(etatDispo) && SelectionEtatDispo.FUTURE.equals(etat)){ %>
                  <p class="crDispos">
                  <img class="crDisposPictoPrint" src="plugins/AssmatPlugin/img/icon-enfant.png">
                  <jalios:if predicate='<%= dispo.getAgrement().contains("dom-") %>'>
				            <trsb:glp key="PROFIL-ASSMAT-CONTENT-DISPONIBILITE-DISPO-FUTUR-DOM" parameter='<%= new String[]{dispo.getLibelle(), AssmatUtil.getFutureDispoDateLibelle(dispo.getDateDispoPlaceFuture()) } %>' />    
				          </jalios:if>
				          <jalios:if predicate='<%= dispo.getAgrement().contains("mam-") %>'>
				            <trsb:glp key="PROFIL-ASSMAT-CONTENT-DISPONIBILITE-DISPO-FUTUR-MAM" parameter='<%= new String[]{dispo.getLibelle(), AssmatUtil.getFutureDispoDateLibelle(dispo.getDateDispoPlaceFuture()) } %>' />  
				          </jalios:if></p>
                 <%} %>
                 
                 <%if( Util.notEmpty(etatDispo) && SelectionEtatDispo.NON_DISPO.equals(etat)){ %>
                   <p class="crNonDispos">
                   <img class="crNonDisposPictoPrint" src="plugins/AssmatPlugin/img/icon-enfant-rouge.png">
                   <jalios:if predicate='<%= dispo.getAgrement().contains("dom-") %>'>
				             <trsb:glp key="PROFIL-ASSMAT-CONTENT-DISPONIBILITE-NON-DISPO-DOM" parameter='<%= new String[]{dispo.getLibelle()} %>' />				             
					         </jalios:if>
					         <jalios:if predicate='<%= dispo.getAgrement().contains("mam-") %>'>
					            <trsb:glp key="PROFIL-ASSMAT-CONTENT-DISPONIBILITE-NON-DISPO-MAM" parameter='<%= new String[]{dispo.getLibelle()} %>' />   
					         </jalios:if></p>
                 <%} %>
                 
                 <%if(Util.notEmpty(s) && Util.notEmpty(etatDispo) && (SelectionEtatDispo.IMMEDIATE.equals(etat) || SelectionEtatDispo.FUTURE.equals(etat) )  && !isEmptySemainier ){ %>
                  <table class="tabDispos" summary="chaque ligne correspond à un créneau, chaque colonne à une journée de la semaine">
                    <tr>
                      <th>&nbsp;</th>
                      <th scope="col">L</th>
                      <th scope="col">M</th>
                      <th scope="col">M</th>
                      <th scope="col">J</th>
                      <th scope="col">V</th>
                      <th scope="col">S</th>
                      <th scope="col">D</th>
                    </tr>
                    <tr>
                    
    
                    <th scope="row">Avant l'école</th>
					<td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=lundi_avant_ecole1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=lundi_avant_ecole1 %>.png" class="PictoCreneauPrint"></td>
					<td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=mardi_avant_ecole1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=lundi_avant_ecole1 %>.png" class="PictoCreneauPrint"></td>
					<td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=mercredi_avant_ecole1 %>" alt="Créneau non disponible"><img src="plugins/AssmatPlugin/img/<%=mercredi_avant_ecole1 %>.png" class="PictoCreneauPrint"></td>
					<td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=jeudi_avant_ecole1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=jeudi_avant_ecole1 %>.png" class="PictoCreneauPrint"></td>
					<td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=vendredi_avant_ecole1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=vendredi_avant_ecole1 %>.png" class="PictoCreneauPrint"></td>
					<td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=samedi_avant_ecole1 %>" alt="Créneau non travaillé"><img src="plugins/AssmatPlugin/img/<%=samedi_avant_ecole1 %>.png" class="PictoCreneauPrint"></td>
					<td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=dimanche_avant_ecole1 %>" alt="Créneau non travaillé"><img src="plugins/AssmatPlugin/img/<%=dimanche_avant_ecole1 %>.png" class="PictoCreneauPrint"></td>
                    </tr>
                    <tr>
                    
                     <th scope="row">Matin</th>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=lundi_matin1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=lundi_matin1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=mardi_matin1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=mardi_matin1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=mercredi_matin1 %>" alt="Créneau non disponible"><img src="plugins/AssmatPlugin/img/<%=mercredi_matin1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=jeudi_matin1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=jeudi_matin1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=vendredi_matin1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=vendredi_matin1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=samedi_matin1 %>" alt="Créneau non travaillé"><img src="plugins/AssmatPlugin/img/<%=samedi_matin1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=dimanche_matin1 %>" alt="Créneau non travaillé"><img src="plugins/AssmatPlugin/img/<%=dimanche_matin1 %>.png" class="PictoCreneauPrint"></td>
                    </tr>
                    <tr>
                      <th scope="row">Midi</th>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=lundi_midi1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=lundi_midi1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=mardi_midi1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=mardi_midi1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=mercredi_midi1 %>" alt="Créneau non disponible"><img src="plugins/AssmatPlugin/img/<%=mercredi_midi1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=jeudi_midi1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=jeudi_midi1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=vendredi_midi1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=vendredi_midi1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=samedi_midi1 %>" alt="Créneau non travaillé"><img src="plugins/AssmatPlugin/img/<%=samedi_midi1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=dimanche_midi1 %>" alt="Créneau non travaillé"><img src="plugins/AssmatPlugin/img/<%=dimanche_midi1 %>.png" class="PictoCreneauPrint"></td>
                    </tr>
                    <tr>
                    
                    
                      <th scope="row">Après midi</th>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=lundi_pm1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=lundi_pm1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=mardi_pm1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=mardi_pm1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=mercredi_pm1 %>" alt="Créneau non disponible"><img src="plugins/AssmatPlugin/img/<%=mercredi_pm1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=jeudi_pm1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=jeudi_pm1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=vendredi_pm1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=vendredi_pm1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=samedi_pm1 %>" alt="Créneau non travaillé"><img src="plugins/AssmatPlugin/img/<%=samedi_pm1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=dimanche_pm1 %>" alt="Créneau non travaillé"><img src="plugins/AssmatPlugin/img/<%=dimanche_pm1 %>.png" class="PictoCreneauPrint"></td>
                    </tr>
                    <tr>
                      <th scope="row">Après l'école</th>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=lundi_apres_ecole1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=lundi_apres_ecole1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=mardi_apres_ecole1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=mardi_apres_ecole1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=mercredi_apres_ecole1 %>" alt="Créneau non disponible"><img src="plugins/AssmatPlugin/img/<%=mercredi_apres_ecole1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=jeudi_apres_ecole1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=jeudi_apres_ecole1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=vendredi_apres_ecole1 %>" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/<%=vendredi_apres_ecole1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=samedi_apres_ecole1 %>" alt="Créneau non travaillé"><img src="plugins/AssmatPlugin/img/<%=samedi_apres_ecole1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=dimanche_apres_ecole1 %>" alt="Créneau non travaillé"><img src="plugins/AssmatPlugin/img/<%=dimanche_apres_ecole1 %>.png" class="PictoCreneauPrint"></td>
                    </tr>
                    <tr>
                      <th scope="row">Nuit</th>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=lundi_nuit1 %>" alt="Créneau non disponible"><img src="plugins/AssmatPlugin/img/<%=lundi_nuit1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=mardi_nuit1 %>" alt="Créneau non disponible"><img src="plugins/AssmatPlugin/img/<%=mardi_nuit1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=mercredi_nuit1 %>" alt="Créneau non disponible"><img src="plugins/AssmatPlugin/img/<%=mercredi_nuit1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=jeudi_nuit1 %>" alt="Créneau non disponible"><img src="plugins/AssmatPlugin/img/<%=jeudi_nuit1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=vendredi_nuit1 %>" alt="Créneau non disponible"><img src="plugins/AssmatPlugin/img/<%=vendredi_nuit1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=samedi_nuit1 %>" alt="Créneau non travaillé"><img src="plugins/AssmatPlugin/img/<%=samedi_nuit1 %>.png" class="PictoCreneauPrint"></td>
                      <td><img src="plugins/AssmatPlugin/img/s.gif" class="<%=dimanche_nuit1 %>" alt="Créneau non travaillé"><img src="plugins/AssmatPlugin/img/<%=dimanche_nuit1 %>.png" class="PictoCreneauPrint"></td>
                    </tr>
                  </table>
                
                  <ul class="legendeDispos" aria-hidden="true">
                    <li><img src="plugins/AssmatPlugin/img/s.gif" class="creneauDispo" alt="Créneau disponible"><img src="plugins/AssmatPlugin/img/creneauDispo.png" class="PictoCreneauPrint creneauDispoPrint"><trsb:glp key="PROFIL-ASSMAT-CONTENT-DISPONIBILITE-CRENEAU-DISPO" /></li>
                    <li><img src="plugins/AssmatPlugin/img/s.gif" class="creneauNonDispo creneauNonDispoPrint" alt="Créneau non disponible"><trsb:glp key="PROFIL-ASSMAT-CONTENT-DISPONIBILITE-CRENEAU-NON-DISPO" /></li>
                  </ul>
                  
                    <%} %>
                    
                    <%if(Util.notEmpty(etatDispo) && SelectionEtatDispo.IMMEDIATE.equals(etat) && Util.notEmpty(dispo.getPrecisionsPlaceDisponible())){ %>
                  <div class="precisionDispo">
                    <h3>Précisions :</h3>
                    <p><%= AssmatUtil.replaceCharSpaceBR(dispo.getPrecisionsPlaceDisponible()) %></p>
                  </div>
                  <%} %>
                  
                  
                   <%if(Util.notEmpty(etatDispo) && SelectionEtatDispo.FUTURE.equals(etat) && Util.notEmpty(dispo.getPrecisionPlaceFuture())){%>
                  <div class="precisionDispo">
                    <h3>Précisions :</h3>
                    <p><%= AssmatUtil.replaceCharSpaceBR(dispo.getPrecisionPlaceFuture())%></p>
                  </div>
                  <%} %>
                  
               

                </li>
                
                <%} %>
                </jalios:if>
                
                <!--  Si toutes les places sont à non disponibles -->
                <jalios:if predicate="<%= hasNonDispo && !hasInconnu %>">
                  <li><trsb:glp key="PROFIL-ASSMAT-LIBELLE-AUCUNE-DISPO-HTML" /></li>
                </jalios:if>
                
                <!--  Si toutes les places sont à non renseignées -->
                <jalios:if predicate="<%= hasInconnu && !hasNonDispo %>">
                  <li><trsb:glp key="PROFIL-ASSMAT-LIBELLE-NON-RENSEIGNE-HTML" /></li>
                </jalios:if>
                
                <!-- Si places inconnus et non renseignées -->
                <jalios:default>
                  <li><trsb:glp key="PROFIL-ASSMAT-LIBELLE-AUCUNE-DISPO-ET-RENSEIGNE-HTML" /></li>
                </jalios:default>
                
                
                </jalios:select>
                
              </ul>
            </div><!-- /disponibilites -->
            
            <%} %>
            
            <div class="cadreType cadreBleu prepEntretien">
              <h2><trsb:glp key="PROFIL-ASSMAT-LIBELLE-ENTRETIEN-HTML" ></trsb:glp></h2>
              <p><trsb:glp key="PROFIL-ASSMAT-CONTENT-ENTRETIEN-HTML" /></p>
             
              <%for(Place itPlace : setPlace ){%>
               
 
              <div class="blocAdresse">
                <div>
                  <strong><%=itPlace.getTitle() %></strong><br />
                  
                  <jalios:wiki><%= AssmatUtil.replaceCharSpaceBR(itPlace.getStreet()) %></jalios:wiki>
                                 
                  <%=itPlace.getZipCode() %>  <%=itPlace.getCity() %><br />
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
						     <u class="relais-courriel"><a class="gras" href="<%=lien %>">Courriel</a></u>
						     <%} %>
						     </jalios:foreach>
						     
						  </jalios:if>
               <jalios:if predicate="<%=Util.notEmpty(itPlace.getWebsites()) %>">
							     <div class="relais-mail">
								     <jalios:foreach name="itSite" type="String" array="<%= itPlace.getWebsites()%>">
								        <a class="gras" target='_blank' href="<%=itSite%>">Site internet</a>
								     </jalios:foreach>
							     </div>
							  </jalios:if>
              </div>
               <%}%>
            </div>
            
          </div>
          <!-- /COLONNE GAUCHE -->
          
          <!-- COLONNE DROITE -->
          <div class="span6 offset1 colDroite">
               
               
               
          <div class="distance">

            <jalios:if predicate="<%= hasQuartierMam || hasQuartierDom || distanceMam != -1 || distanceDom != -1 %>">
              <img style="vertical-align: top;" src='plugins/AssmatPlugin/img/target-with-circle.png'/> 
            </jalios:if>
            
            <div style="display: inline-block; line-height: normal;">         
		 
		          <%
		             boolean isMamAndDomDistance = distanceMam != -1 && distanceDom != -1;
		             boolean isMamAndDomQuartier = hasQuartierMam && hasQuartierDom;
		             boolean has2info = isMamAndDomQuartier || isMamAndDomDistance || (distanceMam != -1 && hasQuartierDom) || (distanceDom != -1 && hasQuartierMam) ;
		          %>
		 
		          <%-- Mam --%>
		          <jalios:if predicate='<%= hasQuartierMam %>'>
		             <div class="quartier"><%= has2info ? "Mam - " : "" %>Quartier : <%= QuartierDAO.getLibQuartier(QuartierDAO.getIdQuartier(asmmatSolis.getIdMicroQuartierMam())) %> 
		                <jalios:if predicate="<%= Util.notEmpty(QuartierDAO.getLibMicroQuartier(asmmatSolis.getIdMicroQuartierMam())) %>">
		                  (<%= QuartierDAO.getLibMicroQuartier(asmmatSolis.getIdMicroQuartierMam()) %>)
		                </jalios:if>
		             </div>
		          </jalios:if>  
		               
		          <jalios:if predicate='<%= distanceMam != -1 %>'>        
		            <div><%= has2info && !hasQuartierMam ? "Mam : " : "" %>à <%= nf.format(distanceMam) %> km de l'adresse recherchée</div>
		          </jalios:if>
		          
		          <%--Domicile--%>
              <jalios:if predicate='<%= hasQuartierDom %>'>
                 <div class="quartier"><%= has2info ? "Domicile - " : "" %>Quartier : <%= QuartierDAO.getLibQuartier(QuartierDAO.getIdQuartier(asmmatSolis.getIdMicroQuartierDom())) %> 
                    <jalios:if predicate="<%= Util.notEmpty(QuartierDAO.getLibMicroQuartier(asmmatSolis.getIdMicroQuartierDom())) %>">
                      (<%= QuartierDAO.getLibMicroQuartier(asmmatSolis.getIdMicroQuartierDom()) %>)
                    </jalios:if>
                 </div>
              </jalios:if>     
                   
              <jalios:if predicate='<%= distanceDom != -1 %>'>        
                <div><%= has2info && !hasQuartierDom ? "Domicile : " : "" %>à <%= nf.format(distanceDom) %> km de l'adresse recherchée</div>
              </jalios:if>
		          
		          
		        </div>
		        
          </div>
            
            <!-- Ici la map -->
   
       <div class="carto">
       
       
       
                    <% request.setAttribute("gMapsType", "medium"); 
                    PointAssmat assmatPointfull = new PointAssmat(String.valueOf(profilAM.getLatitudeAssmat()), String.valueOf(profilAM.getLongitudeAssmat()),"#AEC900" );
                    PointAssmat assmatPointfullMAM = new PointAssmat(String.valueOf(profilAM.getLatitudeMAM()), String.valueOf(profilAM.getLongitudeMAM()),"#AEC900" );

                    // Le point1 doit être rensigné
                    if( asmmatSolis.getExerceDomicile() != null && asmmatSolis.getExerceDomicile() && (assmatPointfull.getLatitude() != 0 || assmatPointfull.getLatitude() != 0) ) {
                      request.setAttribute("point", assmatPointfull);
                      if(asmmatSolis.getExerceMam() != null && asmmatSolis.getExerceMam() && (assmatPointfullMAM.getLatitude() != 0 || assmatPointfullMAM.getLatitude() != 0) ) {
                        request.setAttribute("point2", assmatPointfullMAM);
                      }
                    }else if(asmmatSolis.getExerceMam() != null && asmmatSolis.getExerceMam() && (assmatPointfullMAM.getLatitude() != 0 || assmatPointfullMAM.getLatitude() != 0)){
                      request.setAttribute("point", assmatPointfullMAM);
                    }
                    
                    
                    
                    %>
                    <%@ include file='/plugins/AssmatPlugin/jsp/recherche/map/mapAssmatDisplay.jsp'%>
                  <% request.removeAttribute("gMapsType"); %>
                  <% request.removeAttribute("listPoint");
                  request.removeAttribute("point");
                  request.removeAttribute("point2");
                  %>
      
                    </div>
          
          
            <div class="blocStd blocAgrement">
              <h2><trsb:glp key="PROFIL-ASSMAT-LIBELLE-AGREMENT-HTML" ></trsb:glp></h2>
              <p><trsb:glp key="PROFIL-ASSMAT-CONTENT-AGREMENT-PREMIER" /><%=formater.format(asmmatSolis.getDatePremierAgrement()) %></p>
              <p><trsb:glp key="PROFIL-ASSMAT-CONTENT-AGREMENT-TYPE" /></p>
              
              
                                       
              <jalios:if predicate='<%= asmmatSolis.getExerceDomicile() %>'>            	              	                            
                   <p>A <trsb:glp key="VERIF-LIEU-EXERCICE-DOM"></trsb:glp></p>	              		              
		               <ul class="typeAgrement">
		               
		                     <% for(int itPlace=1; itPlace<=8; itPlace++) { %>		                        
		                          <%
		                          // agrementTrancheAgeKey
		                          Field agrementTracheAgeKeyField = ReflectUtil.getField(asmmatSolis.getClass(), "place"+itPlace+"AgrementTrancheAgeKey");
		                          String agremenTracheAgeKey = (String) ReflectUtil.getFieldValue(asmmatSolis, agrementTracheAgeKeyField);
		                          
		                          // placeTracheAgeKey
		                          Field placeTracheAgeKeyField = ReflectUtil.getField(asmmatSolis.getClass(), "place"+itPlace+"TrancheAgeKey");
		                          Integer placeTracheAgeKey = (Integer) ReflectUtil.getFieldValue(asmmatSolis, placeTracheAgeKeyField);    
		                          
		                          // PlaceNbPlaces
		                          Field placeNbPlacesField = ReflectUtil.getField(asmmatSolis.getClass(), "place"+itPlace+"NbPlaces");
		                          Integer placeNbPlaces = (Integer) ReflectUtil.getFieldValue(asmmatSolis, placeNbPlacesField);
		    
		                          // placeTrancheAge
		                          Field placeTrancheAgeField = ReflectUtil.getField(asmmatSolis.getClass(), "place"+itPlace+"TrancheAge");
		                          String placeTrancheAge = (String) ReflectUtil.getFieldValue(asmmatSolis, placeTrancheAgeField);
		    
		                          // PlaceLibCompl
		                          Field placeLibComplField = ReflectUtil.getField(asmmatSolis.getClass(), "place"+itPlace+"LibCompl");
		                          String placeLibCompl = (String) ReflectUtil.getFieldValue(asmmatSolis, placeLibComplField);
		                          
		                          // SaisieDisponibilite
                              Field placeSaisieDisponibiliteField = ReflectUtil.getField(asmmatSolis.getClass(), "place"+itPlace+"SaisieDisponibilite");
                              Boolean placeSaisieDisponibilite = (Boolean) ReflectUtil.getFieldValue(asmmatSolis, placeSaisieDisponibiliteField);
		                          %>
		                        
		                          <% if(Util.notEmpty(agremenTracheAgeKey) && agremenTracheAgeKey.contains("dom-") && placeSaisieDisponibilite ){%>                   
		                           <li class="enfant enfant<%=placeNbPlaces%>">
		                           		<img src="plugins/AssmatPlugin/img/icon-enfant<%=placeNbPlaces%>.png" class="pictoPrint">
		                           		<%=JcmsUtil.glp(channel.getCurrentUserLang(), "jcmsplugin.assmatplugin.inscription.verification.place", placeNbPlaces)%>
		                           		<%= AssmatUtil.getTitlePlace(placeTrancheAge, placeLibCompl, placeTracheAgeKey) %>
		                           </li>
		                          <%} %>		                        
		                     <%} %> 
		                 </ul>  		                         
              </jalios:if>
              
              
              <jalios:if predicate='<%=  asmmatSolis.getExerceMam() != null && asmmatSolis.getExerceMam() %>'>  
                   <p>En <trsb:glp key="VERIF-LIEU-EXERCICE-MAM"></trsb:glp></p>           
                   <ul class="typeAgrement">
                   
                         <% for(int itPlace=1; itPlace<=8; itPlace++) { %>                          
                              <%
                              // agrementTrancheAgeKey
                              Field agrementTracheAgeKeyField = ReflectUtil.getField(asmmatSolis.getClass(), "place"+itPlace+"AgrementTrancheAgeKey");
                              String agremenTracheAgeKey = (String) ReflectUtil.getFieldValue(asmmatSolis, agrementTracheAgeKeyField);
                              
                              // placeTracheAgeKey
                              Field placeTracheAgeKeyField = ReflectUtil.getField(asmmatSolis.getClass(), "place"+itPlace+"TrancheAgeKey");
                              Integer placeTracheAgeKey = (Integer) ReflectUtil.getFieldValue(asmmatSolis, placeTracheAgeKeyField);    
                              
                              // PlaceNbPlaces
                              Field placeNbPlacesField = ReflectUtil.getField(asmmatSolis.getClass(), "place"+itPlace+"NbPlaces");
                              Integer placeNbPlaces = (Integer) ReflectUtil.getFieldValue(asmmatSolis, placeNbPlacesField);
        
                              // placeTrancheAge
                              Field placeTrancheAgeField = ReflectUtil.getField(asmmatSolis.getClass(), "place"+itPlace+"TrancheAge");
                              String placeTrancheAge = (String) ReflectUtil.getFieldValue(asmmatSolis, placeTrancheAgeField);
        
                              // PlaceLibCompl
                              Field placeLibComplField = ReflectUtil.getField(asmmatSolis.getClass(), "place"+itPlace+"LibCompl");
                              String placeLibCompl = (String) ReflectUtil.getFieldValue(asmmatSolis, placeLibComplField);
                              
                              // SaisieDisponibilite
                              Field placeSaisieDisponibiliteField = ReflectUtil.getField(asmmatSolis.getClass(), "place"+itPlace+"SaisieDisponibilite");
                              Boolean placeSaisieDisponibilite = (Boolean) ReflectUtil.getFieldValue(asmmatSolis, placeSaisieDisponibiliteField);
                              %>
                            
                              <% if(Util.notEmpty(agremenTracheAgeKey) && agremenTracheAgeKey.contains("mam-") && placeSaisieDisponibilite){%>                   
                               <li class="enfant<%=placeNbPlaces%>">
                               		<img src="plugins/AssmatPlugin/img/icon-enfant<%=placeNbPlaces%>.png" class="pictoPrint">
                               		<%=JcmsUtil.glp(channel.getCurrentUserLang(), "jcmsplugin.assmatplugin.inscription.verification.place", placeNbPlaces)%>
                               		<%= AssmatUtil.getTitlePlace(placeTrancheAge, placeLibCompl, placeTracheAgeKey) %>
                               </li>
                              <%} %>                           
                         <%} %> 
                     </ul>                               
              </jalios:if>
              
              
              
              
            
            </div>
            
            <div class="blocStd blocFormation">
              <h2><trsb:glp  key="PROFIL-ASSMAT-LIBELLE-FORMATION-HTML" ></trsb:glp></h2>
             
              
              <trsb:glp  key="PROFIL-ASSMAT-CONTENT-FORMATION-HTML" ></trsb:glp>
              
              
            </div>
            
            <jalios:if predicate='<%=
                 (
                    Util.notEmpty(profilAM.getAccueilPeriscolaire()) && "true".equalsIgnoreCase(profilAM.getAccueilPeriscolaire()) ||
                    Util.notEmpty(profilAM.getAccueilMercredi()) && "true".equalsIgnoreCase(profilAM.getAccueilMercredi()) ||
                    Util.notEmpty(profilAM.getAccueilPendantLesVacancesSco()) && "true".equalsIgnoreCase(profilAM.getAccueilPendantLesVacancesSco()) ||
                    Util.notEmpty(profilAM.getHorairesAtypiques()) && "true".equalsIgnoreCase(profilAM.getHorairesAtypiques())
                 )%>'>
	            <div class="blocStd">
	              <h2><trsb:glp  key="PROFIL-ASSMAT-LIBELLE-TYPE-ACCUEIL-HTML" ></trsb:glp></h2>
	              <ul class="typeAccueil">
	              <%if(Util.notEmpty(profilAM.getAccueilPeriscolaire()) && "true".equalsIgnoreCase(profilAM.getAccueilPeriscolaire())){ %>
	                <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-PERISCOLAIRE" /></li>
	               <%} %> 
	                 <%if(Util.notEmpty(profilAM.getAccueilMercredi()) && "true".equalsIgnoreCase(profilAM.getAccueilMercredi())){ %>
	                <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-MERCREDI" /></li>
	               <%} %> 
	              <%if(Util.notEmpty(profilAM.getAccueilPendantLesVacancesSco()) && "true".equalsIgnoreCase(profilAM.getAccueilPendantLesVacancesSco())){ %>
	                <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-VACANCES" /></li>
	               <%} %> 
	            <%if(Util.notEmpty(profilAM.getHorairesAtypiques()) && "true".equalsIgnoreCase(profilAM.getHorairesAtypiques())){ %>
	               <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE" />
	                  <ul>
	                  <%if(Util.notEmpty(profilAM.getAvant7h()) && "true".equalsIgnoreCase(profilAM.getAvant7h())){ %>
	                    <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-MATIN" /></li>
	                    <%} %>
	                     <%if(Util.notEmpty(profilAM.getApres20h()) && "true".equalsIgnoreCase(profilAM.getApres20h())){ %>
	                    <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-SOIR" /></li>
	                     <%} %>
	                     <%if(Util.notEmpty(profilAM.getLeSamedi()) && "true".equalsIgnoreCase(profilAM.getLeSamedi())){ %>
	                    <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-SAMEDI" /></li>
	                     <%} %>
	                     <%if(Util.notEmpty(profilAM.getLeDimanche()) && "true".equalsIgnoreCase(profilAM.getLeDimanche())){ %>
	                    <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-DIMANCHE" /></li>
	                     <%} %>
	                     <%if(Util.notEmpty(profilAM.getLaNuit()) && "true".equalsIgnoreCase(profilAM.getLaNuit())){ %>
	                    <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-NUIT" /></li>
	                     <%} %>
	                    
	                  </ul>
	                </li>
	               <%} %> 
	               
	              </ul>
	            </div>
            </jalios:if>
            
               <%if((Util.notEmpty(profilAM.getAccueilEnfantHandicap()) && "true".equalsIgnoreCase(profilAM.getAccueilEnfantHandicap()) 
                   || Util.notEmpty(profilAM.getLogementAccessible()) && "true".equalsIgnoreCase(profilAM.getLogementAccessible())
                   || Util.notEmpty(profilAM.getAccueilTempsPartiel()) && "true".equalsIgnoreCase(profilAM.getAccueilTempsPartiel())
                   || Util.notEmpty(profilAM.getAccepteDepannage()) && "true".equalsIgnoreCase(profilAM.getAccepteDepannage())
                   
                   )){ %>
            <div class="blocStd">
              <h2><trsb:glp  key="PROFIL-ASSMAT-LIBELLE-SPECIFICITE-HTML" ></trsb:glp></h2>              
              
              <div class="specificitesAssMat">
               <%if(Util.notEmpty(profilAM.getAccueilEnfantHandicap()) && "true".equalsIgnoreCase(profilAM.getAccueilEnfantHandicap())){ %>
                <div class="sp spHandi">
                  <p><img src="plugins/AssmatPlugin/img/picto-handicape.png" class="pictoPrint"></p><trsb:glp key="PROFIL-ASSMAT-CONTENT-SPECIFICITE-HANDICAP" /> 
                  <jalios:if predicate="<%= Util.notEmpty(profilAM.getPrecisionsEnfantHandicap()) %>">
                    <%-- Pas de taglib wiki car une assmat ne peut pas contribuer de balise WIKI --%>
                    <p class="commentaire-specificite"><%= AssmatUtil.replaceCharSpaceBR(profilAM.getPrecisionsEnfantHandicap()) %></p>
                  </jalios:if> 
                </div>
                <%} %>
                 <%if(Util.notEmpty(profilAM.getLogementAccessible()) && "true".equalsIgnoreCase(profilAM.getLogementAccessible())){ %>
                <div class="sp spLogAcc">
                  <p><img src="plugins/AssmatPlugin/img/picto-accessibilite.png" class="pictoPrint"><trsb:glp key="PROFIL-ASSMAT-CONTENT-SPECIFICITE-ACCESSIBLE" /> 
                  <jalios:if predicate="<%= Util.notEmpty(profilAM.getPrecisionsLogementAccessible()) %>">
                    <%-- Pas de taglib wiki car une assmat ne peut pas contribuer de balise WIKI --%>
                    <p class="commentaire-specificite"><%= AssmatUtil.replaceCharSpaceBR(profilAM.getPrecisionsLogementAccessible()) %></p>
                  </jalios:if>    
                </div>
                <%} %>
                  <%if(Util.notEmpty(profilAM.getAccueilTempsPartiel()) && "true".equalsIgnoreCase(profilAM.getAccueilTempsPartiel())){ %>
                <div class="sp spTmpPart">
                	<p><img src="plugins/AssmatPlugin/img/picto-temps-partiel.png" class="pictoPrint"></p><trsb:glp key="PROFIL-ASSMAT-CONTENT-SPECIFICITE-PARTIEL" />
                </div>
                <%} %>
                  <%if(Util.notEmpty(profilAM.getAccepteDepannage()) && "true".equalsIgnoreCase(profilAM.getAccepteDepannage())){ %>
                <div class="sp spDepann">
                  <p><img src="plugins/AssmatPlugin/img/picto-depannage.png" class="pictoPrint"></p><trsb:glp key="PROFIL-ASSMAT-CONTENT-SPECIFICITE-REMPLACEMENT" />
                  <jalios:if predicate="<%= Util.notEmpty(profilAM.getPrecisionsDepannage()) %>">
                    <%-- Pas de taglib wiki car une assmat ne peut pas contribuer de balise WIKI --%>
                    <p class="commentaire-specificite"><%= AssmatUtil.replaceCharSpaceBR(profilAM.getPrecisionsDepannage()) %></p>
                  </jalios:if>
               </div>
               <%} %>
                
              </div>
            </div>
            <%} %>
          </div>
          <!-- /COLONNE DROITE -->
          
        </div>
        <!-- /COLONNAGE -->
        
        <div class="row-fluid">
          <div class="span12">
              <ul class="btnNavHz">
            
              <li class="retour"><a href="<%=urlRetour %>" class="btnNoirType btnRetourRecherche">
              <%if(isSelection){%>
                 <trsb:glp  key="PROFIL-ASSMAT-LIBELLE-RETOUR-SELECTION-HTML" ></trsb:glp>
             
              <%}else{%>             
              <trsb:glp  key="PROFIL-ASSMAT-LIBELLE-RETOUR-RECHERCHE-HTML" ></trsb:glp>
              <%} %>
               </a></li>
               
              <%if(Util.notEmpty(profilPrecedent)){ %>
              <li class=""><a href="<%=profilPrecedent.getDisplayUrl(userLocale) %>?portal=<%=idPortalRecherche%>" class="btnNoirType btnPrec"><trsb:glp  key="PROFIL-ASSMAT-LIBELLE-FICHE-PRECED-HTML" ></trsb:glp></a></li>
              <%} %>
                <%if(Util.notEmpty(profilSuivant)){ %>
              <li class="btnSuivant"><a href="<%=profilSuivant.getDisplayUrl(userLocale) %>?portal=<%=idPortalRecherche%>" class="btnNoirType btnSuiv"><trsb:glp  key="PROFIL-ASSMAT-LIBELLE-FICHE-SUIV-HTML" ></trsb:glp></a></li>
               <%} %>
            </ul>
          </div>
        </div>
        <!-- /boutons de navigation horizontale -->

    </div> <!-- /AM -->