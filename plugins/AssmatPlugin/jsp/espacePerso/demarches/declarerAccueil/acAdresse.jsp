<%@page import="fr.cg44.plugin.assmat.api.json.beans.Properties"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ page contentType="application/pdf; charset=UTF-8"%>

<%
String txtSearch = getUntrustedStringParameter("autocomplete", "");
String representant = getUntrustedStringParameter("representant", "");

if(txtSearch.length() < 3) {%>
  <div class="typeahead-menu noTooltipCard typeahead-search ajax-refresh-div" style="display: none;">
    <ul class="dropdown-menu dropdown-menu-adress-r1 dropdown-menu-cg44" style="display: none;">
      <li>Nombre de caractères insuffisants</li>
    </ul>
  </div>
  <% return ;  
}

ArrayList<Properties> propList = AssmatUtil.getPropertiesFromAdresse(txtSearch); 
%>

<div class="typeahead-menu noTooltipCard typeahead-search ajax-refresh-div" >

  <ul class="dropdown-menu dropdown-menu-adress-<%= representant %> dropdown-menu-cg44">
    
    <jalios:if predicate="<%= Util.isEmpty(propList) %>">
      <li>Pas de résultat</li>
    </jalios:if>
    
    <jalios:foreach collection="<%= propList %>" name="itProp" type="Properties">    
      <li class='<%= itCounter == 1 ? "active" : ""  %>' ><a class="adresseAutocompleteR1" data-cityPostCode="<%= itProp.getPostcode() %>" data-cityName="<%= itProp.getCommune() %>" data-street="<%= itProp.getName() %>"> <%= itProp.getLabel() %></a></li>    
    </jalios:foreach>
    
    
    
    
  </ul>

</div>