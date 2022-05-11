<%@page import="fr.cg44.plugin.assmat.api.json.beans.Properties"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ page contentType="application/pdf; charset=UTF-8"%>

<%
String txtSearch = getUntrustedStringParameter("autocomplete", "");
String city = request.getParameter("cityCode");

if(txtSearch.length() < 3) {%>
  <div class="typeahead-menu noTooltipCard typeahead-search ajax-refresh-div" style="display: none;">
    <ul class="dropdown-menu dropdown-menu-adress-r1 dropdown-menu-cg44" style="display: none;">
      <li>Nombre de caractères insuffisants</li>
    </ul>
  </div>
  <% return ;  
}
%>

<div class="typeahead-menu noTooltipCard typeahead-search ajax-refresh-div" >

  <ul id="adress-select" class="dropdown-menu dropdown-menu-adress dropdown-menu-cg44">
    <li id="load-adress"><img style="padding-left: 20px;" src="plugins/AssmatPlugin/img/ajax-loading.gif" width="20" alt="chargement" /></li>
  </ul>
  
	  
	<script>
	
	jQuery.ajax({ 
	    type: 'GET', 
	    url: 'https://api-adresse.data.gouv.fr/search/?limit=5&citycode='+jQuery("input[name='codeInsee']").val(), 
	    data: { q: jQuery("input[name='adresse']").val() },      
	    dataType: 'json',
	    crossDomain : true,
	    success: function (data) {
          document.getElementById("load-adress").remove();   
	        if(data.features.length != 0) {	         	    
		        jQuery.each(data.features, function(index, element) {
		            var list = document.createElement('li');
		            if(index==0){
		            	list.setAttribute('class', 'active');
		            }
		            var text = document.createTextNode(element.properties.label);
		            
		            var link = document.createElement('a');
		            link.setAttribute('class', 'adresseAutocomplete');
		            link.setAttribute('data-cityPostCode', element.properties.postcode);
		            link.setAttribute('data-cityName', element.properties.city);
		            link.setAttribute('data-street', element.properties.name);
		            link.setAttribute('data-long', element.geometry.coordinates[0]);
		            link.setAttribute('data-lat', element.geometry.coordinates[1]);
		            
		            link.appendChild(text);
		            list.appendChild(link);	            
		            document.getElementById('adress-select').appendChild(list);
		        });		        
		      }else {		      
		        var list = document.createElement('li');
            var text = document.createTextNode("L'adresse n'est pas trouvée");
            list.appendChild(text);
            document.getElementById('adress-select').appendChild(list);		      
		      }
	    }
	});
		
	</script>

</div>





