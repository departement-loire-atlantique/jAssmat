<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="javax.mail.MessagingException"%>
<%@page import="com.jalios.jcms.mail.MailMessage"%>
<%@page import="com.orange.ceo.client.bean.SendMessage"%>
<%@page import="com.jalios.jcms.handler.QueryHandler"%>
<%@page import="com.jalios.jcms.tools.FormHandlerChecker"%>
<%@ include file='/jcore/doInitPage.jsp' %><%
%><%@ include file='/admin/doAdminHeader.jsp' %>

<% 

if (!isAdmin) { 
  sendForbidden(request, response);
  return;
}
String idFormDebut = "";
if(Util.notEmpty(request.getParameter("idFormDebut"))) {
  idFormDebut = request.getParameter("idFormDebut");
}
%>

<%!

private void sendMail(String to, String content, String replyTo) {
  // Création du mail
  MailMessage msg = new MailMessage();
  msg.setTo(to);
  msg.setFrom(AssmatUtil.getDefaultEmail());
  if(Util.notEmpty(replyTo)) {
    msg.setReplyTo(replyTo);
  }
  msg.setSubject("Message du site assmat");
  content = retourALaLigne(content);
  msg.setContentHtml(content);
  try {
   msg.send();
  } catch (MessagingException e) {
   logger.error(JcmsUtil.glpd("jcmsplugin.assmatplugin.inscription.error.mail.exception"),e);
  }   
}

private String retourALaLigne(String content){
  return Util.replaceAll(content, "\n", "<br />");
}

%>

<style>

.non-valide td {
  background-color: orange !important;
}

</style>

<h1 class="boTitle">Renvoyer les messages aux assistantes maternelles</h1>

<form method="get">

	<div style="margin-bottom: 20px;">
	  Depuis <input type="text" size="50" name="idFormDebut" value="<%= idFormDebut%>"> id debut (inclus). Les formulaires à partir de cet id et tous les plus récents
	</div>
	
	<div style="margin-bottom: 25px;">
		<input type="submit" value="Test" name="opTest" class="btn btn-primary">
		<input type="submit" value="Envoyer" name="opSend" class="btn btn-danger" style="margin-left: 0px;">
	</div>

</form>


<%

SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy à HH:mm");

Set<FormulaireDeContactDuneAM> resultAll = new TreeSet(Publication.getPdateComparator());
resultAll.addAll(channel.getAllPublicationSet(FormulaireDeContactDuneAM.class, channel.getDefaultAdmin()));


Set<FormulaireDeContactDuneAM> result = new TreeSet(Publication.getPdateComparator());



for(FormulaireDeContactDuneAM itForm : resultAll) {
  result.add(itForm);
  
  // envoie l'e-mail 
  if(Util.notEmpty(request.getParameter("opSend"))) {
    String courriel = itForm.getCourriel();
    String mailAm = itForm.getMailam();
    String cDate = formater.format(itForm.getCdate());    
    Boolean notValide = Util.isEmpty(courriel) || Util.isEmpty(mailAm);
    // Si les infos du form sont correct alors envoyer le mail
    if(!notValide) {
      
      String mailContent = "Bonjour <br/>" +
          "<br/>"+
          "Suite à un problème technique sur le site assmat.loire-atlantique.fr, le message ci-dessous ne vous est pas parvenu le " + cDate + ".<br/>" +
          "Pour être certains que vous avez pu rentrer en contact avec l'émetteur du message, nous préférons vous l'envoyer à nouveau.<br/>" +
          "<i>NB : il est normal que vous puissiez recevoir plusieurs fois le même message.</i><br/><br/>" +
          "Merci de votre compréhension.<br/><br/>"+
          "<i>Le Département de Loire-Atlantique</i><br/>"+
          "--------------------------------------------------------<br/>"+
          "<br/>"+
          "<b>Message envoyé depuis le site par " + itForm.getFirstName() + " " + itForm.getName() +":</b><br/>"+
          retourALaLigne(itForm.getMessage()) + "<br/>"+
          "<br/>"+
          "<b>Pour répondre : </b><br />" + 
          "<b>Par email : </b>" + courriel + "<br/>"+
          "<b>Par téléphone : </b>" + (Util.isEmpty(itForm.getPhone()) ? "non renseigné" : itForm.getPhone());
          
          
      sendMail(mailAm, mailContent, courriel);      
    }
  }
  
  if(idFormDebut.equals(itForm.getId()) ){
    break;
  }
}

%>



<div class="box bubble search"> 
  <div class="box-body">    
    <h3>Il y a <%= result.size() %> résultats.</h3>
  </div>
  <div class="bubble-tail"></div>
</div>


<jalios:if predicate='<%= Util.notEmpty(request.getParameter("opSend")) %>'>
  <h3 class="alert alert-success">Les formulaires suivants ont été envoyés</h3>
</jalios:if>

<table class="data-table">
  <tbody>
    <tr>
      <td class="listHeader">E-mail</td>
      <td class="listHeader">To</td>
      <td class="listHeader">Date de publication</td>
      <td class="listHeader">Id du form</td>
      <td class="listHeader">Lien</td>
    </tr>
    
		<jalios:foreach collection="<%= result %>" name="itForm" type="FormulaireDeContactDuneAM">
		  <% 
			  String courriel = itForm.getCourriel();
			  String mailAm = itForm.getMailam();
			  String cDate = formater.format(itForm.getCdate());			  
			  Boolean notValide = Util.isEmpty(courriel) || Util.isEmpty(mailAm);
		  %>
		  
		  <tr <%= notValide ? "class='non-valide'" : ""  %>>
		    <td><%= courriel %></td>
		    <td><%= mailAm %></td>
		    <td><%= cDate %></td>
		    <td><%= itForm.getId() %></td>
		    <td><%= itForm.getDisplayLink(userLocale) %></td>
		  </tr>
		</jalios:foreach>
	</tbody>
</table>

<%@ include file='/admin/doAdminFooter.jsp' %>