<%@ page contentType="text/html; charset=UTF-8" %><%
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
<%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>
<jalios:if predicate='<%= channel.isDataWriteEnabled() %>'>

	  <%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>
	  <%@ include file='/plugins/AssmatPlugin/jsp/contact/form/doActualiserCoordRAMFormHeader.jsp' %>
	  <jsp:include page="/plugins/AssmatPlugin/jsp/contact/doEditFormulaireActualiserCoordRAM.jsp" />
      <input type="hidden" name="noSendRedirect" value="true" data-technical-field/>
      <input type="hidden" name="redirect" value="<%= HttpUtil.encodeForHTML(ServletUtil.getResourcePath(request)) %>" data-technical-field/>
      
      <div class="ds44-form__container">
          <button data-send-native class="ds44-btnStd ds44-btn--invert" data-submit-value="true" data-submit-key="opCreate" title='<%= glp("jcmsplugin.assmatplugin.btn.envoyer") %>'><%= glp("jcmsplugin.assmatplugin.btn.envoyer") %></button>
      </div>
      
      </form>

</jalios:if>
<jalios:if predicate='<%= !formInPortal %>'>
<%@ include file='/jcore/doFooter.jsp' %>
</jalios:if>