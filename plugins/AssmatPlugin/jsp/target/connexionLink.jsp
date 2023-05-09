<%@ include file='/jcore/doInitPage.jsp' %>


<jalios:select>

	<jalios:if predicate="<%= isLogged %>">
	    <button type="button" class="ds44-btn--menu ds44-btnIcoText--maxi ds44--xl-padding" aria-label="Ouvrir le menu mon compte" aria-controls="menu" data-open-sub-menu="#subMenu-monCompte">
	        <span class="ds44-btnInnerText">Mon compte</span><i class="icon icon-user icon--large" aria-hidden="true"></i><i class="icon icon-toggle icon-down icon--large" aria-hidden="true"></i>
	    </button>
	</jalios:if>
	
	<jalios:default>
	   <% Category connexionCat = channel.getCategory("$jcmsplugin.assmat.connexion.cat"); %>
	   <a href="<%= connexionCat.getDisplayUrl(userLocale) %>" class="ds44-btnIcoText--maxi ds44--xl-padding"><span class="ds44-btnInnerText"><%= connexionCat.getName() %></span><i class="icon <%= connexionCat.getIcon() %> icon--large" aria-hidden="true"></i></a>                             
	</jalios:default>

</jalios:select>