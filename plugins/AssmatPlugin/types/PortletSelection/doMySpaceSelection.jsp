  <%@ include file='/jcore/doInitPage.jspf'                    %><%
%><%@ include file='/jcore/portal/doPortletParams.jspf'        %><% 
%><%@ include file='/types/PortletSelection/doSelection.jspf' %><% 

  PortletSelection selection = (PortletSelection) portlet;
  PortalElement    child     = (PortalElement) Util.getObject(selection.getChildren(),0,null);
%>

    <jalios:include target="SOCLE_ALERTE"/>
    
    <jalios:include id="<%= selection.getChildren()[0].getId() %>" />