<%--
  This template adds a url to a folder.
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="ADDURL" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="currentfolder"/>
<mm:import externid="callerpage"/>
<mm:import externid="typeof"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>

<mm:node number="$currentfolder" id="mycurrentfolder"/>

<mm:import externid="contact">-1</mm:import>
<mm:compare referid="contact" value="-1" inverse="true">
  <mm:import id="user" reset="true"><mm:write referid="contact"/></mm:import>
</mm:compare>

<%-- Check if the create button is pressed --%>
<mm:present referid="action1">
  <mm:import id="action1text"><fmt:message key="CREATE" /></mm:import>
  <mm:compare referid="action1" referid2="action1text">

    <mm:node referid="mycurrentfolder">

      <mm:field name="name"/>

      <mm:createnode type="urls" id="myurls">

        <mm:fieldlist type="all" fields="url,name,description">
		  <mm:fieldinfo type="useinput" />
	    </mm:fieldlist>

      </mm:createnode>

    <%-- create permissions --%>
    <mm:createnode type="portfoliopermissions" id="permissions">
         <mm:fieldlist fields="readrights,allowreactions">
            <mm:fieldinfo type="useinput" />
        </mm:fieldlist>
    </mm:createnode>

    <mm:createrelation source="myurls" destination="permissions" role="related"/>


      
      <mm:createrelation role="related" source="mycurrentfolder" destination="myurls"/>

    </mm:node>

    <mm:redirect referids="$referids,currentfolder,typeof,contact?" page="$callerpage"/>

  </mm:compare>
</mm:present>


<%-- Check if the back button is pressed --%>
<mm:present referid="action2">
  <mm:import id="action2text"><fmt:message key="BACK" /></mm:import>
  <mm:compare referid="action2" referid2="action2text">
    <mm:redirect referids="$referids,currentfolder,typeof,contact?" page="$callerpage"/>
  </mm:compare>
</mm:present>

<div class="rows">


<div class="navigationbar">
<div class="titlebar">
<img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<fmt:message key="PORTFOLIO" />"/>
<fmt:message key="PORTFOLIO" />
</div>
</div>

<div class="folders">

<div class="folderHeader">
<fmt:message key="PORTFOLIO" />
</div>
<div class="folderBody"></div>
</div>


<div class="mainContent">

  <div class="contentHeader">
    <fmt:message key="ADDURL" />
  </div>

  <div class="contentBodywit">

    <br><br><br>
    <%-- Show the form --%>
    <form name="addurl" method="post" action="<mm:treefile page="/portfolio/addurl.jsp" objectlist="$includePath" referids="$referids"/>">

      <!-- TODO making a more precise layout -->
      <mm:node referid="mycurrentfolder">
        <mm:field name="name"/><br/>
      </mm:node>

      <table class="Font">
      <tr><td>Url</td><td><input name="_url" value="http://" size="80"></td></tr>

 	  <mm:fieldlist nodetype="urls" fields="name,description">

        <tr>
	    <td><mm:fieldinfo type="guiname"/></td>
	    <td><mm:fieldinfo type="input"/></td>
	    </tr>

	  </mm:fieldlist>

        <tr>
            <td>Leesrechten</td>
            <td><select name="_readrights">
                <option value="0" >Niet zichtbaar</option>
                <option value="1" >Zichtbaar voor studenten uit mijn klassen</option>
                <option value="2" >Zichtbaar voor mijn docenten</option>
                <option value="3" >Zichtbaar voor iedereen.</option>
                <option value="4" >Zichtbaar voor niet-ingelogde (anonieme) gebruikers.</option>
            </select>
            </td>
        </tr>
        <tr>
            <td>Reacties</td>
            <td><select name="_allowreactions">
                <option value="0" >Geen reacties toestaan</option>
                <option value="1" >Reacties toestaan</option>
                </select>
            </td>
        </tr>

	  </table>

      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="currentfolder" value="<mm:write referid="currentfolder"/>"/>
      <input type="hidden" name="typeof" value="<mm:write referid="typeof"/>"/>
      <mm:compare referid="contact" value="-1" inverse="true">
        <input type="hidden" name="contact" value="<mm:write referid="contact"/>"/>
      </mm:compare>
      <input class="formbutton" type="submit" name="action1" value="<fmt:message key="CREATE" />"/>
      <input class="formbutton" type="submit" name="action2" value="<fmt:message key="BACK" />" />
    </form>
  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</fmt:bundle>
</mm:cloud>
</mm:content>
