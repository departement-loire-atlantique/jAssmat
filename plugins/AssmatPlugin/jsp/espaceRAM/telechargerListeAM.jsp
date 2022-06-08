<%--
   @Summary: Login form for public channel
   @Category: Authentication
   @Deprecated: False
   @Customizable: True
   @Requestable: True
--%><%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.tools.googlemaps.proxy.Proxy"%>
<%@page import="fr.cg44.plugin.tools.modal.ModalCreator"%><%
%><%@ page import="com.jalios.jcms.authentication.handlers.DelegationAuthenticationHandler" %><%
%><%@ include file='/jcore/doInitPage.jsp' %>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%

jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/PortletLogin/portletLoginFullDisplay.css");


String idPortailRAM = channel.getProperty("plugin.assmatplugin.portal.ram.id");
Publication portalRAM = channel.getPublication(idPortailRAM);


jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");
%><%@ include file='/jcore/doHeader.jsp' %>

<div class="espaceRAMTelechargement">

<b><p class="entete1"><trsb:glp key="ESPACE-RAM-TELECHARGEMENT-ENTETE1" ></trsb:glp></p>
<p class="entete2"><trsb:glp key="ESPACE-RAM-TELECHARGEMENT-ENTETE2" ></trsb:glp></p>
</b>

<!-- Complet -->
<div class="blocTelechargement">
<img src ="plugins/ToolsPlugin/images/bullet.jpg"/><a href='<%=AssmatUtil.getLienTelechargementRAM(loggedMember, "complet")%>'><trsb:glp key="ESPACE-RAM-TELECHARGEMENT-LIEN" ></trsb:glp></a><span class="infoFile"><%=AssmatUtil.getInfoFileExport(loggedMember, "complet") %></span>
<p><trsb:glp key="ESPACE-RAM-TELECHARGEMENT-NOTA" ></trsb:glp></p>
</div>

<!-- Mouvements -->
<div class="blocTelechargement" style="margin-top: 20px;">
<img src ="plugins/ToolsPlugin/images/bullet.jpg"/><a href="<%=AssmatUtil.getLienTelechargementRAM(loggedMember, "mouvements")%>"><trsb:glp key="ESPACE-RAM-TELECHARGEMENT-LIEN-MOUVEMENTS" ></trsb:glp></a><span class="infoFile"><%=AssmatUtil.getInfoFileExport(loggedMember, "nouveaux") %></span>
<p><trsb:glp key="ESPACE-RAM-TELECHARGEMENT-NOTA-MOUVEMENTS" ></trsb:glp></p>
</div>

<!-- Accueils -->
<div class="blocTelechargement" style="margin-top: 20px;">
<img src ="plugins/ToolsPlugin/images/bullet.jpg"/><a href="<%=AssmatUtil.getLienTelechargementRAM(loggedMember, "accueils")%>"><trsb:glp key="ESPACE-RAM-TELECHARGEMENT-LIEN-ACCUEILS" ></trsb:glp></a><span class="infoFile"><%=AssmatUtil.getInfoFileExport(loggedMember, "accueils") %></span>
<p><trsb:glp key="ESPACE-RAM-TELECHARGEMENT-NOTA-ACCUEILS" ></trsb:glp></p>
</div>

</div>
<%@ include file='/jcore/doFooter.jsp' %>
