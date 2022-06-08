<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="org.jsoup.select.Evaluator.IsEmpty"%>
<%@page import="fr.cg44.plugin.tools.modal.ModalCreator"%>
<%@page import="java.lang.reflect.InvocationTargetException"%>
<%@page import="java.lang.reflect.Method"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%
  
%><%@ include file='/jcore/doInitPage.jsp'%>

<%
// On recupere l'id de la portlet JSP d'activation du profil qui est catégorisée sur l'accueil
String idJspInscription = channel.getProperty("jcmsplugin.assmatplugin.socle.jsp.inscription");
Publication jspInscription = channel.getPublication(idJspInscription);
%>

<jalios:if predicate="<%=Util.isEmpty(loggedMember) %>">
      <div class="skinBoite skinLogAM">
        <div class="">
          <h2><trsb:glp key="ESPACE-PERSO-ENCART-CONNEXION-TITRE" /></h2>
        </div>
        
            <p class="textIntro"><trsb:glp key="ESPACE-PERSO-ENCART-CONNEXION-TEXTE" /></p>
         
          
          <div>
           <%  String url = ModalCreator.doLink(null, jcmsContext, null, null, "plugins/AssmatPlugin/jsp/loginAM.jsp&redirectAccueilAssmat=true", false, false, null, true); %>         
          <a class="modal modal-cg btnType btnLogAM" data-jalios-modal-url="<%=channel.getUrl()%><%= url %>" href="<%=channel.getUrl()%><%= url %>" data-color="#b2cb00" title="Connectez-vous" data-rel="iframe">Connexion</a>
          <%  url = ModalCreator.doLink(null, jcmsContext, null, null, "plugins/AssmatPlugin/jsp/login/mailPassword.jsp", false, false, null, true); %>       
          <a class="modal modal-cg mdpLost" data-jalios-modal-url="<%=channel.getUrl()%><%= url %>" href="<%=channel.getUrl()%><%= url %>" data-color="#b2cb00" data-rel="iframe">Mot de passe oublié ? </a>
          
         </div>
        </div>
        </jalios:if>
        
        
        
        
     <jalios:if predicate="<%=Util.notEmpty(loggedMember) && !AssmatUtil.isMemberAsso(loggedMember) && !AssmatUtil.isMemberRAM(loggedMember) %>">

      <div class="skinBoite">
        <div class="">
          <h2>Bonjour <%=loggedMember.getLastName() %> <%=loggedMember.getFirstName() %></h2>
        </div>
        <div class="gabarit connexion">
        <% String url1 = ModalCreator.doLink(null, jcmsContext, null, null, "plugins/AssmatPlugin/jsp/login/mailPassword.jsp", false, false, null, true); %>       
         
          <p>Pour modifier ou actualiser votre profil :</p>
          <jalios:if predicate="<%= Util.isEmpty(loggedMember) %>">
<%--           <%  String url = ModalCreator.doLink(null, jcmsContext, null, null, "plugins/AssmatPlugin/jsp/loginAM.jsp", false, false, null, true); %>          --%>
          <a class="modal modal-cg" data-jalios-modal-url="<%= url1 %>" href="<%= url1 %>" data-color="#b2cb00" title="Connectez-vous" data-rel="iframe">Connectez-vous</a>
          
        
          <%  url1 = ModalCreator.doLink(null, jcmsContext, null, null, "plugins/AssmatPlugin/jsp/login/mailPassword.jsp", false, false, null, true); %>       
          <a class="modal modal-cg" data-jalios-modal-url="<%= url1 %>" href="<%= url1 %>" data-color="#b2cb00" data-rel="iframe"><%=glp("jcmsplugin.assmatplugin.accueil.mdp.oublie") %></a>
          </jalios:if> 
           <jalios:if predicate="<%= Util.notEmpty(loggedMember) %>">    
                       <%                          
                       Publication portalPerso = channel.getPublication(channel.getProperty("jcmsplugin.assmatplugin.socle.portail.param.id"));
                       %>
                   <a class=""  href='<%=portalPerso.getDisplayUrl(userLocale)%>' title="Accéder à votre espace personnel" >Mon espace personnel</a>
                   <a class=""  href="front/logout.jsp" title="Se déconnectez" >Me déconnecter</a>
                     </jalios:if>    
          
        </div>
      </div>
  </jalios:if>

<!-- FIN -->




