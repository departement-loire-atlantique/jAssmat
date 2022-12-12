<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="fr.cg44.plugin.socle.SocleUtils"%>
<%@ include file='/jcore/doInitPage.jsp'%>
<%@ include file='/jcore/portal/doPortletParams.jsp' %>

<%-- Gestion du bandeau de titre :  
     Affiche le nom de la catégorie courante, sauf si on est sur l'accueil de l'espace perso.
     Dans ce cas, affiche les infos sur l'assmat connectée :
     - nom , prénom dans la zone principale de titre
     - n° agrément, date de dernière modification du profil dans la zone secondaire
--%>

<%
ProfilManager profilMngr = ProfilManager.getInstance();
ProfilASSMAT profil = (ProfilASSMAT) profilMngr.getProfilASSMAT(channel.getCurrentLoggedMember());
%>

<%
Category accueilEspacePersoCat = (Category)channel.getCategory(channel.getProperty("jcmsplugin.assmatplugin.socle.categ.accueil.perso"));
boolean accueilEspacePerso = currentCategory.equals(accueilEspacePersoCat);
StringBuffer sbfComplement = new StringBuffer();
StringBuffer sbfInfosAssmat = new StringBuffer();

if(accueilEspacePerso && Util.notEmpty(loggedMember) && Util.notEmpty(profil)){
    Date dateModif = profil.getMdate();
    
    sbfInfosAssmat.append(glp("jcmsplugin.assmatplugin.accueil.bienvenue", loggedMember.getFirstName(), loggedMember.getName()));
    
    sbfComplement.append("<p>");
    sbfComplement.append(glp("jcmsplugin.assmatplugin.numero-dossier", profil.getNum_agrement()));
    sbfComplement.append("</p>");
    
    sbfComplement.append("<p>");
    sbfComplement.append(glp("jcmsplugin.assmatplugin.derniere-actualisation.profil", SocleUtils.formatDate("dd/MM/yyyy", dateModif)));
    sbfComplement.append("</p>");
}

String title = accueilEspacePerso ? sbfInfosAssmat.toString() : currentCategory.getName();

%>

<section class="ds44-container-large">
    <ds:titleNoBanner title="<%= title %>" breadcrumb="true" complement="<%= sbfComplement.toString() %>"></ds:titleNoBanner>
</section>