<%@page import="fr.cg44.plugin.assmat.policyfilter.ImportPolicyFilterExpireCd44"%>
<%@page import="fr.cg44.plugin.assmat.policyfilter.ImportExpireStatus"%>
<%@ include file='/jcore/doInitPage.jsp' %><%
%><%@ include file='/admin/doAdminHeader.jsp' %>

<% 

if (!isAdmin) { 
  sendForbidden(request, response);
  return;
}
%>


<h1 class="boTitle">Journal des imports (expiré/publié)</h1>


<%
  List<ImportExpireStatus> result = ImportExpireStatus.getImportExpireStatus();
  Collections.reverse(result);  
%>

<p>Fichier de log : WEB-INF/data/mashup/import/expire.log</p>


<table class="data-table">
  <tbody>
    <tr>
      <td class="listHeader">Date</td>
      <td class="listHeader">id expiré</td>
      <td class="listHeader">id publié</td>
    </tr>
    
		<jalios:foreach collection="<%= result %>" name="itImport" type="ImportExpireStatus">		 		  
		 
		 
		 <%
		  // Les publications passées à expiré
		  Map<String, Set<Publication>> pubExpireMap = ImportPolicyFilterExpireCd44.getRapportMapPub(itImport.getExpire().split(" ")) ;		  		  
	    // Les publications passées à publié 
		  Map<String, Set<Publication>> pubPublieMap = ImportPolicyFilterExpireCd44.getRapportMapPub(itImport.getPublie().split(" ")) ;		  
		 %>
		 
		  <tr>
		    <td>
		      <jalios:date date="<%= itImport.getImportDate() %>"  format="dd MMMM yyyy - HH:mm" />
		    </td>
		    
		    <td>
		      <jalios:foreach collection="<%= pubExpireMap.keySet() %>" name="itKey" type="String">
		        <h5><%= itKey %></h5>
		        <ul>
		          <jalios:foreach collection="<%= pubExpireMap.get(itKey) %>" name="itPub" type="Publication">
                <li><%= itPub %> <jalios:edit pub="<%= itPub %>"/> <b>(<%= itPub.getId() %>)</b> </li>		        
		          </jalios:foreach>
		        </ul>
		      </jalios:foreach>	      
		    </td>
		    
		    <td>
		      <jalios:foreach collection="<%= pubPublieMap.keySet() %>" name="itKey" type="String">
            <h5><%= itKey %></h5>
            <ul>
              <jalios:foreach collection="<%= pubPublieMap.get(itKey) %>" name="itPub" type="Publication">
                <li><%= itPub %> <jalios:edit pub="<%= itPub %>"/> <b>(<%= itPub.getId() %>)</b> </li>            
              </jalios:foreach>
            </ul>
          </jalios:foreach>  
		    </td>
		  </tr>
		  
		</jalios:foreach>
	</tbody>
</table>

<%@ include file='/admin/doAdminFooter.jsp' %>