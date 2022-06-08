<%-- This file was automatically generated. --%>
<%--
  @Summary: FormulaireDeContactRAM editor
  @Category: Generated
  @Author: JCMS Type Processor
  @Customizable: True
  @Requestable: True
--%><%@page import="fr.cg44.plugin.tools.modal.ModalCreator"%>
<%
%><%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%
%><%@ include file='/jcore/doInitPage.jsp' %>
<% String[] formTitles = JcmsUtil.getLanguageArray(channel.getTypeEntry(FormulaireDeContactRAM.class).getLabelMap()); %>
<jsp:useBean id='formHandler' scope='page' class='generated.EditFormulaireDeContactRAMHandler'>
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
  // 0011838: Evolution formulaire contact RAM et association 
  // Cache le formulaire une fois celui-ci envoyé (ou si le destinataire est perdu)
  boolean hasParam = Util.notEmpty(request.getParameter("mailRAM"));
%> 

<jalios:if predicate='<%= !formInPortal %>'>
<%@ include file='/jcore/doHeader.jsp' %>
</jalios:if>
<jalios:if predicate='<%= formHandler.isOneSubmit() && formHandler.isSubmitted() %>'>
  <% setWarningMsg(glp("msg.edit.already-one-submit"), request); %>
</jalios:if>
<div class="form-cg <%= ModalCreator.isModalPortal()? "modal-cg":""%>">
  <div class='<%= hasParam ? "form-cg-gray" : "" %> '>
<%@ include file='/jcore/doMessageBox.jsp' %>
<jalios:if predicate='<%= channel.isDataWriteEnabled() && hasParam %>'>
<%-- <%@ include file='/work/doEditPubFormHeader.jsp' %> --%>
 <%@ include file='/plugins/AssmatPlugin/jsp/contact/form/doEditPubFormRAMHeader.jsp' %>
  <jsp:include page="/plugins/AssmatPlugin/jsp/contact/doEditFormulaireDeContactRAM.jsp" />
<tr>
  <td style='display: none;'>

    <input type='hidden' name='redirect' value='<%= ServletUtil.getResourcePath(request) %>' />

    <input type='hidden' name='ws' value='<%= workspace.getId() %>' />
  </div>
  </div>
  </td>
</tr>
 <%@ include file='/plugins/AssmatPlugin/jsp/contact/form/doEditPubFormFooter.jsp' %>
</jalios:if>
<jalios:if predicate='<%= !formInPortal %>'>
<%@ include file='/jcore/doFooter.jsp' %>
</jalios:if>
<%-- **********4A616C696F73204A434D53 *** SIGNATURE BOUNDARY * DO NOT EDIT ANYTHING BELOW THIS LINE *** --%>
<%-- 0M9bYNl7JtmUDe7nKINXHQ== --%>
