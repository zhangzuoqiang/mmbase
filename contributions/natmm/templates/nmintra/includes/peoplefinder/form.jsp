<% 
// **************** people finder: right bar with the form *****************

if(!action.equals("print")) {
    SearchUtil su = new SearchUtil();
    %>
    <%@include file="../whiteline.jsp" %>
    <form method="POST" action="<%= ph.createPaginaUrl(paginaID,request.getContextPath()) %>" name="smoelenboek">
      <input type="hidden" name="name" value="<%= nameId %>">
      <table cellpadding="0" cellspacing="0"  align="center">
        <tr>
          <td class="bold">&nbsp;<span class="light">Voornaam:</span></td>
          <td class="bold"><input type="text" style="width:103px;" name="firstname" value="<%= firstnameId %>">
            &nbsp;<br><div align="right"><span class="light"><% if(nameId.equals("")){ %>en<% } else { %>of<% } %></span></div>
          </td>
        </tr>
        <tr>
          <td class="bold">&nbsp;<span class="light">Achternaam:</span>&nbsp;</td>
          <td class="bold"><input type="text" style="width:103px;" name="lastname" value="<%= lastnameId %>">
             <%  
             if(showAllSelect || showProgramSelect || iRubriekLayout==NMIntraConfig.SUBSITE1_LAYOUT) {
                %>&nbsp;<br><div align="right"><span class="light">en</span></div><%          
             }
             %>
          </td>
        </tr>
        <%
        if(showAllSelect) { 
          %>
          <tr>
            <td class="bold">&nbsp;<span class="light">En verder:</span>&nbsp;</td>
            <td class="bold"><input type="text" style="width:103px;" name="description" size="13" value="<%= descriptionId %>">
              &nbsp;<br><div align="right"><span class="light">en</span></div>
            </td>
          </tr>
          <tr>
            <td colspan="2" class="bold">
              <select name="department" style="width:195px;">
                <option value="default" <%  if(departmentId.equals("default")) { %>SELECTED<% } 
                    %>>alle afdelingen en regio's
                <mm:list path="afdelingen" orderby="afdelingen.naam" directions="UP"
                    constraints="<%= su.sAfdelingenConstraints %>"
                  ><mm:field name="afdelingen.number" jspvar="departments_number" vartype="String" write="false"
                  ><mm:field name="afdelingen.naam" jspvar="departments_name" vartype="String" write="false"
                  ><option value="<%= departments_number %>" <%   if(departments_number.equals(departmentId))  { %>SELECTED<% } 
                      %>><%= departments_name 
                  %></mm:field
                  ></mm:field
                ></mm:list
              ></select>&nbsp;<br><div align="right"><span class="light">en</span></div>
            </td>
          </tr>
          <tr>
            <td colspan="2" class="bold">
              <select name="location" style="width:195px;">
                <option value="default" <%  if(programId.equals("default")) { %>SELECTED<% } 
                    %>>alle lokaties
                <mm:list path="locations" orderby="locations.naam" directions="UP"
                  ><mm:field name="locations.number" jspvar="locations_number" vartype="String" write="false"
                  ><mm:field name="locations.naam" jspvar="locations_name" vartype="String" write="false"
                      ><mm:list nodes="<%= locations_number %>" path="locations,readmore,medewerkers" max="1"
                          ><option value="<%= locations_number %>" <%  if(locations_number.equals(locationId))  { %>SELECTED<% } 
                              %>><%= locations_name
                      %></mm:list
                  ></mm:field>
                  </mm:field
                ></mm:list
              ></select>
              <%  
              if(showProgramSelect || iRubriekLayout==NMIntraConfig.SUBSITE1_LAYOUT) {
                %>&nbsp;<br><div align="right"><span class="light">en</span></div><%
              } %>
            </td>
          </tr>
          <%
        }
        
        if(showProgramSelect) {
          %>
          <tr>
            <td colspan="2">
              <select name="program" style="width:195px;">
                <option value="default" <%  if(programId.equals("default")) { %>SELECTED<% } 
                    %>><%= (showAllSelect ? "alle overige groepen" : "alle teams") %>
                <mm:list nodes="<%= thisPrograms %>" path="programs" orderby="programs.title" directions="UP"
                    ><mm:field name="programs.number" jspvar="programs_number" vartype="String" write="false"
                    ><mm:field name="programs.title" jspvar="programs_title" vartype="String" write="false"
                        ><mm:list nodes="<%= programs_number %>" path="programs,readmore,medewerkers" max="1"
                            ><option value="<%= programs_number %>" <%  if(programs_number.equals(programId))  { %>SELECTED<% } 
                                %>><%= programs_title
                        %></mm:list
                    ></mm:field>
                    </mm:field
                ></mm:list
              ></select>
              <%  
              if(iRubriekLayout==NMIntraConfig.SUBSITE1_LAYOUT) {
                %>&nbsp;<br><div align="right"><span class="light">en</span></div><%
              } %>
            </td>
          </tr>
          <%          
        }
        if(iRubriekLayout==NMIntraConfig.SUBSITE1_LAYOUT) { %>
          <tr>
            <td colspan="2">
            <select name="k" style="width:195px;">
              <option value="" <%  if(keywordId.equals("")) { %>SELECTED<% } %>>alle termen
              <mm:listnodes type="keywords" orderby="word" directions="UP">
                  <mm:field name="number" jspvar="keywords_number" vartype="String" write="false">
                  <option value="<%= keywords_number %>" <%  if(keywords_number.equals(keywordId))  { %>SELECTED<% } 
                          %>><mm:field name="word" />
                  </mm:field>
              </mm:listnodes>
            </select>
            </td>
          </tr>
          <%
        } %>
        <tr><td colspan="2"><img src="media/spacer.gif" width="1" height="20"></td></tr>
        <tr>
          <td>
            <input type="submit" name="submit" value="Wis" style="text-align:center;font-weight:bold;width:42px;">
          </td>
          <td>
            <div align="right"><input type="submit" name="submit" value="Zoek" style="text-align:center;font-weight:bold;width:42px;">&nbsp;</div>
          </td>
        </tr>
      </table>
    </form>
   <% 
} %>