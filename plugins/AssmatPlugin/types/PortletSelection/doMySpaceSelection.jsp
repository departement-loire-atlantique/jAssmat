  <%@ include file='/jcore/doInitPage.jspf'                    %><%
%><%@ include file='/jcore/portal/doPortletParams.jspf'        %><% 
%><%@ include file='/types/PortletSelection/doSelection.jspf' %><% 

  PortletSelection selection = (PortletSelection) portlet;
  PortalElement    child     = (PortalElement) Util.getObject(selection.getChildren(),0,null);
%>

<main role="main" id="content">
    <jalios:include target="SOCLE_ALERTE"/>
    
    <section class="ds44-container-large ds44--xxl-padding-b">
        <div class="ds44-mt3 ds44--xl-padding-t">
            <div class="ds44-inner-container">
                <div class="grid-12-medium-1 grid-12-small-1">

                    <aside class="col-4 ds44-hide-tiny-to-medium">
                        <section class="ds44-box ds44-theme">
                            <jalios:include id="<%= selection.getChildren()[0].getId() %>" />
                        </section>
                    </aside>
                    
                    <div class="col-1 grid-offset ds44-hide-tinyToLarge"></div>
                    
                    <article class="col-7">
                        <%@ include file='/jcore/portal/doIncludePortlet.jspf'%>
                    </article>
                </div>
            </div>
        </div>
    </section>
</main>