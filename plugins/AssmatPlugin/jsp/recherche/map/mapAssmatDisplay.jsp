<%@ page import="fr.cg44.plugin.assmat.AssmatUtil"%><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.kml.KmlStyle"%><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.kml.KmlElementHandler"%><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.kml.KmlElement"%><%
%><%@ page import="fr.cg44.plugin.assmat.PointAssmat"%><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.StaticMap" %><%
%><%@ page import="com.jalios.util.Util"%><%
%><%@ page import="java.awt.image.BufferedImage" %><%
%><%@ page import="java.io.ByteArrayInputStream" %><%
%><%@ page import="java.io.File" %><%
%><%@ page import="java.io.InputStream" %><%
%><%@ page import="java.nio.ByteBuffer" %><%
%><%@ page import="java.math.BigDecimal"%><%
%><%@ page import="java.util.ArrayList"%><%
%><%@ page import="java.util.List" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.MarkerShape" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.proxy.Proxy" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.Geolocation" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.geometry.Point" %><%
%><%@ page import="fr.cg44.plugin.tools.ToolsUtil" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.exception.InvalidMarkerShapeException" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.exception.UnknowCurrentColorException" %><%
%><%@ page import="fr.cg44.plugin.tools.googlemaps.exception.UnknowPublicationMarkerException" %><%
%><%@ page import="fr.cg44.downloader.Downloader" %><%
%><%@ page import="fr.cg44.downloader.ImageDownloader" %><%
String mapJspType = (String) request.getAttribute("gMapsType");
String width = ToolsUtil.getProperty("plugin.tools.googlemaps.v3.typemap."+mapJspType+".width");
String widthpixels = ToolsUtil.getProperty("plugin.tools.googlemaps.v3.typemap."+mapJspType+".width.pixels");
String height = ToolsUtil.getProperty("plugin.tools.googlemaps.v3.typemap."+mapJspType+".height");
String mapType = ToolsUtil.getProperty("plugin.tools.googlemaps.v3.typemap."+mapJspType+".type");
String mapZoomMaptiler = ToolsUtil.getProperty("plugin.tools.maptiler.typemap."+mapJspType+".zoom");
String initMethodName = (String) request.getAttribute("initMethodName");
String deferInit = (String) request.getAttribute("deferInit");

PointAssmat point = (PointAssmat) request.getAttribute("point");
PointAssmat point2 = (PointAssmat) request.getAttribute("point2");

if(Util.notEmpty(point)){
  
BigDecimal BGlatitude = new BigDecimal(point.getLatitude());
BigDecimal BGlongitude =  new BigDecimal(point.getLongitude());
String color = point.getCouleurPoint();

if(width != null && height != null && mapType != null && BGlatitude != null && BGlongitude != null){

	Downloader downloader = new ImageDownloader();
	
	float latitude = BGlatitude.floatValue();
	float longitude = BGlongitude.floatValue();
	
	color = color.substring(1);
	String linkLabel = "Cliquer pour agrandir la carte";
	
	String urlImage = StaticMap.getSrc(widthpixels, height, color, latitude, longitude, mapZoomMaptiler, downloader);
	String linkUrl = "https://www.openstreetmap.org/directions?engine=graphhopper_car&route=" + latitude + "%2C" + longitude + "#map=11/" + latitude + "/" + longitude;

	%>
	<a href="<%= linkUrl %>">
		<img srcset="<%= urlImage %>" title="<%= linkLabel %>" alt="<%= linkLabel %>" style="margin: auto; display: block;">
	</a>
   <%

%><% } else { %><%
  logger.error("Type ("+mapJspType+") for integrated google map isn't defined.");
%><% }} %><%
%>