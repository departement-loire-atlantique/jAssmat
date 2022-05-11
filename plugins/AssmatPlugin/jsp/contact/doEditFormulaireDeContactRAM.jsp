<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file='/jcore/doInitPage.jsp' %><%
  jcmsContext.addCSSHeader("plugins/ToolsPlugin/css/facets/citiesFacet.css");
  jcmsContext.addJavaScript("plugins/CorporateIdentityPlugin/js/facets/citiesCommunitiesFacet.js");

  
 


  EditFormulaireDeContactRAMHandler formHandler = (EditFormulaireDeContactRAMHandler)request.getAttribute("formHandler");
  int formElementCount = ((Integer)request.getAttribute("formElementCount")).intValue();
  ServletUtil.backupAttribute(pageContext , "classBeingProcessed");
  request.setAttribute("classBeingProcessed", FormulaireDeContactRAM.class);
%>
<% if (formHandler.isFieldEdition("name")) { %>
<%-- Name ------------------------------------------------------------ --%>
  <% String nameValues = formHandler.getAvailableName();
  TypeFieldEntry nameTFE = channel.getTypeFieldEntry(formHandler.getPublicationClass(), "name", true);
  String nameLabel = nameTFE.getLabel(userLang); %>
	<tr class='name'>
	  <td>
			<div class="name row-fluid">
				<div class="span4"><label for="name"><%= nameLabel %><span class="required"></span></label></div>
				<div class="span8"><input class="personal-input full-width" name="name" id="name" type="text" ></div>
			</div>
		</td>
	</tr>
<% } %>
<% if (formHandler.isFieldEdition("firstName")) { %>
<%-- FirstName ------------------------------------------------------------ --%>
  <% String firstNameValues = formHandler.getAvailableFirstName(); %>
  <% TypeFieldEntry firstNameTFE = channel.getTypeFieldEntry(formHandler.getPublicationClass(), "firstName", true); %>
  <% String firstNameLabel = firstNameTFE.getLabel(userLang); %>
	<tr class='firstName'>
	  <td>
			<div class="name row-fluid">
				<div class="span4"><label for="firstName"><%= firstNameLabel %><span class="required"></span></label></div>
				<div class="span8"><input class="personal-input full-width" name="firstName" id="firstName" type="text" ></div>
			</div>
		</td>
	</tr>
<% } %>
<% if (formHandler.isFieldEdition("courriel")) { %>
<%-- Couriel ------------------------------------------------------------ --%>
  <% String courrielValues = formHandler.getAvailableCourriel(); %>
  <% TypeFieldEntry courrielTFE = channel.getTypeFieldEntry(formHandler.getPublicationClass(), "courriel", true); %>
  <% String courrielLabel = courrielTFE.getLabel(userLang); %>
	<tr class='courriel'>
	  <td>
			<div class="name row-fluid">
				<div class="span4"><label for="courriel"><%= courrielLabel %><span class="required"></span><span class="label-info"><%= glp("plugin.corporateidentity.form.mail.helpLabel") %></span></label></div>
				<div class="span8"><input class="personal-input full-width" name="courriel" id="courriel" type="text" ></div>
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
<% if (Util.notEmpty(request.getParameter("mailRAM"))) { %>
<%-- MemberId ------------------------------------------------------------ --%>
  <% String mailRAMValues = getUntrustedStringParameter("mailRAM", ""); %>

  <% TypeFieldEntry mailRAMTFE = channel.getTypeFieldEntry(formHandler.getPublicationClass(), "mailRAM", true); %>
  <% String mailRAMLabel = mailRAMTFE.getLabel(userLang); %>

<tr class='to'>
  <td>
  <jalios:widget 
                  formHandler     ='<%= formHandler %>'
                  editor          ='<%= AbstractWidget.UI_EDITOR_TEXTFIELD %>'
                  widgetName      ='<%= "mailRAM" %>'
                  value           ='<%= mailRAMValues %>'
				          label           ='<%= mailRAMLabel %>'
                  printLabel      ='<%= true %>'
                  size            ='<%= 80 %>'
                  css			  ='hide'
  />

  </td>
</tr>
<% } %>


<% if (Util.notEmpty(request.getParameter("titleRAM"))) { %>
<%-- Titre du ralais ou de l'asso ------------------------------------------------------------ --%>
  <% String titleRAMValues = getUntrustedStringParameter("titleRAM", ""); %>
  <input name="titleRAM" type="hidden" value="<%= titleRAMValues %>">
<% } %>



<% request.setAttribute("formElementCount", new Integer(formElementCount)); %>

<%-- *** PLUGINS ********************** --%>
<jalios:include target="EDIT_PUB_MAINTAB" targetContext="tr" />
<jalios:include jsp="/jcore/doEditExtraData.jsp" />
<%
  ServletUtil.restoreAttribute(pageContext , "classBeingProcessed");
%>
<div class="clear"></div>
