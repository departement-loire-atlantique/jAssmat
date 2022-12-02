
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
<%
ProfilManager profilMngr = ProfilManager.getInstance();
ProfilASSMAT profil = (ProfilASSMAT) profilMngr.getProfilASSMAT(channel.getCurrentLoggedMember());
%>
<div class="hidden-phone" id="menuEspacePerso">

<%
TreeSet<Category> rootSet = new TreeSet<Category>(Category.getOrderComparator(userLang));
rootSet.addAll(rootCategory.getChildrenSet());

//Pour chacune de ces catégories, on ajoute leurs catégories comme point de départ
TreeSet<Category> childrentSetBeggining = new TreeSet< Category>(Category.getOrderComparator(userLang));
for (Category cat : rootSet) {
  childrentSetBeggining.addAll(cat.getChildrenSet());
}
%>

<nav role="navigation" class="ds44-innerBoxContainer ds44-box ds44-theme" aria-labelledby="titreMenuEspacePerso">
    <p role="heading" aria-level="2" id="titreMenuEspacePerso" class="ds44-box-heading"><%= box.getDisplayTitle(userLang) %></p>
    <ul>
        <jalios:foreach name="itCat" type="Category" collection="<%= rootSet  %>">
            <jalios:if predicate="<%= itCat.canBeReadBy(loggedMember) %>">
                <li><img src="https://www.loire-atlantique.fr/s.gif" class="bullet spr-puce" alt="">
                    <jalios:select>
                        <jalios:if predicate="<%= Util.notEmpty(itCat.getDescendantSet()) %>">
	                        <span><%= itCat %></span>
		                    <%
		                    TreeSet level2Set = new TreeSet<Category>(Category.getOrderComparator(userLang));
		                    level2Set.addAll(itCat.getChildrenSet());
		                    %>
		                    <jalios:if predicate="<%= level2Set.size() > 0 %>">
		                        <ul>
		                            <jalios:foreach name="itCatLevel2" type="Category" collection="<%= level2Set %>" counter="itLvl2">
		                                <li><img src="https://www.loire-atlantique.fr/s.gif" class="bullet spr-puce" alt="">
		                                <jalios:select>
		                                    <jalios:if predicate="<%= Util.notEmpty(itCatLevel2.getDescendantSet()) %>">
		                                        <span><%= itCatLevel2 %></span> 
		                                        <%
		                                        TreeSet level3Set = new TreeSet<Category>(Category.getOrderComparator(userLang));
		                                        level3Set.addAll(itCatLevel2.getChildrenSet());
		                                        %>
		                                        <ul>
		                                         <jalios:foreach name="itCatLevel3" type="Category" collection="<%= level3Set %>" counter="itLvl2">
		                                            <li><img src="https://www.loire-atlantique.fr/s.gif" class="bullet spr-puce" alt="">
			                                            <a href="<%=itCatLevel3.getDisplayUrl(userLocale)%>"><%= itCatLevel3 %></a>
		                                            </li>
		                                        </jalios:foreach>
		                                        </ul>
		                                    </jalios:if>
		                                    <jalios:default>
		                                        <a href="<%=itCatLevel2.getDisplayUrl(userLocale)%>"><%= itCatLevel2 %></a>
		                                    </jalios:default>
		                                </jalios:select>
		                                </li>
		                            </jalios:foreach>
		                        </ul>
		                    </jalios:if>
                        </jalios:if>
                        <jalios:default>
                            <a href="<%=itCat.getDisplayUrl(userLocale)%>"><%= itCat %></a>
                        </jalios:default>
                    </jalios:select>
                </li>
            </jalios:if>
        </jalios:foreach>
    </ul>
</nav>

</div>
</div>