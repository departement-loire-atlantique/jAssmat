<%@ include file='/jcore/doInitPage.jsp' %>

<%
String idMember = request.getParameter("id");
if(Util.notEmpty(idMember)){
Member mbr = channel.getMember(idMember);
if(Util.notEmpty(mbr)){
Group assmatGrout = channel.getGroup(channel.getProperty("jcmsplugin.assmatplugin.groupe.assistante_maternelle"));
%>

<jalios:if predicate="<%= mbr.belongsToGroup(assmatGrout) %>">
    <jalios:javascript>    
    jQuery("[name='login']").attr('readonly', true);    
    </jalios:javascript>
</jalios:if>
<% }} %>