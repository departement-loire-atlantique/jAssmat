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
%><%@ page import="fr.cg44.plugin.tools.googlemaps.comparator.ClosenessPointComparator" %><%
%><%@ page import="org.json.simple.JSONObject" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.GeoJsonUtil" %><%
%><%@ page import="org.json.simple.JSONArray" %>

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
String cityIdImport = "";
if(Util.notEmpty(codeInsee)){
    try {
        city =(City) cityManager.getCityByCode(String.valueOf(codeInsee), City.class);
        if(city.isImported()){
        	cityIdImport=city.getImportId();  
        }
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

JSONObject jRelais = new JSONObject();
JSONArray jFeaturesRelais = new JSONArray();

JSONObject jAssmats = new JSONObject();
JSONArray jFeaturesAssmats = new JSONArray();

JSONObject jCommune = GeoJsonUtil.getCoordonneesCommune(cityIdImport);

String boxText = "";

%><%
%><% if(Util.notEmpty(box)){ %><%
  if(box.getKml() != null){
    try{
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser parser = factory.newSAXParser();
      XMLReader xmlReader = parser.getXMLReader();
          
      // Préparation du fichier kml - connexion, parsing du fichier et prépare les éléments kml dans une liste (kmlElementHandler)
      URL kmlUrl = null;
      URLConnection urlConnection = null;
      KmlElementHandler kmlElementHandler = new KmlElementHandler();    
      if(Util.notEmpty(box.getKml())){
        kmlUrl = new URL(box.getKml()); 
        // la variable kmlUrl est forcément une url valide
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
         %><jalios:cache timeout='<%= time %>' id='<%= "googlemaps_js_pqfkml_"+box.getId()+"_"+box.getColorStrategy() %>' disabled='<%= true %>' classes='<%= new Class[] { ProfilASSMAT.class} %>'>
         
        <%
        // kml éléments si pas de ville
        if(Util.notEmpty(city)){
         KmlElement kmlElement = kmlElementHandler.getKmlElementByName(city.getImportId());
         if(Util.notEmpty(kmlElement)){
         KmlStyle kmlStyle = kmlElement.getKmlStyle();
          
           Publication publication = Util.getFirst(JcmsUtil.applyDataSelector(channel.getAllDataSet(City.class), new CommuneImportIdSelector(kmlElement.getName())));
           
           
           if(publication != null){
             jcmsContext.setTemplateUsage("geolocationInfoWindowResult");
             request.setAttribute("publication", publication);
             if(publication.getTemplate("geolocationInfoWindow").equals("geolocationInfoWindow.default")){
                List<FicheLieu> listRelais =(List<FicheLieu>) request.getAttribute("listPointRelais"); 

                 if(Util.notEmpty(listRelais)){
                   for(FicheLieu itPlace : listRelais){ %>
                <!--  DEBUT BOUCLE -->
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
			               String jsonNom = "";
			               if(itPlace.getTemplate("geolocationInfoWindow").equals("geolocationInfoWindow.default")){
			                %>var boxText = document.createElement("div");
			                boxText.className = "infoWindow";
			                <jalios:buffer name="publicationTemplateRenderer">
			                   <jsp:include page='<%= "/"+itPlace.getTemplatePath(jcmsContext) %>' flush='true' />
			                   </jalios:buffer>
			                   <%
			                   boxText = publicationTemplateRenderer.toString();
			                   %>
			                   boxText.innerHTML = '<%= ToolsUtil.escapeJavaStringChar(publicationTemplateRenderer) %>';<%
			                   jsonNom = ToolsUtil.escapeJavaStringChar(publicationTemplateRenderer);
			               } else if(itPlace.getTemplate("geolocationInfoWindowList").equals("geolocationInfoWindowList.default")){ 
			
			                 %>var boxText = document.createElement("div");<%
			                 %>boxText.className = "infoWindow";<%
			                 %><jalios:buffer name="publicationListTemplateRenderer"><%
			                    %><jsp:include page='<%= "/plugins/ToolsPlugin/jsp/googlemaps/queryDisplay.jsp" %>' flush='true' /><%
			                 %></jalios:buffer>
			                 <%
			                 boxText = publicationListTemplateRenderer.toString();
			                 %>
			                 <%
			                 %>boxText.innerHTML = '<%= ToolsUtil.escapeJavaStringChar(publicationListTemplateRenderer) %>';<%
			                 jsonNom = ToolsUtil.escapeJavaStringChar(publicationListTemplateRenderer);
			               } else{
			                %>var boxText = null;<%
			               }
			              
			                String colorStrategy = box.getColorStrategy().toLowerCase();
			                %>var color = '<%= color.substring(1, color.length()).toLowerCase() %>';<%
			                
			                %><%-- Initialisation des clusters de marqueurs --%><%
			                //La génération des clusters ne peut commencer qu'après récupération d'une couleur.
			                //Si les publications apparaissent dans des couleurs différentes,
			                //c'est la premiére couleur qui sera retenue.
					                if(markerCluster){
					                  color="#ffffff";
					                  markerCluster = false;
					                }
					                //Si un élément est affiché en tant que périmétre, il ne doit pas se présenter
					                //sous forme de marqueur.
					                if(!box.getDisplayKmlElement() || kmlElementHandler.getKmlElementByName(publication.getId()) == null){

										JSONObject jMarker = new JSONObject();
										JSONObject jGeometry = new JSONObject();
										JSONObject jProperties = new JSONObject();
										jProperties.put("nom", boxText);
										jProperties.put("couleur", color);
										jProperties.put("marqueur", type+color);
										jMarker.put("type", "Feature");
										jMarker.put("properties", jProperties);
										JSONArray jCoordinates = new JSONArray();
					                	double jLongitude = Double.parseDouble(itPlace.getExtraData("extra.Place.plugin.tools.geolocation.longitude"));
					                	double jLatitude = Double.parseDouble(itPlace.getExtraData("extra.Place.plugin.tools.geolocation.latitude"));
										jCoordinates.add(jLongitude);
										jCoordinates.add(jLatitude);
										jGeometry.put("coordinates", jCoordinates);
										jGeometry.put("type", "Point");
										jMarker.put("geometry", jGeometry);
										jFeaturesRelais.add(jMarker);
					                	
					                }
			               
			               
			                } 
				jRelais.put("features", jFeaturesRelais);
               }}
                 
             }
           }
           }
         
         
         
         %></jalios:cache><%
         
         %><%-- Périmètre--%><%
         
         
         %><%-- Eléments --%><%
                  
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
         double jLongitude;
         double jLatitude;
         while(itPoint.hasNext()){
           point = itPoint.next();
           
           if(!generalClosenessPoints.contains(point)){
        	   if(Util.notEmpty(publicationPoints.get(point))){

        	   publication = publicationPoints.get(point).get(0);
        	   
        	   jLongitude = point.getLongitude();
        	   jLatitude = point.getLatitude();
        	   
        	   
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
                %><jalios:buffer name="publicationTemplateRenderer"><%
                   %>
                   <%request.setAttribute("pointAssmat", point); %>
                   <%
                   // Template de l'infoBox
                   //logger.warn(publication.getTemplatePath(jcmsContext)); %>
                   <jsp:include page='<%= "/"+publication.getTemplatePath(jcmsContext) %>' flush='true' /><%
                %></jalios:buffer><%
					boxText = publicationTemplateRenderer.toString();
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
					boxText = publicationListTemplateRenderer.toString();
                 %>boxText.innerHTML = '<%= ToolsUtil.escapeJavaStringChar(publicationListTemplateRenderer) %>';<%
               } else{
                %>var boxText = null;<%
               }
              
                String colorStrategy = box.getColorStrategy().toLowerCase();
                %>var color = '<%= color.substring(1, color.length()).toLowerCase() %>';<%
                
                %><%-- Initialisation des clusters de marqueurs --%><%
                //La génération des clusters ne peut commencer qu'après récupération d'une couleur.
                //Si les publications apparaissent dans des couleurs différentes,
                //c'est la premiére couleur qui sera retenue.
                if(markerCluster){
                  color="#aec900";
                  markerCluster = false;
                }
                //Si un élément est affiché en tant que périmétre, il ne doit pas se présenter
                //sous forme de marqueur.
                if(!box.getDisplayKmlElement() || kmlElementHandler.getKmlElementByName(publication.getId()) == null){

					JSONObject jMarker = new JSONObject();
					JSONObject jGeometry = new JSONObject();
					JSONObject jProperties = new JSONObject();
					jProperties.put("nom", boxText);
					jProperties.put("couleur", color);
					jProperties.put("marqueur", type+color);
					jMarker.put("type", "Feature");
					jMarker.put("properties", jProperties);
					JSONArray jCoordinates = new JSONArray();
					jCoordinates.add(jLongitude);
					jCoordinates.add(jLatitude);
					jGeometry.put("coordinates", jCoordinates);
					jGeometry.put("type", "Point");
					jMarker.put("geometry", jGeometry);
					jFeaturesAssmats.add(jMarker);
                }
               
               
             } catch(UnknowPublicationMarkerException upme){
              %><%-- Le marqueur n'est pas traité --%><%
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
         
         jAssmats.put("features", jFeaturesAssmats);
         jAssmats.put("type", "FeatureCollection");
         
         Iterator<ProfilASSMAT> itKmlPublication = kmlPublications.iterator();
         %><jalios:buffer name="putForward"><%
         while(itKmlPublication.hasNext()){
           publication = itKmlPublication.next();
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

// Mapbox

var map = new mapboxgl.Map({
    container: 'osMap',
    style: '<%= channel.getProperty("plugin.tools.maps.style") %>',
    center: [<%= box.getLongitude() %>, <%= box.getLatitude() %>],
    zoom: 8
});

var hoveredStateId =  null;


// Icones de marqueurs
// ---------------------------------------------------------------	
<%@ include file='/plugins/ToolsPlugin/jsp/googlemaps/mapsLoadCustomMarqueurs.jspf' %>	

map.on('load', function() {

	jsonRelais = <%= jRelais.toString() %>
	jsonAssmats = <%= jAssmats.toString() %>
	jsonCommune = <%= jCommune.toString() %>

	// Ajout des contrôles de plein écran et de navigation
	// ---------------------------------------------------------------
	map.addControl(new mapboxgl.FullscreenControl());
	map.addControl(new mapboxgl.NavigationControl(),'bottom-right');

	<!-- Contours -->
	map.addSource("commune", {
        type: "geojson",
        data: jsonCommune
    });
    
	map.addLayer({
        'id': 'commune-line',
        'type': 'line',
        'source': "commune",
        'layout': {},
        'paint': {
            'line-color': 'red',
            'line-opacity': 1,
            'line-width': 1
        }
    });

     map.addLayer({
        'id': 'commune-fill',
        'type': 'fill',
        'source': "commune",
        'layout': {},
        'paint': {
            'fill-color': 'red',
            'fill-opacity': 0.1
        }
    });

	<!-- Relais -->
	
	map.addSource("relais", {
        type: "geojson",
        data: jsonRelais,
        cluster: true,
        clusterMaxZoom: 14, // Max zoom to cluster points on
        clusterRadius: 50 // Radius of each cluster when clustering points (defaults to 50)
    });
    
    map.addLayer({
        id: "relais-clusters",
        type: "circle",
        source: "relais",
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
                "white",
                100,
                "white"
            ],
            "circle-stroke-width": 2
            ,
            "circle-radius": [
                "step",
                ["get", "point_count"],
                20,
                100,
                30
            ]
        }
    });

    map.addLayer({
        id: "relais-cluster-count",
        type: "symbol",
        source: "relais",
        filter: ["has", "point_count"],
        layout: {
            "text-field": "{point_count_abbreviated}",
            "text-font": ["DIN Offc Pro Medium", "Arial Unicode MS Bold"],
            "text-size": 12
        }
    });
    
    map.addLayer({
      id: "relais-point",
      type: "symbol",
      source:  "relais",
      filter: ["!has", "point_count"],
      layout: {
        "icon-image": "#44BEDF",
        'icon-offset': [0, -14]
      }
    });

	<!-- Assmat -->
    map.addSource("assmat", {
        type: "geojson",
        data: jsonAssmats,
        cluster: true,
        clusterMaxZoom: 14, // Max zoom to cluster points on
        clusterRadius: 50 // Radius of each cluster when clustering points (defaults to 50)
    });
    
	map.addLayer({
        id: "assmat-clusters",
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
                "white",
                100,
                "white"
            ],
            "circle-stroke-width": 2
            ,
            "circle-radius": [
                "step",
                ["get", "point_count"],
                20,
                100,
                30
            ]
        }
    });

    map.addLayer({
        id: "assmat-cluster-count",
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
      type: "symbol",
      source:  "assmat",
      filter: ["!has", "point_count"],
      layout: {
        "icon-image": ['get', 'marqueur'],
        'icon-offset': [0, -14],
      }
    });	

    // When a click event occurs on a feature in the places layer, open a popup at the
    // location of the feature, with description HTML from its properties.
    
    function clickPoint(e) {
        var coordinates = e.features[0].geometry.coordinates.slice();
        var nom = e.features[0].properties.nom;

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
    }
    
    map.on('click', 'assmat-point', function (e) {
		clickPoint(e);
    });

    map.on('click', 'relais-point', function (e) {
		clickPoint(e);
    });

    // Change the cursor to a pointer when the mouse is over the places layer.
    map.on('mouseenter', 'assmat-point', function () {
        map.getCanvas().style.cursor = 'pointer';
    });
    map.on('mouseenter', 'relais-point', function () {
        map.getCanvas().style.cursor = 'pointer';
    });
    map.on('mouseenter', 'relais-clusters', function () {
        map.getCanvas().style.cursor = 'pointer';
    });
    map.on('mouseenter', 'assmat-clusters', function () {
        map.getCanvas().style.cursor = 'pointer';
    });
    

    // Change it back to a pointer when it leaves.
    map.on('mouseleave', 'assmat-point', function () {
        map.getCanvas().style.cursor = '';
    });
    map.on('mouseleave', 'relais-point', function () {
        map.getCanvas().style.cursor = '';
    });
    map.on('mouseleave', 'relais-clusters', function () {
        map.getCanvas().style.cursor = '';
    });
    map.on('mouseleave', 'assmat-clusters', function () {
        map.getCanvas().style.cursor = '';
    });
    

	// Centre la carte sur les marqueurs
	var bounds = new mapboxgl.LngLatBounds();
	
	jsonAssmats.features.forEach(function(feature) {
		var arrayCoordinates = [];
		feature.geometry.coordinates.forEach(function(coordinate) {
	    	arrayCoordinates.push(coordinate);

	    });
	    bounds.extend(arrayCoordinates);
	});
	
	map.fitBounds(bounds, {
		padding: 30
	});
	
	
	map.on('click', 'relais-clusters', function (e) {
	    const cluster = map.queryRenderedFeatures(e.point, { layers: ["relais-clusters"] });
	    const coordinates = cluster[0].geometry.coordinates;
	    const currentZoom = map.getZoom();	
	    flyIntoCluster(map, coordinates, currentZoom);
	})
	
	
	map.on('click', 'assmat-clusters', function (e) {	    
	    var features = map.queryRenderedFeatures(e.point, { layers: ['assmat-clusters'] });
        var clusterId = features[0].properties.cluster_id;
        map.getSource('assmat').getClusterExpansionZoom(clusterId, function (err, zoom) {
            if (err)
                return;

            map.easeTo({
                center: features[0].geometry.coordinates,
                zoom: zoom + 1
            });
        });	    	    
	})
	
	
	function flyIntoCluster(map, coordinates, currentZoom){

	    const maxZoom = map.getZoom() + 1;
	
	    map.flyTo({
	        // These options control the ending camera position: centered at
	        // the target, at zoom level 16, and north up.
	        center: coordinates,
	        zoom: maxZoom,
	        bearing: 0,
	
	        // These options control the flight curve, making it move
	        // slowly and zoom out almost completely before starting
	        // to pan.
	        speed: 1, // make the flying slow
	        curve: 1, // change the speed at which it zooms out
	
	        // This can be any easing function: it takes a number between
	        // 0 and 1 and returns another number between 0 and 1.
	        easing: function (t) {
	            return t;
	        }
	    });

	}


});
