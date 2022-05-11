<%@page import="fr.cg44.plugin.assmat.selector.CommuneImportIdSelector"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="generated.ProfilASSMAT"%>
<%@page import="fr.cg44.plugin.tools.facetedsearch.FacetedSearchUtil"%><%
%><%@ include file='/jcore/doInitPage.jsp' %><%
%><%@ include file='/jcore/portal/doPortletParams.jsp' %><%
%><%@ page import="java.util.List" %><%
%><%@ page import="java.util.ArrayList" %><%
%><%@ page import="java.util.Collections" %><%
%><%@ page import="com.jalios.util.Util" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.MarkerShape" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.Geolocation" %><%
%><%@ page import="fr.cg44.plugin.tools.ToolsUtil" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.exception.UnknowCurrentColorException" %><%
%><%@ page import="javax.xml.parsers.SAXParserFactory" %><%
%><%@ page import="javax.xml.parsers.SAXParser" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.geometry.Point" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.kml.KmlElement" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.kml.KmlStyle" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.kml.KmlElementHandler" %><%
%><%@ page import="fr.cg44.plugin.tools.ProxyUtil"%><%
%><%@ page import="org.xml.sax.XMLReader" %><%
%><%@ page import="org.xml.sax.InputSource" %><%
%><%@ page import="java.net.URL" %><%
%><%@ page import="java.net.ConnectException" %><%
%><%@ page import="java.net.URLConnection" %><%
%><%@ page import="java.net.Proxy" %><%
%><%@ page import="java.io.InputStream" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.exception.InvalidMarkerShapeException" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.exception.UnknowPublicationMarkerException" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.comparator.ClosenessPointComparator" %><%
if(!jcmsContext.isAjaxRequest()){
  %><%@ include file='/plugins/ToolsPlugin/types/PortletQueryForeachGoogleMaps/includeJsPqfGoogleMaps.jspf' %><%
}
%>

<%
PortletQueryForeachGoogleMaps box = new PortletQueryForeachGoogleMaps();
box.setCacheType("None");
box.setId( ""+System.currentTimeMillis());
box.setLatitude("42.2554");
box.setLongitude("1.25545");
box.setKml("http://cartes.loire-atlantique.fr/refonte/codecommunes.kml");
box.setCenterOnKmlElement(true);
box.setDisplayKmlElement(true);
box.setColorStrategy("category");

String cacheType = box.getCacheType();
boolean disabledCache = "None".equalsIgnoreCase(cacheType) ? true : false;
long time = box.getInvalidTime();
%>

<jalios:cache id='<%= "carte_assmat_" + box.getId() %>' timeout="<%= time %>" disabled="<%= false %>" classes='<%= new Class[] {ProfilASSMAT.class} %>'>

<%
%><%
%><% if(Util.notEmpty(box) && Util.notEmpty(box.getLatitude()) && Util.notEmpty(box.getLongitude())){ %><%
  if(box.getKml() != null){
    try{
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser parser = factory.newSAXParser();
      XMLReader xmlReader = parser.getXMLReader();
          
      // Pr�paration du fichier kml - connexion, parsing du fichier et pr�pare les �l�ments kml dans une liste (kmlElementHandler)
      URL kmlUrl = null;
      URLConnection urlConnection = null;
      KmlElementHandler kmlElementHandler = new KmlElementHandler();    
      if(Util.notEmpty(box.getKml())){
        kmlUrl = new URL(box.getKml()); 
        // la variable kmlUrl est forc�ment une url valide
        urlConnection = kmlUrl.openConnection();
      }
     try{
       InputStream httpStream = urlConnection.getInputStream();
       xmlReader.setContentHandler(kmlElementHandler);
       xmlReader.parse(new InputSource(httpStream));
       String initMethodName = "initialize"+box.getId()+System.currentTimeMillis();
       %>function <%= initMethodName %>() {<%
         ServletUtil.backupAttribute(jcmsContext.getPageContext(), "templateUsage");
         ServletUtil.backupAttribute(jcmsContext.getPageContext(), "publication");
         boolean markerCluster = true;
         boolean centerOnKmlElement = box.getCenterOnKmlElement() && !FacetedSearchUtil.partialFacetActive(request, "facet_delegations");
         %><jalios:cache timeout='<%= time %>' id='<%= "googlemaps_js_pqfkml_"+box.getId()+"_"+box.getColorStrategy() %>' disabled='<%= true %>' classes='<%= new Class[] { ProfilASSMAT.class} %>'><%
         %><%-- Initialisation de la map --%><%
         %>var mapOptions = {<%
             %>center: new google.maps.LatLng(<%= box.getLatitude() %>, <%= box.getLongitude() %>),<%
             %>zoom: <%= Util.notEmpty(box.getMinZoom()) ? box.getMinZoom() : "9" %>,<%
             %>maxZoom: <%= Util.notEmpty(box.getMaxZoom()) ? box.getMaxZoom() : "19" %>,<%
             %>minZoom: <%= Util.notEmpty(box.getMinZoom()) ? box.getMinZoom() : "1" %>,<%
             %>mapTypeId: google.maps.MapTypeId.<%= Util.notEmpty(box.getMapType()) ? box.getMapType().toUpperCase() : "ROADMAP" %>,<%
             %>disableDefaultUI: false<%
         %>};<%
         %>map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);<%
           
         %><%-- Initialisation de la map --%><%
         %>googleMapsGenerator.generateMap(map);<%
           
         %><%-- Kml --%><%
         %>var kmlLayer = new google.maps.KmlLayer({<%
           %>url: 'http://cartes.loire-atlantique.fr/refonte/codecommunes.kml',<%
           %>preserveViewport: <%= "true" %><%
         %>});<%
         %>kmlLayer.setMap(map);
         
         google.maps.event.addListener(kmlLayer, 'defaultviewport_changed', function() {
					   var bounds = kmlLayer.getDefaultViewport();
					   map.setCenter(bounds.getCenter());
					});
         
         <%
         
         %><%-- kml �l�ments --%><%
         int kmlElementNumber = 1;
         for(KmlElement kmlElement : kmlElementHandler.getKmlElements()){
           KmlStyle kmlStyle = kmlElement.getKmlStyle();
           
         
          
           Publication publication = Util.getFirst(JcmsUtil.applyDataSelector(channel.getAllDataSet(City.class), new CommuneImportIdSelector(kmlElement.getName())));
           if(publication != null){
             jcmsContext.setTemplateUsage("geolocationInfoWindowAssmat");
             request.setAttribute("publication", publication);
             if(publication.getTemplate("geolocationInfoWindowAssmat").equals("geolocationInfoWindowAssmat.assmat")){
               %>var boxText<%= kmlElementNumber %> = document.createElement("div");<%
               %><jalios:buffer name="publicationTemplateRenderer"><%
                %>
               <jsp:include page='<%= "/"+publication.getTemplatePath(jcmsContext) %>' flush='true' />
                
                
                <%
               %></jalios:buffer><%
               %>boxText<%= kmlElementNumber %>.innerHTML = '<%= ToolsUtil.escapeJavaStringChar(publicationTemplateRenderer) %>';<%
               %>googleMapsGenerator.addKmlElement(<%= kmlElement.getCoordinatesAsStringArrayJs() %>,<%
                 %>'#<%= kmlStyle != null ? kmlStyle.getLineColor() : "#000000" %>',<%
                 %>0,<%
                 %>'<%= kmlStyle != null ? kmlStyle.getLineWidth() : "1" %>',<%
                 %>'#<%= kmlStyle != null ? kmlStyle.getPolyColor() : "#AEC900" %>',<%
                 %>0,<%
                 %>true,<%
                 %><%= box.getDisplayKmlElement() ? "boxText"+kmlElementNumber : "null" %>,<%
                 %>'<%= publication.getId() %>');<%
             }
             kmlElementNumber++;
           }
         }
         
         %></jalios:cache><%
         
         %><%-- P�rim�tre --%><%
         %>var bounds = <%= centerOnKmlElement ? "null" : "new google.maps.LatLngBounds()" %>;<%
         
         %><%-- �l�ments --%><%
         
        
         
         //Map<Point, Publication> publicationPoints = Geolocation.convertPublicationsToMapPoints(publications);
         
       
         
      
         
         %><jalios:buffer name="putForward"><%
         %></jalios:buffer><%
         %><%= putForward %><%
       %>};<%
       %><%= initMethodName %>();<%
       ServletUtil.restoreAttribute(jcmsContext.getPageContext(), "publication");
       ServletUtil.restoreAttribute(jcmsContext.getPageContext(), "templateUsage");
     } catch(ConnectException ce){
      logger.error("Wrong kml url: "+box.getKml()+".");
     }
    } catch(MalformedURLException mue){
     logger.error("Kml url for Google Maps is malformed.", mue);
    }
  }
} %>

</jalios:cache>