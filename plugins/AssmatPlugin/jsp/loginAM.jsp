<%@page import="fr.cg44.plugin.socle.SocleUtils"%>
<%@ page import="fr.cg44.plugin.socle.VideoUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %><%
%><%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%><%
%><%@ include file='/jcore/doInitPage.jspf' %><%
%><% PortletJsp portlet = (PortletJsp)request.getAttribute(PortalManager.PORTAL_PUBLICATION); %><%
%><%@ include file='/front/doFullDisplay.jspf' %><%

request.setAttribute("title", glp("ui.fo.login.title"));

// Labels
String loginText = glp("jcmsplugin.socle.identifiant");
String passwordText = glp("ui.fo.login.lbl.passwd");

// Lien "oubli de mot de passe"
String urlOubliMotDePasse = channel.getSecuredBaseUrl(request) + channel.getProperty("jcms.resource.mail-password");

// Après authentification réussie, renvoie vers l'espace perso des assmat
Publication portalPerso = channel.getPublication(channel.getProperty("jcmsplugin.assmatplugin.socle.portail.param.id"));
String redirectUrl = Util.getString(getValidHttpUrl("redirect"), ServletUtil.getBaseUrl(request) + "index.jsp");
String redirectAccueilAssmat = Util.notEmpty(portalPerso) ? portalPerso.getDisplayUrl(userLocale) : ServletUtil.getBaseUrl(request); 
if (isLogged) {
  Group assmatGrp = channel.getGroup(channel.getProperty("jcmsplugin.assmatplugin.groupe.assistante_maternelle"));
  if (loggedMember.belongsToGroup(assmatGrp)) {
    sendRedirect(redirectAccueilAssmat);
  } else {
    sendRedirect(redirectUrl);
  }
  return;
}
%>

<main role="main" id="content">
   <div class="ds44-container-large">
      
    <ds:titleNoImage title='<%= Util.notEmpty(portlet) ? portlet.getTitle(userLang) : glp("plugin.assmatplugin.screen.login.title") %>' breadcrumb="true"></ds:titleNoImage>

      <div class="ds44-img50 ds44--xxl-padding-tb">
         <div class="ds44-inner-container">
            <div class="ds44-grid12-offset-1">
               <section class="ds44-box ds44-theme">
                  <div class="ds44-innerBoxContainer">
                     <div class="grid-12-small-1">
                        <div class="col-5-small-1">
                           <form novalidate="true" data-is-initialized="true" action="<%= ServletUtil.getResourcePath(request) %>" method="post" name="login">
                              <h2 class="h4-like ds44-mb-std"><%= glp("plugin.assmatplugin.screen.login.alreadyhaveaccount") %></h2>
                              <%= glp("plugin.assmatplugin.screen.login.assistantmaternel.desc.html") %>
                              <%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>
                              <div>
                                 <div class="ds44-form__container">
                                    <div class="ds44-posRel">
                                       <label for="form-element-78494" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= glp("ui.adm.mail-info.login") %></span></span></label>
                                       <input type="text" id="login" name="<%= channel.getAuthMgr().getLoginParameter() %>" class="ds44-inpStd" title='<%= glp("jcmsplugin.socle.facette.champ-obligatoire.title", loginText) %>' required size="14"/>
                                       <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", glp("ui.adm.mail-info.login")) %></span></button>
                                    </div>
                                 </div>
                              </div>

                              <div>
                                <div class="ds44-form__container">
                                  <div class="ds44-posRel">
                                     <label for="current-password" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= glp("ui.adm.mail-info.password") %></span></span></label>
                                     <input type="password" id="current-password" name="JCMS_password" class="ds44-inpStd" autocomplete="current-password" title='<%= glp("jcmsplugin.socle.facette.champ-obligatoire.title", passwordText) %>' required>
                                     <button class="ds44-showPassword" type="button">
                                     <i class="icon icon-visuel icon--sizeL" aria-hidden="true"></i>
                                     <span class="visually-hidden"><%= glp("plugin.assmatplugin.facette.afficher-contenu-champ", glp("ui.adm.mail-info.password")) %></span>
                                     </button>
                                     <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", glp("ui.adm.mail-info.password")) %></span></button>
                                  </div>
                               </div>
                             </div>
                              
                              <div>
                                 <div id="form-element-47598" data-name="form-element-47598" class="ds44-form__checkbox_container ds44-form__container" title="null">
                                    <div class="ds44-form__container ds44-checkBox-radio_list ">
                                       <input type="checkbox" id="name-check-form-element-47598-connect" name="form-element-47598" value="connect" class="ds44-checkbox"><label for="name-check-form-element-47598-connect" class="ds44-boxLabel" id="name-check-label-form-element-47598-connect"><%= glp("jcmsplugin.socle.form.login.memoriser") %></label>
                                    </div>
                                 </div>
                              </div>
                              <button class="ds44-btnStd ds44-btn--invert ds44-bntALeft ds44-mtb1" title='<%= glp("plugin.assmatplugin.screen.login.connecter") %>'>
                                <span class="ds44-btnInnerText"><%= glp("plugin.assmatplugin.screen.login.seconnecter") %></span><i class="icon icon-long-arrow-right" aria-hidden="true"></i>
                              </button>
                              <p class="ds44-noMrg"><a href="<%=urlOubliMotDePasse%>"><%= glp("jcmsplugin.assmatplugin.accueil.mdp.oublie") %></a></p>
                              
                              
                              <input type='hidden' name="redirect" value='<%= ServletUtil.getResourcePath(request) %>' data-technical-field/>
                              <input type="hidden" name="csrftoken" value="<%= HttpUtil.getCSRFToken(request) %>" data-technical-field>

                              <jalios:if predicate="<%= !channel.getAuthMgr().isShowingPersistentOption() %>">
                                <input type="hidden" name="<%= channel.getAuthMgr().getPersistentParameter() %>" value="<%= channel.getAuthMgr().getDefaultPersistentValue() %>" />
                              </jalios:if>
                           </form>
                        </div>
                        <div class="col-2-small-1 txtcenter ds44-h100">
                           <div class="ds44-separator ds44-flex-valign-center ds44-flex-align-center ds44-flex-container"></div>
                        </div>
                        <div class="col-5-small-1">
                           <h2 class="h4-like ds44-mb-std" id="titreCreerCompte"><%= glp("plugin.assmatplugin.screen.login.noaccount") %></h2>
                           <h3 class="h5-like"><%= glp("plugin.assmatplugin.screen.login.assistantmaternel") %></h3>
                           <p><%= glp("plugin.assmatplugin.screen.login.assistantmaternel.intro") %></p>
                           <a class="ds44-btnStd ds44-mt2" href="<%= JcmsUtil.getDisplayUrl(channel.getData(channel.getProperty("jcmsplugin.assmatplugin.socle.jsp.inscription")), userLocale)%>"><span class="ds44-btnInnerText"><%= glp("plugin.assmatplugin.screen.login.enableaccount") %></span><i class="icon icon-long-arrow-right" aria-hidden="true"></i></a>
                        </div>
                     </div>
                  </div>
               </section>
            </div>
         </div>
      </div>
   </div>
</main>
