<%-- This file has been automatically generated. --%>
<%--
  @Summary: FormulaireDeContactCommune content editor
  @Category: Generated
  @Author: JCMS Type Processor 
  @Customizable: True
  @Requestable: False 
--%>
<%@page import="fr.cg44.plugin.tools.facetedsearch.exception.UnknowCityException"%>
<%@page import="fr.cg44.plugin.tools.facetedsearch.manager.CityManager"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="fr.cg44.plugin.assmat.managers.AssmatSearchDAO"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file='/jcore/doInitPage.jsp' %>
<%
  jcmsContext.addCSSHeader("plugins/ToolsPlugin/css/facets/citiesFacet.css");
  jcmsContext.addJavaScript("plugins/CorporateIdentityPlugin/js/facets/citiesCommunitiesFacet.js");

  String nomAssmat ="";
  if(Util.notEmpty(request.getParameter("name"))){
    nomAssmat= request.getParameter("name");
  }
  String prenomAssmat ="";
  if(Util.notEmpty(request.getParameter("firstName"))){
    prenomAssmat= request.getParameter("firstName");
  }

  String courielAssmat ="";
  if(Util.notEmpty(request.getParameter("courriel"))){
    courielAssmat= request.getParameter("courriel");
  }
  String communeAssmat ="";
  if(Util.notEmpty(request.getParameter("cityName"))){
    communeAssmat= request.getParameter("cityName");
  }
  
  
  String telephoneAssmat ="";
  if(Util.notEmpty(request.getParameter("phone"))){
    telephoneAssmat= request.getParameter("phone");
  }
  
  String cityCode="";
  if(Util.notEmpty(request.getParameter("city"))){
    cityCode= request.getParameter("city");
  }
  
  AssmatSolis assmatSolis= null;
  if(Util.notEmpty(request.getAttribute("realLoggedMember"))){
    Member realLoggedMember = (Member) request.getAttribute("realLoggedMember");
    
    if(Util.notEmpty(realLoggedMember.getFirstName())){
      prenomAssmat=realLoggedMember.getFirstName();
    }
    if(Util.notEmpty(realLoggedMember.getLastName())){
      nomAssmat=realLoggedMember.getLastName();
    }
    if(Util.notEmpty(realLoggedMember.getEmail())){
      courielAssmat=realLoggedMember.getEmail();
    }
    if(Util.notEmpty(realLoggedMember.getMobile())){
      telephoneAssmat = realLoggedMember.getMobile();
    }
    SolisManager solisMgr = SolisManager.getInstance();
    City city= null;
   
    assmatSolis= Util.getFirst(solisMgr.getAssmatSolisByNameFirstname(realLoggedMember.getLastName(), realLoggedMember.getFirstName()));
    
    if(Util.notEmpty(assmatSolis)){
      if(Util.notEmpty(assmatSolis.getCommuneDomicile())){
        communeAssmat= assmatSolis.getCommuneDomicile();
        if(Util.notEmpty(communeAssmat)){
         try{ 
         city= (City)AssmatUtil.getCityByName(communeAssmat, City.class);
         communeAssmat = city.getTitle();
         }catch(UnknowCityException uce){
           if(logger.isDebugEnabled()){
             logger.debug("This is not current city.");
           }
         }
         if(Util.notEmpty(city)){
           cityCode=city.getZipCode();
         }
        }
      }
    }
  }
  
  String targetPortletId = (String)request.getAttribute("ajaxTarget");
  String[] cities = request.getParameterValues("cities");
  
  String cityName = getStringParameter("cityName", "", ".*");
  if(Util.notEmpty(cities)){
    cityCode = cities[0];
  }
  boolean borderingCitiesChecked = getBooleanParameter("borderingCities",false);


  EditFormContactAssmatHandler formHandler = (EditFormContactAssmatHandler)request.getAttribute("formHandler");
  int formElementCount = ((Integer)request.getAttribute("formElementCount")).intValue();
  ServletUtil.backupAttribute(pageContext , "classBeingProcessed");
  request.setAttribute("classBeingProcessed", FormContactAssmat.class);
%>
<input type="hidden" name="redirect" value="false" />
<% if (formHandler.isFieldEdition("name")) { %>
<%-- Name ------------------------------------------------------------ --%>
  <% 
  TypeFieldEntry nameTFE = channel.getTypeFieldEntry(formHandler.getPublicationClass(), "name", true);
  String nameLabel = nameTFE.getLabel(userLang); %>
	<tr class='name'>
	  <td>
			<div class="name row-fluid">
				<div class="span4"><label for="name"><%= nameLabel %><span class="required"></span></label></div>
				<div class="span8"><input class="personal-input full-width" name="name" id="name" type="text" <% if(Util.notEmpty(nomAssmat)) { %>value="<%= nomAssmat %>" <% } %>></div>
			</div>
		</td>
	</tr>
<% } %>
<% if (formHandler.isFieldEdition("firstName")) { %>
<%-- FirstName ------------------------------------------------------------ --%>
  
  <% TypeFieldEntry firstNameTFE = channel.getTypeFieldEntry(formHandler.getPublicationClass(), "firstName", true); %>
  <% String firstNameLabel = firstNameTFE.getLabel(userLang); %>
	<tr class='firstName'>
	  <td>
			<div class="name row-fluid">
				<div class="span4"><label for="firstName"><%= firstNameLabel %><span class="required"></span></label></div>
				<div class="span8"><input class="personal-input full-width" name="firstName" id="firstName" type="text" <% if(Util.notEmpty(prenomAssmat)) { %>value="<%= prenomAssmat %>" <% } %>></div>
			</div>
		</td>
	</tr>
<% } %>
<% if (formHandler.isFieldEdition("courriel")) { %>
<%-- Couriel ------------------------------------------------------------ --%>
  
  <% TypeFieldEntry courrielTFE = channel.getTypeFieldEntry(formHandler.getPublicationClass(), "courriel", true); %>
  <% String courrielLabel = courrielTFE.getLabel(userLang); %>
	<tr class='courriel'>
	  <td>
			<div class="name row-fluid">
				<div class="span4"><label for="courriel"><%= courrielLabel %><span class="required"></span><span class="label-info"><%= glp("plugin.corporateidentity.form.mail.helpLabel") %></span></label></div>
				<div class="span8"><input class="personal-input full-width" name="courriel" id="courriel" type="text" <% if(Util.notEmpty(courielAssmat)) { %>value="<%= courielAssmat %>" <% } %>></div>
			</div>
		</td>
	</tr>
<% } %>
<% if (formHandler.isFieldEdition("city")) { %>
<%-- City ------------------------------------------------------------ --%>
  <% String cityValues = formHandler.getAvailableCity(); %>

  
<tr class='city'>
  <td>
 
  <% TypeFieldEntry cityTFE = channel.getTypeFieldEntry(formHandler.getPublicationClass(), "city", true); %>
  <% String cityLabel = cityTFE.getLabel(userLang); %>
  <div class="subject row-fluid">
	<div class="span4">
		<label for="cityName"><%= cityLabel %><span class="required"></span><span class="label-info"></span></label>
	</div>

	<div class="inputwithaction span8">
 		<input name="cityName" id="cityName" class="typeahead personal-input full-width" size="80" maxlength="80"
 			title="Saisir une commune" placeholder="" value="<%if(Util.notEmpty(cityCode)){ %> <%= encodeForHTMLAttribute(communeAssmat) %><%} %>"
 			data-jalios-ajax-refresh-url="plugins/AssmatPlugin/jsp/autocomplete/acsearchCityCommunities.jsp?activeBorderingCities=true&activeCids=false"
 			autocomplete="off" type="text" />

		<input type="hidden" name="city" id="cityId" value="<%= cityCode %>" />
 	</div>
  </div>

  </td>
</tr>
<% } %>
<% if (formHandler.isFieldEdition("phone")) { %>
<%-- Phone ------------------------------------------------------------ --%>
 
  <% TypeFieldEntry phoneTFE = channel.getTypeFieldEntry(formHandler.getPublicationClass(), "phone", true); %>
  <% String phoneLabel = phoneTFE.getLabel(userLang); %>
	<tr class='phone'>
	  <td>
			<div class="name row-fluid">
				<div class="span4"><label for="phone"><%= phoneLabel %><span class="label-info"><%= glp("plugin.corporateidentity.form.phone.helpLabel") %></span></label></div>
				<div class="span8"><input class="personal-input full-width" name="phone" id="phone" type="text" <% if(Util.notEmpty(telephoneAssmat)) { %>value="<%= telephoneAssmat %>" <% } %>></div>
			</div>
		</td>
	</tr>
<% } %>
<% if (formHandler.isFieldEdition("subject")) { %>
<%-- Subject ------------------------------------------------------------ --%>
  <% Set subjectValues = formHandler.getSubjectCatSet(); %>

<tr class='subject'>
	<td>
		<div class="subject row-fluid">
		  	<div class="span4">
				<% TypeFieldEntry subjectTFE = channel.getTypeFieldEntry(formHandler.getPublicationClass(), "subject", true);
				String subjectLabel = subjectTFE.getLabel(userLang);
				boolean isRequired = subjectTFE.isRequired();
				TreeSet catSet = new TreeSet(Category.getOrderComparator(userLang));
				catSet.addAll(formHandler.getSubjectRoot().getChildrenSet());%>
				<label for="subject"><%= subjectLabel %><%= isRequired? "<span class=\"required\"></span>":"" %></label>
			</div>
			<div class="span8">
				<div class="form-select">
					<%@ include file='/plugins/CorporateIdentityPlugin/jsp/style/getBackgroundStyle.jspf' %>
			      	<span class="input-box" <%= backgroundStyle %>><span class="spr-select_arrow"></span></span>
			      	<select  name="cids" id="subject">
				      	<option value=""></option>
				      	<jalios:foreach name="itCat" type="Category" collection="<%= catSet %>">
							<option <%= subjectValues.contains(itCat)? "selected=\"selected\"":"" %> value="<%= itCat.getId() %>">
								<%= itCat.getName() %>
							</option>
				      	</jalios:foreach>
					</select>
			  	</div>
			</div>
		</div>
	</td>
</tr>

<% } %>
<% if (formHandler.isFieldEdition("message")) { %>
<%-- Message ------------------------------------------------------------ --%>
  <% String messageValues = formHandler.getAvailableMessage(); %>
  
  
<tr class='message'>
  <td>
  	<div class='message'><% 
	  TypeFieldEntry messageTFE = channel.getTypeFieldEntry(formHandler.getPublicationClass(), "message", true);
	  String messageLabel = messageTFE.getLabel(userLang);
	  boolean isRequired = messageTFE.isRequired(); %>
      <label class="block" for="message"><%= messageLabel %><%= isRequired? "<span class=\"required\"></span>":"" %></label>
      <textarea cols="80" rows="5" name="message" id="message"><%= Util.notEmpty(messageValues)? messageValues:"" %></textarea>
  	</div>
  </td>
</tr>
<% } %>
<% if (formHandler.isFieldEdition("memberId")) { %>
<%-- MemberId ------------------------------------------------------------ --%>
  <% String memberIdValues = formHandler.getAvailableMemberId(); %>
  
  
<tr class='memberId'>
  <td>
  <% TypeFieldEntry memberIdTFE = channel.getTypeFieldEntry(formHandler.getPublicationClass(), "memberId", true); %>
  
  <% String memberIdLabel = memberIdTFE.getLabel(userLang); %>
  <jalios:widget 
                  formHandler     ='<%= formHandler %>'
                  editor          ='<%= AbstractWidget.UI_EDITOR_TEXTFIELD %>'
                  widgetName      ='<%= "memberId" %>'
                  value           ='<%= memberIdValues %>' 
                  label           ='<%= memberIdLabel %>'
                  printLabel      ='<%= true %>'
                  size            ='<%= 80 %>'
                  css			  ='hide'
  />
  
  
  
  </td>
</tr>
<% } %>
<% request.setAttribute("formElementCount", new Integer(formElementCount)); %>
  
<%-- *** PLUGINS ********************** --%>
<jalios:include target="EDIT_PUB_MAINTAB" targetContext="tr" />
<jalios:include jsp="/jcore/doEditExtraData.jsp" />
<%
  ServletUtil.restoreAttribute(pageContext , "classBeingProcessed");
%>
<div class="clear"></div>
