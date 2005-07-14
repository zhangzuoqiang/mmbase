<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import = "java.util.*" %>


<%
   String sNode = request.getParameter("node");

   String [] MONTHS = { "januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november", "december" };
   String [] LANGUAGES = {"nl", "en", "fr"};
   String EMPTY_VALUE = "...";


   HashSet hsetRelatedNodes = new HashSet();
   SortedSet hsetLangCodes = new TreeSet();
   HashSet hsetAssignedVals = new HashSet();
   HashSet hsetVocabularis = new HashSet();
%>

<style type="text/css">
   .bottom_link{
      color:18248C;
      font-family:arial;
      font-size:20px;
      font-weight:normal;
      text-decoration:none;
   }

   body, .body{
      font-family:arial;
      font-size:13px;
   }
</style>

<mm:content postprocessor="reducespace">
   <mm:cloud>

     <%// List assigned metadefinitions %>
     <%
	Enumeration enumAssignedParamNames = request.getParameterNames();
        while(enumAssignedParamNames.hasMoreElements())
        {
	  String sParameter = (String) enumAssignedParamNames.nextElement(); 
	  String[] arrstrParameters = request.getParameterValues(sParameter);

          if(sParameter.charAt(0) == 'm')
          {
		String sMetadataDefinitionID = sParameter.substring(1);

		for (int i=0 ; i < arrstrParameters.length ; i++)
		{
                   
                   if(arrstrParameters[i]!= null && 
                      !arrstrParameters[i].equals(EMPTY_VALUE) && 
                      !arrstrParameters[i].equals(""))
                   {
                       // Put metadefinition in the list
                       
                       hsetAssignedVals.add(sMetadataDefinitionID);
                       // Let's check this metadefinition metavocabularies
                    %>
                      

                      <mm:list nodes="<%=sMetadataDefinitionID %>"  path="metadefinition,related,metavocabulary" 
                               searchdir="destination" fields="metavocabulary.number,metavocabulary.value" >
                      <mm:field name="metavocabulary.value" jspvar="sVocValue" vartype="String">
                      <mm:field name="metavocabulary.number" jspvar="sVocNumber" vartype="String">
                               
                      <%  
                          if(sVocValue.equals(arrstrParameters[i]))
                           {
                               hsetVocabularis.add(sVocNumber);
                    
                           }
                       %>

                      </mm:field>
                      </mm:field>
                      </mm:list>

                   <%
                   } 
		}

          }

	}
        
     %>
     


      <%// Get languages list %>
      <mm:list path="metastandard,metadefinition,metavocabulary" constraints="metadefinition.handler='taal'">
    
           <mm:field name="metavocabulary.value" jspvar="sLang" vartype="String">
              <%
                 hsetLangCodes.add(sLang);
              %>
           </mm:field>
        </mm:list>

      <form name="meta_form">

         <div style="overflow-y:scroll; width:100%; height:90%; position:absolute">
            <input type="hidden" name="node" value="<%= sNode %>"/>
            <input type="hidden" name="number" value="<%= sNode %>"/>
            <input type="hidden" name="submitted" value="submit"/>
            <input type="hidden" name="add" value=""/>
            <input type="hidden" name="close" value="yes"/>
            <%
               if(request.getParameter("set_defaults") != null)
               {//If we are setting default values, we should repeat "set_default" parameter
                  %>
                     <input type="hidden" name="set_defaults" value="true"/>
                  <%
               }
            %>

               <%@include file="metaedit_header.jsp" %>
               <font style="font-size:5px">&nbsp;<br/></font>

               <%
                  //if we are working with default values, we have to show only one metastandart
                  //So, we have to use constraints
                  String sMetastandartConstraints;
                  String sCheckrelations ;

                  if(request.getParameter("set_defaults") != null) sMetastandartConstraints = "number=" + sNode;
                     else sMetastandartConstraints = "";
               %>
               <mm:listnodes type="metastandard" orderby="name" constraints="<%= sMetastandartConstraints %>">
                  <mm:first inverse="true">
                     <br/><br/>
                  </mm:first>

                  <font style="font-family:arial; font-size:20px; font-weight:normal"><mm:field name="name"/></font>
                  <br/>
                  <mm:field name="description"/>
                  <hr style="width:99%; height:1px; color:#CCCCCC">

                  <mm:relatednodes type="metadefinition" orderby="name">


                              <%
				sCheckrelations = "Ok";
			      %>

                              <mm:field name="number" jspvar="rNum" vartype="String">
                              <mm:list nodes="<%=rNum %>"  path="metadefinition,posrel,metadefinition2" 
                                       searchdir="source" fields="metadefinition.number,metadefinition.name,posrel.pos" >

        		            <mm:field name="metadefinition2.number" jspvar="rMd" vartype="String">
				    <mm:field name="posrel.pos" jspvar="rPos" vartype="String">
               				<%			                  
                                          if(rPos.equals("3") && hsetAssignedVals.contains(rMd))
                                              {
                                                sCheckrelations = "Fail"; 
                                              }
                                          else
					      {
						sCheckrelations = "Ok";
 					      }
			               %>
			            </mm:field> <%-- posrel.pos --%>

				 </mm:field> <%-- metadefinition2.number --%>
			      </mm:list>

                              <!--
                              Let's check metavocabulary references ...

                              -->

                              <%
                              if("Ok".equals(sCheckrelations))
                                 {
                                 
                              %>

                              <mm:list nodes="<%=rNum %>"  path="metadefinition,posrel,metavocabulary" searchdir="source" 
                                       fields="metavocabulary.number,metavocabulary.value,posrel.pos" >

			            <mm:field name="metavocabulary.number" jspvar="rMv" vartype="String">
				    <mm:field name="posrel.pos" jspvar="vPos" vartype="String">
               				<%			                  
                                          if(vPos.equals("3") && hsetVocabularis.contains(rMv))
                                              {
                                                sCheckrelations = "Fail"; 
                                              }
                                          else
					      {
						sCheckrelations = "Ok";
 					      }
			               %>
			            </mm:field>
                                    </mm:field>
			      </mm:list>
                              <%
                                   }
                               %>

                              

			      </mm:field>


                     <%
                       if("Ok".equals(sCheckrelations))
                        {
                     %>
                     <a name="m<mm:field name="number"/>">
                     <font style="font-family:arial; font-size:13px; font-weight:bold"><mm:field name="name"/></font>
                     <br/>
                     <mm:field name="description"/>
                     <br/>
                     <br/>
                     <%
                        String sDefType = "";
                        String sMinValues = "";
                        String sMaxValues = "";
                        String sMetaDefinitionID = "";
                     %>
                     <mm:field name="type" jspvar="sType" vartype="String" write="false">
                        <%
                           sDefType = sType;
                        %>
                     </mm:field>
                     <mm:field name="minvalues" jspvar="sValue" vartype="String" write="false">
                        <%
                           sMinValues = sValue;
                        %>
                     </mm:field>
                     <mm:field name="maxvalues" jspvar="sValue" vartype="String" write="false">
                        <%
                           sMaxValues = sValue;
                        %>
                     </mm:field>
                     <mm:field name="number" jspvar="sID" vartype="String" write="false">
                        <%
                           sMetaDefinitionID = sID;
                        %>
                     </mm:field>


                     <%
                        if(request.getParameter("set_defaults") != null)
                        {// We have to insert default values here, so we have to create metadata
                           %>
                              <jsp:include page="metaedit_metaget.jsp" flush="true">
                                 <jsp:param name="node" value="<%= sNode %>" />
                                 <jsp:param name="metadata_definition" value="<%= sMetaDefinitionID %>" />
                                 <jsp:param name="set_defaults" value="true" />
                              </jsp:include>
                           <%
                        }
                        else
                        {
                           %>
                              <jsp:include page="metaedit_metaget.jsp" flush="true">
                                 <jsp:param name="node" value="<%= sNode %>" />
                                 <jsp:param name="metadata_definition" value="<%= sMetaDefinitionID %>" />
                              </jsp:include>
                           <%
                        }

                        
			String sConstraints = "metadefinition.number=" + sMetaDefinitionID;

                        if(sDefType.equals("1"))
                        {//vocabulary

                           String sCheckVocabularies = "Ok";

                           if(sMaxValues.equals("1"))
                           {
                              String sSelected = "";
                              %>
                                 <mm:list nodes="<%= sNode %>" path="object,metadata,metavocabulary,metadata,metadefinition" 
							constraints="<%= sConstraints %>">

                                    <mm:node element="metavocabulary">
                                       <mm:field name="value" jspvar="sValue" vartype="String" write="false">
                                          <%
                                             sSelected = sValue;
                                          %>
                                       </mm:field>
                                    </mm:node>
                                 </mm:list>

                                 <select name="m<mm:field name="number"/>">

                                     <option><%= EMPTY_VALUE %></option>

                                     <mm:relatednodes type="metavocabulary" orderby="value" searchdir="destination">

					<mm:field name="number" jspvar="sCurrentNum" vartype="String" write="false">

                                        <%-- Let's test all posrel related metavocabularies --%>
                                        <%  sCheckVocabularies ="Ok";   %>

                                         <mm:list nodes="<%=sCurrentNum %>"  path="metavocabulary,posrel,metavocabulary2" 
                                            searchdir="source" fields="metavocabulary2.number,metavocabulary2.value,posrel.pos">

                                            <mm:field name="metavocabulary2.number" jspvar="sCurrentNum2" vartype="String" write="false">
					    <mm:field name="metavocabulary2.value" jspvar="sCurrentVal2" vartype="String" write="false">
					    <mm:field name="posrel.pos" jspvar="sCurrentPos" vartype="String" write="false">

                                        	<%

                                                if("3".equals(sCurrentPos) && hsetVocabularis.contains(sCurrentNum2))
						{
						  sCheckVocabularies = "Fail";  
						}
                                                else
                                                {
						  sCheckVocabularies = "Ok";
						}

						%>
                                                

					    </mm:field>
					    </mm:field>
					    </mm:field>



						

                                          </mm:list>
     					</mm:field>

					  <%
					   if(sCheckVocabularies.equals("Ok"))
                                           {
						// ---- print control -----
					  %>
 					
                                        
                                      
                                        <mm:field name="value" jspvar="sCurrent" vartype="String" write="false">
                                           <option name="m<%= sMetaDefinitionID %>" value="<%= sCurrent %>"
                                              <%
                                                 if(sSelected.equals(sCurrent))
                                                 {
                                                    %> selected="selected" <%
                                                 }
                                              %>
                                           ><%= sCurrent %></option>
                                        </mm:field>

                                       <%
                                        } // end of if(sCheckVocabularies.equals("Ok"))
                                       %>


                                     </mm:relatednodes>
                                 </select>
                              <%
                           }
                           else
                           {
                              HashSet hsetSelected = new HashSet();
			       
                              %>



                                 <%// List for connected metavocabularies
                                  // snode - Target Object of any type which can have a metadata
                                 %>


                                <mm:list nodes="<%= sNode %>" path="object,metadata,metavocabulary,metadata,metadefinition"  constraints="<%= sConstraints %>">
                                   <mm:node element="metavocabulary">
                                      <mm:field name="value" jspvar="sValue" vartype="String" write="false">
                                         <%
                                            hsetSelected.add(sValue);
                                         %>
                                      </mm:field>
                                   </mm:node>
                                </mm:list>


                                 <mm:relatednodes type="metavocabulary" searchdir="destination">
                                    <mm:field name="number" jspvar="sCurrentNumber" vartype="String" write="false">


					<%-- Let's test all posrel related metavocabularies --%>
                                        <%  sCheckVocabularies ="Ok";   %>

                                         <mm:list nodes="<%=sCurrentNumber %>"  path="metavocabulary,posrel,metavocabulary2" 
                                            searchdir="source" fields="metavocabulary2.number,metavocabulary2.value,posrel.pos">

                                            <mm:field name="metavocabulary2.number" jspvar="sCurrentNum2" vartype="String" write="false">
					    <mm:field name="metavocabulary2.value" jspvar="sCurrentVal2" vartype="String" write="false">
					    <mm:field name="posrel.pos" jspvar="sCurrentPos" vartype="String" write="false">

                                        	<%

                                                if("3".equals(sCurrentPos) && hsetVocabularis.contains(sCurrentNum2))
						{
						  sCheckVocabularies = "Fail";  
						}
                                                else
                                                {
						  sCheckVocabularies = "Ok";
						}
						%>
                                                

					    </mm:field>
					    </mm:field>
					    </mm:field>
					
                                       </mm:list>

                                     </mm:field> <!-- Close metavocabulary number field tag -->

                                     <%
				     if(sCheckVocabularies.equals("Ok"))
                                      {
                                      
					// ---- print control -----
                                      %>

	   			     <mm:field name="value" jspvar="sCurrent" vartype="String" write="false">
                                       <input type="checkbox" name="m<%= sMetaDefinitionID %>" value="<%= sCurrent %>"
                                       <%
                                          if(hsetSelected.contains(sCurrent))
                                          {
                                       %> 
                                          checked="checked" 
                                       <%
                                          }
                                       %>
                                       /><%= sCurrent %>
                                       <br/>
                                    </mm:field>

                                   <%
                                    } // end of if(sCheckVocabularies.equals("Ok"))
                                   %>

                                 </mm:relatednodes>
                              <%
                           }
                        }
                        if(sDefType.equals("2"))
                        {//date
                           Date date = null;
                           %>
                              <mm:list nodes="<%= sNode %>" path="object,metadata,metadate,metadata,metadefinition" max="1"  constraints="<%= sConstraints %>">
                                 <mm:node element="metadate">
                                    <mm:field name="value" jspvar="dateValue" vartype="Date" write="false">
                                       <%
                                          date = dateValue;
                                       %>
                                    </mm:field>
                                 </mm:node>
                              </mm:list>


                              <table border="0" cellpadding="0" cellspacing="0" class="body">
                                 <tr>
                                    <td>Dag</td>
                                    <td>Maand</td>
                                    <td>Jaar</td>
                                    <td>&nbsp;</td>
                                    <td>Uur</td>
                                    <td>Minuut</td>
                                 </tr>
                                 <tr>
                                    <td><input type="text" name="m<%= sMetaDefinitionID %>" value="<% if (date != null) out.print(date.getDate()); %>" style="width:30px;"/></td>
                                    <td>
                                       <select name="m<%= sMetaDefinitionID %>">
                                           <option><%= EMPTY_VALUE %></option>
                                           <%
                                              for(int i = 0; i < 12; i++)
                                              {
                                                 %>
                                                    <option value="<%= i + 1 %>"
                                                    <%
                                                       if((date!= null) && (date.getMonth() == i))
                                                       {
                                                          %> selected="selected" <%
                                                       }
                                                    %>
                                                    ><%= MONTHS[i] %></option>
                                                 <%
                                              }
                                           %>
                                       </select>
                                    </td>
                                    <td><input type="text" name="m<%= sMetaDefinitionID %>" value="<% if (date != null) out.print(1900 + date.getYear()); %>" style="width:60px;"/></td>
                                    <td>&nbsp;om&nbsp;</td>
                                    <td>
                                       <select name="m<%= sMetaDefinitionID %>">
                                           <option><%= EMPTY_VALUE %></option>
                                           <%
                                              for(int i = 0; i < 24; i++)
                                              {
                                                 %>
                                                    <option value="<%= i %>"
                                                    <%
                                                       if((date!= null) && (date.getHours() == i))
                                                       {
                                                          %> selected="selected" <%
                                                       }
                                                    %>
                                                    >
                                                    <%
                                                       if(i < 10)
                                                       {
                                                          %>0<%
                                                       }
                                                    %><%= i %></option>
                                                 <%
                                              }
                                           %>
                                       </select>
                                    </td>
                                    <td>
                                       <select name="m<%= sMetaDefinitionID %>">
                                           <option><%= EMPTY_VALUE %></option>
                                           <%
                                              for(int i = 0; i < 60; i++)
                                              {
                                                 %>
                                                    <option value="<%= i %>"
                                                    <%
                                                       if((date!= null) && (date.getMinutes() == i))
                                                       {
                                                          %> selected="selected" <%
                                                       }
                                                    %>
                                                    >
                                                    <%
                                                       if(i < 10)
                                                       {
                                                          %>0<%
                                                       }
                                                    %><%= i %></option>
                                                 <%
                                              }
                                           %>
                                       </select>
                                    </td>
                                 </tr>
                              </table>
                           <%
                        }
                        if(sDefType.equals("3"))
                        {//lang
                           int iCounter = 0;
                           boolean bEmpty = true;
                           %>
                              <mm:list nodes="<%= sNode %>" path="object,metadata,posrel,metalangstring,metadata,metadefinition" constraints="<%= sConstraints %>" orderby="posrel.pos">
                                 <mm:node element="metalangstring">
                                    <mm:field name="language" jspvar="sLanguage" vartype="String" write="false">
                                       <mm:field name="value" jspvar="sValue" vartype="String" write="false">
                                          <mm:field name="number" jspvar="sMetalangID" vartype="String" write="false">
                                             <table border="0" cellpadding="0" cellspacing="0">
                                                <tr>
                                                   <td>
                                                      <select name="m<%= sMetaDefinitionID %>">
                                                         <option style="width:20px"><%= EMPTY_VALUE %></option>
                                                            <%
                                                               for(Iterator it = hsetLangCodes.iterator(); it.hasNext();)
                                                               {
                                                                  String sLangCode = (String) it.next();
                                                                  %>
                                                                     <option value="<%= sLangCode %>"
                                                                        <%
                                                                           if(sLanguage.equals(sLangCode))
                                                                           {
                                                                              %>selected="selected"<%
                                                                           }
                                                                        %>
                                                                     ><%= sLangCode %></option>
                                                                  <%
                                                               }
                                                               bEmpty = false;
                                                            %>
                                                      </select>
                                                   </td>
                                                   <td>
                                                      <input name="m<%= sMetaDefinitionID %>" type="text" value="<%= sValue %>" style="width:150px"/>
                                                   </td>
                                                   <td>
                                                      &nbsp;
                                                   </td>
                                                   <td>
                                                      <input type="image" src="gfx/minus.gif" onClick="meta_form.action='#m<%= sMetaDefinitionID %>'; submitted.value='remove'; add.value='<%= sMetaDefinitionID %>,<%= iCounter %>'">
                                                   </td>
                                                </tr>
                                             </table>
                                          </mm:field>
                                       </mm:field>
                                    </mm:field>
                                 </mm:node>
                              </mm:list>
                              
                              <%
                                 if(bEmpty)
                                 {
                                    %>
                                       <table border="0" cellpadding="0" cellspacing="0">
                                          <tr>
                                             <td>
                                                <select name="m<%= sMetaDefinitionID %>">
                                                   <option style="width:20px"><%= EMPTY_VALUE %></option>
                                                      <%
                                                         boolean bFirst = true;
                                                         for(Iterator it = hsetLangCodes.iterator(); it.hasNext();)
                                                         {
                                                            String sLangCode = (String) it.next();
                                                            %>
                                                               <option value="<%= sLangCode %>"
                                                                  <%
                                                                     if(bFirst)
                                                                     {
                                                                        %>selected="selected"<%
                                                                        bFirst = false;
                                                                     }
                                                                  %>
                                                               ><%= sLangCode %></option>
                                                            <%
                                                         }
                                                         iCounter++;
                                                         bEmpty = false;
                                                      %>
                                                </select>
                                             </td>
                                             <td>
                                                <input name="m<%= sMetaDefinitionID %>" type="text" value="" style="width:150px"/>
                                             </td>
                                          </tr>
                                       </table>

                                    <%
                                 }
                              %>
                              <input type="image" src="gfx/plus.gif" onClick="meta_form.action='#m<%= sMetaDefinitionID %>'; submitted.value='add'; add.value='<%= sMetaDefinitionID %>'"> voeg meer tekenreeksen toe
                           <%
                        }
                        if(sDefType.equals("4"))
                        {//Duration
                           Date[] date = new Date[2];
                           %>


                              <mm:list nodes="<%= sNode %>" path="object,metadata,posrel,metadate,metadata,metadefinition" constraints="<%= sConstraints %>" orderby="posrel.pos">
                                 <mm:node element="metadate">
                                    <mm:first>
                                       <mm:field name="value" jspvar="dateValue" vartype="Date" write="false">
                                          <%
                                             date[0] = dateValue;
                                          %>
                                       </mm:field>
                                    </mm:first>
                                    <mm:first inverse="true">
                                       <mm:field name="value" jspvar="dateValue" vartype="Date" write="false">
                                          <%
                                             date[1] = dateValue;
                                          %>
                                       </mm:field>
                                    </mm:first>
                                 </mm:node>
                              </mm:list>


                           <%
                           for(int f = 0; f < 2; f++)
                           {
                              %>
                                 <table border="0" cellpadding="0" cellspacing="0" class="body">
                                    <tr>
                                       <td>Dag</td>
                                       <td>Maand</td>
                                       <td>Jaar</td>
                                       <td>&nbsp;</td>
                                       <td>Uur</td>
                                       <td>Minuut</td>
                                    </tr>
                                    <tr>
                                       <td><input type="text" name="m<%= sMetaDefinitionID %>" value="<% if (date[f] != null) out.print(date[f].getDate()); %>" style="width:30px;"/></td>
                                       <td>
                                          <select name="m<%= sMetaDefinitionID %>">
                                              <option><%= EMPTY_VALUE %></option>
                                              <%
                                                 for(int i = 0; i < 12; i++)
                                                 {
                                                    %>
                                                       <option value="<%= i + 1 %>"
                                                       <%
                                                          if((date[f]!= null) && (date[f].getMonth() == i))
                                                          {
                                                             %> selected="selected" <%
                                                          }
                                                       %>
                                                       ><%= MONTHS[i] %></option>
                                                    <%
                                                 }
                                              %>
                                          </select>
                                       </td>
                                       <td><input type="text" name="m<%= sMetaDefinitionID %>" value="<% if (date[f] != null) out.print(1900 + date[f].getYear()); %>" style="width:60px;"/></td>
                                       <td>&nbsp;om&nbsp;</td>
                                       <td>
                                          <select name="m<%= sMetaDefinitionID %>">
                                              <option><%= EMPTY_VALUE %></option>
                                              <%
                                                 for(int i = 0; i < 24; i++)
                                                 {
                                                    %>
                                                       <option value="<%= i %>"
                                                       <%
                                                          if((date[f]!= null) && (date[f].getHours() == i))
                                                          {
                                                             %> selected="selected" <%
                                                          }
                                                       %>
                                                       >
                                                       <%
                                                          if(i < 10)
                                                          {
                                                             %>0<%
                                                          }
                                                       %><%= i %></option>
                                                    <%
                                                 }
                                              %>
                                          </select>
                                       </td>
                                       <td>
                                          <select name="m<%= sMetaDefinitionID %>">
                                              <option><%= EMPTY_VALUE %></option>
                                              <%
                                                 for(int i = 0; i < 60; i++)
                                                 {
                                                    %>
                                                       <option value="<%= i %>"
                                                       <%
                                                          if((date[f]!= null) && (date[f].getMinutes() == i))
                                                          {
                                                             %> selected="selected" <%
                                                          }
                                                       %>
                                                       >
                                                       <%
                                                          if(i < 10)
                                                          {
                                                             %>0<%
                                                          }
                                                       %><%= i %></option>
                                                    <%
                                                 }
                                              %>
                                          </select>
                                       </td>
                                    </tr>
                                 </table>
                              <%
                           }
                        }

                     } // end of if("Ok".equals(sCheckrelations))
		     
                     %>

                     <%
                     if("Ok".equals(sCheckrelations))
                      {
                     %>

                     <mm:last inverse="true">
                        <hr style="width:99%; height:1px; color:#CCCCCC">
                     </mm:last>

                    <%
                      }
                     %>
                      
                  </mm:relatednodes>

                                    

                  <mm:last inverse="true">
                     <hr style="width:99%; height:1px; color:#CCCCCC">
                  </mm:last>

                

               </mm:listnodes>

            <style type="text/css">
               .special_buttons 
               {
                  background-color:transparent;
                  color: #18248C;
                  font-size: 20px;
                  font-weight: normal;
                  font-family:arial;
                  border: none;
                  vertical-align: middle;
               }
            </style>

         </div>

         <div align="center" style="position:absolute; bottom:0px">
            <hr style="width:100%; height:3px; color:#000000">
            <table border="0" cellpadding="0" cellspacing="0" width="0" style="height:0px">
               <tr>
                  <td align="right">
                     <%
                        if(session.getAttribute("show_metadata_in_list") == null)
                        {
                           %><input type="reset" name="command" value="Annuleren" class="special_buttons" style="width:90px" onClick="document.location.href='menu.jsp'"/><%
                        }
                        else
                        {
                           %><input type="reset" name="command" value="Annuleren" class="special_buttons" style="width:90px" onClick="document.location.href='<%= session.getAttribute("metalist_url") %>'"/><%
                        }
                     %>
                  </td>
                  <td>-</td>
                  <td><input type="submit" name="command" value="Opslaan en beŽindigen" class="special_buttons" style="width:200px" /></td>
                  <td>-</td>
                  <td><input type="submit" name="command" value="Opslaan" class="special_buttons" style="width:80px" onClick="close.value='no'"/></td>
               </tr>
            </table>
         </div>
      </form>
   </mm:cloud>
</mm:content>
