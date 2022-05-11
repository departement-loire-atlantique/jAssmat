<%@page import="fr.cg44.plugin.assmat.util.SmsUtil"%>
<%@page import="com.orange.ceo.client.bean.WSResultReport"%>
<%@page import="com.orange.ceo.client.bean.WSResultsReport"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="fr.cg44.plugin.assmat.beans.EnvoiSms"%>
<%@page import="fr.cg44.plugin.assmat.managers.SmsDAO"%>
<%@ include file='/jcore/doInitPage.jsp'%>
<%
request.setAttribute("reportingAdminMenu", "true");
request.setAttribute("smsReportSubAdminMenu", "true");
%><%@ include file='/admin/doAdminHeader.jsp' %>

<%
	SmsDAO dao = new SmsDAO();
	List<EnvoiSms> listSms = dao.getAllSms();
	if (Util.notEmpty(listSms)) {
		
%>
<jalios:pager name="smsListPager" declare='true' pageSize="500"
	action="init" size="<%=listSms.size()%>" />


<table class="table-data table table-striped table-bordered">
	<tbody>
		<tr>
			<th><%= glp("jcmsplugin.assmatplugin.tableau-bord.sms.identifiant") %></th>
			<th><%= glp("jcmsplugin.assmatplugin.tableau-bord.sms.cdate") %></th>
			<th><%= glp("jcmsplugin.assmatplugin.tableau-bord.sms.tel-and-name") %></th>
			<th><%= glp("jcmsplugin.assmatplugin.tableau-bord.sms.content") %></th>
			<th><%= glp("jcmsplugin.assmatplugin.tableau-bord.sms.id-diffusion") %></th>
			<th><%= glp("jcmsplugin.assmatplugin.tableau-bord.sms.statut-diffusion") %></th>
		</tr>
        <jalios:foreach collection="<%=listSms%>" name="itSms" type="EnvoiSms"
            max='<%=smsListPager.getPageSize()%>'
            skip='<%=smsListPager.getStart()%>'>
			<tr>
				<td><%=itSms.getId()%></td>
				<td>
				<%=itSms.getCreatedDate()%> 
				<%
				   	if (Util.notEmpty(itSms.getUpdatedDate())) {
				   	   if (!itSms.getCreatedDate().equals(itSms.getUpdatedDate())) {
                        %> <br> <%=itSms.getUpdatedDate()%> <%
                       }
				    } %>
				</td>

				<%
					//On recupere l'assmat correspondant
					ProfilASSMAT profilAM = (ProfilASSMAT) channel.getPublication(itSms.getAssmatId());
					Member member = null;
					if (Util.notEmpty(profilAM)) {
						member = profilAM.getAuthor();
					} 
				%>

				<td>
					<%
						if (Util.notEmpty(itSms.getSendProfiles())) {
								String tel = itSms.getSendProfiles();
							   if(itSms.getSendProfiles().indexOf("<TERMINAL_ADDR>") != -1) {
							     tel = itSms.getSendProfiles().substring(itSms.getSendProfiles().indexOf("<TERMINAL_ADDR>"), itSms.getSendProfiles().indexOf("</TERMINAL_ADDR>"));
							   }
					
					%> <%=glp("jcmsplugin.assmatplugin.tableau-bord.sms.tel-and-name.value", tel)%><br> <%
					 	}
					 %> <%
					 	if (Util.notEmpty(member)) {
					 %> <a href="<%=member.getDisplayUrl(userLocale)%>"><%=member.getName()%></a> <%
					 	}
					 %>
				</td>
				<td>
				    <%=itSms.getContent()%> <br> 
				    <%=glp("jcmsplugin.assmatplugin.tableau-bord.sms.content.value1", itSms.getContent().length())%><br>
					<%= glp("jcmsplugin.assmatplugin.tableau-bord.sms.content.value2",itSms.getSubject())%>
				</td>

				<%	if (Util.notEmpty(itSms.getIdDiffusion())) {	%>
				<td><%=itSms.getIdDiffusion()%></td>
				<%	} else { %>
				<td><b><%=itSms.getLastErrorCode()%><b></b></td>
				<%
					}
				%>
				<%	if (Util.notEmpty(itSms.getIdDiffusion()) && Util.notEmpty(itSms.getStatutDiffusion())) { %>
				<td
					<%if (!SmsUtil.STATUS_COMPLETED.equalsIgnoreCase(itSms.getStatutDiffusion())) {%>
					style="color: red;" <%}%>><%= itSms.getStatutDiffusion() %></td>
				<%	} else { %>

				<td></td>
				<%	} %>
			</tr>
		</jalios:foreach>

	</tbody>
</table>
<jalios:pager name="smsListPager" />


<%
	}
%>

<%@ include file='/admin/doAdminFooter.jsp' %> 