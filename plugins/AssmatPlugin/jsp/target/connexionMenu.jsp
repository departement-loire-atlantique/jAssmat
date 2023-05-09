<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@ include file='/jcore/doInitPage.jsp' %>

<% Category menuMonCompteCat = channel.getCategory("$jcmsplugin.assmat.connexion.menu.cat"); %>


<div id="subMenu-monCompte" class="ds44-subMenu" data-sub-menu="" aria-hidden="true">
    <p role="heading" aria-level="1" class="visually-hidden"><%= menuMonCompteCat.getName()  %></p>
    <ul class="ds44-list">    
	    	    
	    <jalios:if predicate="<%= isLogged && loggedMember.belongsToGroup(AssmatUtil.getGroupAssmat()) %>">
		    <jalios:foreach collection="<%= menuMonCompteCat.getDescendantSet() %>" name="itCat" type="Category">	        	        	                    
		        <li>	            	            
		            <a href="<%= itCat.getDisplayUrl(userLocale) %>"><%= itCat.getName() %></a>                          	
		       </li>	        
		    </jalios:foreach>
	    </jalios:if>
	    	    
	    <li>
            <a href='<%= ResourceHelper.getLogout() %>'><%= glp("jcmsplugin.assmatplugin.menu.deconnexion") %></a>
        </li>	    
    </ul>
</div>