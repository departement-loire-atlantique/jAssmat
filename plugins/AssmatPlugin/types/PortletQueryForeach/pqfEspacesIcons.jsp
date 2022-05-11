<%@page import="fr.cg44.plugin.corporateidentity.news.DirectoryFromNews"%><%
%><%@page import="com.jalios.jcms.handler.PagerHandler"%><%
%><%@ include file='/jcore/doInitPage.jsp'%><%
%><%@ include file='/jcore/portal/doPortletParams.jsp'%><%
%><% 
  PortletQueryForeach box = (PortletQueryForeach) portlet;
  jcmsContext.setTemplateUsage(TypeTemplateEntry.USAGE_DISPLAY_QUERY);

  jcmsContext.addCSSHeader("plugins/AssmatPlugin/css/plugin.css");
  
  
%>

<div class="espaces-icons-container">
  <%@ include file='/types/PortletQueryForeach/doQuery.jsp'%>
  <%@ include file='/types/PortletQueryForeach/doSort.jsp'%>
  
  <div class="icons-column">
    <%@ include file='/types/PortletQueryForeach/doForeachHeader.jsp'%>
      <% 
      WelcomeSection itSection = (WelcomeSection) itPub;
      Category itLinkedCategorySet = itSection.getFirstTargetCategory(channel.getDefaultAdmin());
      %>
      
      <div class="espaces-pages-box-display-container" style="border-color: <%= itLinkedCategorySet.getAvailableColor()%>">
        <jalios:link css="noTooltipCard category-icon"  data="<%= itLinkedCategorySet %>" title="<%= itLinkedCategorySet.getName()%>">
          <img src="<%= itLinkedCategorySet.getIcon() %>" alt="" />
          <h2>
            <jalios:if predicate='<%=Util.notEmpty(itSection.getLabelLine1())%>'><span><%=itSection.getLabelLine1()%></span></jalios:if>
            <jalios:if predicate='<%=Util.notEmpty(itSection.getLabelLine2())%>'><span><%=itSection.getLabelLine2()%></span></jalios:if>
          </h2>
        </jalios:link>
      </div>
      
    <%@ include file='/types/PortletQueryForeach/doForeachFooter.jsp'%>
    </div>
</div>

