<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.tools.facetedsearch.manager.CityManager"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.commons.lang.time.DateUtils"%>
<%@page	import="fr.cg44.plugin.assmat.hibernate.HibernateCD44Util"%>
<%@page import="fr.cg44.plugin.assmat.beans.DispoAssmat"%>
<%@page import="fr.cg44.plugin.assmat.beans.AssmatSearch"%>
<%@page import="fr.cg44.plugin.assmat.managers.AssmatSearchDAO"%>
<%@ include file='/jcore/doInitPage.jsp'%>



<%
  jcmsContext.addCSSHeader("plugins/ToolsPlugin/css/facets/citiesFacet.css");
  jcmsContext.addCSSHeader("plugins/ToolsPlugin/css/types/PortletFacetedSearch/doPortletFacetedSearchFullDisplay.css");
  jcmsContext.addCSSHeader("plugins/ToolsPlugin/css/facets/categoriesSelectFacet.css");
  jcmsContext.addJavaScript("plugins/AssmatPlugin/js/eventFacilities.js");
  jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");
  jcmsContext.addJavaScript("plugins/AssmatPlugin/js/rechercheAvancee.js");
%>

<%@ include file='/plugins/AssmatPlugin/jsp/recherche/parameters.jspf'%>

<div class="insert portletRechercheAssmat"
	style="border: 2px solid #aec900">
	<div class="facets">
		<h2><%=  glp("jcmsplugin.assmatplugin.recherche.am.form.title") %></h2>
		<form
			action="<%=ServletUtil.getResourcePath(request)%>"
			method="get" name="search" id="formSearchAsmmat" class="noSingleSubmitButton facets">

			<!--	COMMUNE -->
			<%
			String currentCityLong = "";
			String currentCityLat = "";

			if(Util.notEmpty(communeId)){
				City currentCity = (City)channel.getPublication(communeId);
				currentCityLong = currentCity.getExtraData("extra.City.plugin.tools.geolocation.longitude");
				currentCityLat = currentCity.getExtraData("extra.City.plugin.tools.geolocation.latitude");
			}

			%>
			<fieldset class="citiesFacet" id="cityFieldSet">
				<legend><%= glp("jcmsplugin.assmatplugin.recherche.am.commune.label") %></legend>
				<input type="hidden" name="idCommune" id="idCommune"  size="14" value="<%= encodeForHTMLAttribute(communeId) %>" />
                <input type="hidden" name="codeInsee" id="codeInsee"  size="14" value="<%= codeInsee %>" />
				<input type="hidden" name="cities" id="cityId" size="14" value="<%= encodeForHTMLAttribute(communeCode) %>">
				<input type='hidden' name="longitude" id="longitude" value="<%= encodeForHTMLAttribute(longitude) %>"/>
  				<input type='hidden' name="latitude" id="latitude" value="<%= encodeForHTMLAttribute(latitude) %>"/>
				<div class="inputwithaction">
					<label class="hide-accessible" for="cityName"><%= glp("jcmsplugin.assmatplugin.recherche.am.commune.placeholder") %></label> 
						<input type="text"  name="cityName" id="cityName"
						<%=Util.notEmpty(commune)?"readonly":"" %>
						class="typeahead autoCompleteCityJSVerif" size="14" maxlength="30"
						title='<%= encodeForHTMLAttribute(glp("jcmsplugin.assmatplugin.recherche.am.commune.placeholder")) %>'  placeholder='<%= encodeForHTMLAttribute(glp("jcmsplugin.assmatplugin.recherche.am.commune.placeholder")) %>'
						value="<%= encodeForHTMLAttribute(commune) %>"
						data-longitude="<%= currentCityLong %>"
						data-latitude="<%= currentCityLat %>"
						data-jalios-ajax-refresh-url="plugins/ToolsPlugin/jsp/facets/acsearchCity.jsp?activeBorderingCities=false">
					<button id="resetCity" class="resetCross <%=Util.isEmpty(commune) ? "hide" : ""%>" type="button"
						title="Vider le champ : Recherche par commune">
						<img src="s.gif" class="spr-select_cross"
							alt="Vider le champ : Recherche par commune"
							title="Vider le champ : Recherche par commune">
					</button>
				</div>
			</fieldset>

	 
			<!--	PAR ADRESSE -->
			<fieldset class="citiesFacet adresseFacet">
				<legend><%= glp("jcmsplugin.assmatplugin.recherche.am.rue.label") %> <span class="facultatif"> (facultatif)</span></legend>
				
				<div class="inputwithaction <%=Util.notEmpty(adresse) ? "readonly" : ""%>">
					<label class="hide-accessible" for="adresse"><%= glp("jcmsplugin.assmatplugin.recherche.am.rue.placeholder") %></label>
					<input type="text" name="adresse" id="adresse" onChange="jQuery.plugin.AssmatPlugin.submitForm()"
						<%=Util.notEmpty(adresse)?"readonly":"" %>
						style="width:83% !important;"
						class="typeahead adresseAutoComplete"
						title='<%= encodeForHTMLAttribute(glp("jcmsplugin.assmatplugin.recherche.am.rue.placeholder")) %>' placeholder='<%= encodeForHTMLAttribute(glp("jcmsplugin.assmatplugin.recherche.am.rue.placeholder")) %>'
						data-jalios-ajax-refresh-url="plugins/AssmatPlugin/jsp/autocomplete/acAdresseInCity.jsp"
						value="<%=adresse %>">
						<button id="resetAddress" class="resetCross <%=Util.isEmpty(adresse) ? "hide" : ""%>" onclick="return false;"
						title="Vider le champ : Recherche par adresse">
						<img src="s.gif" class="spr-select_cross"
							alt="Vider le champ : Recherche par adresse"
							title="Vider le champ : Recherche par adresse">
						</button>
				</div>
			</fieldset>

			<!--	PAR PERIMETRE -->

      <%Category categDistance = channel.getCategory("$jcmsplugin.assmatplugin.categ.distance"); %>
      <% Set<Category> setCategory = new TreeSet(Category.getDeepOrderComparator()); %>
      <% setCategory.addAll(categDistance.getChildrenSet()); %>
			<fieldset class="perimetre">
				<legend><%=glp("jcmsplugin.assmatplugin.recherche.am.perimetre.label")%></legend>
				<div class="form-select">
					<span class="input-box" style="background-color: #b0cc00;"><span
						class="spr-select_arrow"></span></span>
					<select <%=Util.isEmpty(commune) ? "disabled" : ""%> name="distance" id="listePerimetre"
						onChange="jQuery.plugin.AssmatPlugin.submitForm()"
						title='<%= encodeForHTMLAttribute(glp("jcmsplugin.assmatplugin.recherche.am.perimetre.title")) %>'>
						
						<%
						/* Ajout de la recherche par quartier et micro quartiers (pour Nantes uniquement)
						 * L'option "micro quartiers" est conditionnée en + par les droits de catégorie
						 * Les distances courtes (200m, 500m, 1km) ne sont dispos que si on a saisi une adresse.
						*/
						boolean isNantes = Util.notEmpty(request.getParameter("codeInsee")) && request.getParameter("codeInsee").equals("44109");
						boolean afficheQuartiersDansCommune = isNantes;
						boolean afficheDistancesCourtes = Util.notEmpty(request.getParameter("adresse"));
						%>
						
			            <jalios:foreach name="itCat" type="Category" collection="<%=setCategory %>">
				            <jalios:if predicate="<%=itCat.canBeReadBy(loggedMember) %>">
				            <%
				            // On récupère les paramètres postionnés surles catégories de distance
				            String params = itCat.getExtraData("extra.Category.plugin.corporateIdentity.pageTitleValue");
				            
				            // On contrôle les catégories ayant la propriété "checkAffichage" (Quartier + Microquartiers)
				            String style="";

				            // On affiche systématiquement "Toute la commune"
				            if(!itCat.getExtraData("extra.Category.jcmsplugin.assmatplugin.search.mapping").equals("0")){
				            	
				            	// Pour Nantes, si pas d'adresse, on laisse juste la recherche par quartiers
					            if(
					            	(params.contains("checkAffichage") && !afficheQuartiersDansCommune) ||
					            	(params.contains("distanceCourte") && !afficheDistancesCourtes) ||
					            	(isNantes && Util.isEmpty(adresse) && !params.contains("optQuartier")) ||
					            	(isNantes && params.contains("notAgglo")) ||
					            	(!isNantes && params.contains("agglo"))
					            ){
					            	style="hideOpt";
					            }
				            }
				            %>
					              <option
					              	class="<%=itCat.getExtraData("extra.Category.plugin.corporateIdentity.pageTitleValue") %> <%=style%>"
									<%if(Integer.toString(distance).equals(itCat.getExtraData("extra.Category.jcmsplugin.assmatplugin.search.mapping"))){ %>
					                	selected <%} %>
					                value="<%=itCat.getExtraData("extra.Category.jcmsplugin.assmatplugin.search.mapping") %>"><%=itCat.getName() %>
								  </option>
						  </jalios:if>
			            </jalios:foreach>
					</select>
				</div>
			</fieldset>

		<%@ include file='/plugins/AssmatPlugin/jsp/recherche/listeQuartiers.jsp'%>

		<%@ include file='/plugins/AssmatPlugin/jsp/recherche/listeMicroQuartiers.jsp'%>
		
        <%-- A partir de --%>
            <fieldset class="nbPlacesDispo">
                <legend><%= glp("jcmsplugin.assmatplugin.recherche.am.aPartirDe.label") %></legend>
                <div class="form-select">
                    <span class="input-box" style="background-color: #b0cc00;">
                       <span class="spr-select_arrow"></span>
                    </span>
                    <select onChange="jQuery.plugin.AssmatPlugin.submitForm()"
                        class="ajax-refresh selectDatePlace" name="month"
                        title='<%= encodeForHTMLAttribute(glp("jcmsplugin.assmatplugin.recherche.am.aPartirDe.title"))%>'>
                        <%
            
                        java.util.Date dateRef = new java.util.Date();
                           SimpleDateFormat formatterTLE = new SimpleDateFormat("MMMM yyyy");
                            java.util.Date dateTLE = new java.util.Date(dateRef.getTime());
                        %>
                        <%
                            for (int iterator = 1; iterator < 13; iterator++) {
                        %>
                              <option 
                              <%if(Long.toString(time).equalsIgnoreCase(Long.toString(dateTLE.getTime()).substring(0, Long.toString(dateTLE.getTime()).length() - 8) + "00000000")){ %>
                                selected <%} %>
                              value="<%= Long.toString(dateTLE.getTime()).substring(0, Long.toString(dateTLE.getTime()).length() - 8) + "00000000"  %>"><%=StringUtils.capitalize(formatterTLE.format(dateTLE))%></option>
                        <%
                           dateTLE = DateUtils.addMonths(dateTLE, 1);
                            }
                        %>
                    </select>
                </div>
            </fieldset>
            
            <%-- Tranche d'age --%>
			<%Category categAge = channel.getCategory("$jcmsplugin.assmatplugin.categ.trancheage"); %>
            <fieldset class="ageEnfant">
                <legend><%= glp("jcmsplugin.assmatplugin.recherche.am.age.label") %></legend>
        <div class="form-select">
            <span class="input-box" style="background-color: #b0cc00;"><span
                class="spr-select_arrow"></span></span> 
                <% // Récupération des catégories liés aux tranche age
                TreeSet<Category> ageSet = new TreeSet<Category>(Category.getDeepOrderComparator(channel.getLanguage()));
                ageSet.addAll(categAge.getChildrenSet());
                %>
                <select 
                	onChange="jQuery.plugin.AssmatPlugin.submitForm()"
                	class="ajax-refresh selectTranche" name="age" title='<%= encodeForHTMLAttribute(glp("jcmsplugin.assmatplugin.recherche.am.age.title"))%>'>
                <jalios:foreach name="itCat" type="Category" collection="<%=ageSet %>">
                    <option <%if(trancheAgeKey.equals(itCat.getExtraData("extra.Category.jcmsplugin.assmatplugin.search.mapping"))){ %> selected <%} %> value="<%=itCat.getExtraData("extra.Category.jcmsplugin.assmatplugin.search.mapping") %>"><%=itCat.getName() %></option>
                </jalios:foreach>
            </select>
        </div>              
                </fieldset>

 
			<%Category categLieuexercice = channel.getCategory("$jcmsplugin.assmatplugin.categ.lieuexerce"); %>
			<fieldset class="categories">
				<legend><%=categLieuexercice.getName() %></legend>
				<ul class="unstyled categories">

					<li class="checkingFacility"><button
							class="checkAll ajax-refresh active"
							data-jalios-target="DIV.ID_p1_64459"
							data-jalios-action="Ajax-Refresh"><%= glp("plugin.tools.list.checkall") %></button> /
						<button class="uncheckAll ajax-refresh"
							data-jalios-target="DIV.ID_p1_64459"
							data-jalios-action="Ajax-Refresh"><%= glp("plugin.tools.list.uncheckall") %></button></li>

					<jalios:foreach name="itCat" type="Category"
						collection="<%=categLieuexercice.getChildrenSet() %>">
						<li class=""><input  type="checkbox"
							<%if(categorySet.contains(itCat)){ %> checked <%} %>
							id="<%= itCat.getId()%>" name="lieuexercice"
							value="<%= itCat.getId()%>"> <label
							for="<%= itCat.getId()%>" class="checkbox-label"><%=itCat.getName()%>
							<%if(Util.notEmpty(itCat.getDescription())){ %>
              <button class="cg-tooltip buttonHelp" data-category-id="none" aria-label="<%=itCat.getDescription() %>" 
                data-color="#aec900">
                <img alt="?" src="s.gif" class="spr-interrogation">
              </button>
              <%} %>
							
							</label>
							
							
						</li>
					</jalios:foreach>
				</ul>
				<input type="hidden" value="<%=categLieuexercice.getId() %>"
					name="branchesId">
			</fieldset>


			<%Category categTypeAccueil = channel.getCategory("$jcmsplugin.assmatplugin.categ.typeaccueil"); %>
			<fieldset class="categories">
				<legend><%=categTypeAccueil.getName()%></legend>
				<ul class="unstyled categories">
					<li class="checkingFacility"><button
							class="checkAll ajax-refresh active"
							data-jalios-target="DIV.ID_p1_64459"
							data-jalios-action="Ajax-Refresh"><%= glp("plugin.tools.list.checkall") %></button> /
						<button class="uncheckAll ajax-refresh"
							data-jalios-target="DIV.ID_p1_64459"
							data-jalios-action="Ajax-Refresh"><%= glp("plugin.tools.list.uncheckall") %></button></li>

					<jalios:foreach name="itCat" type="Category"
						collection="<%=categTypeAccueil.getChildrenSet() %>">
						<li class=""><input type="checkbox" id="<%=itCat.getId()%>"
							<%if(categorySet.contains(itCat)){ %> checked <%} %>
							<%if(Util.notEmpty(itCat.getChildrenSet())){ %> <%} %>
							name="typeaccueil" value="<%= itCat.getId()%>"
							class="noSubmitEven <%if(Util.notEmpty(itCat.getChildrenSet())){ %> nextNiveau  <%} %> ">
							<label for="<%=itCat.getId()%>" class="checkbox-label"><%=itCat.getName()%>
							<%if(Util.notEmpty(itCat.getDescription())){ %>
              <button class="cg-tooltip buttonHelp" data-category-id="none" aria-label="<%=itCat.getDescription() %>" 
                data-color="#aec900">
                <img alt="?" src="s.gif" class="spr-interrogation">
              </button>
              <%} %>
							
							</label>
							<%if(Util.notEmpty(itCat.getChildrenSet())){ %>
							<ul class="unstyled categories secondNiveauUl <%if(!categorySet.contains(itCat)){ %> hide <%} %>">
								<jalios:foreach name="itCat2" type="Category"
									collection="<%=itCat.getChildrenSet() %>">
									<li class="secondNiveauCateg noSubmitEven"><input type="checkbox"
										<%if(categorySet.contains(itCat2)){ %> checked <%} %>
										id="<%= itCat2.getId()%>" value="<%= itCat2.getId()%>"
										name="typeaccueil" class=""> <label
										for="<%= itCat2.getId()%>" class="checkbox-label"><%=itCat2.getName()%>
										
										<%if(Util.notEmpty(itCat2.getDescription())){ %>
              <button class="cg-tooltip buttonHelp" data-category-id="none" aria-label="<%=itCat2.getDescription() %>" 
                data-color="#aec900">
                <img alt="?" src="s.gif" class="spr-interrogation">
              </button>
              <%} %>
										</label>
										
									</li>
								</jalios:foreach>
							</ul> <%} %></li>
							
					</jalios:foreach>
				</ul>
				<input type="hidden" value="<%=categTypeAccueil.getId() %>"
					name="branchesId">
			</fieldset>

			<jalios:javascript>
  
      jQuery( ".nextNiveau" ).change(function() {  
        if(jQuery( ".nextNiveau" ).is(':checked')){
         jQuery(".secondNiveauUl").show();
        }else{
        jQuery(".secondNiveauUl").hide();
        }
      });
			</jalios:javascript>

			<%Category categSpecificite = channel.getCategory("$jcmsplugin.assmatplugin.categ.specificite"); %>
			<fieldset class="categories">
				<legend><%=categSpecificite.getName() %></legend>
				<ul class="unstyled categories">
					<li class="checkingFacility"><button
							class="checkAll ajax-refresh active"
							data-jalios-target="DIV.ID_p1_64459"
							data-jalios-action="Ajax-Refresh"><%= glp("plugin.tools.list.checkall") %></button> /
						<button class="uncheckAll ajax-refresh"
							data-jalios-target="DIV.ID_p1_64459"
							data-jalios-action="Ajax-Refresh"><%= glp("plugin.tools.list.uncheckall") %></button></li>



					<jalios:foreach name="itCat" type="Category"
						collection="<%=categSpecificite.getChildrenSet() %>">
						<li class=""><input type="checkbox"
							<%if(categorySet.contains(itCat)){ %> checked <%} %>
							id="<%= itCat.getId()%>" name="specificite"
							value="<%= itCat.getId()%>" class="noSubmitEven"> <label
							for="<%= itCat.getId()%>" class="checkbox-label"><%=itCat.getName()%>
							
							<%if(Util.notEmpty(itCat.getDescription())){ %>
              <button class="cg-tooltip buttonHelp" data-category-id="none" aria-label="<%=itCat.getDescription() %>" 
                data-color="#aec900">
                <img alt="?" src="s.gif" class="spr-interrogation">
              </button>
              <%} %>
							</label>
							
						</li>
					</jalios:foreach>
				</ul>
				<input type="hidden" value="<%=categSpecificite.getIcon() %>"
					name="branchesId">
			</fieldset>


			<!--      PAR NOM -->
			<fieldset class="citiesFacet">
				<legend><%= glp("jcmsplugin.assmatplugin.recherche.am.nom.label") %></legend>
				<div class="inputwithaction">
					<label class="hide-accessible" for="title"><%= glp("jcmsplugin.assmatplugin.recherche.am.nom.label") %></label> 
					
					<div>				
						<input
							type="text" name="nomassmat" id=nomAssmat class="typeahead"
							<%=Util.notEmpty(nomAssmat)?"readonly":"" %>
							size="14" maxlength="30" title='<%= encodeForHTMLAttribute(glp("jcmsplugin.assmatplugin.recherche.am.nom.title")) %>' placeholder=""
							value="<%=nomAssmat %>" />
						<button id="resetNomAssmat" class="resetCross <%=Util.isEmpty(nomAssmat) ? "hide" : ""%>" type="button"
							title="Vider le champ : Recherche par commune">
							<img src="s.gif" class="spr-select_cross"
								alt="Vider le champ : Recherche par commune"
								title="Vider le champ : Recherche par commune">
						</button>
													
				  </div>
	
				</div>
			</fieldset>
			
			<p class="champObligatoire">* champs obligatoires</p>
	
  <%-- 
  <input type='hidden' name="geoLong" id="geoLongId" value="<%=geolocLong%>"/>
  <input type='hidden' name="geoLat" id="geoLatId" value="<%=geolocLat%>"/>
  --%>
  <input type='hidden' name="isSearch" id="isSearch" value="Ok"/>

  <input type='hidden' name="hashKey" id="hashKey" value="<%=hashKey%>"/>
  
  <div id="sticky-container" style="height: 50px;">
  	<div id="btnRechercheAMContainer" class="sticky stuck">
		<input type="submit" class="submitButton btnRechercheAM" value="Rechercher"/>
  	</div>
  </div>
  
  
  
  
  <%
  String valueWithDispo ="true";
  if(Util.notEmpty(request.getParameter("withDispo"))){
    valueWithDispo = request.getParameter("withDispo");
  }
  
  String valueWithDispoFuture ="true";
  if(Util.notEmpty(request.getParameter("withDispoFuture"))){
    valueWithDispoFuture = request.getParameter("withDispoFuture");
  }
  
  String valueWithNoDispo ="false";
  if(Util.notEmpty(request.getParameter("withNonDispo"))){
    valueWithNoDispo = request.getParameter("withNonDispo");
  }
  
  String valueWithDispoNonRen ="false";
  if(Util.notEmpty(request.getParameter("withDispoNonRenseigne"))){
    valueWithDispoNonRen = request.getParameter("withDispoNonRenseigne");
  }
  %>
   <input type='hidden' name="withDispo" class="avecDispo" value="<%=valueWithDispo%>"/>
   <input type='hidden' name="withDispoFuture" class="futureDispo" value="<%=valueWithDispoFuture%>"/>
   <input type='hidden' name="withNonDispo" class="nonDispo" value="<%=valueWithNoDispo%>"/>
   <input type='hidden' name="withDispoNonRenseigne" class="nonRenseigne" value="<%=valueWithDispoNonRen%>"/>
<!-- 			<p class="submit"> -->
<!-- 				<label for="submit"> <input type="submit" id="submit" -->
<!-- 					name="isSearch" value="Rechercher" class="submitButton"> <span -->
<!-- 					class="input-box" style="background-color: #aec900"><span -->
<!-- 						class="spr-recherche-ok"></span></span> -->
<!-- 				</label> -->
				

<!-- 			</p> -->

		</form>
	</div>
</div>

<jalios:javascript>

jQuery(document).ready(function() {
  jQuery('#AjaxCtxtDeflate').removeAttr("id");
  jQuery('body').removeAttr("id");
  jQuery('#CSRFTokenElm').removeAttr("id"); 
});

</jalios:javascript>