<%-- This file has been automatically generated. --%>
<%--
  @Summary: FormulaireDeContactDuneAM content editor
  @Category: Generated
  @Author: JCMS Type Processor 
  @Customizable: True
  @Requestable: False 
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file='/jcore/doInitPage.jsp' %>
<% 
  EditFormulaireDeContactDuneAMHandler formHandler = (EditFormulaireDeContactDuneAMHandler)request.getAttribute("formHandler");
  int formElementCount = ((Integer)request.getAttribute("formElementCount")).intValue();
  ServletUtil.backupAttribute(pageContext , "classBeingProcessed");
  request.setAttribute("classBeingProcessed", FormulaireDeContactDuneAM.class);
%>

<%
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

	  String telephoneAssmat ="";
	  if(Util.notEmpty(request.getParameter("phone"))){
	    telephoneAssmat= request.getParameter("phone");
	  }
	  
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
  }
%>
<% if (formHandler.isFieldEdition("name")) { %>
<%-- Name ------------------------------------------------------------ --%>
  <% String nameValues = formHandler.getAvailableName(); %>
  
  
<tr class='name'>
  <td>
  <% TypeFieldEntry nameTFE = channel.getTypeFieldEntry(formHandler.getPublicationClass(), "name", true); %>
  
  <% String nameLabel = nameTFE.getLabel(userLang); %>
    <tr class='name'>
      <td>
            <div class="name row-fluid">
                <div class="span4"><label for="name"><%= nameLabel %><span class="required"></span></label></div>
                <div class="span8"><input class="personal-input full-width" name="name" id="name" type="text" <% if(Util.notEmpty(nomAssmat)) { %>value="<%= nomAssmat %>" <% } %>></div>
            </div>
        </td>
    </tr>
  
  </td>
</tr>
<% } %>
<% if (formHandler.isFieldEdition("firstName")) { %>
<%-- FirstName ------------------------------------------------------------ --%>
  <% String firstNameValues = formHandler.getAvailableFirstName(); %>
  
  
<tr class='firstName'>
  <td>
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
  
  
  </td>
</tr>
<% } %>
<% if (formHandler.isFieldEdition("courriel")) { %>
<%-- Courriel ------------------------------------------------------------ --%>
  <% String courrielValues = formHandler.getAvailableCourriel(); %>
  
  
<tr class='courriel'>
  <td>
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
  
  
  
  </td>
</tr>
<% } %>
<% if (formHandler.isFieldEdition("phone")) { %>
<%-- Phone ------------------------------------------------------------ --%>
  <% String phoneValues = formHandler.getAvailablePhone(); %>
  
  
<tr class='phone'>
  <td>
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
<% if (Util.notEmpty(request.getParameter("idMAM"))) { %>
<%-- MemberId ------------------------------------------------------------ --%>
  <% String id = getUntrustedStringParameter("idMAM", ""); %>

  <% 
  Member mbrRam = channel.getMember(id);

   String mailAMValues ="";
   if(Util.notEmpty(mbrRam)){
    mailAMValues = mbrRam.getEmail();
   }
  %>

<tr class='to'>
  <td>
  <jalios:widget 
                  formHandler     ='<%= formHandler %>'
                  editor          ='<%= AbstractWidget.UI_EDITOR_TEXTFIELD %>'
                  widgetName      ='<%= "mailam" %>'
                  value           ='<%= mailAMValues %>'
                  label           ='mailam'
                  printLabel      ='<%= true %>'
                  size            ='<%= 80 %>'
                  css       ='hide'
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
<%-- **********4A616C696F73204A434D53 *** SIGNATURE BOUNDARY * DO NOT EDIT ANYTHING BELOW THIS LINE *** --%>
<%-- m947IhtoTVtROZODkr3Axw== --%>
