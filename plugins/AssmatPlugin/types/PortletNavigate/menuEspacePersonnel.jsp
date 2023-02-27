
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@ include file='/jcore/doInitPage.jsp' %><%
%><%@ include file='/jcore/portal/doPortletParams.jsp' %><%
%><%@ include file='/types/PortletNavigate/doInitPortletNavigate.jspf'%><%
%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%
boolean display = rootCategory != null && rootCategory.isNode(loggedMember);
if (display && box.getHideWhenNoResults()) {
  request.setAttribute("ShowPortalElement", Boolean.FALSE);
  return;
}

int level = box.getLevels();

Publication portalPerso = channel.getPublication(channel.getProperty("jcmsplugin.assmatplugin.socle.portail.param.id"));

ProfilManager profilMngr = ProfilManager.getInstance();
ProfilASSMAT profil = (ProfilASSMAT) profilMngr.getProfilASSMAT(channel.getCurrentLoggedMember());

TreeSet<Category> rootSet = new TreeSet<Category>(Category.getOrderComparator(userLang));
rootSet.addAll(rootCategory.getChildrenSet());
%>

<div class="ds44-innerBoxContainer ">

<%-- Note SGU 20/12/2022 : voir si on intègre un retour à l'accueil de l'espace perso en dur. --%>

<%-- Niveau 1 : titres non cliquables --%>
<jalios:foreach name="itCat" type="Category" collection="<%= rootSet  %>">
	<jalios:if predicate="<%= itCat.canBeReadBy(loggedMember) %>">
		<p role="heading" aria-level="2" class="<%= itCounter > 1 ? "ds44-mt3 " : ""%>ds44-box-heading"><%= itCat %></p>
		<jalios:if predicate="<%= Util.notEmpty(itCat.getDescendantSet()) %>">
            <%
            TreeSet level2Set = new TreeSet<Category>(Category.getOrderComparator(userLang));
            level2Set.addAll(itCat.getChildrenSet());
            for (Iterator<Category> iter = level2Set.iterator(); iter.hasNext();) {
              Category itCatIter = iter.next();
              if (!itCatIter.canBeReadBy(loggedMember)) {
                iter.remove();
              }
            }
            %>
			<jalios:if predicate="<%= level2Set.size() > 0 %>">
                <ul>
                    <%-- Niveau 2 : Si catégories filles alors simple libellé, sinon lien --%>
                    <jalios:foreach name="itCatLevel2" type="Category" collection="<%= level2Set %>" counter="itLvl2">
                        <li>
                            <jalios:select>
                                <jalios:if predicate="<%= Util.notEmpty(itCatLevel2.getDescendantSet()) %>">
                                    <%= itCatLevel2 %>
								    <%
								    TreeSet level3Set = new TreeSet<Category>(Category.getOrderComparator(userLang));
	                                level3Set.addAll(itCatLevel2.getChildrenSet());
	                                for (Iterator<Category> iter = level3Set.iterator(); iter.hasNext();) {
	                                  Category itCatIter = iter.next();
	                                  if (!itCatIter.canBeReadBy(loggedMember)) {
	                                    iter.remove();
	                                  }
	                                }
                                    %>
									<ul>
										<jalios:foreach name="itCatLevel3" type="Category" collection="<%= level3Set %>" counter="itLvl2">
                                            <li><a href="<%=itCatLevel3.getDisplayUrl(userLocale)%>"><%= itCatLevel3 %></a></li>
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
    </jalios:if>
</jalios:foreach>


</div>