<%-- *** PLUGINS ****************** --%>
<%@page import="com.jalios.util.Util"%>
<jalios:include target="EDIT_PUB_FORM_HEADER" />

<%
  String typeLabel = channel.getTypeLabel(formHandler.getPublicationClass(),formHandler.getWorkspace(), userLang); 
  request.setAttribute("title", typeLabel); 
  
  String forcedRedirect = (String)request.getAttribute("forcedRedirect");

  Category currentCategory = (Category)request.getAttribute(PortalManager.PORTAL_CURRENTCATEGORY);  
  String formAction;
  
  if(Util.notEmpty(forcedRedirect)){
	  formAction = forcedRedirect;
  }else{
	  if (formInPortal) {
	    formAction = currentCategory.getDisplayUrl(userLocale);
	  } else {
	    formAction = "types/" + Util.getClassShortName(formHandler.getPublicationClass()) +"/editForm" + Util.getClassShortName(formHandler.getPublicationClass()) + ".jsp";
	  } 
  }
  
  
  Member realLoggedMember = loggedMember;
  request.setAttribute("loggedMember", formHandler.getAvailableAuthor());
  request.setAttribute("realLoggedMember", realLoggedMember);
%>

<%-- -- FORM -------------------------------------------- --%>
<jalios:query name='__memberSet' dataset='<%= channel.getDataSet(Member.class) %>' comparator='<%= Member.getNameComparator() %>'/>
<% request.setAttribute("formMemberSet", __memberSet); %>
<jalios:query name='__groupSet' dataset='<%= channel.getDataSet(Group.class) %>' comparator='<%= Group.getNameComparator() %>'/>
<% request.setAttribute("formGroupSet", __groupSet); %>

<% int formElementCount  = 0; %>
<form class="personal-form" action='<%= formAction %>' method='post' name='editForm' accept-charset="UTF-8"  enctype="multipart/form-data">
<div>
    <div class="box-area form-area" style="margin-left: auto; margin-right: auto;">
      <p class="form-required-text bold"><%= glp("jcmsplugin.assmatplugin.form.requiredText.question") %></p>
      <p class="form-required-text"><%= glp("plugin.tools.form.requiredText") %></p>
            



<% request.setAttribute("formElementCount", new Integer(formElementCount)); %>
<% request.setAttribute("formHandler", formHandler); %>
