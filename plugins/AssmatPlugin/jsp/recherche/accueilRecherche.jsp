<%@page import="java.util.ArrayList"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>

<div class="presentationSearch">
<div class="span7 introRecherche">

<%

jcmsContext.addJavaScript("https://api.tiles.mapbox.com/mapbox-gl-js/v0.49.0/mapbox-gl.js"); 

String idIntro = channel.getProperty("$jcmsplugin.assmatplugin.portlet.intro.id");
String idRelais = channel.getProperty("$jcmsplugin.assmatplugin.portlet.relais.id");
%>

<jalios:include  id='<%=idIntro %>' />


</div>
<div class="span5">
<jalios:include id='<%=idRelais %>' />

</div>



</div>
<div class="sousIntroduction">
    <div class="label"><p class="intro"><trsb:glp key="AM-INTRO-SEARCH" ></trsb:glp></p></div>
  </div>
  
<link href='https://api.tiles.mapbox.com/mapbox-gl-js/v0.49.0/mapbox-gl.css' rel='stylesheet' />
  
<div class="carto">
   <% request.setAttribute("gMapsType", "large"); %>
                    
   <div id="map-canvas" class="gmaps" style="height:600px;"></div>
   
   <jalios:javascript><% request.setAttribute("collection", new ArrayList()); %>
  <jsp:include page="/plugins/AssmatPlugin/jsp/recherche/map/accueilGlobalMapAssmatDisplay.jsp"  flush="true"/>
  </jalios:javascript>
                  <% request.removeAttribute("gMapsType"); %>          
</div>
<%
String idBandeauSousCarto = channel.getProperty("$jcmsplugin.assmatplugin.portlet.publicite-bottom-carto.id");
%>
<div class="publicite">
<jalios:include id='<%=idBandeauSousCarto %>' />
</div>