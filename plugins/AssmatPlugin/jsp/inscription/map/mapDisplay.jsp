<%@ include file='/jcore/doInitPage.jsp'%>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.util.ArrayList"%>
<%
%><%@ page import="java.util.List" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.MarkerShape" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.proxy.Proxy" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.Geolocation" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.geometry.Point" %><%
%><%@ page import="fr.cg44.plugin.tools.ToolsUtil" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.exception.InvalidMarkerShapeException" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.exception.UnknowCurrentColorException" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.exception.UnknowPublicationMarkerException" %><%
String mapJspType = (String) request.getAttribute("gMapsType");
String width = ToolsUtil.getProperty("plugin.tools.googlemaps.v3.typemap."+mapJspType+".width");
String height = ToolsUtil.getProperty("plugin.tools.googlemaps.v3.typemap."+mapJspType+".height");
String mapType = ToolsUtil.getProperty("plugin.tools.googlemaps.v3.typemap."+mapJspType+".type");
String mapZoom = ToolsUtil.getProperty("plugin.tools.googlemaps.v3.typemap."+mapJspType+".zoom");
String initMethodName = (String) request.getAttribute("initMethodName");
String deferInit = (String) request.getAttribute("deferInit");

BigDecimal BGlatitude = (BigDecimal) request.getAttribute("latitude");
BigDecimal BGlongitude = (BigDecimal) request.getAttribute("longitude");

if(width != null && height != null && mapType != null && mapZoom != null && BGlatitude != null && BGlongitude != null){

	float latitude = BGlatitude.floatValue();
	float longitude = BGlongitude.floatValue();
	
    //Inclusion des apis Google
    jcmsContext.addJavaScript("https://maps.googleapis.com/maps/api/js?key="+channel.getProperty("plugin.tools.googlemaps.v3.apikey")); %><%
    jcmsContext.addJavaScript("plugins/ToolsPlugin/js/googlemaps/googleMapsGenerator.js");

  String colorStrategy = null;
  

String id = request.getParameter("idMap")+System.currentTimeMillis();
if(Util.isEmpty(initMethodName))
    initMethodName="initialize_" + id;

%><%
  %><div id="map-canvas-<%= id %>" class="gmaps" style="<%= "height:"+height+";width:"+width+";" %>"></div><%
  %><jalios:buffer name="publicationMap"><%
   %>function <%= initMethodName %>() {<%
   ServletUtil.backupAttribute(jcmsContext.getPageContext(), "templateUsage");
   ServletUtil.backupAttribute(jcmsContext.getPageContext(), "publication");
   %>var mapId = "<%= id %>";<%
   %><jalios:cache timeout='1440' id='<%= "googlemaps_js_integratedmap_"+latitude+"_"+longitude %>' ><%
   List<Point> points = new ArrayList<Point>();
   points.add(new Point(latitude,longitude,0));
   Point firstPoint = points.iterator().next();
   %>var bounds = new google.maps.LatLngBounds(new google.maps.LatLng(<%= firstPoint.getLatitude() %>, <%= firstPoint.getLongitude() %>), new google.maps.LatLng(<%= firstPoint.getLatitude() %>, <%= firstPoint.getLongitude() %>));<%
   %><%-- Initialisation de la map --%><%
   %>var mapOptions = {<%
       %>center: new google.maps.LatLng(<%= firstPoint.getLatitude() %>, <%= firstPoint.getLongitude() %>),<%
       %>zoom: <%= mapZoom %>,<%
       %>mapTypeId: google.maps.MapTypeId.<%= mapType.toUpperCase() %>,<%
       %>disableDefaultUI: false, mapTypeControl:false<%
     %>};<%
     %>map = new google.maps.Map(document.getElementById("map-canvas-"+mapId), mapOptions);<%
     
     %><%-- Markers --%><%
     for(Point point: points){
      %>var latLng = new google.maps.LatLng(<%= point.getLatitude() %>, <%= point.getLongitude() %>);<%
         
           String type = "drop";
           %>var type = '<%= type %>';<%
           String shadow = ToolsUtil.getProperty("plugin.tools.googlemaps.v3.marker.shape."+type+".shadow");
           if(shadow != null){ %><%
            %>var shadow = <%=  shadow %>;<%
           }
           %><% String marginTop = ToolsUtil.getProperty("plugin.tools.googlemaps.v3.marker.shape."+type+".margin.top"); %><%
         %>var marginTop = '<%=  marginTop != null ? marginTop : 0 %>';<%
           jcmsContext.setTemplateUsage("geolocationInfoWindowIntegrated");
   
           try{
             MarkerShape markerShape = new MarkerShape(type);
             %>var shape = {coord: [<%= markerShape.getCoordinates() %>], type: '<%= markerShape.getType() %>'};<%
           }catch(InvalidMarkerShapeException imse){
             %>var shape = null;<%
           }
  
             %>var boxText = null;<%
           
             
               String color;
               if(Util.notEmpty(colorStrategy)){
                 color = Proxy.getMainColor();
               } else {
            	   color = Proxy.getMainColor();
               }
              %>var color = '<%= color.substring(1, color.length()).toLowerCase() %>';<%
              %>var myMarker = googleMapsGenerator.addDraggableMarker(latLng, shape, shadow, type, color, boxText, bounds, map, null, marginTop);
              
              
              
              google.maps.event.addListener(marker, 'dragend', function (event) {
              <%if("mam".equalsIgnoreCase(request.getParameter("idMap"))){ %>
					    document.getElementById("latMAMTampon").value = this.getPosition().lat();
					    document.getElementById("longMAMTampon").value = this.getPosition().lng();
					     jQuery( ".submitNewPositionMAM" ).show();
					    <%}else{ %>
					    document.getElementById("latAMTampon").value = this.getPosition().lat();
              document.getElementById("longAMTampon").value = this.getPosition().lng();
					     jQuery( ".submitNewPositionAM" ).show();
					    <%} %>
					    
              });
              
              
              
              <%
           
    
     }
     
     if(points.size() > 1){
      %>googleMapsGenerator.centerMap(map, bounds);<%
     }
    
     /** If "defer init" is used, we have to recenter the map 
         in case of div size properties changed */
     if (Util.notEmpty(request.getAttribute("deferInit"))) {
        %>/**googleMapsGenerator.centerMap(map, bounds);*/<%
          %>google.maps.event.trigger(map, "resize");<%
     }
   
     %></jalios:cache><%
   %>};<%
   if (Util.isEmpty(request.getAttribute("deferInit"))) {
     %><%= initMethodName %>();<%
   }
   ServletUtil.restoreAttribute(jcmsContext.getPageContext(), "publication");
   ServletUtil.restoreAttribute(jcmsContext.getPageContext(), "templateUsage");
   %></jalios:buffer><%
    //Ex�cution du code en bas de page 
    %><jalios:javascript><%
    %><%= publicationMap %><%
   %></jalios:javascript><%
  
 %><%
%><% } else { %><%
  logger.error("Type ("+mapJspType+") for integrated google map isn't defined.");
%><% } %><%
%>