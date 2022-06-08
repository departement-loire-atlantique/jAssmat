<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="fr.trsb.cd44.solis.beans.Quartier"%>
<%@ page import="fr.trsb.cd44.solis.manager.QuartierDAO"%>

<%
// Récupération des quartiers de Nantes
List<Quartier> listQuartiers = QuartierDAO.getAllQuartiersNantes();
boolean afficheQuartiers = Integer.toString(distance).equals("-10");


%>

<fieldset id="listeQuartiers" class="categories" style="<%= afficheQuartiers ? "" : "display:none;"%>">
	<legend style="display:none;"><%=glp("jcmsplugin.assmatplugin.recherche.am.quartier.label")%> <span class="facultatif"> (facultatif)</span></legend>
	<ul class="unstyled categories">

		<li class="checkingFacility"><button
				class="checkAll ajax-refresh active"
				data-jalios-target="DIV.ID_p1_64459"
				data-jalios-action="Ajax-Refresh"><%= glp("plugin.tools.list.checkall") %></button>
			/
			<button class="uncheckAll ajax-refresh"
				data-jalios-target="DIV.ID_p1_64459"
				data-jalios-action="Ajax-Refresh"><%= glp("plugin.tools.list.uncheckall") %></button></li>

		
		<%if (Util.notEmpty(listQuartiers)) {%>
		<jalios:foreach collection="<%=listQuartiers%>" name="itQuartier" type="Quartier">
			<li class=""><input type="checkbox"
				<%if(listeIdQuartiers.contains(itQuartier.getIdQuartier())){ %> checked <%} %>
				id="<%=itQuartier.getIdQuartier()%>" name="quartier"
				value="<%=itQuartier.getIdQuartier()%>">
				<label for="<%=itQuartier.getIdQuartier()%>" class="checkbox-label"><%=itQuartier.getLibQuartier()%></label></li>
		</jalios:foreach>
		<%} %>
	</ul>

</fieldset>