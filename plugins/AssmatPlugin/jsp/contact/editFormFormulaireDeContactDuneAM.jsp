<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%
%><%@ include file='/jcore/doInitPage.jsp' %>
<% String[] formTitles = JcmsUtil.getLanguageArray(channel.getTypeEntry(FormulaireDeContactDuneAM.class).getLabelMap()); %>
<jsp:useBean id='formHandler' scope='page' class='generated.EditFormulaireDeContactDuneAMHandler'>
  <jsp:setProperty name='formHandler' property='request' value='<%= request %>'/>
  <jsp:setProperty name='formHandler' property='response' value='<%= response %>'/>
  <jsp:setProperty name='formHandler' property='*' />
  <jsp:setProperty name='formHandler' property='author' value='j_2'/>
  <jsp:setProperty name='formHandler' property='title' value='<%= formTitles %>'/>
</jsp:useBean>
<%
  if ( formHandler.validate()) {
    jcmsContext.addMsgSession(new JcmsMessage(JcmsMessage.Level.INFO, "Votre message a bien été envoyé"));
    return;
  }

  boolean hasParam = Util.notEmpty(request.getParameter("mailRAM"));
  
  Publication currentPub = (Publication) request.getAttribute(PortalManager.PORTAL_PUBLICATION);
  String idMam = getUntrustedStringParameter("idMAM", "");
%> 

<jalios:if predicate='<%= formHandler.isOneSubmit() && formHandler.isSubmitted() %>'>
  <% setWarningMsg(glp("msg.edit.already-one-submit"), request); %>
</jalios:if>

<%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>

<jalios:if predicate="<%= Util.notEmpty(idMam) %>">

<% request.setAttribute("titreFormulaire", "Formulaire de contact"); %>
<%@ include file='/plugins/SoclePlugin/jsp/forms/doFormHeader.jspf' %>

<form data-no-encoding="true" action='<%= ServletUtil.getResourcePath(request) %>' method='post' name='editForm' accept-charset="UTF-8"  enctype="multipart/form-data">

    <%
    request.setAttribute("formHandler", formHandler);
    %>
        
    <jsp:include page="doEditFormulaireDeContactDuneAM.jsp" />
    
    <input type='hidden' name='redirect' value='<%= currentPub.getDisplayUrl(userLocale) %>' data-technical-field />
    <input type='hidden' name='ws' value='<%= formHandler.getWorkspace().getId() %>' data-technical-field />
    <input type='hidden' name='opCreate' value='<%= glp("ui.com.btn.submit") %>' data-technical-field />
    <input type="hidden" name="csrftoken" value="<%= HttpUtil.getCSRFToken(request) %>" data-technical-field>
    <input type="hidden" name="noSendRedirect" value='true' data-technical-field />

</form>


<%@ include file='/plugins/SoclePlugin/jsp/forms/doFormFooter.jspf' %>

</jalios:if>