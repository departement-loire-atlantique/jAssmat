<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="fr.cg44.plugin.tools.facetedsearch.exception.UnknowCityException"%>
<%@page import="org.apache.commons.collections.MapUtils"%>
<%@page import="fr.cg44.plugin.assmat.PointAssmat"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="fr.cg44.plugin.assmat.beans.AssmatSearch"%>
<%@page import="fr.cg44.plugin.assmat.selector.CommuneImportIdSelector"%>
<%@page import="fr.cg44.plugin.tools.facetedsearch.manager.CityManager"%>
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
%><%@ page import="fr.cg44.plugin.tools.googlemaps.comparator.ClosenessPointComparator" %>

<%
jcmsContext.addCSSHeader("https://api.tiles.mapbox.com/mapbox-gl-js/v0.45.0/mapbox-gl.css");
jcmsContext.addJavaScript("https://api.tiles.mapbox.com/mapbox-gl-js/v0.45.0/mapbox-gl.js");
%>

<script src='https://api.tiles.mapbox.com/mapbox-gl-js/v0.45.0/mapbox-gl.js'></script>
<link href='https://api.tiles.mapbox.com/mapbox-gl-js/v0.45.0/mapbox-gl.css' rel='stylesheet' />

    
<script>
mapboxgl.accessToken = 'pk.eyJ1Ijoic2d1ZWxsZWMiLCJhIjoiY2ppMGQ4cjc4MDAyMDN3czcyNG5iemJuOCJ9.B-V2m44J4ehkvnlpEdoijg';
var map = new mapboxgl.Map({
    container: 'map',
    style: 'mapbox://styles/mapbox/streets-v9',
    center: [-1.537021, 47.231835],
    zoom: 12
});

map.on('load', function() {
    // Add a new source from our GeoJSON data and set the
    // 'cluster' option to true. GL-JS will add the point_count property to your source data.
    map.addSource("assmat", {
        type: "geojson",
        data: "plugins/AssmatPlugin/jsp/recherche/map/features-assmat.geojson",
        cluster: true,
        clusterMaxZoom: 14, // Max zoom to cluster points on
        clusterRadius: 50 // Radius of each cluster when clustering points (defaults to 50)
    });
    
    map.addSource("relais", {
        type: "geojson",
        data: "plugins/AssmatPlugin/jsp/recherche/map/features-relais.geojson",
        cluster: true,
        clusterMaxZoom: 14, // Max zoom to cluster points on
        clusterRadius: 50 // Radius of each cluster when clustering points (defaults to 50)
    });    
    

    map.addLayer({
        id: "clusters",
        type: "circle",
        source: "assmat",
        filter: ["has", "point_count"],
        paint: {
            // Use step expressions (https://www.mapbox.com/mapbox-gl-js/style-spec/#expressions-step)
            // with three steps to implement three types of circles:
            //   * Blue, 20px circles when point count is less than 100
            //   * Yellow, 30px circles when point count is between 100 and 750
            //   * Pink, 40px circles when point count is greater than or equal to 750
            "circle-color": [
                "step",
                ["get", "point_count"],
                "#51bbd6",
                100,
                "#f1f075",
                750,
                "#f28cb1"
            ],
            "circle-radius": [
                "step",
                ["get", "point_count"],
                20,
                100,
                30,
                750,
                40
            ]
        }
    });

    map.addLayer({
        id: "cluster-count",
        type: "symbol",
        source: "assmat",
        filter: ["has", "point_count"],
        layout: {
            "text-field": "{point_count_abbreviated}",
            "text-font": ["DIN Offc Pro Medium", "Arial Unicode MS Bold"],
            "text-size": 12
        }
    });

    map.addLayer({
        id: "assmat-point",
        type: "circle",
        source: "assmat",
        filter: ["!has", "point_count"],
        paint: {
            "circle-color": "#AEC900",
            "circle-radius": 10,
            "circle-stroke-width": 1,
            "circle-stroke-color": "#fff"
        }
    });
    
    map.addLayer({
        id: "relais-point",
        type: "circle",
        source: "relais",
        filter: ["!has", "point_count"],
        paint: {
            "circle-color": "#44BEDF",
            "circle-radius": 10,
            "circle-stroke-width": 1,
            "circle-stroke-color": "#fff"
        }
    });    

    // When a click event occurs on a feature in the places layer, open a popup at the
    // location of the feature, with description HTML from its properties.
    map.on('click', 'assmat-point', function (e) {
        var coordinates = e.features[0].geometry.coordinates.slice();
        var nom = e.features[0].properties.nom;

        console.log(coordinates);
        console.log(nom);

        // Ensure that if the map is zoomed out such that multiple
        // copies of the feature are visible, the popup appears
        // over the copy being pointed to.
        while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
            coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
        }

        new mapboxgl.Popup()
            .setLngLat(coordinates)
            .setHTML(nom)
            .addTo(map);
    });

    // Change the cursor to a pointer when the mouse is over the places layer.
    map.on('mouseenter', 'assmat-point', function () {
        map.getCanvas().style.cursor = 'pointer';
    });

    // Change it back to a pointer when it leaves.
    map.on('mouseleave', 'assmat-point', function () {
        map.getCanvas().style.cursor = '';
    });


});
</script>











<%
if(!jcmsContext.isAjaxRequest()){
  %><%@ include file='/plugins/ToolsPlugin/types/PortletQueryForeachGoogleMaps/includeJsPqfGoogleMaps.jspf' %><%
}
%>

<%
PortletQueryForeachGoogleMaps box = new PortletQueryForeachGoogleMaps();
box.setCacheType("None");
box.setId( ""+System.currentTimeMillis());

//Récuperation des parametres
//Commune
String commune = getUntrustedStringParameter("cityName",null);
int codeInsee = getIntParameter("codeInsee",0);

CityManager cityManager = CityManager.INSTANCE;
City city = null;
String kml="";
String communeCode = null;
String cityId = "";
if(Util.notEmpty(codeInsee)){
    try {
        city =(City) cityManager.getCityByCode(String.valueOf(codeInsee), City.class);
        communeCode = city.getLibelleDeVoie();
        kml = city.getExtraData("extra.City.plugin.tools.geolocation.kml");
    } catch (UnknowCityException e){
           logger.warn("La commune <"+commune+"> <"+codeInsee+"> n'a pas été trouvé.");
       }
}
if(Util.isEmpty(kml)){
  kml = "http://cartes.loire-atlantique.fr/refonte/codecommunes.kml";
}

box.setKml(kml);
box.setCenterOnKmlElement(true);
box.setDisplayKmlElement(true);
box.setColorStrategy("category");
box.setLatitude("47.217250");
box.setLongitude("-1.553360");

String cacheType = box.getCacheType();
boolean disabledCache = "None".equalsIgnoreCase(cacheType) ? true : false;
long time = box.getInvalidTime();
%>

<% 

List<AssmatSearch> totalSetAssmatSearch = (ArrayList<AssmatSearch>) request.getAttribute("totalSet");
PointAssmat pointUser =(PointAssmat) request.getAttribute("userLocation");

Map< AssmatSearch, PointAssmat> nonTrieAssmatPoints = (Map<AssmatSearch, PointAssmat>) request.getAttribute("nonTrieAssmatPoints");


%><%
%><% if(Util.notEmpty(box)){ %><%
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
         boolean centerOnKmlElement = box.getCenterOnKmlElement();
         %><jalios:cache timeout='<%= time %>' id='<%= "googlemaps_js_pqfkml_"+box.getId()+"_"+box.getColorStrategy() %>' disabled='<%= true %>' classes='<%= new Class[] { ProfilASSMAT.class} %>'><%
         %><%-- Initialisation de la map --%><%
         %>var mapOptions = {<%
             %>center: new google.maps.LatLng(<%= box.getLatitude() %>, <%= box.getLongitude() %>),<%
             %>zoom: <%= Util.notEmpty(box.getMinZoom()) ? box.getMinZoom() : "8" %>,<%
             %>maxZoom: <%= Util.notEmpty(box.getMaxZoom()) ? box.getMaxZoom() : "19" %>,<%
             %>minZoom: <%= Util.notEmpty(box.getMinZoom()) ? box.getMinZoom() : "1" %>,<%
             %>mapTypeId: google.maps.MapTypeId.<%= Util.notEmpty(box.getMapType()) ? box.getMapType().toUpperCase() : "ROADMAP" %>,<%
             %>disableDefaultUI: false<%
         %>};<%
         %>map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
       
         

         
         
         <%-- kml �l�ments si pas de ville --%><%
        if(Util.notEmpty(city)){
         KmlElement kmlElement = kmlElementHandler.getKmlElementByName(city.getImportId());
         if(Util.notEmpty(kmlElement)){
         KmlStyle kmlStyle = kmlElement.getKmlStyle();
          
           Publication publication = Util.getFirst(JcmsUtil.applyDataSelector(channel.getAllDataSet(City.class), new CommuneImportIdSelector(kmlElement.getName())));
           
           
           if(publication != null){
             jcmsContext.setTemplateUsage("geolocationInfoWindowResult");
             request.setAttribute("publication", publication);
             if(publication.getTemplate("geolocationInfoWindow").equals("geolocationInfoWindow.default")){
               %>var boxText= document.createElement("div");
  
                 var coordinates = <%= kmlElement.getCoordinatesAsStringArrayJs() %>;
                    googleMapsGenerator.addKmlElement(coordinates, '#FF0000', '1', '1', '#FF0000', '0.1',<%
               %>true,<%
               %>null, '<%= publication.getId() %>');
                 googleMapsGenerator.centerOnKmlElement('<%= publication.getId() %>', map);
                 
                 
                 
                 
                 <% //Geolocalition de l'utilisateur 
                 if(Util.notEmpty(pointUser)){%>
                   var myLatLng = {lat: <%=pointUser.getLatitude() %>, lng: <%=pointUser.getLongitude() %>};
                   var marker = new google.maps.Marker({
                         position: myLatLng,
                         map: map  ,
                         icon:   'plugins/AssmatPlugin/img/target-with-circle.png'    
                  });
                   
                 <%}//Fin geolocalition de l'utilisateur  %>
                 
                 
                 <%
                List<FicheLieu> listRelais =(List<FicheLieu>) request.getAttribute("listPointRelais"); 
                 
                 if(Util.notEmpty(listRelais)){
                   for(FicheLieu itPlace : listRelais){ %>
                <!--  DEBUT BOUCLE -->
                        var latLngRelais = new google.maps.LatLng(<%= itPlace.getExtraData("extra.Place.plugin.tools.geolocation.latitude") %>, <%= itPlace.getExtraData("extra.Place.plugin.tools.geolocation.longitude") %>);
			               <%
			               String type = fr.cg44.plugin.tools.googlemaps.proxy.Proxy.getMarkerPublication(itPlace, box); %><%
			               %>var type = '<%= type %>';<%
			                String shadow = ToolsUtil.getProperty("plugin.tools.googlemaps.v3.marker.shape."+type+".shadow"); 
			                if(shadow != null){ %>
			                var shadow = <%=  shadow %>;
			                <%
			                } 
			                String marginTop = ToolsUtil.getProperty("plugin.tools.googlemaps.v3.marker.shape."+type+".margin.top"); %>
			                var marginTop = '<%=  marginTop != null ? marginTop : 0 %>';
			                <%
			               jcmsContext.setTemplateUsage("geolocationInfoWindow");
			               request.setAttribute("publication", itPlace);
			               try{
			                 MarkerShape markerShape = new MarkerShape(type);%>
			                 var shape = {coord: [<%= markerShape.getCoordinates() %>], type: '<%= markerShape.getType() %>'};<%
			               }catch(InvalidMarkerShapeException imse){
			                 %>var shape = null;<%
			               }
			              
			               String color = "#44bedf"; 
			               if(itPlace.getTemplate("geolocationInfoWindow").equals("geolocationInfoWindow.default")){
			                %>var boxText = document.createElement("div");
			                boxText.className = "infoWindow";
			                <jalios:buffer name="publicationTemplateRenderer">
			                   <jsp:include page='<%= "/"+itPlace.getTemplatePath(jcmsContext) %>' flush='true' />
			                   </jalios:buffer>
			                   boxText.innerHTML = '<%= ToolsUtil.escapeJavaStringChar(publicationTemplateRenderer) %>';<%
			               } else if(itPlace.getTemplate("geolocationInfoWindowList").equals("geolocationInfoWindowList.default")){ 
			
			                 %>var boxText = document.createElement("div");<%
			                 %>boxText.className = "infoWindow";<%
			                 %><jalios:buffer name="publicationListTemplateRenderer"><%
			                    %><jsp:include page='<%= "/plugins/ToolsPlugin/jsp/googlemaps/queryDisplay.jsp" %>' flush='true' /><%
			                 %></jalios:buffer><%
			                 %>boxText.innerHTML = '<%= ToolsUtil.escapeJavaStringChar(publicationListTemplateRenderer) %>';<%
			               } else{
			                %>var boxText = null;<%
			               }
			              
			                String colorStrategy = box.getColorStrategy();
			                %>var color = '<%= color.substring(1, color.length()).toLowerCase() %>';<%
			                
			                %><%-- Initialisation des clusters de marqueurs --%><%
			                //La génération des clusters ne peut commencer qu'apr�s r�cup�ration d'une couleur.
			                //Si les publications apparaissent dans des couleurs diff�rentes,
			                //c'est la premi�re couleur qui sera retenue.
					                if(markerCluster){
					                  color="#ffffff";
					                  %>var markerCluster = googleMapsGenerator.generateCluster(map, '<%= color.substring(1, color.length()).toLowerCase() %>', <%= box.getGridsize() > 0 ? box.getGridsize() : "30" %>);<%
					                  markerCluster = false;
					                }
					                //Si un �l�ment est affich� en tant que p�rim�tre, il ne doit pas se pr�senter
					                //sous forme de marqueur.
					                if(!box.getDisplayKmlElement() || kmlElementHandler.getKmlElementByName(publication.getId()) == null){
					                  %>
					                  googleMapsGenerator.addMarker(latLngRelais, shape, shadow, type, color, boxText, bounds, map, markerCluster, marginTop);
					                  <%
					                }
			               
			               
			                } 
               }}
                 
                 %>
                 
                 
                <!-- Generation de la map -->
                 googleMapsGenerator.generateMap(map);
                 
                 <%
             }
           }
           }
         
         
         
         %></jalios:cache><%
         
         %><%-- P�rim�tre --%><%
         %>var bounds = <%= centerOnKmlElement ? "null" : "new google.maps.LatLngBounds()" %>;<%
         
         %><%-- �l�ments --%><%
        
                  
         //Map<Point, Publication> publicationPoints = Geolocation.convertPublicationsToMapPoints(publications);
         
        //Assmat avec dispos
         
         ProfilManager profilMgr = ProfilManager.getInstance();
         
         Map<Point,List<ProfilASSMAT>> publicationPoints = new HashMap<Point,List<ProfilASSMAT>>();

         
     
         
         List<ProfilASSMAT> collection = new ArrayList<ProfilASSMAT>();
       //Les assmat dispo
       Map< AssmatSearch, PointAssmat> assmatPoints = (Map<AssmatSearch, PointAssmat>) request.getAttribute("assmatPoints");
       
       for (Map.Entry<AssmatSearch, PointAssmat> entry : assmatPoints.entrySet()){
		  AssmatSearch assmat = entry.getKey();
		  PointAssmat point = entry.getValue();
	
		  if(point.getLatitude() == 0 && point.getLongitude() == 0) {
		    continue;
		  }
		  
		  
		  ProfilASSMAT profilAM = profilMgr.getProfilASSMATbyAssmatSearch(assmat);
	      collection.add(profilAM);
	      if(Util.notEmpty(point)){
			boolean isDomicile = assmat.getIsDomicile();        
			String color = point.getCouleurPoint();
			String infoPoint = point.getInfoPoint();
			
			List<ProfilASSMAT> liste = new ArrayList<ProfilASSMAT>();
			if(publicationPoints.containsKey(point)){
				liste = publicationPoints.get(point);
			}
			
			liste.add(profilAM);
			
			publicationPoints.put(point, liste);    
		  }
      }     
         List<ProfilASSMAT> publications = new ArrayList<ProfilASSMAT>(collection);
         List<ProfilASSMAT> kmlPublications = new ArrayList<ProfilASSMAT>(collection);
        // Collections.sort(publications, ComparatorManager.getComparator(Publication.class, "comparator.Publication.relevance"));
         
         List<Point> points = new ArrayList(publicationPoints.keySet());
         List<Point> generalClosenessPoints = new ArrayList<Point>();
         Iterator<Point> itPoints = points.iterator();
        // Collections.sort(points, new ClosenessPointComparator());
         ListIterator<Point> itPoint = points.listIterator();
         
         ProfilASSMAT publication;
         Point point;
         
         Iterator<ProfilASSMAT> itPublication = publications.iterator();
         
         itPublication = publications.iterator();
         while(itPoint.hasNext()){
           point = itPoint.next();
           
           if(!generalClosenessPoints.contains(point)){
        	   if(Util.notEmpty(publicationPoints.get(point))){
        		   
        	   
        	   publication = publicationPoints.get(point).get(0);
        	   %>var latLng = new google.maps.LatLng(<%= point.getLatitude() %>, <%= point.getLongitude() %>);<%
             ListIterator<Point> itClosenessPoints = points.listIterator(itPoint.nextIndex());
             List<Point> closenessPoints = Geolocation.getClosenessPoints((Point)point, itClosenessPoints);
             boolean noClosenessPoint = Util.isEmpty(closenessPoints);

             if(!noClosenessPoint){
               generalClosenessPoints.addAll(closenessPoints);
             }
             
             try{
               String type = fr.cg44.plugin.tools.googlemaps.proxy.Proxy.getMarkerPublication(publication, box); %><%
               %>var type = '<%= type %>';<%
               %><% String shadow = ToolsUtil.getProperty("plugin.tools.googlemaps.v3.marker.shape."+type+".shadow"); %><%
               %><% if(shadow != null){ %><%
                %>var shadow = <%=  shadow %>;<%
               %><% } %><%
               %><% String marginTop = ToolsUtil.getProperty("plugin.tools.googlemaps.v3.marker.shape."+type+".margin.top"); %><%
               %>var marginTop = '<%=  marginTop != null ? marginTop : 0 %>';<%
               jcmsContext.setTemplateUsage("geolocationInfoWindow");
               request.setAttribute("publication", publication);
               try{
                 MarkerShape markerShape = new MarkerShape(type);
                 %>var shape = {coord: [<%= markerShape.getCoordinates() %>], type: '<%= markerShape.getType() %>'};<%
               }catch(InvalidMarkerShapeException imse){
                 %>var shape = null;<%
               }
               PointAssmat pointAM = (PointAssmat) point;
               String color = pointAM.getCouleurPoint(); 
               
               if(noClosenessPoint && publication.getTemplate("geolocationInfoWindow").equals("geolocationInfoWindow.default")){
                %>var boxText = document.createElement("div");<%
                %>boxText.className = "infoWindow";<%
                // Génération du contenu de l'infoBox
                %><jalios:buffer name="publicationTemplateRenderer"><%
                   %>
                   <%request.setAttribute("pointAssmat", point); %>
                   <jsp:include page='<%= "/"+publication.getTemplatePath(jcmsContext) %>' flush='true' /><%
                %></jalios:buffer><%
                %>boxText.innerHTML = '<%= ToolsUtil.escapeJavaStringChar(publicationTemplateRenderer) %>';<%
               } else if(publication.getTemplate("geolocationInfoWindow").equals("geolocationInfoWindow.default")){ 
                   closenessPoints.add(point);
                   List<Publication> closenessPublications = new ArrayList<Publication>();
                   for(Point pt: closenessPoints){
                     closenessPublications.addAll(publicationPoints.get(pt));
                   }
                   request.setAttribute("publications", closenessPublications);
                   request.setAttribute("pointAssmat",point);
                
                 %>var boxText = document.createElement("div");<%
                 %>boxText.className = "infoWindow";<%
                 %><jalios:buffer name="publicationListTemplateRenderer"><%
                    %><jsp:include page='<%= "/plugins/AssmatPlugin/jsp/recherche/map/queryDisplay.jsp" %>' flush='true' /><%
                 %></jalios:buffer><%
                 %>boxText.innerHTML = '<%= ToolsUtil.escapeJavaStringChar(publicationListTemplateRenderer) %>';<%
               } else{
                %>var boxText = null;<%
               }
              
                String colorStrategy = box.getColorStrategy();
                %>var color = '<%= color.substring(1, color.length()).toLowerCase() %>';<%
                
                %><%-- Initialisation des clusters de marqueurs --%><%
                //La génération des clusters ne peut commencer qu'apr�s r�cup�ration d'une couleur.
                //Si les publications apparaissent dans des couleurs diff�rentes,
                //c'est la premi�re couleur qui sera retenue.
                if(markerCluster){
                  color="#aec900";
                  %>var markerCluster = googleMapsGenerator.generateCluster(map, '<%= color.substring(1, color.length()).toLowerCase() %>', <%= box.getGridsize() > 0 ? box.getGridsize() : "30" %>);<%
                  markerCluster = false;
                }
                //Si un �l�ment est affich� en tant que p�rim�tre, il ne doit pas se pr�senter
                //sous forme de marqueur.
                if(!box.getDisplayKmlElement() || kmlElementHandler.getKmlElementByName(publication.getId()) == null){
                  %>googleMapsGenerator.addMarker(latLng, shape, shadow, type, color, boxText, bounds, map, markerCluster, marginTop);<%
                }
               
               
             } catch(UnknowPublicationMarkerException upme){
              %><%-- Le marqueur n'est pas trait� --%><%
              StringBuffer log = new StringBuffer();
              log.append(channel.getUrl())
                 .append(jcmsContext.getCurrentCategory().getDisplayUrl(userLocale))
                 .append("Publication marker for Google Maps doesn't exist (id : ")
                 .append(publication.getId())
                 .append(") -> not added marker.");
              logger.warn(log.toString());
             }
           }
           }
          }
         
     
         
         Iterator<ProfilASSMAT> itKmlPublication = kmlPublications.iterator();
         %><jalios:buffer name="putForward"><%
         while(itKmlPublication.hasNext()){
           publication = itKmlPublication.next();
           if(centerOnKmlElement){
             %>googleMapsGenerator.centerOnKmlElement('<%= publication.getId() %>', map);<%
           } else {
             %>googleMapsGenerator.putForwardKmlElement('<%= publication.getId() %>');<%
             %>googleMapsGenerator.centerMap(map, bounds);<%
           }
         }
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
