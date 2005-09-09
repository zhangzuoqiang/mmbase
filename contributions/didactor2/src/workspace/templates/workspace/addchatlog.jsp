<%--
  This template adds a chatlog to a folder.
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="ADDCHATLOG" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="currentchatlog"/>
<mm:import externid="foldername"/>
<mm:import externid="callerpage"/>
<mm:import externid="typeof"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>


<%-- Check if the create button is pressed --%>
<mm:present referid="action1">
  <mm:import id="action1text"><fmt:message key="CREATE" /></mm:import>
  <mm:compare referid="action1" referid2="action1text">

    <mm:compare referid="typeof" value="1">
      <mm:node number="$user">
        <mm:relatednodes type="workspaces">
          <mm:relatednodescontainer type="folders">
            <mm:constraint field="name" value="$foldername"/>
            <mm:listnodes>
              <mm:import id="myfolder"><mm:field name="number"/></mm:import>
            </mm:listnodes>
          </mm:relatednodescontainer>
        </mm:relatednodes>
      </mm:node>
    </mm:compare>

    <mm:compare referid="typeof" value="2">
      <mm:node number="$class">
        <mm:relatednodes type="workspaces">
          <mm:relatednodescontainer type="folders">
            <mm:constraint field="name" value="$foldername"/>
            <mm:listnodes>
              <mm:import id="myfolder"><mm:field name="number"/></mm:import>
            </mm:listnodes>
          </mm:relatednodescontainer>
        </mm:relatednodes>
      </mm:node>
    </mm:compare>

    <mm:present referid="myfolder">
      <mm:node number="$currentchatlog" id="mycurrentchatlog">
        <mm:createrelation role="related" source="myfolder" destination="mycurrentchatlog"/>
      </mm:node>
    </mm:present>

    <mm:redirect referids="$referids,typeof,currentchatlog" page="$callerpage"/>

  </mm:compare>
</mm:present>


<%-- Check if the back button is pressed --%>
<mm:present referid="action2">
  <mm:import id="action2text"><fmt:message key="BACK" /></mm:import>
  <mm:compare referid="action2" referid2="action2text">
    <mm:redirect referids="$referids,typeof,currentchatlog" page="$callerpage"/>
  </mm:compare>
</mm:present>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <mm:compare referid="typeof" value="1">
      <mm:import id="titletext"><fmt:message key="MYDOCUMENTS" /></mm:import>
    </mm:compare>
    <mm:compare referid="typeof" value="2">
      <mm:import id="titletext"><fmt:message key="SHAREDDOCUMENTS" /></mm:import>
    </mm:compare>
     <mm:compare referid="typeof" value="3">
        <mm:import id="titletext"><fmt:message key="WORKGROUPDOCUMENTS" /></mm:import>
    </mm:compare>




    <img src="<mm:treefile write="true" page="/gfx/icon_portfolio.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<fmt:message key="MYDOCUMENTS" />" />
    <mm:write referid="titletext"/>
  </div>
</div>

<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">
  </div>
</div>

<div class="mainContent">

  <div class="contentHeader">
    <fmt:message key="ADDCHATLOG" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="addchatlog" method="post" action="<mm:treefile page="/workspace/addchatlog.jsp" objectlist="$includePath" referids="$referids"/>">

      <table class="Font">

        <mm:compare referid="typeof" value="1">
          <mm:node number="$user">
  	        <mm:relatednodes type="workspaces">
	          <mm:relatednodes type="folders" id="myfolders"/>
            </mm:relatednodes>
          </mm:node>
        </mm:compare>

        <mm:compare referid="typeof" value="2">
          <mm:node number="$class">
  	        <mm:relatednodes type="workspaces">
	          <mm:relatednodes type="folders" id="myfolders"/>
	        </mm:relatednodes>
	      </mm:node>
        </mm:compare>

        <mm:relatednodes referid="myfolders">
          <mm:first><fmt:message key="FOLDERS" /><select name="foldername"></mm:first>
          <option><mm:field name="name"/></option>
          <mm:last></select></mm:last>
        </mm:relatednodes>

        <mm:node referid="currentchatlog">
          <mm:fieldlist fields="date,text">

            <tr>
  	        <td><mm:fieldinfo type="guiname"/></td>
	        <td><mm:fieldinfo type="value" escape="p"/></td>
	        </tr>

	      </mm:fieldlist>
	    </mm:node>
	  </table>

      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="currentchatlog" value="<mm:write referid="currentchatlog"/>"/>
      <input type="hidden" name="typeof" value="<mm:write referid="typeof"/>"/>
      <input class="formbutton" type="submit" name="action1" value="<fmt:message key="CREATE" />" />
      <input class="formbutton" type="submit" name="action2" value="<fmt:message key="BACK" />" />
    </form>

  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</fmt:bundle>
</mm:cloud>
</mm:content>
