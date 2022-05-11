<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="fr.trsb.cd44.solis.beans.Quartier"%>
<%@ page import="fr.trsb.cd44.solis.manager.QuartierDAO"%>

<%@ include file='/jcore/doInitPage.jsp'%>
<h3>Liste des quartiers :</h3> 

<%
	//QuartierDAO dao = new QuartierDAO();
	//List<Quartier> listQuartiers = dao.getAllQuartiersNantes();
	
	List<Quartier> listQuartiers = QuartierDAO.getAllQuartiersNantes();
	
	if (Util.notEmpty(listQuartiers)) {

%>
<ol>
        <jalios:foreach collection="<%=listQuartiers%>" name="itQuartier" type="Quartier">

			<li><%=itQuartier.getIdQuartier()%> <%=itQuartier.getLibQuartier()%></li>

		</jalios:foreach>
</ol>
<%
	}
%> 

<h3>Liste des micros-quartiers :</h3>

<%
List<Quartier> listMicroQuartiers = QuartierDAO.getAllMicrosQuartiersNantes();
	
	if (Util.notEmpty(listMicroQuartiers)) {

%>
<ol>
        <jalios:foreach collection="<%=listMicroQuartiers%>" name="itMicroQuartier" type="Quartier">

			<li><%=itMicroQuartier.getIdMicroQuartier()%> <%=itMicroQuartier.getLibMicroQuartier()%></li>

		</jalios:foreach>
</ol>
<%
	}
%> 