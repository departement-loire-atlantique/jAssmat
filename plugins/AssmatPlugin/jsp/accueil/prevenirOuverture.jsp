<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file='/jcore/doInitPage.jsp'%>
<%@ taglib prefix="trsb"
  uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>

<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.InscriptionOuvertureSiteHandler'>
	<jsp:setProperty name='formHandler' property='request' value='<%=request%>' />
	<jsp:setProperty name='formHandler' property='response'	value='<%=response%>' />
	<jsp:setProperty name='formHandler' property="noRedirect" value="true" />
	<jsp:setProperty name='formHandler' property='*' />
</jsp:useBean>
<%
if (formHandler.validate()) {
  %>
  <%@ include file="/plugins/AssmatPlugin/jsp/accueil/prevenirOuvertureValidation.jspf" %>
  <% 
   return; 
} 
%>
<div class="contact boxBordure">
	<%@ include file='/jcore/doMessageBox.jsp' %>
	<div class="form-cg">
		<div class="form-cg-gray">
			<p><trsb:glp key="INFO-OUVERTURE-LBL-TITLE-HTML"></trsb:glp></p>
			<p class="ouvertureItaliqueText"><i><trsb:glp key="INFO-OUVERTURE-LBL-HTML"></trsb:glp></i></p>
			<form class="formContact" action="<%=ServletUtil.getUrl(request)%>">
			
				<div class="title-bar-container dotted-portlet">
					<label for="email"><%=glp("jcmsplugin.assmatplugin.accueil.prevenu.email")%></label>
					<input type="text" name="email" value="<%= formHandler.getEmail() %>"> <label for="tel"><%=glp("jcmsplugin.assmatplugin.accueil.prevenu.sms")%></label>
					<input type="text" name="tel" value="<%= formHandler.getTel() %>">
					<div class="clearfix"></div>
				</div>
				<p class="submit">
					<label for="submit"> 
						<input type="submit" id="submit" name="opCreate" value="Valider" class="ajax-refresh submitButton"> 
						<span class="input-box" style="background-color: #aec900"><span class="spr-recherche-ok"></span></span>
					</label> 
					<input type="hidden" name="noSendRedirect" value="true"> 
				</p>
			</form>
		</div>
	</div>
</div>