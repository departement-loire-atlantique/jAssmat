<%@page import="org.jsoup.select.Evaluator.IsEmpty"%>
<%@page import="fr.cg44.plugin.tools.modal.ModalCreator"%>
<%@page import="java.lang.reflect.InvocationTargetException"%>
<%@page import="java.lang.reflect.Method"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%
  
%><%@ include file='/jcore/doInitPage.jsp'%>

<%
// On recupere l'id de la portlet JSP d'activation du profil qui est catégorisée sur l'accueil
String idJspInscription = channel.getProperty("jcmsplugin.assmatplugin.socle.jsp.inscription");
Publication jspInscription = channel.getPublication(idJspInscription);
%>

			<div class="skinBoite">
				<div class="titre">
					<h2><%=glp("jcmsplugin.assmatplugin.accueil.activeprofil")%></h2>
				</div>
				<div class="gabarit activationProfil">
					<div>
						<div>
							<img src="plugins/AssmatPlugin/img/icon-activation.png"
								alt="etape1" />
						</div>
						<div>
							<p class="instructions"><%=glp("jcmsplugin.assmatplugin.accueil.activecompte") %></p>
						</div>
						<div style="clear: both;"></div>
					</div>

					<div>
						<div>
							<img src="plugins/AssmatPlugin/img/icon-calendrier.png"
								alt="etape2" />
						</div>
						<div>
							<p class="instructions"><%=glp("jcmsplugin.assmatplugin.accueil.dispo") %></p>
						</div>
						<div style="clear: both;"></div>
					</div>

					<div>
						<a href="<%=jspInscription.getDisplayUrl(userLocale) %>" class="btnType"><%=glp("jcmsplugin.assmatplugin.accueil.activego") %></a>
					</div>

				</div>
			</div>

			<div class="skinBoite">
				<div class="titre">
					<h2><%=glp("jcmsplugin.assmatplugin.accueil.deja.active") %></h2>
				</div>
				<div class="gabarit connexion">
					<p><%=glp("jcmsplugin.assmatplugin.accueil.modifier.actualiser") %></p>
					<jalios:if predicate="<%= Util.isEmpty(loggedMember) %>">
					<%	String url = ModalCreator.doLink(null, jcmsContext, null, null, "plugins/AssmatPlugin/jsp/loginAM.jsp", false, false, null, true); %>					
					<a class="modal modal-cg" data-jalios-modal-url="<%= url %>" href="<%= url %>" data-color="#b2cb00" title="Connectez-vous" data-rel="iframe">Connectez-vous</a>
					
				
					<%  url = ModalCreator.doLink(null, jcmsContext, null, null, "plugins/AssmatPlugin/jsp/login/mailPassword.jsp", false, false, null, true); %>       
					<a class="modal modal-cg" data-jalios-modal-url="<%= url %>" href="<%= url %>" data-color="#b2cb00" data-rel="iframe"><%=glp("jcmsplugin.assmatplugin.accueil.mdp.oublie") %></a>
					</jalios:if> 
					 <jalios:if predicate="<%= Util.notEmpty(loggedMember) %>">    
			                 <% 
			                 String idCategPerso = channel.getProperty("jcmsplugin.assmatplugin.socle.categ.accueil.perso");
			                 Category categPerso = channel.getCategory(idCategPerso); %>
			             <a class=""  href="<%= categPerso.getDisplayUrl(userLocale) %>" title="Accéder à votre espace personnel" >Mon espace personnel</a>
			             <a class=""  href="front/logout.jsp" title="Se déconnectez" >Me déconnecter</a>
                     </jalios:if>    
					
				</div>
			</div>


<!-- FIN -->




