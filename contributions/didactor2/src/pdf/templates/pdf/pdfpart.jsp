<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:content postprocessor="reducespace" expires="0">
<%-- 
    we need to get the parameters from the request by hand
    for some reason mmbase keeps the old versions if we use
    referid (even with from="parameters" !)
--%>
<mm:import id="number" jspvar="number"><%= request.getParameter("partnumber") %></mm:import>
<mm:import id="level"  jspvar="level" vartype="Integer"><%= request.getParameter("level") %></mm:import>
<mm:cloud jspvar="cloud" method="anonymous">
<%@include file="/shared/setImports.jsp" %>

<mm:import id="providerurl">geen.standaard.aanbieders.url</mm:import>

<mm:node referid="provider">
    <mm:relatednodes type="urls">
        <mm:first>
            <mm:import id="providerurl" reset="true"><mm:field name="url"/></mm:import>
        </mm:first>
    </mm:relatednodes>
</mm:node>


<mm:node number="$number">
<mm:nodeinfo type="type" id="node_type" jspvar="nodeType">
<mm:import jspvar="layout" id="layout"><mm:field name="layout"/></mm:import>

<% System.err.println("rendering node "+number+" of type "+nodeType+" at level "+level+" with layout "+layout); %>
<mm:compare referid="node_type" value="learnblocks">
    <mm:import id="display">1</mm:import>
</mm:compare>
<mm:compare referid="node_type" value="pages">
    <mm:import id="display">1</mm:import>
</mm:compare>
<mm:compare referid="node_type" value="educations">
    <mm:import id="display">1</mm:import>
</mm:compare>


<mm:present referid="display">

    <%= "<h"+level.toString()+">" %><mm:field name="title"/><mm:field name="name"/><%= "</h"+level.toString()+">" %>
    <br/>
    <mm:import jspvar="text" reset="true"><mm:field name="intro" escape="none"/><mm:field name="text" escape="none"/></mm:import>
 <%
//    System.err.println("Cleaning up '"+text+"'");
        //
        // remove some of the annoying html that messes up the PDFs
        // 
        text = text.replaceAll("</?(font|style)[^>]*>","");
        text = text.replaceAll("(?<=[^>]\\s)+(width|height|style|align)=\\s*(\"[^\"]*\"|'[^']*'|\\S+)","");
        text = text.replaceAll("<(t[dh][^>]*)>","<$1 width=\"100%\">");
        text = text.replaceAll("<br>","<br/>");
        
/*        if (nodeType.equals("pages") && "2".equals(layout)) {
            text = text.replaceAll("<table[^>]*>","<table border='1' cellpadding='4' width='50%' align='left'>");
        }
        else if (nodeType.equals("pages") && "3".equals(layout)) {
            text = text.replaceAll("<table[^>]*>","<table border='1' cellpadding='4' width='50%' align='right'>");
        }
        else { */
            text = text.replaceAll("<table[^>]*>","<table border='1' cellpadding='4' width='100%'>");
//        }
        text = text.replaceAll("<p\\s*/>","");
        text = text.replaceAll("<p\\s*>\\s*</p>\\s*","");
        text = text.replaceFirst("\\A\\s*","");
        text = text.replaceFirst("\\s*\\z","");
        if (!text.startsWith("<p>")) {
            text = "<p>"+text;
        }
        if (!text.endsWith("</p>")) {
            text = text+"</p>";
        }

        text = text.replaceAll("<p>\\s*<table","<table");
        text = text.replaceAll("</table>\\s*</p>","</table>");
//    System.err.println("Result: '"+text+"'");

%>  
    <mm:compare referid="node_type" value="learnblocks">
       <%= text %>
    </mm:compare>

    <mm:compare referid="node_type" value="pages">

        <mm:countrelations type="images">
            <mm:isgreaterthan value="0">
                <mm:field name="imagelayout" id="imagelayout" write="false"/>
                <mm:compare referid="layout" value="0">
                <%= text %>
                <%@include file="pdfimages.jsp"%>
                </mm:compare>
                <mm:compare referid="layout" value="1">
                <%@include file="pdfimages.jsp"%>
                <%= text %>
                </mm:compare>
                <mm:compare referid="layout" value="2">
                <table width="100%" ><tr>
                <td width="100%" valign="top"><%= text %></td><td>
                <%@include file="pdfimages.jsp"%>
                </td>
                </tr>
                </table>
                </mm:compare>
                <mm:compare referid="layout" value="3">
                <table width="100%" ><tr><td>
                <%@include file="pdfimages.jsp"%></td>
                <td width="100%" valign="top"><%= text %></td>
                </tr>
                </table>
                </mm:compare>
            </mm:isgreaterthan>
            <mm:islessthan value="1">
                <%= text %>
            </mm:islessthan>
        </mm:countrelations>

       
        <mm:relatednodes type="attachments">
            <br/>
            <p>
            <mm:field name="title"/>
            <br/>
            <mm:field name="description"/>
            <br/>
            http://<mm:write referid="providerurl"/>/attachment.db?<mm:field name="number"/>
            </p>
        </mm:relatednodes>

        <mm:relatednodes type="audiotapes">
        <br/>
        <p>
        <mm:field name="title"/>
        <br/>
        <mm:field name="subtitle"/>
        <br/>
        <mm:field name="playtime"/>
        <br/>
        <mm:field name="intro"/>
        <br/>
        <mm:field name="body"/>
        <br/>
        <mm:field name="url" />
        </p>
        </mm:relatednodes>

        <mm:relatednodes type="videotapes">
        <br/>
        <p>
            <mm:field name="title"/>
            <br/>
            <mm:field name="subtitle"/>
            <br/>
            <mm:field name="playtime"/>
            <br/>
            <mm:field name="intro"/>
            <br/>
            <mm:field name="body"/>
            <br/>
            <mm:field name="url" />
        </p>
        </mm:relatednodes>

        <mm:relatednodes type="urls">
        <br/>
        <p>
            <mm:field name="name"/>
            <br/>
            <mm:field name="description"/>
            <br/>
            <mm:field name="url" />
        </p>
        </mm:relatednodes>

        
        </mm:compare>
        <br/>

    <% if (level.intValue() < 20) { %>
        <mm:compare referid="node_type" value="educations">
    <mm:list nodes="$number" path="educations,posrel,learnobjects" orderby="posrel.pos" fields="learnobjects.number" searchdir="destination" distinct="true">
        <mm:remove referid="number"/>
        <mm:remove referid="level"/>
        <mm:field name="learnobjects.number" jspvar="partnumber">
            <mm:include page="pdfpart.jsp">
                <mm:param name="partnumber"><%= partnumber %></mm:param>
                <mm:param name="level"><%= (level.intValue()+1) %></mm:param>
            </mm:include>
        </mm:field>
    </mm:list>
        </mm:compare>

        <mm:compare referid="node_type" value="educations" inverse="true">
    <mm:list  path="learnobjects1,posrel,learnobjects2" orderby="posrel.pos" fields="learnobjects2.number" searchdir="destination" distinct="true" constraints="learnobjects1.number = $number">
        <mm:remove referid="number"/>
        <mm:remove referid="level"/>
        <mm:field name="learnobjects2.number" jspvar="partnumber">
            <mm:include page="pdfpart.jsp">
                <mm:param name="partnumber"><%= partnumber %></mm:param>
                <mm:param name="level"><%= (level.intValue()+1) %></mm:param>
            </mm:include>
        </mm:field>
    </mm:list>
        </mm:compare>
    <% } %>

</mm:present>
</mm:nodeinfo>
</mm:node>
</mm:cloud>
</mm:content>
