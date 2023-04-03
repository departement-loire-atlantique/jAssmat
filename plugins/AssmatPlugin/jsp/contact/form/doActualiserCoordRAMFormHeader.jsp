<%-- *** PLUGINS ****************** --%>
<%@page import="com.jalios.util.Util"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
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
	    formAction = ServletUtil.getResourcePath(request);
	  } else {
	    formAction = "types/" + Util.getClassShortName(formHandler.getPublicationClass()) +"/editForm" + Util.getClassShortName(formHandler.getPublicationClass()) + ".jsp";
	  }
  }
  
  
  Member realLoggedMember = loggedMember;
  //request.setAttribute("loggedMember", formHandler.getAvailableAuthor());
  //request.setAttribute("realLoggedMember", realLoggedMember);
%>

<%-- -- FORM -------------------------------------------- --%>
<jalios:query name='__memberSet' dataset='<%= channel.getDataSet(Member.class) %>' comparator='<%= Member.getNameComparator() %>'/>
<% request.setAttribute("formMemberSet", __memberSet); %>
<jalios:query name='__groupSet' dataset='<%= channel.getDataSet(Group.class) %>' comparator='<%= Group.getNameComparator() %>'/>
<% request.setAttribute("formGroupSet", __groupSet); %>
<% int formElementCount  = 0; %>
<form method="POST" action="<%=formAction%>" name="formAccueil" id="formAccueil">

    <p role="heading" aria-level="2"><trsb:glp key="MESSAGE-INTRO-FORMULAIRE-ACTUALISER_COORD_RAM" ></trsb:glp></p>

<% request.setAttribute("formElementCount", new Integer(formElementCount)); %>
<% request.setAttribute("formHandler", formHandler); %>
