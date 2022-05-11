<%-- This file was automatically generated. --%>
<%--
  @Summary: FormulaireDeContactDuneAM editor
  @Category: Generated
  @Author: JCMS Type Processor
  @Customizable: True
  @Requestable: True
--%><%
%><%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%@page import="fr.cg44.plugin.tools.modal.ModalCreator"%><%
%><%@ include file='/jcore/doInitPage.jsp' %>
<% String[] formTitles = JcmsUtil.getLanguageArray(channel.getTypeEntry(FormulaireDeContactDuneAM.class).getLabelMap());

%>
<jsp:useBean id='formHandler' scope='request' class='generated.EditFormulaireDeContactDuneAMHandler'>
  <jsp:setProperty name='formHandler' property='request' value='<%= request %>'/>
  <jsp:setProperty name='formHandler' property='response' value='<%= response %>'/>
  <jsp:setProperty name='formHandler' property='*' />
  <jsp:setProperty name='formHandler' property='author' value='j_2'/>
  <jsp:setProperty name='formHandler' property='title' value='<%= formTitles %>'/>
</jsp:useBean>
<%
  if (formHandler.validate()) {
    jcmsContext.addMsgSession(new JcmsMessage(JcmsMessage.Level.INFO, "Votre message a bien été envoyé"));
  return;
   
  }
  boolean formInPortal = jcmsContext.isInFrontOffice(); 
%>   
<jalios:if predicate='<%= !formInPortal %>'>
<%@ include file='/jcore/doHeader.jsp' %>
</jalios:if>
<jalios:if predicate='<%= formHandler.isOneSubmit() && formHandler.isSubmitted() %>'>
  <% setWarningMsg(glp("msg.edit.already-one-submit"), request); %>
</jalios:if>

<jalios:if predicate='<%= channel.isDataWriteEnabled() %>'>

  <div class="form-cg <%= ModalCreator.isModalPortal()? "modal-cg":""%>">
  <div class="form-cg-gray">
    <%@ include file='/jcore/doMessageBox.jsp' %>
    <%@ include file='/plugins/AssmatPlugin/jsp/contact/form/doEditPubFormAMHeader.jsp' %>
    <jsp:include page="/plugins/AssmatPlugin/jsp/contact/doEditFormulaireDeContactDuneAM.jsp" />
     <input type='hidden' name='redirect' value='<%= ServletUtil.getResourcePath(request) %>' />  
    <input type="hidden" name="noSendRedirect" value="true" /> 
    <input type="hidden" name="idMAM" value='<%= request.getParameter("idMAM") %>' /> 
    <%@ include file='/plugins/CorporateIdentityPlugin/jsp/common/form/doEditPubFormFooter.jsp' %>
  </div>
</div>

</jalios:if>
<jalios:if predicate='<%= !formInPortal %>'>
<%@ include file='/jcore/doFooter.jsp' %>
</jalios:if>
<%-- **********4A616C696F73204A434D53 *** SIGNATURE BOUNDARY * DO NOT EDIT ANYTHING BELOW THIS LINE *** --%>
<%-- cziMimDrcpdMK2zn3HzKvQ== --%>
