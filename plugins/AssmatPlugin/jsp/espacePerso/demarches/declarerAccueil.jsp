<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.cg44.plugin.assmat.SemainierUtil"%>
<%@page import="fr.cg44.plugin.assmat.Semainier"%>
<%@page import="fr.cg44.plugin.tools.googlemaps.proxy.ProxyTarget"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ include file='/jcore/portal/doPortletParams.jsp' %>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>

<%
PortletJsp box = (PortletJsp) portlet;

ProfilManager profilMngr = ProfilManager.getInstance();
ProfilASSMAT profil = (ProfilASSMAT) profilMngr.getProfilASSMAT(channel.getCurrentLoggedMember());

if(Util.isEmpty(profil)){
  sendForbidden(request, response);
  return;
}
%>

<jsp:useBean id='formHandler' scope='page' class='generated.EditDeclarerAccueilHandler'>
  <jsp:setProperty name='formHandler' property='request' value='<%= request %>'/>
  <jsp:setProperty name='formHandler' property='response' value='<%= response %>'/>
  <jsp:setProperty name='formHandler' property='*' />
</jsp:useBean>


<%

if (formHandler.validate()) {
  // TODO afficher le ok 
  //sendRedirect(ServletUtil.getResourcePath(request));
  jcmsContext.addMsg(new JcmsMessage(com.jalios.jcms.context.JcmsMessage.Level.INFO, AssmatUtil.getMessage("PROFIL-CHANGE-SAVE")));

}

AssmatSolis solisAssmat = Util.getFirst(SolisManager.getInstance().getAssmatSolisByNumAgrement(profil.getNum_agrement()));
%>


<!-- DEBUT DECLARER UN ACCUEIL / MES DEMARCHES -->

<div class="mesDemarches declarerAccueil form-cg">

<%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
<%@ include file='/jcore/doMessageBox.jsp' %>


	<p>Dans les 8 jours qui suivent l’arrivée ou le départ de l’enfant,
		vous devez transmettre à l’unité agrément la déclaration de début ou
		de fin d’accueil. Il s'agit d'une obligation prévue dans l'article R
		421 - 39 du code de l'action sociale et des familles.</p>
	<p>Cette déclaration permet d'actualiser automatiquement vos
		disponibilités sur le site assmat.loire-atlantique et d'informer le
		service protection maternelle et infantile du Département, de votre
		activité professionnelle effective et prévisionnelle.</p>
	<p>Merci de renseigner une déclaration à chaque nouvel accueil. Un
		formulaire de <a class="lienSouligne" href="#">déclaration de fin d'accueil</a> est également proposé sur
		le site.</p>

	<div class="borderDot title-bar-container dotted-portlet"></div>
	
	<div class="saisieDossier form-cg-gray form-cg-white">
		<form action="<%= ServletUtil.getResourcePath(request) %>" method="post">
			<div class="boxBordure coordonnees">
				<p class="nDossier">N° de dossier : <%= profil.getNum_agrement() %></p>
				<p class="prenomNom"><%= loggedMember.getFirstName() %> <%= loggedMember.getName() %></p>
				<p class="adresse"><%= solisAssmat.getAdresseDomicile() %></p>
				<p class="ville"><%= solisAssmat.getCpDomicile() %> <%= solisAssmat.getCommuneDomicile() %></p>
			</div>
       
      <input type="hidden" name="title" value="<%= loggedMember %>">
      <% if (isLogged && HttpUtil.isCSRFEnabled()) { %>
		    <input type="hidden" name="csrftoken" value="<%= getCSRFToken() %>" />
		  <% } %>

      
			<p class="important">Agréée pour un enfant de 0 à 3 ans et pour
				deux enfants de 2 à 6 ans</p>
			<p>Les champs marqués d'un * sont obligatoires</p>

			<div class="ligneSaisie">
				<p class="important">Déclare que l'enfant*</p>
				<label for="prenomEnfant">Prénom</label> <input type="text"
					name="prenomEnfant" class="prenom" id="prenomEnfant" value='<%= formHandler.getAvailablePrenomEnfant() != null ? formHandler.getAvailablePrenomEnfant() : "" %>'> 
			</div>
			
			<div class="ligneSaisie">
			<label for="nomEnfant">Nom</label> <input type="text" name="nomEnfant"
          id="nomEnfant" value='<%= formHandler.getAvailableNomEnfant()!= null ? formHandler.getAvailableNomEnfant() : "" %>'>
			</div>

			<div class="ligneSaisie">
				<label for="dateNaissance">Né le *</label> <input type="date"
					name="dateNaissanceEnfant" id="dateNaissanceEnfant" value='<%= formHandler.getAvailableDateNaissanceEnfant()!= null ? formHandler.getAvailableDateNaissanceEnfant() : "" %>'/>
			</div>

			<div class="ligneSaisie">
				<p class="important">Représentant légal*</p>
				<label for="prenomRepresentant">Prénom</label> <input type="text"
					name="prenomRepresentant" id="prenomRepresentant" class="prenom" value='<%= formHandler.getAvailablePrenomRepresentant()!= null ? formHandler.getAvailablePrenomRepresentant() : "" %>'>
			</div>
			
			<div class="ligneSaisie">
				<label for="nomRepresentant">Nom</label> <input type="text"
					name="nomRepresentant" id="nomRepresentant" value='<%= formHandler.getAvailableNomRepresentant()!= null ? formHandler.getAvailableNomRepresentant() : "" %>'>
			</div>

			<div class="ligneSaisie">
				<label for="adresse">Adresse*</label> <input type="text"
					name="adresse" id="adresse" class="adresse" value='<%= formHandler.getAvailableAdresse() != null ? formHandler.getAvailableAdresse() : "" %>'>
			</div>

			<div class="ligneSaisie">
				<label for="codePostal">Code postal*</label> <input type="text"
					name="codePostal" id="codePostal" value='<%= formHandler.getAvailableCodePostal() != null ? formHandler.getAvailableCodePostal() : "" %>'>
				<p class="exemple">(ex : 44000)</p>
			</div>

			<div class="ligneSaisie">
				<label for="commune">Commune*</label> <input type="text"
					name="commune" id="commune" value='<%= formHandler.getAvailableCommune() != null ? formHandler.getAvailableCommune() : "" %>'>
			</div>

			<div class="ligneSaisie">
				<label for="telephone">Téléphone*</label> <input type="tel"
					name="telephone" id="telephone" value='<%= formHandler.getAvailableTelephone() != null ? formHandler.getAvailableTelephone() : "" %>'>
				<p class="exemple">(ex : 02 xx xx xx xx)</p>
			</div>

			<div class="ligneSaisie">
				<label for="dateAccueil" class="important">est accueilli
					depuis le*</label> <input type="date" name="accueilliDepuis" id="dateAccueil" value='<%= formHandler.getAvailableAccueilliDepuis() != null ? formHandler.getAvailableAccueilliDepuis() : "" %>'>
				<p class="exemple">(ex : JJ/MM/AAAA)</p>
			</div>

			<p class="important intituleSaisie">Selon les modalités suivantes</p>
			<textarea rows="8" cols="80" name="modalites"><%= formHandler.getAvailableModalites()!= null ? formHandler.getAvailableModalites() : "" %></textarea>

			<p class="important intituleSaisie">Merci de renseigner le
				tableau de présence de l'enfant*</p>

			<table class="semainier horaires">

				<tr>
					<th scope="col"></th>
					<th scope="col">8h</th>
					<th scope="col">9h</th>
					<th scope="col">10h</th>
					<th scope="col">11h</th>
					<th scope="col">12h</th>
					<th scope="col">13h</th>
					<th scope="col">14h</th>
					<th scope="col">15h</th>
					<th scope="col">16h</th>
					<th scope="col">17h</th>
					<th scope="col">18h</th>
					<th scope="col">19h</th>
					<th scope="col">20h</th>
				</tr>
				<tr>
					<th scope="row">Lundi</th>
					<td><input type="checkbox" id="test1" name="test1" checked /><label
						for="test1"></label></td>
					<td><input type="checkbox" id="test2" name="test2" checked /><label
						for="test2"></label></td>
					<td><input type="checkbox" id="test3" name="test3" /><label
						for="test3"></label></td>
					<td><input type="checkbox" id="test4" name="test4" /><label
						for="test4"></label></td>
					<td><input type="checkbox" id="test5" name="test5" /><label
						for="test5"></label></td>
					<td><input type="checkbox" id="test6" name="test6" /><label
						for="test6"></label></td>
					<td><input type="checkbox" id="test7" name="test7" /><label
						for="test7"></label></td>
					<td><input type="checkbox" id="test8" name="test8" /><label
						for="test8"></label></td>
					<td><input type="checkbox" id="test9" name="test9" /><label
						for="test9"></label></td>
					<td><input type="checkbox" id="test10" name="test10" /><label
						for="test10"></label></td>
					<td><input type="checkbox" id="test11" name="test11" /><label
						for="test11"></label></td>
					<td><input type="checkbox" id="test12" name="test12" /><label
						for="test12"></label></td>
					<td><input type="checkbox" id="test13" name="test13" /><label
						for="test13"></label></td>
					<td class="toutHoraires"><input type="checkbox" id="test14"
						name="test14" /><label for="test14">Tout</label></td>
				</tr>
				<tr>
					<th scope="row">Mardi</th>
					<td><input type="checkbox" id="test15" name="test15" /><label
						for="test15"></label></td>
					<td><input type="checkbox" id="test16" name="test16" /><label
						for="test16"></label></td>
					<td><input type="checkbox" id="test17" name="test17" /><label
						for="test17"></label></td>
					<td><input type="checkbox" id="test18" name="test18" /><label
						for="test18"></label></td>
					<td><input type="checkbox" id="test19" name="test19" /><label
						for="test19"></label></td>
					<td><input type="checkbox" id="test20" name="test21" /><label
						for="test21"></label></td>
					<td><input type="checkbox" id="test21" name="test21" /><label
						for="test21"></label></td>
					<td><input type="checkbox" id="test22" name="test22" /><label
						for="test22"></label></td>
					<td><input type="checkbox" id="test23" name="test23" /><label
						for="test23"></label></td>
					<td><input type="checkbox" id="test24" name="test24" /><label
						for="test24"></label></td>
					<td><input type="checkbox" id="test25" name="test25" /><label
						for="test25"></label></td>
					<td><input type="checkbox" id="test26" name="test26" /><label
						for="test26"></label></td>
					<td><input type="checkbox" id="test27" name="test27" /><label
						for="test7"></label></td>
					<td class="toutHoraires"><input type="checkbox" id="test28"
						name="test28" /><label for="test28">Tout</label></td>
				</tr>
				<tr>
					<th scope="row">Mercredi</th>
					<td><input type="checkbox" id="test29" name="test29" /><label
						for="test29"></label></td>
					<td><input type="checkbox" id="test30" name="test30" /><label
						for="test31"></label></td>
					<td><input type="checkbox" id="test31" name="test31" /><label
						for="test31"></label></td>
					<td><input type="checkbox" id="test32" name="test32" /><label
						for="test32"></label></td>
					<td><input type="checkbox" id="test33" name="test33" /><label
						for="test33"></label></td>
					<td><input type="checkbox" id="test34" name="test34" /><label
						for="test34"></label></td>
					<td><input type="checkbox" id="test35" name="test35" /><label
						for="test35"></label></td>
					<td><input type="checkbox" id="test36" name="test36" /><label
						for="test36"></label></td>
					<td><input type="checkbox" id="test37" name="test37" /><label
						for="test37"></label></td>
					<td><input type="checkbox" id="test38" name="test38" /><label
						for="test38"></label></td>
					<td><input type="checkbox" id="test39" name="test39" /><label
						for="test39"></label></td>
					<td><input type="checkbox" id="test40" name="test40" /><label
						for="test40"></label></td>
					<td><input type="checkbox" id="test41" name="test41" /><label
						for="test41"></label></td>
					<td class="toutHoraires"><input type="checkbox" id="test41"
						name="test41" /><label for="test41">Tout</label></td>
				</tr>
				<tr>
					<th scope="row">Jeudi</th>
					<td><input type="checkbox" id="test42" name="test42" /><label
						for="test42"></label></td>
					<td><input type="checkbox" id="test43" name="test43" /><label
						for="test23"></label></td>
					<td><input type="checkbox" id="test44" name="test44" /><label
						for="test44"></label></td>
					<td><input type="checkbox" id="test45" name="test45" /><label
						for="test25"></label></td>
					<td><input type="checkbox" id="test46" name="test46" /><label
						for="test46"></label></td>
					<td><input type="checkbox" id="test47" name="test47" /><label
						for="test47"></label></td>
					<td><input type="checkbox" id="test48" name="test48" /><label
						for="test48"></label></td>
					<td><input type="checkbox" id="test49" name="test49" /><label
						for="test49"></label></td>
					<td><input type="checkbox" id="test50" name="test50" /><label
						for="test50"></label></td>
					<td><input type="checkbox" id="test51" name="test51" /><label
						for="test51"></label></td>
					<td><input type="checkbox" id="test52" name="test52" /><label
						for="test52"></label></td>
					<td><input type="checkbox" id="test53" name="test53" /><label
						for="test53"></label></td>
					<td><input type="checkbox" id="test54" name="test54" /><label
						for="test54"></label></td>
					<td class="toutHoraires"><input type="checkbox" id="test55"
						name="test55" /><label for="test55">Tout</label></td>
				</tr>
				<tr>
					<th scope="row">Vendredi</th>
					<td><input type="checkbox" id="test56" name="test56" /><label
						for="test56"></label></td>
					<td><input type="checkbox" id="test57" name="test57" /><label
						for="test57"></label></td>
					<td><input type="checkbox" id="test58" name="test58" /><label
						for="test58"></label></td>
					<td><input type="checkbox" id="test59" name="test59" /><label
						for="test59"></label></td>
					<td><input type="checkbox" id="test60" name="test60" /><label
						for="test60"></label></td>
					<td><input type="checkbox" id="test61" name="test61" /><label
						for="test61"></label></td>
					<td><input type="checkbox" id="test62" name="test62" /><label
						for="test62"></label></td>
					<td><input type="checkbox" id="test63" name="test63" /><label
						for="test63"></label></td>
					<td><input type="checkbox" id="test64" name="test64" /><label
						for="test64"></label></td>
					<td><input type="checkbox" id="test65" name="test65" /><label
						for="test65"></label></td>
					<td><input type="checkbox" id="test66" name="test66" /><label
						for="test66"></label></td>
					<td><input type="checkbox" id="test67" name="test67" /><label
						for="test7"></label></td>
					<td><input type="checkbox" id="test68" name="test68" /><label
						for="test68"></label></td>
					<td class="toutHoraires"><input type="checkbox" id="test69"
						name="test69" /><label for="test69">Tout</label></td>
				</tr>
				<tr>
					<th scope="row">Samedi</th>
					<td><input type="checkbox" id="test70" name="test70" /><label
						for="test70"></label></td>
					<td><input type="checkbox" id="test71" name="test72" /><label
						for="test72"></label></td>
					<td><input type="checkbox" id="test73" name="test73" /><label
						for="test73"></label></td>
					<td><input type="checkbox" id="test74" name="test74" /><label
						for="test74"></label></td>
					<td><input type="checkbox" id="test75" name="test75" /><label
						for="test75"></label></td>
					<td><input type="checkbox" id="test76" name="test76" /><label
						for="test76"></label></td>
					<td><input type="checkbox" id="test77" name="test77" /><label
						for="test77"></label></td>
					<td><input type="checkbox" id="test78" name="test78" /><label
						for="test7"></label></td>
					<td><input type="checkbox" id="test79" name="test79" /><label
						for="test79"></label></td>
					<td><input type="checkbox" id="test80" name="test80" /><label
						for="test80"></label></td>
					<td><input type="checkbox" id="test81" name="test81" /><label
						for="test81"></label></td>
					<td><input type="checkbox" id="test82" name="test82" /><label
						for="test82"></label></td>
					<td><input type="checkbox" id="test83" name="test83" /><label
						for="test83"></label></td>
					<td class="toutHoraires"><input type="checkbox" id="test84"
						name="test84" /><label for="test84">Tout</label></td>
				</tr>
				<tr>
					<th scope="row">Dimanche</th>
					<td><input type="checkbox" id="test85" name="test85" /><label
						for="test85"></label></td>
					<td><input type="checkbox" id="test86" name="test86" /><label
						for="test86"></label></td>
					<td><input type="checkbox" id="test87" name="test87" /><label
						for="test87"></label></td>
					<td><input type="checkbox" id="test88" name="test88" /><label
						for="test88"></label></td>
					<td><input type="checkbox" id="test89" name="test89" /><label
						for="test89"></label></td>
					<td><input type="checkbox" id="test90" name="test90" /><label
						for="test90"></label></td>
					<td><input type="checkbox" id="test91" name="test92" /><label
						for="test92"></label></td>
					<td><input type="checkbox" id="test93" name="test93" /><label
						for="test93"></label></td>
					<td><input type="checkbox" id="test94" name="test94" /><label
						for="test94"></label></td>
					<td><input type="checkbox" id="test95" name="test95" /><label
						for="test95"></label></td>
					<td><input type="checkbox" id="test96" name="test96" /><label
						for="test96"></label></td>
					<td><input type="checkbox" id="test97" name="test97" /><label
						for="test97"></label></td>
					<td><input type="checkbox" id="test98" name="test98" /><label
						for="test98"></label></td>
					<td class="toutHoraires"><input type="checkbox" id="test99"
						name="test99" /><label for="test99">Tout</label></td>
				</tr>
			</table>

			<div class="boxCouleur planningIrreguliers">
				<p>
					<span class="important">Si les plannings sont irréguliers,</span>
					merci de charger un document spécifique
				</p>
				
					<input type="file" name="file" /> <input type="submit"
						class="envoyer" id="envoyer" name="envoyer" value="Envoyer">
				
			</div>

			<div class="listeVerticale">
				<p class="important intituleSaisie">et déclare</p>
				<p>que je dispose encore de</p>
				<input type="text" name="place03Ans" id="place03Ans" class="nbPlace">
				<label for="place03Ans">place pour un enfant de 0 à 3 ans</label> <input
					type="text" name="place26Ans" id="place26Ans" class="nbPlace">
				<label for="place26Ans">place pour un enfant de 2 à 6 ans</label> <input
					type="checkbox" name="pasDisponible" id="place26Ans"> <label
					for="pasDisponible">que je ne suis plus disponible</label>
			</div>


			<div class="borderDot title-bar-container dotted-portlet"></div>

			<div class="listeVerticale2 mailNotif">
				<p>Un Mail de notification va être envoyé au représentant légal
					pour visa et confirmation des informations saisies</p>

				<label for="lieuDeSaisie">À*</label> <input type="text"
					name="lieuDeSaisie" id="lieuDeSaisie"> <label
					for="dateDeSaisie">le*</label> <input type="date"
					name="dateDeSaisie" id="dateDeSaisie">
			</div>

			<div class="listeVerticale mentionsParticulieres">
				<p class="important intituleSaisie">Mentions particulières</p>
				<p>Sed tincidunt dictum varius. Sed viverra ornare nibh
					vulputate feugiat. Interdum et malesuada fames ac ante ipsum primis
					in faucibus. Mauris cursus ultricies pretium. Quisque eget mi ante.
					Sed venenatis posuere sodales. Praesent ut leo et nisl sollicitudin
					rutrum eu eget nulla. Suspendisse potenti. Suspendisse in massa
					eros. Pellentesque blandit sapien id est venenatis ultricies. In
					hac habitasse platea dictumst. Lorem ipsum dolor sit amet,
					consectetur adipiscing elit.</p>

				<input type="checkbox" name="certifieExactitude"
					id="certificationInformations" <%= formHandler.getAvailableCertifieExactitude() ? "checked":"" %>> 
				<label
					for="certificationInformations">Je certifie l'exactitude
					des informations*</label> 
				<input type="checkbox" name="accepteMentions" 
				  id="mentionsParticulieres" <%= formHandler.getAvailableAccepteMentions() ? "checked":"" %>> 
				<label
					for="mentionsParticulieres">J'ai lu et j'accepte les
					mentions particulières*</label>
			</div>
    

			<div class="borderDot title-bar-container dotted-portlet"></div>

			<p class="submit">
        <label for="submit"> <input type="submit" id="submit"
          name="opCreate" value="Enregistrer" class="submitButton"> <span
          class="input-box" style="background-color: #000000"> <span
            class="spr-recherche-ok"></span>
        </span>
        </label>
      </p>
				
			<input type='hidden' name='redirect' value='<%= ServletUtil.getResourcePath(request) %>' />	

		</form>
	</div>

</div>