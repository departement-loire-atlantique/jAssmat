<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file='/jcore/doInitPage.jsp' %>

<%
Data data = getDataParameter("id");
%>
<jalios:if predicate="<%= data instanceof ProfilASSMAT %>">
<%
	ProfilASSMAT profil = (ProfilASSMAT) data;
	if(Util.notEmpty(profil)){
		List<AssmatSolis> listSolis = SolisManager.getInstance().getAssmatSolisByNumAgrement(profil.getNum_agrement());
		if(Util.notEmpty(listSolis)){
			AssmatSolis solis = listSolis.get(0);
			if(Util.notEmpty(solis)){
%>
				<%= AssmatUtil.toString(solis) %>
<%				
			}
		}
		
	}
%>
</jalios:if>