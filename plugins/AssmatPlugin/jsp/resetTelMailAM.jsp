<%@ include file='/jcore/doInitPage.jsp' %><%
%><%@ include file='/admin/doAdminHeader.jsp' %>

<%-- Remplace les emails et tel portables des Assmat par les infos du testeur
	 Evite d'envoyer de vraies infos par inadvertance
--%>
<%
if(!isAdmin) {
  sendForbidden(request, response);
  return;
}
%>

<h1 class="boTitle">R�initialisation des donn�es des assmats</h1>
<p>Les mails et tel portables des Assmat seront remplac�es par les valeurs ci-dessous : </p>


<form>
	<label for="email">Email</label>
	<input type="text" id="email" name="email" size="30">
	<label for="mobile">Tel portable</label>
	<input type="text" id="mobile" name="mobile">
	
    <input type="hidden" name="executeReprise" value="true">
    <input class="btn btn-danger modal confirm" type="submit" value="Lancer"/>
</form>


<%
if(getBooleanParameter("executeReprise", false)) {
	
	Group assmatGroup = channel.getGroup("agi_60678");
	Set<Member> assmatSet = assmatGroup.getMemberSet(); 
	
	String newMail = request.getParameter("email");
	String newMobile = request.getParameter("mobile");
	
	logger.warn("Assmat - D�but r�initialisation...");
    logger.warn("Nouveau mail : "+newMail);
    logger.warn("Nouveau tel : "+newMobile);
    
    for(Member itMember : assmatSet) {
    	
	    logger.warn(itMember.getName() + " / tel :  " + itMember.getMobile() + " / email : " + itMember.getEmail()); 
	    
	    Member itClone = (Member) itMember.getUpdateInstance();
	    
	    // Sauvegarde le vrai email de l'AM dans le champ "Coordonn�es"
	    itClone.setAddress(itMember.getEmail());
	    
	 	// Sauvegarde le vrai tel portable de l'AM dans le champ "Informations"
	    itClone.setInfo(itMember.getMobile());
	 	
	 	// Remplace le mail par la valeur re�ue du formulaire
	    itClone.setEmail(newMail);
	 	
	 	// Remplace le tel portable par la valeur re�ue du formulaire
	    itClone.setMobile(newMobile);
	 	
	    logger.warn(itMember.getName() + " / new tel :  " + itClone.getMobile() + " / new email : " + itClone.getEmail());

	    itClone.performUpdate(loggedMember);
            
    }
    logger.warn("Assmat - Fin r�initialisation");
}

%>


<%@ include file='/admin/doAdminFooter.jsp' %>