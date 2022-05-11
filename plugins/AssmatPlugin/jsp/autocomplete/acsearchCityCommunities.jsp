<%@page import="fr.cg44.plugin.corporateidentity.tools.ASDUtil"%>
<%@page import="com.jalios.jcms.analytics.*"%><%
%><%@ include file="/jcore/doInitPage.jsp" %><%
%><%@ page import="com.jalios.jcms.handler.QueryHandler" %><%
%><%@ page import="fr.cg44.plugin.tools.comparator.NameAutoCompleteComparator" %><%
	int maxResultShow = 10;
	String typeAutoComplet = "City";
	String[] searchedFields = getStringParameterValues("searchedFields",HttpUtil.ALPHANUM_REGEX);
	
	String[] cids=getStringParameterValues("cids",HttpUtil.ALPHANUM_REGEX);
	String txtSearch=getUntrustedStringParameter("autocomplete", "");
	Boolean activeBorderingCities = getBooleanParameter("activeBorderingCities", false);
	Boolean activeCids = getBooleanParameter("activeCids", false);

	//Recherche sur le titre par d�faut.
	String[] tabSearchedFields = new String[]{com.jalios.jcms.search.LucenePublicationSearchEngine.TITLE_FIELD};
	if(searchedFields!=null &&searchedFields.length>0){
		tabSearchedFields=searchedFields;
	}
	
	request.setAttribute("jcms.live-search",true);
	boolean isTopBarSearch = getBooleanParameter("topbarSearch",false);

%><jalios:include target="LIVE_SEARCH_INIT" /><%
	
	boolean refineWorkspace = false;
	
	//Publication Search
	QueryHandler qh = new QueryHandler();
	qh.setText(txtSearch);
	qh.setSearchedFields(tabSearchedFields);
	if(cids!=null && cids.length>0 && activeCids){
		qh.setCids(cids);
		qh.setCatMode("and");
	}
	qh.setLoggedMember(loggedMember);
	qh.setWorkspace(refineWorkspace ? workspace : null);
	qh.setCheckPstatus(true);
	qh.setSort("relevance");
	  qh.setTypes(new String[]{typeAutoComplet});

	Set<Publication> pubSet = new TreeSet<Publication>(new NameAutoCompleteComparator(txtSearch));	  
	pubSet.addAll(qh.getResultSet());  
	
	//Reset searched fields for query.jsp link
	qh.setSearchedFields(new String[]{});
    
	boolean hasResults = Util.notEmpty(pubSet) || Util.toBoolean("jcms.live-search.hasResults",false);
	
	//Clean 
	request.removeAttribute("jcms.live-search");
	
%><div class="typeahead-menu noTooltipCard typeahead-search ajax-refresh-div<%= isTopBarSearch ? " typeahead-search-topbar" : ""%>" ><%  
	%>
	
	<span role="status" aria-live="polite" class="helper-accessible" style="position: absolute; clip: rect(1px, 1px, 1px, 1px);">
		<jalios:select>
			<jalios:if predicate="<%= pubSet.size() == 0 %>">
				<%=  glp("plugin.tools.facetedSearch.autocomplete.accesible.aria.empty")  %>
			</jalios:if>
			<jalios:if predicate="<%= pubSet.size() <= maxResultShow %>">
				<%=  glp("plugin.tools.facetedSearch.autocomplete.accesible.aria", pubSet.size())  %>
			</jalios:if>
			<jalios:if predicate="<%= pubSet.size() > maxResultShow %>">
				<%=  glp("plugin.tools.facetedSearch.autocomplete.accesible.aria.more", maxResultShow, pubSet.size())  %>
			</jalios:if>
		</jalios:select>
	</span> 
	
	<ul class="dropdown-menu dropdown-menu-cities dropdown-menu-cg44"<%= hasResults ? "" : " style=\"display:none;\"" %>><%
	    if (Util.notEmpty(pubSet)) { 
    	Set<City> cities = channel.getAllDataSet(City.class);
    	boolean first = true;
      %><jalios:foreach collection="<%= pubSet %>" name="itPublication" type="City" max="<%= maxResultShow %>"><%
        if(activeBorderingCities){
        	Set<City> setCity = ASDUtil.getClosenessCitiesAndCommunities(itPublication);
        	City[] borderingCities = setCity.toArray(new City[setCity.size()]);
        	
	      String borderingCitiesArray = "[]";
	      if(Util.notEmpty(borderingCities)){	    	  
	    	  int tailleBorderingCities = 0;
	    	  for(int i=0; i<borderingCities.length; i++){
			    	if(borderingCities[i] != null) {
			    		tailleBorderingCities++;
			    	}
	    	  }	    	  
		      String[] borderingCitiesElements = new String[tailleBorderingCities];
		      int cpt = 0;
		      for(int i=0; i<borderingCities.length; i++){
		    	if(borderingCities[i] != null) { 
			        borderingCitiesElements[cpt] = new StringBuffer().append("['")
			                                                      .append(borderingCities[i].getCityCode())
			                                                      .append("','")
			                                                      .append(borderingCities[i].getTitle())
			                                                      .append("']")
			                                                      .toString();
			        cpt++;
			    }		    	
		      }
		      borderingCitiesArray = "["+Util.join (borderingCitiesElements, ",")+"]";
	      }
	      %><li <%= first ? "class=\"active\"" : "" %> data-value="<%= encodeForHTMLAttribute(itPublication.getTitle(userLang)) %>"><%
	       %><a class="citiesAutocomplete"
	       data-cityCode="<%= itPublication.getCityCode() %>"
	       data-cityId="<%= itPublication.getId() %>"
	       data-cityName="<%= itPublication.getTitle() %>"
	       data-borderingCities="<%= borderingCitiesArray %>"
	       title="<%= encodeForHTMLAttribute(itPublication.getTitle(userLang)) %>" href="#"><%
	         %><%= encodeForHTMLAttribute(itPublication.getTitle()) %><%
	       %></a><%
	      %></li><%
        } else {
          %><li <%= first ? "class=\"active\"" : "" %> data-value="<%= encodeForHTMLAttribute(itPublication.getTitle(userLang)) %>"><%
           %><a class="citiesAutocomplete"
           data-cityCode="<%= itPublication.getCityCode() %>"
	       data-cityId="<%= itPublication.getId() %>"
	       data-cityName="<%= itPublication.getTitle() %>"
           title="<%= encodeForHTMLAttribute(itPublication.getTitle(userLang)) %>" href="#"><%
             %><%= encodeForHTMLAttribute(itPublication.getTitle()) %><%
           %></a><%
          %></li><%
        }
	      if(first){
	        first = !first;
	      }
      %></jalios:foreach><%     
    }
    if(pubSet.size()>maxResultShow){
    	 %><li><%
    	   %><span style="padding-left: 15px;">
    	     <%=pubSet.size()-maxResultShow %> <%= glp("plugin.tools.autocomplete.moreresult") %><%
    	   %></span><%
    	   %></li><%
    }
   %></ul><%
%></div><%
EventData eventData = new EventData(AnalyticsManager.AC_SEARCH,loggedMember, null, null, request); 
Map<String, String> contextMap = AnalyticsManager.generateAccessContextMap(request, eventData);
String statQueryString = qh.getQueryString();
//log an event data 
 if(statQueryString != null){
   contextMap.put(AnalyticsManager.QUERYSTRING,statQueryString);
 }
 AnalyticsManager.processQueries(contextMap);
 eventData.getContext().putAll(contextMap);
 AnalyticsManager.appendEventData(eventData, request);

%>