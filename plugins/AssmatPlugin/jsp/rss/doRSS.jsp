<% request.setAttribute("ContentType", "text/xml; charset=UTF-8"); %>
<%@ page contentType="text/xml; charset=UTF-8"%>
<%
	
%><%@ include file='/jcore/doInitPage.jsp'%>
<%
	
%><%!public String renderPubAbstract(Publication pub, Locale userLocale) {
		String userLang = I18nUtil.getLanguageKey(userLocale);
		TypeFieldEntry fieldEntry = channel.getTypeAbstractFieldEntry(pub.getClass());
		if (fieldEntry != null && fieldEntry.isFieldWiki()) {
			String baseUrl = ServletUtil.getBaseUrl(channel.getCurrentServletRequest());
			return WikiRenderer.wiki2html(pub.getAbstract(userLang), userLocale, new WikiRenderingHints("wiki feed", false, baseUrl));
		}
		if (fieldEntry != null && fieldEntry.isWysiwyg()) {
			String txt = pub.getAbstract(userLang);
			return JcmsUtil.convertUri2Url(txt, ServletUtil.getBaseUrl(channel.getCurrentServletRequest()));
		}
		return pub.getAbstract(userLang);
	}%>
<%
	Set<Publication> publications = new TreeSet<Publication>(Publication.getPdateComparator());
	String catActualites = "r1_58216";
	
	%>
	<jalios:query name="newsPubs" dataset="<%= channel.getDataSet(News.class) %>" cids="<%= catActualites %>"/>
	<jalios:query name="directoryPubs" dataset="<%= channel.getDataSet(Directory.class) %>" cids="<%= catActualites %>"/>
	<%
	
	publications = Util.unionSet(publications, newsPubs);
	publications = Util.unionSet(publications, directoryPubs);

	String contextLink = "";

%>
<rss version="2.0">
<channel>
	<title>assmat.loire-atlantique.fr</title>
	<link><%=ServletUtil.getBaseUrl(request)%></link>
	<language><%=channel.getLanguage()%></language>
	<description></description>
	<lastBuildDate><%=Util.formatRfc822Date(new Date())%></lastBuildDate>
	<jalios:foreach name="itPub" type="Publication"
		collection="<%=publications%>" counter="count">
		<item> <guid><%=ServletUtil.getBaseUrl(request)%><jalios:url data='<%=itPub%>' /><%= contextLink %></guid>
		<title><%=XmlUtil.normalize(itPub.getTitle(userLang))%></title>

		<link><%=ServletUtil.getBaseUrl(request)%><jalios:url data='<%=itPub%>' /><%= contextLink %></link>
		<description><%=XmlUtil.normalize(renderPubAbstract(itPub, userLocale))%></description>
		<pubDate><%=Util.formatRfc822Date(itPub.getCdate())%></pubDate> </item>
	</jalios:foreach>
</channel>
</rss>