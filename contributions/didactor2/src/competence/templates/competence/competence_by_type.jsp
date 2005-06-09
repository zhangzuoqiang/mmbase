<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.Iterator" %>
<%@ page import = "java.util.HashSet" %>
<%@ page import = "java.util.TreeMap" %>


<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>

<mm:import id="wizardjsp"><mm:treefile write="true" page="/editwizards/jsp/wizard.jsp" objectlist="$includePath" /></mm:import>
<mm:import id="listjsp"><mm:treefile write="true" page="/editwizards/jsp/list.jsp" objectlist="$includePath" /></mm:import>

<%
   String sSearchValue = request.getParameter("searchvalue");
   String sRealSearchField = request.getParameter("realsearchfield");
   String sSearchAge = request.getParameter("age");
   if(sSearchAge == null) sSearchAge = "-1";
   String sDeleteNode = request.getParameter("delete_node");
   String bundleCompetence = null;
%>
<mm:write referid="lang_code" jspvar="sLangCode" vartype="String" write="false">
   <%
      bundleCompetence = "nl.didactor.component.competence.CompetenceMessageBundle_" + sLangCode;
   %>
</mm:write>


<fmt:bundle basename="<%= bundleCompetence %>">
<html>
<head>
   <link rel="stylesheet" type="text/css" href='<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />' />
   <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/editwizards/style/layout/list.css" />
   <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/editwizards/style/color/list.css" />
</head>

<body>
<%
   if((sDeleteNode != null) && (!sDeleteNode.equals("")))
   {//delete selected node
      %>
         <mm:node number="<%=sDeleteNode%>" notfound="skip">
            <mm:deletenode deleterelations="true"/>
         </mm:node>
      <%
   }

   HashSet hsetCompetences = new HashSet();
   String sConstraints = "(1=1) ";
   if(sSearchValue == null) sSearchValue = "";

   if((sRealSearchField != null) && (sRealSearchField.equals("number")) && (!sSearchValue.equals("")))
   {
      sConstraints += " AND (number=" + sSearchValue + ") ";
   }
   if((sRealSearchField != null) && (sRealSearchField.equals("name")) && (!sSearchValue.equals("")))
   {
      sConstraints += " AND (name like '%" + sSearchValue + "%') ";
   }
   if((sRealSearchField != null) && (sRealSearchField.equals("owner")) && (!sSearchValue.equals("")))
   {
      sConstraints += " AND (owner=" + sSearchValue + ") ";
   }
%>

<mm:listnodes type="competencies" constraints="<%= sConstraints %>">
   <mm:field name="number" jspvar="sID" vartype="String">
      <%
         hsetCompetences.add(sID);
      %>
   </mm:field>
</mm:listnodes>


<table class="head">
   <tr class="headsubtitle">
      <td>
         <%
            String sResults = "";
         %>
         <mm:import id="SearchResultsItems" reset="true"><fmt:message key="CompetenceTypesMatrixSearchResultsItems"/></mm:import>
         <mm:write referid="SearchResultsItems" write="false" jspvar="sTemplate" vartype="String">
            <%
               sResults = sTemplate.replaceAll("\\{\\$\\$\\$\\}", "" + hsetCompetences.size());
            %>
         </mm:write>
         <div title="<fmt:message key="CompetenceTypesMatrixSearchResultsItemsDescription"/>"><%= sResults %></div>
      </td>
   </tr>
</table>

<table class="body">
   <tr class="searchcanvas">
      <td>
         <table class="searchcontent">
            <tr>
               <td>
                  <a href='<mm:write referid="wizardjsp"/>?wizard=competencies&objectnumber=new'>
                     <img border="0" src="<%= request.getContextPath() %>/editwizards/media/new.gif">
                  </a>
               </td>
               <td>
                  <form>
                     <input type="hidden" id="delete_node" name="delete_node" value=""/>
                     <span class="header"><fmt:message key="CompetenceTypesMatrixSearchResultsTitle"/></span>
                     <br>
                     <select class="input" name="age">
                        <option value="-1"
                           <%
                              if((sSearchAge != null) && (sSearchAge.equals("-1")))
                              {
                                 %> selected="selected" <%
                              }
                           %>
                        ><fmt:message key="CompetenceTypesMatrixSearchTimeAll"/></option>
                        <option value="0"
                           <%
                              if((sSearchAge != null) && (sSearchAge.equals("0")))
                              {
                                 %> selected="selected" <%
                              }
                           %>
                        ><fmt:message key="CompetenceTypesMatrixSearchTimeToday"/></option>
                        <option value="1"
                           <%
                              if((sSearchAge != null) && (sSearchAge.equals("1")))
                              {
                                 %> selected="selected" <%
                              }
                           %>
                        ><fmt:message key="CompetenceTypesMatrixSearchTimeDay"/></option>
                        <option value="7"
                           <%
                              if((sSearchAge != null) && (sSearchAge.equals("7")))
                              {
                                 %> selected="selected" <%
                              }
                           %>
                        ><fmt:message key="CompetenceTypesMatrixSearchTimeWeek"/></option>
                        <option value="31"
                           <%
                              if((sSearchAge != null) && (sSearchAge.equals("31")))
                              {
                                 %> selected="selected" <%
                              }
                           %>
                        ><fmt:message key="CompetenceTypesMatrixSearchTimeMonth"/></option>
                        <option value="365"
                           <%
                              if((sSearchAge != null) && (sSearchAge.equals("365")))
                              {
                                 %> selected="selected" <%
                              }
                           %>
                        ><fmt:message key="CompetenceTypesMatrixSearchTimeYear"/></option>
                     </select>


                     <select name="realsearchfield">
                        <option value="name"
                           <%
                              if((sRealSearchField != null) && (sRealSearchField.equals("name")))
                              {
                                 %> selected="selected" <%
                              }
                           %>
                        ><fmt:message key="CompetenceTypesMatrixSearchByName"/></option>
                        <option value="number"
                           <%
                              if((sRealSearchField != null) && (sRealSearchField.equals("number")))
                              {
                                 %> selected="selected" <%
                              }
                           %>
                        ><fmt:message key="CompetenceTypesMatrixSearchByNumber"/></option>
                        <option value="owner"
                           <%
                              if((sRealSearchField != null) && (sRealSearchField.equals("owner")))
                              {
                                 %> selected="selected" <%
                              }
                           %>
                        ><fmt:message key="CompetenceTypesMatrixSearchByOwner"/></option>
                     </select>


                     <input value="true" name="proceed" type="hidden">
                     <input value="editwizard" name="sessionkey" type="hidden">
                     <input value="$" name="language" type="hidden">
                     <input class="search" value="<%= sSearchValue %>" name="searchvalue" type="text">
                     <a href="javascript:document.forms[0].submit();">
                        <img border="0" src="<%= request.getContextPath() %>/editwizards/media/search.gif">
                     </a>
                     <br>
                     <span class="subscript">
                        <fmt:message key="CompetenceTypesMatrixSearchTime"/>
                        <fmt:message key="CompetenceTypesMatrixSearchTerms"/>
                     </span>
                  </form>
               </td>
            </tr>
         </table>
      </td>
   </tr>
</table>



<%// We have to divide our competnces into groups before makeing the table
   TreeMap mapCompetenceTypeGroups = new TreeMap();
   int iMaxRows = 0;
%>

<mm:listnodes type="competencetypes" orderby="pos">

   <mm:field name="number" jspvar="sCompetenceTypeID" vartype="String">
      <%
         ArrayList arliTmp = null;
      %>
      <mm:relatedcontainer path="posrel,competencies">
         <mm:ageconstraint field="competencies.number" maxage="<%= sSearchAge %>"/>
         <mm:sortorder field="posrel.pos" />
         <mm:related>
            <mm:node element="competencies">
               <mm:field name="number" jspvar="sCompetenceID" vartype="String">
                  <%
                     if(hsetCompetences.contains(sCompetenceID))
                     {
                        if (arliTmp == null) arliTmp = new ArrayList();
                        arliTmp.add(sCompetenceID);
                     }
                  %>
               </mm:field>
            </mm:node>
         </mm:related>
      </mm:relatedcontainer>
      <%
         mapCompetenceTypeGroups.put(sCompetenceTypeID, arliTmp);
         if ((arliTmp != null) && (iMaxRows < arliTmp.size())) iMaxRows = arliTmp.size();
      %>
   </mm:field>
</mm:listnodes>



<%//         ------------------ MAIN TABLE ------------------              %>
<table border="1" cellpadding="0" cellspacing="0" style="border:0px; width:100%" class="titlefield2">
   <tr>
      <%
         int iCounter = 1;
         for(Iterator it = mapCompetenceTypeGroups.keySet().iterator(); it.hasNext(); )
         {
            String sCompetenceTypeGroupID = (String) it.next();
            %>
               <mm:node number="<%= sCompetenceTypeGroupID %>">
                  <%
                     if(iCounter == mapCompetenceTypeGroups.keySet().size())
                     {
                        %><td style="border-color:#000000;"><%
                     }
                     else
                     {
                        %><td style="border-color:#000000; border-right:0px"><%
                     }
                  %>
                     <mm:field name="name" />
                  </td>
               </mm:node>
            <%
            iCounter++;
         }
      %>
   </tr>
   <%
      for(int f = 0; f < iMaxRows; f++)
      {
         %><tr><%

         iCounter = 1;
         for(Iterator it = mapCompetenceTypeGroups.keySet().iterator(); it.hasNext(); )
         {
            String sCompetenceTypeID = (String) it.next();
            ArrayList arliTmp = (ArrayList) mapCompetenceTypeGroups.get(sCompetenceTypeID);

            if(iCounter == mapCompetenceTypeGroups.keySet().size())
            {
               %><td style="border-color:#000000; border-top:0px;"><%
            }
            else
            {
               %><td style="border-color:#000000; border-top:0px; border-right:0px"><%
            }
            if((arliTmp != null) && (arliTmp.size() - 1 >= f))
            {
               String sCompetenceID = (String) arliTmp.get(f);
               %>
                  <mm:node number="<%= sCompetenceID %>">
                     <nobr><a href="#" onClick="document.getElementById('delete_node').value='<%= sCompetenceID %>'; document.forms[0].submit(); return false;"><img border="0" src="<%= request.getContextPath() %>/editwizards/media/remove.gif"/></a> <a href='<mm:write referid="wizardjsp"/>?wizard=competencies&objectnumber=<%= sCompetenceID %>'><mm:field name="name" /></a></nobr>
                  </mm:node>
               <%
            }
            else
            {
               %>&nbsp;<%
            }
            iCounter++;

            %></td><%
         }
         %></tr><%
      }
   %>
</table>


</body>
</html>
</fmt:bundle>
</mm:cloud>
