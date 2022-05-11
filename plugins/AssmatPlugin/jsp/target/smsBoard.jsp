<%
String clazz = "";
if("true".equals(request.getAttribute("smsReportSubAdminMenu"))){
	clazz = "active";
}
%>

<li><a href="plugins/AssmatPlugin/jsp/tableauDeBord/tableauBordSMS.jsp" class="<%= clazz %>">
<img src="s.gif" class="icon ss_sprite ss_chart_curve" alt="">Tableau de bord des SMS</a>
</li>
