<%@page import="fr.cg44.plugin.assmat.managers.SmsDAO"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.tools.facetedsearch.manager.CityManager"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.commons.lang.time.DateUtils"%>
<%@ page contentType="text/html; charset=UTF-8"%><%@page
  import="fr.cg44.plugin.assmat.hibernate.HibernateCD44Util"%>
<%@page import="fr.cg44.plugin.assmat.beans.DispoAssmat"%>
<%@page import="fr.cg44.plugin.assmat.beans.AssmatSearch"%>
<%@page import="fr.cg44.plugin.assmat.managers.AssmatSearchDAO"%>
<%@ include file='/jcore/doInitPage.jsp'%>
<%@ include file='/jcore/portal/doPortletParams.jsp'%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>


<%
  jcmsContext.addCSSHeader("plugins/ToolsPlugin/css/facets/citiesFacet.css");
  jcmsContext.addCSSHeader("plugins/ToolsPlugin/css/types/PortletFacetedSearch/doPortletFacetedSearchFullDisplay.css");
  jcmsContext.addCSSHeader("plugins/ToolsPlugin/css/facets/categoriesSelectFacet.css");
  jcmsContext.addJavaScript("plugins/AssmatPlugin/js/eventFacilities.js");
  jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");
  
  String idPortailRecherche = channel.getProperty("jcmsplugin.assmatplugin.portal.recherche.am");
  Publication portalRecherche = channel.getPublication(idPortailRecherche);
 
  PortletJsp box = (PortletJsp) portlet;
%>

<%@ include file='/plugins/AssmatPlugin/jsp/recherche/parameters.jspf'%>

<div class="insert rechercheAMAccueil"
  style="border: 2px solid #aec900">
  <div class="facets">
   <div class="enteteSearch">
    <h2><%= box.getDisplayTitle(userLang) %></h2>
    
    <% String[] tabParameters = new String[]{channel.getAllDataSet(ProfilASSMAT.class).size()+""}; %>
    <p class="nbAM"><%= glp("jcmsplugin.assmatplugin.accueil.recherche.nb.am",channel.getAllDataSet(ProfilASSMAT.class).size()) %></p>
</div>
    <form
      action="<%=portalRecherche.getDisplayUrl(userLocale)%>"
      method="get" name="search" id="formSearchAsmmatAccueil" class="noSingleSubmitButton facets">


 <div class="row-fluid 1">
      <!--      COMMUNE -->
      <div class="span4">
      <fieldset class="citiesFacet" id="cityFieldSet">
        <legend><%= glp("jcmsplugin.assmatplugin.accueil.recherche.commune.label") %></legend>
        <input type="hidden" name="idCommune" id="idCommune"  size="14" value="<%= encodeForHTMLAttribute(communeId) %>" />
        <input type="hidden" name="codeInsee" id="codeInsee"  size="14" value="<%= codeInsee %>" />
        <input type="hidden" name="cities" id="cityId" size="14" value="<%= encodeForHTMLAttribute(communeCode) %>">
        <input type='hidden' name="longitude" id="longitude" value="<%= encodeForHTMLAttribute(longitude) %>"/>
  		<input type='hidden' name="latitude" id="latitude" value="<%= encodeForHTMLAttribute(latitude) %>"/>
  		
        <div class="inputwithaction">
          <label class="hide-accessible" for="cityName"><%= glp("jcmsplugin.assmatplugin.accueil.recherche.commune.placeholder") %></label> 
            <input type="text"  name="cityName" id="cityName"
            class="typeahead autoCompleteCityJSVerif" size="14" maxlength="30"
            title="Saisissez une commune"  placeholder="Saisissez une commune"
            value="<%= encodeForHTMLAttribute(commune) %>"
            data-longitude=""
            data-latitude=""
            data-jalios-ajax-refresh-url="plugins/ToolsPlugin/jsp/facets/acsearchCity.jsp?activeBorderingCities=false">
          <button id="resetCity" class="resetCross hide" type="button"
            title="Vider le champ : Recherche par communes">
            <img src="s.gif" class="spr-select_cross"
              alt="Vider le champ : Recherche par communes"
              title="Vider le champ : Recherche par communes">
          </button>
        </div>
      </fieldset>
      </div>

      <!--      PAR ADRESSE -->
       <div class="span4">
      <fieldset class="citiesFacet adresseFacet">
        <legend><%= glp("jcmsplugin.assmatplugin.accueil.recherche.rue.label") %><span class="facultatif" > (facultatif)</span></legend>
        <div class="inputwithaction readonly">
          <label class="hide-accessible" for="title"><%= glp("jcmsplugin.assmatplugin.accueil.recherche.rue.placeholder") %></label> 
            <input readonly type="text" name="adresse" id="adresse"
            style="width:83% !important;"
            class="typeahead adresseAutoComplete" size="14" maxlength="30"
            title="Précisez une adresse" placeholder='<%= encodeForHTML(glp("jcmsplugin.assmatplugin.accueil.recherche.rue.placeholder")) %>'
            value=""
            data-longitude="<%= encodeForHTMLAttribute(longitude) %>"
            data-latitude="<%= encodeForHTMLAttribute(latitude) %>"
            data-jalios-ajax-refresh-url="plugins/AssmatPlugin/jsp/autocomplete/acAdresseInCity.jsp"
            />
            <button id="resetAddress" class="resetCross hide" onclick="return false;"
				title="Vider le champ : Recherche par noms">
				<img src="s.gif" class="spr-select_cross"
					alt="Vider le champ : Recherche par noms"
					title="Vider le champ : Recherche par noms">
			</button>

        </div>
      </fieldset>
</div>
      <!--      PAR PERIMETRE -->

      <%Category categDistance = channel.getCategory("$jcmsplugin.assmatplugin.categ.distance"); %>
      <% Set<Category> setCategory = new TreeSet(Category.getDeepOrderComparator()); %>
      <% setCategory.addAll(categDistance.getChildrenSet()); %>
       <div class="span4">
      <fieldset class="perimetre">
        <legend><%= glp("jcmsplugin.assmatplugin.accueil.recherche.perimetre.label") %></legend>
        <div class="form-select readonly">
          <span class="input-box" style="background-color: #b0cc00;"><span
            class="spr-select_arrow"></span></span>
            <select disabled id="listePerimetre"
            name="distance" title='<%= encodeForHTMLAttribute(glp("jcmsplugin.assmatplugin.accueil.recherche.perimetre.title")) %>'>
            
            <%
			/* Pas de recherche par quartier sur la page d'accueil
			 * Les distances courtes (200m, 500m, 1km) ne sont dispos que si on a saisi une adresse.
			 * 12/04/18 : finalement aucune distance ne pourra être renseignée si l'adresse est vide.
			 * On mettra donc la propriété "distanceCourte" à toutes les catégories "distance", pour ne pas casser le code.
			*/
			boolean afficheQuartiersDansCommune = Util.notEmpty(request.getParameter("codeInsee")) && request.getParameter("codeInsee").equals("44109");
			boolean afficheDistancesCourtes = Util.notEmpty(request.getParameter("adresse"));
			%>
						
            <jalios:foreach name="itCat" type="Category" collection="<%=setCategory %>">
            	<jalios:if predicate="<%=itCat.canBeReadBy(loggedMember) %>">
	            	<jalios:if predicate='<%=!itCat.getExtraData("extra.Category.plugin.corporateIdentity.pageTitleValue").contains("notAccueil") %>'>
	              		<%
			            // On contrôle les catégories ayant la propriété "checkAffichage" (Quartier + Microquartiers)
			            String style="";
			            if((itCat.getExtraData("extra.Category.plugin.corporateIdentity.pageTitleValue").contains("checkAffichage") && !afficheQuartiersDansCommune) ||
			            	itCat.getExtraData("extra.Category.plugin.corporateIdentity.pageTitleValue").contains("distanceCourte")	&& !afficheDistancesCourtes){
			            	style="hideOpt";
			            }
			            %>
			              <option
			              	class="<%=itCat.getExtraData("extra.Category.plugin.corporateIdentity.pageTitleValue") %> <%=style%>"
							<%if(Integer.toString(distance).equals(itCat.getExtraData("extra.Category.jcmsplugin.assmatplugin.search.mapping"))){ %>
			                	selected <%} %>
			                value="<%=itCat.getExtraData("extra.Category.jcmsplugin.assmatplugin.search.mapping") %>"><%=itCat.getName() %>
						  </option>
					</jalios:if>
				</jalios:if>
            </jalios:foreach>
          </select>
        </div>
      </fieldset>
</div>
</div>

 <div class="row-fluid ageDateEnfant">          
            
	<%-- Tranche d'age --%>
	<%Category categAge = channel.getCategory("$jcmsplugin.assmatplugin.categ.trancheage"); %>
    <div class="span4 offset4">
    	<fieldset class="ageEnfant">
        	<legend><%= glp("jcmsplugin.assmatplugin.accueil.recherche.age.label") %></legend>
        	<div class="form-select  form-select-age">
            	<span class="input-box" style="background-color: #b0cc00;">
            		<span class="spr-select_arrow"></span>
            	</span> 
                
                <select class="ajax-refresh selectTranche" name="age"
                		title='<%= encodeForHTMLAttribute(glp("jcmsplugin.assmatplugin.accueil.recherche.perimetre.title")) %>'>
	                <jalios:foreach name="itCat" type="Category" collection="<%=categAge.getChildrenSet() %>">
	                    <option value="<%=itCat.getExtraData("extra.Category.jcmsplugin.assmatplugin.search.mapping") %>"><%=itCat.getName() %></option>
	                </jalios:foreach>
            	</select>
        	</div>              
		</fieldset>
	</div>

 	<div class="span4">
		<fieldset class="nbPlacesDispo">
			<legend><%= glp("jcmsplugin.assmatplugin.accueil.recherche.aPartirDe.label") %></legend>
			<div class="form-select form-select-date">
				<span class="input-box" style="background-color: #b0cc00;">
					<span class="spr-select_arrow"></span>
				</span>
				<select class="ajax-refresh selectDatePlace" name="month"
                        title='<%= encodeForHTMLAttribute(glp("jcmsplugin.assmatplugin.accueil.recherche.aPartirDe.title")) %>'>
                        <%
                        java.util.Date dateRef = new java.util.Date();
						SimpleDateFormat formatterTLE = new SimpleDateFormat("MMMM yyyy");
						java.util.Date dateTLE = new java.util.Date(dateRef.getTime());
                        %>
                        <%
						for (int iterator = 1; iterator < 13; iterator++) {
                        %>
							<option 
                              <%if(iterator ==1){ %>
                                selected <%} %>
                              value="<%= Long.toString(dateTLE.getTime()).substring(0, Long.toString(dateTLE.getTime()).length() - 8) + "00000000"  %>"><%=StringUtils.capitalize(formatterTLE.format(dateTLE))%>
                             </option>
                        <%
                           dateTLE = DateUtils.addMonths(dateTLE, 1);
                            }
                        %>
                    </select>
                </div>
		</fieldset>
	</div>
           
            
</div>
 <div class="row-fluid lanceSearch">  
  <div class="span4 offset8">
  	<div class="submitSearchContainer">  
	     <div class="submit submitSearch">
	       <label for="submit"> <input onClick="jQuery.plugin.AssmatPlugin.submitForm()" type="button" id="submitForm"
	         name="isSearch" value='<%= encodeForHTMLAttribute(glp("jcmsplugin.assmatplugin.accueil.recherche.launch")) %>' class="submitButton">
	       </label>
	     </div>
	     <div class="champsOblig"><%= glp("plugin.assmatplugin.mention.champ.obligatoire") %></div>
     </div>
  </div>

</div>


 <div class="row-fluid"> 
  <div class="span12" >
  	<ul class="rechercheAvancee">
     <li><a href="<%=portalRecherche.getDisplayUrl(userLocale)%>"><%= glp("jcmsplugin.assmatplugin.accueil.recherche.advancedSearch") %></a></li>
     </ul>
  </div> 
 </div> 
  <%-- Par défaut la case 'sans disponibilité' n'est pas coché, comme placé avant l'input n'est pas priritaire sur celui-ci une fois un choix fait --%>
  <input class="noSubmitEven" type="hidden" name="withNonDispo" value="false">
  <input class="noSubmitEven" type="hidden" name="withDispoNonRenseigne" value="false">

  <!--  <input type='hidden' name="geoLong" id="geoLongId" value=""/>-->
  <!--	<input type='hidden' name="geoLat" id="geoLatId" value=""/>-->
  <input type='hidden' name="isSearch" id="isSearch" value="Ok"/>

  <input type='hidden' name="hashKey" id="hashKey" value="<%=hashKey%>"/>
  

    </form>
  </div>
  <img src="plugins/AssmatPlugin/img/scene.png" class="scene"/>
</div>

<jalios:javascript>

jQuery(document).ready(function() {
  jQuery('#AjaxCtxtDeflate').removeAttr("id");
  jQuery('body').removeAttr("id");
  jQuery('#CSRFTokenElm').removeAttr("id"); 
});

</jalios:javascript>