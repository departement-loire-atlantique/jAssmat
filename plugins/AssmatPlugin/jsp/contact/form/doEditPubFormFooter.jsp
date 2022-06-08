	<tr class='submit'>
	  <td><%  
	    %><%@ include file='/plugins/CorporateIdentityPlugin/jsp/style/getBackgroundStyle.jspf' %>
	    <p class="submit">
	      <label for="submit">
	      	<input type='submit' id='submit' name='opCreate' value='<%= glp("plugin.tools.form.validate") %>' class='submitButton' />
	      	<span class="input-box" <%= backgroundStyle %>><span class="spr-recherche-ok"></span></span>
	      </label>
	      
	      <jalios:if predicate='<%= formInPortal %>'>
	        <input type="hidden" name="noSendRedirect" value='true' />
	        <%= printEnabledHiddenParams(request, Arrays.asList(new String[]{"portal","id","cid","jsp"})) %>
	      </jalios:if>
	    </p>
	  </td>
	</tr>
</table>

<% if (formHandler.showWFRole()) { %>

          <% 
   Workflow wf = formHandler.getWorkflow();
   WFState state = wf.getInitState();
   boolean showRoles = false;
   if (state.hasOpenRole(workspace)) {
%>
    <fieldset><legend><%= glp("ui.work.form.tab.wf-role") %></legend>
      <table class='layout'>  
        <jalios:foreach collection="<%= state.getRoleSet() %>" name='wfRole' type='WFRole'>
         <% WKRole wkRole = workspace.getRole(wfRole); %>
         <% if (wkRole != null && WKRole.isOpen(wkRole)) {
              showRoles = true;
              Set<WFState> wfStateSet = wf.getWorkStateSet(wfRole);
              if (Util.notEmpty(wfStateSet)) { %>
        <tr id="role_choice_<%= wfRole.getId() %>">
          <td class="txt-left">
            <img src='images/jalios/icons/role.gif' class='icon' alt='' />
            <% if (WKRole.OPEN_MODE_REQUIRED.equals(wkRole.getOpenMode())) { %><span class="formMandatory">* </span><% } %>
            <span class='formLabel'><%= wfRole.getName(userLang) %></span>
          </td>
          <td class='formInfo'>
            <% 
              Publication currentPub = formHandler.getPublication();
              Class pubClass = formHandler.getPublicationClass();            
              Set allWorkerSet = JcmsUtil.select(wkRole.getWorkerSet(currentPub, true, pubClass), null, new Member.NameComparator());
            %>
            <input type="hidden" name="roleId" value="<%= wfRole.getId() %>" />
            <jalios:widget  editor          ='<%= AbstractWidget.UI_EDITOR_ENUMERATE_COMBO %>'
                            widgetName      ='<%= "roleMbr_" + wfRole.getId() %>'
                            value           ='<%= null %>'
                            widgetAddCount  ='<%= 1 %>'
                            enumValues      ='<%= allWorkerSet %>' 
            />
          </td>
        </tr>
        <% } }%>
        </jalios:foreach>
      </table>
    </fieldset>
<% } %>
<% } %>
</div>
    
<%-- *** PLUGINS ***************************** --%>
<jalios:include target="EDIT_PUB_FORM_FOOTER" />    
    
<%-- BUTTONS --------------------------------------------------------------- --%> 
<%-- Bouton submit déplacé en haut du fichier --%>
</div>
</form>

<% request.setAttribute("loggedMember", realLoggedMember); %>
