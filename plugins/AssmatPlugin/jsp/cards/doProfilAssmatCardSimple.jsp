<%@page import="fr.cg44.plugin.assmat.PointAssmat"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="java.lang.reflect.Field"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.cg44.plugin.socle.infolocale.entities.DateInfolocale"%>
<%@page import="fr.cg44.plugin.socle.infolocale.util.InfolocaleUtil"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="fr.cg44.plugin.socle.SocleUtils"%>
<%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%>
<%@ include file='/jcore/doInitPage.jspf' %><%
%><%@ page import="com.jalios.jcms.taglib.card.*" %><%
%><%@ include file='/jcore/media/mediaTemplateInit.jspf' %><%
%><%

if (data == null) {
  return;
}

ProfilASSMAT pub = (ProfilASSMAT) data;
Member mbr = pub.getAuthor(); 
SolisManager solisMgr = SolisManager.getInstance();
AssmatSolis assmatSolis = Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(pub.getNum_agrement()));

ProfilManager profilMgr = ProfilManager.getInstance();
Date dateModifDispo = profilMgr.getDateModifDispo(mbr);

NumberFormat nf = new DecimalFormat("0.#");

PointAssmat pointUser = (PointAssmat) request.getAttribute("userLocation");
PointAssmat pointAssmat = (PointAssmat) request.getAttribute("point");

boolean isDomicile = pointAssmat.isDomicile();
double distance = -1;

if(Util.notEmpty(pointAssmat) && Util.notEmpty(pointUser)){
  distance = AssmatUtil.getDistance((double)pointAssmat.getLatitude(),(double) pointAssmat.getLongitude(),(double) pointUser.getLatitude(), (double)pointUser.getLongitude()); 
}



%>

<section class="ds44-card ds44-js-card ds44-card--contact ds44-bgGray  ">
    <div class="ds44-card__section">
        
<!--         <p class="ds44-cardSelect pa0 ma0"> -->
<!--             <button class="pas" type="button" aria-describedby="1"><i class="icon icon-star-empty" aria-hidden="true"></i><span class="visually-hidden">Sélection</span></button> -->
<!--         </p> -->
        <div class="ds44-innerBoxContainer">
            <p role="heading" aria-level="2" class="h4-like ds44-cardTitle" id="1"><a href='<%= pub.getDisplayUrl(userLocale)  %>' class="ds44-card__globalLink"><%= mbr.getFullName() %></a></p>
            <hr class="mbs" aria-hidden="true"/>
            
            
            
            <!-- Adresse -->                            
            <jalios:if predicate="<%= Util.notEmpty(assmatSolis) %>">
                               
                <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-marker ds44-docListIco" aria-hidden="true"></i>
                        
                <!-- Distance -->
	            <jalios:if predicate="<%= Util.notEmpty(pointAssmat) && distance != -1 %>">
	                <%= nf.format(distance) %> km de l'adresse recherchée</br>
	            </jalios:if>
            
                <%-- adresse du domicile --%>
                <jalios:if predicate="<%= assmatSolis.getExerceDomicile() != null && assmatSolis.getExerceDomicile() && isDomicile %>">               
                    <jalios:if predicate="<%= Util.notEmpty(assmatSolis.getAdresseDomicile()) %>">                   
                        
                        <%= assmatSolis.getAdresseDomicile() %></br>                    
               
	                    <jalios:if predicate="<%= Util.notEmpty(assmatSolis.getComplementAdresse()) %>">
	                        <%= assmatSolis.getComplementAdresse() %></br>
	                    </jalios:if>
	                    
                    </jalios:if>
                             
            
	                <!-- Code postale + city -->
	                <jalios:if predicate="<%= Util.notEmpty(assmatSolis.getCpDomicile()) %>">
	                    <%= assmatSolis.getCpDomicile() %> <%= assmatSolis.getCommuneDomicile() %>
	                </jalios:if>           
                </jalios:if>


                <%-- Adresse MAM --%>
                <jalios:if predicate="<%= assmatSolis.getExerceMam() != null && assmatSolis.getExerceMam() && !isDomicile %>">               
                    <jalios:if predicate="<%= Util.notEmpty(assmatSolis.getNomMam()) %>">
                        <%= assmatSolis.getNomMam() %></br>
                    </jalios:if>
                    
                    <jalios:if predicate="<%= Util.notEmpty(assmatSolis.getAdresseMam()) %>">
                        <%= assmatSolis.getAdresseMam() %>
                    </jalios:if>
                    
                    <jalios:if predicate="<%= Util.notEmpty(assmatSolis.getCpMam()) %>">
                        <%= assmatSolis.getCpMam() %> <%= assmatSolis.getCommuneMam() %>
                    </jalios:if>                
                </jalios:if>
                
                
                </p>

            </jalios:if>
            

            <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-date ds44-docListIco" aria-hidden="true"></i><%=pointAssmat.getInfoPoint() %></p>           



            <%    
		    Boolean hasDispo = profilMgr.hasDispo(mbr);
		    // Si l'assmat a des dispo ou accepte d'etre contacter meme sans dispo
		    Boolean showContactDispo = !pub.getAfficherContactUniquementSiD() || hasDispo;
		    %>
		    
		    <jalios:if predicate="<%= showContactDispo  %>">
		    
			    <!-- Tel fixe -->
			    <jalios:if predicate="<%= AssmatUtil.getBooleanFromString(pub.getVisbiliteTelephoneFixe()) && Util.notEmpty(pub.getTelephoneFixe()) %>">		    		        
			        <jalios:if predicate="<%= Util.notEmpty(pub.getTelephoneFixe()) %>">
			          <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-phone ds44-docListIco" aria-hidden="true"></i><%=pub.getTelephoneFixe() %></p>
			        </jalios:if>		   	                         
	            </jalios:if>
	            
	            
	            <jalios:if predicate="<%= Util.notEmpty(pub.getAuthor()) %>">
	            
		            <!-- Tel mobile -->
	                <jalios:if predicate="<%= AssmatUtil.getBooleanFromString(pub.getVisibiliteTelephonePortable()) && Util.notEmpty(pub.getAuthor().getMobile()) %>">
	                   <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-phone ds44-docListIco" aria-hidden="true"></i><%=pub.getAuthor().getMobile() %></p>
	                </jalios:if>
	                	                
	                <!--  Courriel -->
	                <jalios:if predicate="<%= AssmatUtil.getBooleanFromString(pub.getVisibiliteAdresseEmail()) && Util.notEmpty(pub.getAuthor().getEmail()) %>">
	                   <!-- TODO -->
<%-- 	                   <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-mail ds44-docListIco" aria-hidden="true"></i><%= pub.getAuthor().getEmail() %></p> --%>
	                </jalios:if>
	                
	            </jalios:if>
            
            </jalios:if>
        </div>
        <i class="icon icon-arrow-right ds44-cardArrow" aria-hidden="true"></i>
    </div>
</section>
