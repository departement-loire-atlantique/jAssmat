<%@ include file='/jcore/doInitPage.jsp' %><%
String template=channel.getProperty("agendaPlugin.resource.facetDate", null);

int defaultSearchPeriod = 300000;


if (Util.notEmpty(template)) { 
	%><jalios:include jsp="<%= template %>"/><% 
	return ;
}%> 

<%@ page import="com.jalios.jcms.handler.QueryHandler" %><%
 
  
  String facetFormName=(String)request.getAttribute("facetFormName");
	String jsFacetDate = "JCMS.plugin.AgendaPlugin.facetDate.";
	jcmsContext.addJavaScript("plugins/AgendaPlugin/js/agendaFacets.js");
	jcmsContext.addJavaScript("plugins/AgendaPlugin/js/pickadatejs/legacy.js");
	jcmsContext.addJavaScript("plugins/AgendaPlugin/js/pickadatejs/picker.js");
	jcmsContext.addJavaScript("plugins/AgendaPlugin/js/pickadatejs/picker.date.js");
	if("fr".equals(userLang)){
		jcmsContext.addJavaScript("plugins/AgendaPlugin/js/pickadatejs/translations/fr_FR.js");
	}
	jcmsContext.addCSSHeader("plugins/AgendaPlugin/js/pickadatejs/themes/default.css");
	jcmsContext.addCSSHeader("plugins/AgendaPlugin/js/pickadatejs/themes/default.date.css");
	
	PortletSearchFacets box = (PortletSearchFacets) request.getAttribute("box");
%>

	
	<div>
   	   
   		<% String dateSaisie=request.getParameter("dateSaisie");
   		SimpleDateFormat sdf=new SimpleDateFormat(glp("date-format"),userLocale);
   		//sdf.
   		Date date=getDateParameter( "dateSaisie", userLang, null);
   		
   		int nbjourSaisi=getIntParameter( "nbjourSaisi", defaultSearchPeriod);
   		//String nbjourSaisiString = request.getParameter("nbjourSaisi");
	   	%>
		<% 	/** Ajout d'un input pour eviter le BUG #6829 (ouverture du calendrier par focus automatique lors
			d'un refresh ajax de page) 
			Une autre solution aurait �t� d'inverser l'ordre du input date et du select de periode 
			A mettre en oeuvre si la solution actuelle ne convenait plus dans le temps **/ %>
		<input name="nofocus" aria-hidden="true" style="position:absolute; left:-10000px" />
	   	<div class="form-input inputDate">
	   		<div class="img">
			<!--jalios:datechooser widgetName="dateSaisie" date="<%=date %>" showTime="false"  nowifnull="false" css="inputDate"/> -->
				<input type="text" name="dateSaisie" class="datepicker" placeholder="<%=glp("fr.cg44.plugin.agenda.facet.date.placeholder") %>" value="<%=dateSaisie!=null?dateSaisie:"" %>" title="<%=glp("fr.cg44.plugin.agenda.facet.date.title") %>"/>
			</div>
		</div>
		
		<jalios:javascript>
			<%=jsFacetDate %>loadController('<%=facetFormName %>','dateSaisie','nbjourSaisi','<%=userLang %>');
		</jalios:javascript>	   		
	</div>
