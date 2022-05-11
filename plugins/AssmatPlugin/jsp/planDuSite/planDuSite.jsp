<%@ page contentType="text/html; charset=UTF-8" %><%
%><%@ include file='/jcore/doInitPage.jsp' %><%
	// CSS
	jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/planDuSite.css");

	Category principale = channel.getCategory(channel.getProperty("jcmsplugin.assmatplugin.navigation.principale"));
	Set<Category> catPrincipales = new TreeSet<Category>(Category.getDeepOrderComparator());
	catPrincipales.addAll(principale.getChildrenSet());

	Category transverse = channel.getCategory(channel.getProperty("jcmsplugin.assmatplugin.navigation.transverse"));
	Set<Category> catTransverse = new TreeSet<Category>(Category.getDeepOrderComparator());
	catTransverse.addAll(transverse.getChildrenSet());
%>
<div class="planDuSite-box-display-container">
	<ul class="planDuSite">
		<li class="firstLevel accordeon navPrincipale">
			<div class="titre">
				<a href="">
					<div class="backgroundArrow"><img class="arrow spr-chevron-blanc-off" src="s.gif" alt='' /></div>
					<%= principale.getName(userLang) %>
				</a>
			</div>
			<ul class="listeServices navPrincipale">
				<jalios:foreach name="service" type="Category" collection="<%= catPrincipales %>">
					<%
					String serviceColor = service.getColor();
					Set<Category> servicseLevel1 = new TreeSet<Category>(Category.getDeepOrderComparator());
					servicseLevel1.addAll(service.getChildrenSet());
					%>
					<li class='secondLevel <%= (servicseLevel1.size() > 0)? "accordeon" : "" %>'>
						<div class="titre">
							<div class="backgroundArrow" style="background-color:<%= serviceColor %>;"><img class="arrow spr-chevron-blanc-off" src="s.gif" alt=""/></div>
							<a class="serviceLink" alt="" title="<%= HttpUtil.encodeForHTMLAttribute(service.getName(userLang)) %>" href="<%= service.getDisplayUrl(userLocale) %>"><%= service.getName(userLang) %></a>
						</div>
						<ul class="secondLevelContent">
							<jalios:foreach name="serviceLevel1" type="Category" collection="<%= servicseLevel1 %>">
								<%
								Set<Category> servicesLevel2 = new TreeSet<Category>(Category.getDeepOrderComparator());
								servicesLevel2.addAll(serviceLevel1.getChildrenSet());
								%>
								<li>
									<a class="linkLevel1" alt="" title="<%= HttpUtil.encodeForHTMLAttribute(serviceLevel1.getName(userLang)) %>" href="<%= serviceLevel1.getDisplayUrl(userLocale) %>"><%= serviceLevel1.getName(userLang) %></a>
									<ul>
									<jalios:foreach name="serviceLevel2" type="Category" collection="<%= servicesLevel2 %>">
										<%
										Set<Category> servicesLevel3 = new TreeSet<Category>(Category.getDeepOrderComparator());
										servicesLevel3.addAll(serviceLevel2.getChildrenSet());
										%>
										<li class="liLinkLevel2">
											<a class="linkLevel2" alt="" title="<%= HttpUtil.encodeForHTMLAttribute(serviceLevel2.getName(userLang)) %>" href="<%= serviceLevel2.getDisplayUrl(userLocale) %>"><%= serviceLevel2.getName(userLang) %></a>
											<ul class="lastLevel">
											<jalios:foreach name="serviceLevel3" type="Category" collection="<%= servicesLevel3 %>">
												<li><a alt="" title="<%= HttpUtil.encodeForHTMLAttribute(serviceLevel3.getName(userLang)) %>" href="<%= serviceLevel3.getDisplayUrl(userLocale) %>"><%= serviceLevel3.getName(userLang) %></a></li>
											</jalios:foreach>
											</ul>
										</li>
									</jalios:foreach>
									</ul>
								</li>
							</jalios:foreach>
						</ul>
					</li>
				</jalios:foreach>
			</ul>
		</li>
		<li class="firstLevel accordeon navTransverse">
			<div class="titre">
				<a href="">
					<div class="backgroundArrow"><img class="arrow spr-chevron-blanc-off" src="s.gif" alt='' /></div>
					<%= transverse.getName(userLang) %>
				</a>
			</div>
			<ul class="listeServices navTransverse">
				<jalios:foreach name="service" type="Category" collection="<%= catTransverse %>">
					<%
					String serviceColor = service.getColor();
					Set<Category> servicseLevel1 = new TreeSet<Category>(Category.getDeepOrderComparator());
					servicseLevel1.addAll(service.getChildrenSet());
					%>
					<li class='secondLevel <%= (servicseLevel1.size() > 0)? "accordeon" : "" %>'>
						<div class="titre">
							<div class="backgroundArrow" style="background-color:<%= serviceColor %>;"><img class="arrow spr-chevron-blanc-off" src="s.gif" alt=""/></div>
							<a class="serviceLink" alt="" title="<%= HttpUtil.encodeForHTMLAttribute(service.getName(userLang)) %>" href="<%= service.getDisplayUrl(userLocale) %>"><%= service.getName(userLang) %></a>
						</div>
						<ul class="secondLevelContent">
							<jalios:foreach name="serviceLevel1" type="Category" collection="<%= servicseLevel1 %>">
								<%
								Set<Category> servicesLevel2 = new TreeSet<Category>(Category.getDeepOrderComparator());
								servicesLevel2.addAll(serviceLevel1.getChildrenSet());
								%>
								<li>
									<a class="linkLevel1" alt="" title="<%= HttpUtil.encodeForHTMLAttribute(serviceLevel1.getName(userLang)) %>" href="<%= serviceLevel1.getDisplayUrl(userLocale) %>"><%= serviceLevel1.getName(userLang) %></a>
									<ul>
									<jalios:foreach name="serviceLevel2" type="Category" collection="<%= servicesLevel2 %>">
										<%
										Set<Category> servicesLevel3 = new TreeSet<Category>(Category.getDeepOrderComparator());
										servicesLevel3.addAll(serviceLevel2.getChildrenSet());
										%>
										<li class="liLinkLevel2">
											<a class="linkLevel2" alt="" title="<%= HttpUtil.encodeForHTMLAttribute(serviceLevel2.getName(userLang)) %>" href="<%= serviceLevel2.getDisplayUrl(userLocale) %>"><%= serviceLevel2.getName(userLang) %></a>
											<ul class="lastLevel">
											<jalios:foreach name="serviceLevel3" type="Category" collection="<%= servicesLevel3 %>">
												<li><a alt="" title="<%= HttpUtil.encodeForHTMLAttribute(serviceLevel3.getName(userLang)) %>" href="<%= serviceLevel3.getDisplayUrl(userLocale) %>"><%= serviceLevel3.getName(userLang) %></a></li>
											</jalios:foreach>
											</ul>
										</li>
									</jalios:foreach>
									</ul>
								</li>
							</jalios:foreach>
						</ul>
					</li>
				</jalios:foreach>
			</ul>
		</li>
	</ul>
</div>

<jalios:javascript>
	var j = jQuery;
	var speed = "fast";
	var deplierTitle = '<%= glp("plugin.corporateidentity.plandusite.deplier.title") %>';
	var replierTitle = '<%= glp("plugin.corporateidentity.plandusite.replier.title") %>';
	var deplierAlt = '<%= glp("plugin.corporateidentity.plandusite.deplier.alt") %>';
	var replierAlt = '<%= glp("plugin.corporateidentity.plandusite.replier.alt") %>';
	j(document).ready(function(){
		// Cache tous les sous-menus au chargement de la page
		hideAll();

		j('.accordeon > .titre').on('click', function(){
			var parent = j(this).parent();

			var txt = j(this).children('a').text().trim() ;

			if (parent.hasClass("open")) {
				j(this).children('a').attr('title', deplierTitle + " : " + txt);
				j(this).next('ul').slideUp(speed).find('.secondLevelContent').slideUp(speed);
				parent.removeClass("open");
				parent.find('.accordeon').removeClass('open');
			} else {
				j(this).children('a').attr('title', replierTitle + " : " + txt);
				j(this).next('ul').slideDown(speed);
				parent.addClass("open");
				if (parent.hasClass("navPrincipale")) {
					showNav();
				}
				if (parent.hasClass("navTransverse")) {
					showTrans();
				}
			}
			return false;
		});
	});

	function hideAll(){
		j('.listeServices, .secondLevelContent').slideUp(speed);
		j('.listeServices, .secondLevelContent').prev('.titre').children('a').each(function() {
			txt = j(this).text().trim() ;
			console.log(txt);
			j(this).attr('title', deplierTitle + " : " + txt);
		});
	}

	function showNav(){
		j('.navPrincipale, .secondLevelContent').slideDown(speed);
		j('.navPrincipale, .secondLevelContent').prev('.titre').children('a').each(function() {
			txt = j(this).text().trim() ;
			console.log(txt);
			j(this).attr('title', replierTitle + " : " + txt);
		});
	}

	function showTrans(){
		j('.navTransverse, .secondLevelContent').slideDown(speed);
		j('.navTransverse, .secondLevelContent').prev('.titre').children('a').each(function() {
			txt = j(this).text().trim() ;
			console.log(txt);
			j(this).attr('title', replierTitle + " : " + txt);
		});
	}
</jalios:javascript>
