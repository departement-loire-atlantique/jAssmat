<%@page import="fr.cg44.plugin.assmat.PointAssmat"%>
<%@page import="fr.cg44.plugin.assmat.beans.AssmatSearch"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="fr.cg44.plugin.assmat.managers.AssmatSearchDAO"%>
<%@ page contentType="text/html; charset=UTF-8" %><%
%><%@ include file='/jcore/doInitPage.jspf' %><%
%><%@ include file='/jcore/portal/doPortletParams.jspf' %><%
%><%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%><%

Set<String>         panier      = (Set<String>) session.getAttribute("panier");
PortletJsp          obj         = (PortletJsp)portlet; 
Set<ProfilASSMAT>    panierSet   = JcmsUtil.idCollectionToDataTreeSet(panier, ProfilASSMAT.class); 

String              urlAction   = ServletUtil.getResourcePath(request) + "?id=" + portlet.getId();
ProfilManager profilMgr = ProfilManager.getInstance();


if(Util.notEmpty(panierSet)){
  session.setAttribute("isSelection", "true"); 
}

%>
<main role="main" id="content">
<article class="ds44-container-large">

   <ds:titleNoImage title="<%= obj.getTitle(userLang) %>" breadcrumb="true" ></ds:titleNoImage>
            
    <div class="ds44-mt3 ds44-mb5 ds44--xl-padding-t" >
        <div class="ds44-inner-container">
            <jalios:select>
            <jalios:if predicate="<%= Util.notEmpty(panierSet) && panierSet.size() > 0 %>">
            <div class="grid-12-medium-1 grid-12-small-1" >
                <article class="col-7" data-cart-manage>
                    <h2 data-cart-title class="h4-like ds44-mb2"><%= glp("jcmsplugin.assmatplugin.favoris.selection", panierSet.size()) %></h2>
                        <div class="ds44-txtRight">
	                        <button data-cart-remove-url="plugins/SoclePlugin/jsp/panier/select-enabled.jsp?removeAll=true" >
	                            <i class="icon icon-cross" aria-hidden="true"></i>
	                            <span class="ds44-btnInnerText"><%= glp("jcmsplugin.assmatplugin.favoris.tout-retirer") %></span>
	                        </button>                        
                        </div>
                        <table class="selection ds44-mt2">
                            <caption class="visually-hidden"><%= glp("jcmsplugin.assmatplugin.favoris.selection", panierSet.size()) %></caption>

                            <thead>
                                <tr>
                                    <th scope="col"><%= glp("jcmsplugin.assmatplugin.favoris.liste") %></th>
                                    <th scope="col"><%= glp("jcmsplugin.assmatplugin.favoris.action") %></th>         
                                </tr>
                            </thead>



                            <tbody>
                            
                            
                            
	                           <jalios:foreach name="itProfilAM" type="ProfilASSMAT" collection="<%= panierSet %>" >	 
	                        
	                        
	                           <tr data-cart-element>
	                               <td>
	                        
	                               <jalios:if predicate="<%= Util.notEmpty(itProfilAM) %>">
	                                          
	                              
	                               <%
	                               
	                               AssmatSearch assmatSearch = Util.getFirst(AssmatSearchDAO.getAssmatSearchByIdMembre(itProfilAM.getAuthorId()));
	                               boolean isDomicile= assmatSearch.getIsDomicile();
	                               PointAssmat pointAssmat = new PointAssmat(String.valueOf(itProfilAM.getLatitudeAssmat()), String.valueOf(itProfilAM.getLongitudeAssmat()), channel.getProperty("jcmsplugin.assmatplugin.recherche.am.result.recap.avec.dispo.popup.color"), "",isDomicile);
	
	                               
	                               request.setAttribute("assmatSearch", assmatSearch);
	                               request.setAttribute("point", pointAssmat);
	                               %>
	                              
	                               <% request.setAttribute("favoris", true); %>
	                               <jalios:media data="<%= itProfilAM %>" />
                                   <% request.removeAttribute("favoris"); %>


	                                   
	                                   
	                               <% 
	                                 request.removeAttribute("assmatSearch");  
									 request.removeAttribute("point");									   
								   %>  
	                                   
	                               </jalios:if> 
	                                   
	                               </td>
	                               
	                               
	                               <td>
	                                  <button data-cart-remove-url="plugins/SoclePlugin/jsp/panier/select-enabled.jsp?pubId=<%= itProfilAM.getId() %>"><i class="icon icon-cross" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.assmatplugin.favoris.retirer", itProfilAM.getAuthor().getFullName()) %></span></button>
	                               </td>
	                               
	                               
	                            </tr>
	                            	                            	                            
	                            </jalios:foreach>
	                            
	                            
	                            
                            </tbody>
                        </table>
                    </article>

                    <div class="col-1 grid-offset ds44-hide-tinyToLarge"></div>

                    <aside class="col-4 ds44-js-aside-summary ds44-mb35" data-component-aside-summary-uuid="336ea052-d1f2-40d2-9e4b-4ca2ef26a77e">
                        <section class="ds44-box ds44-theme " data-component-aside-summary-uuid="d5683aa7-f8c9-4701-b7da-40649d9d0f82" style="position: static;">
                            <div class="ds44-innerBoxContainer ">
                            
                            
                                <%
                                String lienSelect = "#";
                                if(Util.notEmpty(panierSet)){
                                  lienSelect = channel.getUrl() + "assmat.pdf?type=list";
						        }
                                %>
                            
                                <h2 class="h4-like ds44-mb2"><%= glp("jcmsplugin.socle.recherche.selection") %></h2>                               
                                <ul class="ds44-list">
	                                <li class="ds44-docListElem">
	                                    <i class="icon icon-pdf ds44-docListIco" aria-hidden="true"></i>
	                                    <a href="<%= lienSelect %>" data-link-has-select="" target="_blank" title="PDF"><%= glp("jcmsplugin.assmatplugin.favoris.afficher-tableau") %></a>
	                                </li>   
                                </ul>                                                            
                            </div>
                        </section>
                    </aside>

                </div>
                </jalios:if>
                <jalios:default>
                    <%= glp("jcmsplugin.assmatplugin.favoris.pas-selection") %>
                </jalios:default>
                </jalios:select>
            </div>
        </div>
    </article>
</main>

<script>
function suppression(event) {
    event.closest("TR").remove();
}
</script>