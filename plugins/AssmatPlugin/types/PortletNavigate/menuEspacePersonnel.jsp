
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@ include file='/jcore/doInitPage.jsp' %><%
%><%@ include file='/jcore/portal/doPortletParams.jsp' %><%
%><%@ include file='/types/PortletNavigate/doInitPortletNavigate.jspf'%><%
%><%@page import="fr.cg44.plugin.tools.navigation.Menu" %>
<%@ page contentType="text/html; charset=UTF-8"%><%

  boolean display = rootCategory != null && rootCategory.isNode(loggedMember);
  if (display && box.getHideWhenNoResults()) {
    request.setAttribute("ShowPortalElement", Boolean.FALSE);
    return;
  }


 
 int level = box.getLevels();
 
 Publication portalPerso = channel.getPublication(channel.getProperty("jcmsplugin.assmatplugin.socle.portail.param.id"));
%>

<div class="menuEspacePerso" >

<h2 class="top-menu"><%= box.getDisplayTitle(userLang) %><a href="#" data-jalios-action="toggle:hidden-phone" data-jalios-target="#menuEspacePerso" class="visible-phone" style="float: right"><img src="plugins/AssmatPlugin/img/icone-burger.png" alt="menu" /></a></h2>

<%
ProfilManager profilMngr = ProfilManager.getInstance();
ProfilASSMAT profil = (ProfilASSMAT) profilMngr.getProfilASSMAT(channel.getCurrentLoggedMember());
%>
<div class="hidden-phone" id="menuEspacePerso">
<jalios:if predicate="<%= Util.notEmpty(profil) %>">
<%
	Date dateModif = profil.getMdate();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
%>


<%if(Util.notEmpty(loggedMember)){ %>
<div class="infoMbrLogin"><span class="memberLogged"><%=loggedMember.getFirstName() %> <%=loggedMember.getName() %><img src="plugins/AssmatPlugin/img/icon-person.png"/> </span></div>
<%} %>
<p class="dateActualisation">Numéro de dossier : <%=profil.getNum_agrement() %></p>
<p class="dateActualisation"><%= glp("jcmsplugin.derniere-actualisation.profil",sdf.format(dateModif)) %></p>

</jalios:if>

<p class="dateActualisation"><a class="deconnection surligne" href='<%=portalPerso.getDisplayUrl(userLocale)%>'>Accueil espace personnel</a></p>
<p class="dateActualisation"><a class="deconnection surligne" href='<%= channel.getProperty("jcms.resource.logout") %>'><%= glp("plugin.corporateidentity.header.deconnexion") %></a></p>

<%
  %><%-- Toogle bar --%><%
    int maxLevel = box.getLevels();
  
    TreeSet<Category> rootSet = new TreeSet<Category>(Category.getOrderComparator(userLang));
    rootSet.addAll(rootCategory.getChildrenSet());
  
    //Pour chacune de ces catégories, on ajoute leurs catégories comme point de départ
    TreeSet<Category> childrentSetBeggining = new TreeSet<Category>(Category.getOrderComparator(userLang));  
    for (Category cat : rootSet) {
      childrentSetBeggining.addAll(cat.getChildrenSet());          
    }
    
  %><jalios:foreach name="itCat" type="Category" collection="<%= rootSet  %>"><%
      %><jalios:if predicate="<%= itCat.canBeReadBy(loggedMember) %>"><%
      
      %><%-- Niveau 1 --%><%
      %><ul class="listLvl1"><li><%
        %><span class="level1" href="<%=itCat.getDisplayUrl(userLocale)%>"><%= itCat %></span><%
        
        %><%-- Niveau 2 --%><%
        
          TreeSet level2Set = new TreeSet<Category>(Category.getOrderComparator(userLang));
          level2Set.addAll(itCat.getChildrenSet());
          
          
        
        %><jalios:if predicate="<%= level2Set.size() > 0 %>"><%
           
          %><ul class="listLvl2"><%
             // Catégorie du niveau 2
              %><jalios:foreach name="itCatLevel2" type="Category" collection="<%= level2Set %>" counter="itLvl2"><%
                %><li><jalios:if predicate="<%= itCatLevel2.canBeReadBy(loggedMember) %>"><%
                   %><img src="plugins/ToolsPlugin/images/bullet.jpg" class="imgBullet"/>
                          <jalios:select>
                           <jalios:if predicate="<%= Util.notEmpty(itCatLevel2.getDescendantSet()) %>">
                           		<span class='level2 <%= itCatLevel2.equals(currentCategory)?"current":"" %>' ><%= itCatLevel2 %></span> 
                           </jalios:if>
                           <jalios:default>
                           		<a class='level2 <%= itCatLevel2.equals(currentCategory)?"current":"" %>' href="<%=itCatLevel2.getDisplayUrl(userLocale)%>"><%= itCatLevel2 %></a>      
                           </jalios:default>
                           
                           </jalios:select>      
					<jalios:if predicate="<%= Util.notEmpty(itCatLevel2.getDescription()) %>">     
						<button class="cg-tooltip buttonHelp" data-category-id="none" aria-label="<%= encodeForHTMLAttribute(itCatLevel2.getDescription()) %>">
							<img alt="?" src="s.gif" class="spr-interrogation">
						</button>
					</jalios:if>
	                 
                  </jalios:if><%
                  
                  
                  %><%-- Niveau 3 --%><%
                  
                  TreeSet level3Set = new TreeSet<Category>(Category.getOrderComparator(userLang));
                  level3Set.addAll(itCatLevel2.getChildrenSet());
                  
                  
                
                %><jalios:if predicate="<%= level3Set.size() > 0 %>"><%
                   
                  %><ul class="listLvl3"><%
                     // Catégorie du niveau 3
                      %><jalios:foreach name="itCatLevel3" type="Category" collection="<%= level3Set %>" counter="itLvl3"><%
                        %><li><jalios:if predicate="<%= itCatLevel3.canBeReadBy(loggedMember) %>"><%
                           %><img src="plugins/AssmatPlugin/img/puce_pixels.png" class="imgPixels"/>
                           <jalios:select>
                           <jalios:if predicate="<%= Util.notEmpty(itCatLevel3.getDescendantSet()) %>">
                           		<span class='level3 <%= itCatLevel3.equals(currentCategory)?"current":"" %>' ><%= itCatLevel3 %></span> 
                           </jalios:if>
                           <jalios:default>
                           		<a class='level3 <%= itCatLevel3.equals(currentCategory)?"current":"" %>' href="<%=itCatLevel3.getDisplayUrl(userLocale)%>"><%= itCatLevel3 %></a>      
                           </jalios:default>
                           
                           </jalios:select>
                           
                            
        					<jalios:if predicate="<%= Util.notEmpty(itCatLevel3.getDescription()) %>">     
        						<button class="cg-tooltip buttonHelp" data-category-id="none" aria-label="<%= encodeForHTMLAttribute(itCatLevel3.getDescription()) %>">
        							<img alt="?" src="s.gif" class="spr-interrogation">
        						</button>
        					</jalios:if>
        	                 
                          </jalios:if></li><% 
                      %></jalios:foreach><%
                    %></ul><%
                    %></jalios:if><%      
              %></jalios:foreach><%
            
              
              %></li><%
              %></ul><%
        %></jalios:if><%
        %></li><%
      %></ul><%
    %></jalios:if><% 
  %></jalios:foreach><%
%>
</div>
</div>