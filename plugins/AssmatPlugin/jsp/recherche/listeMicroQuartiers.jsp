<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@ page import="fr.trsb.cd44.solis.beans.Quartier"%>
<%@ page import="fr.trsb.cd44.solis.manager.QuartierDAO"%>

<%
if(AssmatUtil.canSeeMicroQuartiers(loggedMember)) {
	List<Quartier> listMicroQuartiers = QuartierDAO.getAllMicrosQuartiersNantes(loggedMember);
	boolean afficheMicroQuartiers = Integer.toString(distance).equals("-20");
%>

<fieldset id="listeMicroQuartiers" class="categories" style="<%= afficheMicroQuartiers ? "" : "display:none;"%>">
	<legend style="display:none;"><%=glp("jcmsplugin.assmatplugin.recherche.am.microQuartier.label")%> <span class="facultatif"> (facultatif)</span></legend>
	<ul class="unstyled categories">

		<li class="checkingFacility"><button
				class="checkAll ajax-refresh active"
				data-jalios-target="DIV.ID_p1_64459"
				data-jalios-action="Ajax-Refresh"><%= glp("plugin.tools.list.checkall") %></button>
			/
			<button class="uncheckAll ajax-refresh"
				data-jalios-target="DIV.ID_p1_64459"
				data-jalios-action="Ajax-Refresh"><%= glp("plugin.tools.list.uncheckall") %></button></li>

		
		<%if (Util.notEmpty(listMicroQuartiers)) {%>
		<jalios:foreach collection="<%=listMicroQuartiers%>" name="itMicroQuartier" type="Quartier">
			<li class=""><input type="checkbox"
				<%if(listeIdMicroQuartiers.contains(itMicroQuartier.getIdMicroQuartier())){ %> checked <%} %>
				id="<%=itMicroQuartier.getIdMicroQuartier()%>" name="microQuartier"
				value="<%=itMicroQuartier.getIdMicroQuartier()%>">
				<label for="<%=itMicroQuartier.getIdMicroQuartier()%>" class="checkbox-label"><%=itMicroQuartier.getLibMicroQuartier()%></label></li>
		</jalios:foreach>
		<%} %>
	</ul>

</fieldset>


<%
}
%>