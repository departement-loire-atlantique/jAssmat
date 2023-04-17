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
  FicheLieu uniteAgrement = (FicheLieu) channel.getPublication(idUa);
  
%>




<% if (formHandler.isFieldEdition("message")) { %>
<%-- Message ------------------------------------------------------------ --%>
  <% String messageValues = formHandler.getAvailableMessage(); %>

<% String uuid = UUID.randomUUID().toString(); %>
<div class="ds44-form__container">
   <div class="ds44-posRel">
      <label for="form-element-91325" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span>Votre message ici<sup aria-hidden="true">*</sup></span></span></label>
      <textarea rows="5" cols="1" id="form-element-91325" name="message" class="ds44-inpStd" title="Votre message ici - obligatoire"  required  ></textarea>
   </div>
</div>
<%
Member realLoggedMember = (Member) request.getAttribute("realLoggedMember");
if(realLoggedMember == null) {
  realLoggedMember = loggedMember;
}
%>
<input type="hidden" name="nom"  value="<%= realLoggedMember.getName() %>" data-technical-field/>
<input type="hidden" name="prenom"  value="<%= realLoggedMember.getFirstName() %>" data-technical-field/>

<% 
String email ="";
if(Util.notEmpty(uniteAgrement)){
  if(Util.notEmpty(uniteAgrement.getMails())){
    email =Util.getFirst(uniteAgrement.getMails());
  }
}
%>

<input type="hidden" name="couriel"  value="<%=email%>"  data-technical-field/>
<% } %>

<% request.setAttribute("formElementCount", new Integer(formElementCount)); %>

<%-- *** PLUGINS ********************** --%>
<%-- <jalios:include target="EDIT_PUB_MAINTAB" targetContext="tr" /> --%>
<jalios:include jsp="/jcore/doEditExtraData.jsp" />
<%
  ServletUtil.restoreAttribute(pageContext , "classBeingProcessed");
%>
