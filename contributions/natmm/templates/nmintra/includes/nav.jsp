<div class="navlist">
<table border="0" cellpadding="0" cellspacing="0">
   <tr>
      <td><img src="media/spacer.gif" width="1" height="527"></td>
   <td>
      <table border="0" cellpadding="0" cellspacing="0">
      <tr>
          <td><img src="media/spacer.gif" width="158" height="25"></td>
      </tr>
      <mm:log jspvar="log">
      <% 
      // *** referer is used to open navigation on a page which is not visible in the navigation ***
      String tmp_paginaID = paginaID;
      if(!refererId.equals("")) { 
         boolean pageIsVisible = false;
         %><mm:list nodes="<%= rubriekId %>" path="rubriek,posrel,pagina" max="1" constraints="<%= "pagina.number='" + paginaID + "'" %>"><%
            pageIsVisible = true;
         %></mm:list><%
         if(!pageIsVisible) { 
            %><mm:list nodes="<%= rubriekId %>" path="rubriek1,parent,rubriek2,posrel,pagina" max="1" constraints="<%= "pagina.number='" + paginaID + "'" %>"><%
               pageIsVisible = true;
            %></mm:list><%
         }
         if(!pageIsVisible) { paginaID = refererId; }
      }
      
      // show all subObjects for the rootId, both pages and rubrieken
      RubriekHelper rubriekHelper = new RubriekHelper(cloud);

      TreeMap [] nodesAtLevel = new TreeMap[10];
      nodesAtLevel[0] = (TreeMap) rubriekHelper.getSubObjects(rootId);
      int depth = 0;
      
      // invariant: depth = level of present leafs (root has level 0)
      while(depth>-1&&depth<10) { 
         
         log.info(depth + " -> " + nodesAtLevel[depth]);
         if(nodesAtLevel[depth].isEmpty()) {
            
			   // if this nodesAtLevel is empty, try one level back
            depth--; 
         }
         if(depth>-1&&!nodesAtLevel[depth].isEmpty()) {

			   // show all subObjects, both pages and rubrieken
				while(! nodesAtLevel[depth].isEmpty()) { 

					Integer thisKey = (Integer) nodesAtLevel[depth].firstKey();
					String sThisObject = (String) nodesAtLevel[depth].get(thisKey);
					nodesAtLevel[depth].remove(thisKey);
					%><mm:node number="<%= sThisObject %>" jspvar="thisObject"
						><mm:nodeinfo  type="type" write="false" jspvar="nType" vartype="String"><%
							
							if(nType.equals("pagina")){ // show page
							
								%>
								<tr>
									<td>
										<table border="0" cellpadding="0" cellspacing="0">
											<tr>
												<%@include file="rubriek_page.jsp" %>
												<td style="letter-spacing:1px;">
												<a href="<%= ph.createPaginaUrl(sThisObject,request.getContextPath()) 
													%>" class="menuItem<mm:field name="number"><mm:compare value="<%= paginaID %>">Active</mm:compare></mm:field
													>"><mm:field name="titel" /></a>
												</td>
											</tr>
										</table>
								   </td>
								</tr>                  
								<%
								
							} else { // show rubriek, which is a link to the first page in the rubriek
								
								String nextPage =  rubriekHelper.getFirstPage(sThisObject);
								%>
								<tr>
									<td>
										<table border="0" cellpadding="0" cellspacing="0">
											<tr>
												<%@include file="rubriek_page.jsp" %>
												<td style="letter-spacing:1px;">
													<a href="<%= ph.createPaginaUrl(nextPage,request.getContextPath()) %>"
														class="menuItem<%
															if(sThisObject.equals(rubriekId) && nextPage.equals(paginaID) ) {
																%>Active<%
															} %>"><mm:field name="naam" /></a>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<%
								if(breadcrumbs.contains(sThisObject)) {
									// this rubriek is in the breadcrumbs, so show its subobjects in the next iteration
								   depth++;
									nodesAtLevel[depth] = (TreeMap) rubriekHelper.getSubObjects(sThisObject);   
									log.info(depth + " --> " + nodesAtLevel[depth]);
								}
							} 
						%></mm:nodeinfo
					></mm:node><%
				} 
         } 
      } 

      // reset paginaID to original value, if referer is used
      if(!refererId.equals("")) { paginaID = tmp_paginaID; }
      %>
      </mm:log>
      </table>
      </td>
    </tr>
</table>
</div>
