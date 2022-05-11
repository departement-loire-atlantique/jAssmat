 <%@page import="com.jalios.util.Util"%>
<%@page import="generated.Disponibilite"%>
<%@page import="java.util.List"%>
<%@page import="com.jalios.jcms.Member"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="generated.ProfilASSMAT"%>
<%@page import="com.jalios.jcms.Channel"%>
<%@ taglib uri="jcms.tld" prefix="jalios" %>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%String idMember= request.getParameter("id");
 Member mbr = Channel.getChannel().getMember(idMember);
 if(Util.notEmpty(mbr)){
 ProfilASSMAT profil = ProfilManager.getInstance().getProfilASSMAT(mbr);
 if(Util.notEmpty(profil)){
 List<Disponibilite> listeDispo = ProfilManager.getInstance().getDisponibilitesList(mbr, null);

 %>
<fieldset>
          <legend class="formLabel"><trsb:glp key="PROFILAM-BACK-HTML-TITLE"></trsb:glp></legend>
          
            
            <div class="formInfo">
            
             <trsb:glp key="PROFILAM-BACK-HTML"></trsb:glp> <jalios:edit id="<%=profil.getId() %>"  /> 
            </div>
            
            <jalios:if predicate="<%= Util.notEmpty(listeDispo) %>">
            <div class="formInfo">
            
             <trsb:glp key="DISPONIBILITES-BACK-HTML"></trsb:glp> 
             <ul> 
                <jalios:foreach name="itDispo" type="Disponibilite" collection="<%= listeDispo %>">
                    <li><%= itDispo.getTitle(Channel.getChannel().getCurrentUserLang()) %><jalios:edit data="<%= itDispo %>" /></li>
                </jalios:foreach>
             </ul>
            </div>            
            </jalios:if>
                            
</fieldset>
<%}
 }
%>