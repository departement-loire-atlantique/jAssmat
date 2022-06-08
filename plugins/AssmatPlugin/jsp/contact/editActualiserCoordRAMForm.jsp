<%-- This file was automatically generated. --%>
<%--
  @Summary: FormulaireDeContactCommune editor
  @Category: Generated
  @Author: JCMS Type Processor
  @Customizable: True
  @Requestable: True
--%><%@page import="fr.cg44.plugin.tools.modal.ModalCreator"%>
<%
%><%@ page contentType="text/html; charset=UTF-8" %><%
%><%@ include file='/jcore/doInitPage.jsp' %>



<% String[] formTitles = JcmsUtil.getLanguageArray(channel.getTypeEntry(FormulaireDeContactActualiserCoo.class).getLabelMap()); 
boolean validate = false;
if(Util.notEmpty(request.getParameter("validate"))){
  validate=true;
}
%>
<jsp:useBean id='formHandler' scope='page' class='generated.EditFormulaireDeContactActualiserCooHandler'>
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
<%if(validate){ %>
<div class="alert alert-block fade in alert-cg alert-info"><button type="button" class="close" data-dismiss="alert"><span class="spr-modal-close"></span></button><h4>Information</h4>
      <p>Votre message a bien été envoyé</p>
    
    </div>
<%} %>
<jalios:if predicate='<%= channel.isDataWriteEnabled() %>'>

  <div class="form-cg formChangePassRAM <%= ModalCreator.isModalPortal()? "modal-cg":""%>">
	<div class="form-cg-gray">
	  <%@ include file='/jcore/doMessageBox.jsp' %>
	  <%@ include file='/plugins/AssmatPlugin/jsp/contact/form/doActualiserCoordRAMFormHeader.jsp' %>
	  <jsp:include page="/plugins/AssmatPlugin/jsp/contact/doEditFormulaireActualiserCoordRAM.jsp" />
	  <input type='hidden' name='redirect' value='<%= ServletUtil.getResourcePath(request) %>' />  
    <input type="hidden" name="noSendRedirect" value="true" /> 
	  <%@ include file='/plugins/CorporateIdentityPlugin/jsp/common/form/doEditPubFormFooter.jsp' %>
	</div>
</div>

</jalios:if>
<jalios:if predicate='<%= !formInPortal %>'>
<%@ include file='/jcore/doFooter.jsp' %>
</jalios:if>
<%-- **********4A616C696F73204A434D53 *** SIGNATURE BOUNDARY * DO NOT EDIT ANYTHING BELOW THIS LINE *** --%>
<%-- MD67fvw13KJY7il+1Uy4Xw== --%>
