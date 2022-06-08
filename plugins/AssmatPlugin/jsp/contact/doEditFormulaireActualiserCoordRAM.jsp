<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file='/jcore/doInitPage.jsp' %><%
  jcmsContext.addCSSHeader("plugins/ToolsPlugin/css/facets/citiesFacet.css");
  jcmsContext.addJavaScript("plugins/CorporateIdentityPlugin/js/facets/citiesCommunitiesFacet.js");

  
  boolean borderingCitiesChecked = getBooleanParameter("borderingCities",false);


  EditFormulaireDeContactActualiserCooHandler formHandler = (EditFormulaireDeContactActualiserCooHandler)request.getAttribute("formHandler");
  int formElementCount = ((Integer)request.getAttribute("formElementCount")).intValue();
  ServletUtil.backupAttribute(pageContext , "classBeingProcessed");
  request.setAttribute("classBeingProcessed", FormulaireDeContactActualiserCoo.class);
  
  String idUa= request.getParameter("idUA");
  Place uniteAgrement = (Place) channel.getPublication(idUa);
  
%>




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
<%
Member realLoggedMember = (Member) request.getAttribute("realLoggedMember");
if(realLoggedMember == null) {
  realLoggedMember = loggedMember;
}
%>
<input type="hidden" name="nom"  value="<%= realLoggedMember.getName() %>" />
<input type="hidden" name="prenom"  value="<%= realLoggedMember.getFirstName() %>" />

<% 
String email ="";
if(Util.notEmpty(uniteAgrement)){
  if(Util.notEmpty(uniteAgrement.getMails())){
    email =Util.getFirst(uniteAgrement.getMails());
  }
}
%>

<input type="hidden" name="couriel"  value="<%=email%>" />
<% } %>

<% request.setAttribute("formElementCount", new Integer(formElementCount)); %>

<%-- *** PLUGINS ********************** --%>
<%-- <jalios:include target="EDIT_PUB_MAINTAB" targetContext="tr" /> --%>
<jalios:include jsp="/jcore/doEditExtraData.jsp" />
<%
  ServletUtil.restoreAttribute(pageContext , "classBeingProcessed");
%>
<div class="clear"></div>
