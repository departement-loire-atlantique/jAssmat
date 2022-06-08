<%@ include file='/jcore/doInitPage.jsp' 
%><%@ page import="fr.cg44.plugin.tools.AccessibilityLinks"  
%><%@ include file='/jcore/portal/doPortletParams.jsp' 
%><%@ include file='/types/PortletNavigate/doInitPortletNavigate.jsp' %><%
jcmsContext.addCSSHeader("plugins/EServicePlugin/css/types/PortletNavigate/doPortletNavigateMenu.css");
jcmsContext.addCSSHeader("plugins/AssmatPlugin/css/plugin.css");
jcmsContext.addJavaScript("plugins/EServicePlugin/js/menuMobile.js");
%><%
	Set navigateSet = new TreeSet(Category.getOrderComparator(userLang));
	navigateSet.addAll(rootCategory.getChildrenSet());
	
	if (Util.isEmpty(navigateSet) && box.getHideWhenNoResults()){
		request.setAttribute("ShowPortalElement",Boolean.FALSE);
    return;
	}
	
	String nofollow = box.getNavigatePortlet() ? "" : "rel='nofollow'";

	int cpt = 0;
	
	//----element permetant le calcule de l'url----
	// Liste des classes autorisés => évite d'avoir des portlets
	 String[] defaultValue = new String[0];
	 List<String> classAllowed = Arrays.asList(channel.getStringArrayProperty("plugin.tools.welcomeSection.allowedType", defaultValue));
	// Portail d'affichage d'une publication
	 String portalId = channel.getProperty("plugin.tools.fullDisplayPortal");
	 StringBuffer params = new StringBuffer();
	 if(Util.notEmpty(portalId)) params.append("?portal=").append(portalId);
	String[] contentInFullPage = Channel.getChannel().getStringArrayProperty("plugin.corporateidentity.menuWelcomeSection.fullPage.contents", new String[]{}); 
	%><%@ include file='/plugins/CorporateIdentityPlugin/jsp/menu/menuWelcomeSection.jspf' %><%
	//----Fin elements permetant le calcule de l'url----		
			
%><div role="navigation" class="menu">
	<div class="container hidden-phone">
		<div class="row">
			<div class="span12 ">
				<ul class="unstyled" tabindex="-1" id="<%=AccessibilityLinks.NAVIGATION_ID%>">
										
					<jalios:foreach collection='<%= navigateSet %>' type='Category' name='itCategory'>
						<jalios:if predicate='<%= itCategory.canBeReadBy(loggedMember , true, true) %>'>
						<jalios:select>
							<jalios:if predicate="<%= itCategory==currentCategory || currentCategory.getAncestorList().contains(itCategory)%>">
								<li class="menu-item selected" title="<%= itCategory.getName(userLang) %> - actif">
							</jalios:if>
							<jalios:default>
								<li class="menu-item">
							</jalios:default>
						</jalios:select>
				  	<%
				  		String[] urlAndHasLink = getCategoryLink(itCategory, jcmsContext, classAllowed, contentInFullPage, params.toString(), false);
				  	 	String url = (String) urlAndHasLink[0];
				  	 	boolean hasLink = Boolean.parseBoolean(urlAndHasLink[1]);
				  	 	if(!hasLink){
				  	 		url =PortalManager.getUrlWithUpdateCtxCategories(itCategory , ctxCategories, request , !box.getNavigatePortlet());
				  	 	}
				  		%><a <%= nofollow %> href='<%= url %>' ><%
				  		
				  		if(cpt > 0){
				  	%>
				  	<jalios:if predicate="<%= Util.notEmpty(itCategory.getIcon()) %>">
				  	   <img class="img-deco-menu" src="<%= itCategory.getIcon() %>" alt=""/>
				  	</jalios:if>
				  	<div class='lib-top-menu <%= Util.notEmpty(itCategory.getExtraData("extra.Category.jcmsplugin.assmatplugin.menu")) ? "" : "menu-simple-ligne" %>'>	  	
				  	 <%= Util.notEmpty(itCategory.getExtraData("extra.Category.jcmsplugin.assmatplugin.menu")) ? itCategory.getExtraData("extra.Category.jcmsplugin.assmatplugin.menu").replaceAll("\n", "<br/>") : itCategory.getName() %>
				  	</div> 
				  	</a>
				  	<% 
				  		}else {
				  			%>
				  	  
				  			<img src="s.gif" class="picto-home" alt="<%= itCategory.getName(userLang) %>"/>
				  			</a>
				  			</li>
				  			<li class="menu-item-span">
				  			 <%= glp("jcmsplugin.assmatplugin.accueil.menu.ici") %> <img src="plugins/AssmatPlugin/img/fleche_verte.png" alt="">
				  			</li>
				  			<%
				  		} 
				  	%></li><%
				  	 cpt++; 
				  %></jalios:if>
				  </jalios:foreach>
				 </ul>
			</div>
		</div>
 		
	</div>
	<div class="row visible-phone">
		<div class="span12">
			<div class="menu-item searchphone" role="search">
				<a id="recherche" role="button" tabindex="0" href="#" onclick="return false;" title="<%=glp("cg44.plugin.eserviceplugin.menu.label.lien-recherche")%>">
			 	 	<img src="s.gif" class="picto-loupe" alt="<%=glp("cg44.plugin.eserviceplugin.menu.label.lien-recherche")%>"/>
			 	</a>
			 </div>
			 <div class="menu-item sous-menu">
		  		<a id="menu" <%= nofollow %> role="button" tabindex="0" href="#" onclick="return false;" title="<%=glp("cg44.plugin.eserviceplugin.menu.label.lien-menu")%>">
		  			<img src="s.gif" class="picto-menu" alt="<%=glp("cg44.plugin.eserviceplugin.menu.label.lien-menu")%>"/>
		  		</a>
		  	</div>
		  	<div class="menu-item partage" ><%
		  	    String idBlocpartage="blocpartageMenu";
          		%><a id="partage" class="dropdown-toggle" role="button" tabindex="0" href="#" onclick="return false;" title="<%= glp("cg44.plugin.eserviceplugin.menu.label.lien-partage") %>"><%
			      %><img class="picto-partage" alt="<%= glp("plugin.tools.headstall.share") %>" src="s.gif"><%
			   	%></a>
		  	</div>
		  	<div class="clear"></div>
		  	
		</div>
	</div>
	
</div>
<div  class="sous-menu hidden-tablet hidden-desktop">
 		<ul class="unstyled">
 			<%	%><jalios:foreach collection='<%= navigateSet %>' type='Category' name='itCategory'><% 
			%><jalios:if predicate='<%= itCategory.canBeReadBy(loggedMember , true, true) %>'><%
					String[] urlAndHasLink = getCategoryLink(itCategory, jcmsContext, classAllowed, contentInFullPage, params.toString(), false);
	  	 	String url = (String) urlAndHasLink[0];
	  	 	boolean hasLink = Boolean.parseBoolean(urlAndHasLink[1]);
	  	 	if(!hasLink){
	  	 		url =PortalManager.getUrlWithUpdateCtxCategories(itCategory , ctxCategories, request , !box.getNavigatePortlet());
	  	 	}
	  			%><li><a <%= nofollow %> href='<%= url %>' ><%
	  			%><%= itCategory.getName(userLang) %><%
	  			%></a></li>
		  	<%
		  	 cpt++; 
		  	%></jalios:if><%
	%></jalios:foreach>
	</ul>
</div>	
<div class="dropdown partage  hidden-tablet hidden-desktop">
		<%@include file="/plugins/ToolsPlugin/jsp/headstall/shareLinkGenerator.jspf" %><%
				String classUlShareSection="dropdown-menu share-dropdown";
		%><%@include file="\plugins\ToolsPlugin\jsp\socialNetworks\doShareSection.jspf" %>
</div>
<div class="clear"></div>
<!-- <div class="container"> -->

<!--</div> -->

