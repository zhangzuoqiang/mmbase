<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@page import="java.net.URL, nl.didactor.education.PDFConverter, java.io.ByteArrayOutputStream"%>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
	<%@include file="/shared/setImports.jsp"%>
<mm:import externid="number" required="true" jspvar="number"/>
<mm:import externid="action"/>
<mm:present referid="action">
    <mm:compare referid="action" value="mail">


    <mm:list nodes="$user" path="people,mailboxes" fields="mailboxes.number" constraints="mailboxes.type=1">
        <mm:field name="mailboxes.number" id="mailboxNumber" write="false"/>
        <mm:node referid="mailboxNumber" id="mailboxNode"/>
    </mm:list>
    <mm:notpresent referid="mailboxNode">
        Deze gebruiker heeft geen sent mailbox!
    </mm:notpresent>

<%

    URL url = new URL("http://"+java.net.InetAddress.getLocalHost().getHostAddress()+":"+request.getServerPort()+request.getContextPath()+"/education/pdfhtml.jsp?number="+number);
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    PDFConverter.pageAsPDF(url, outStream);
%>
    <mm:node number="$number">
        <mm:import id="attachmentname"><mm:field name="title"/><mm:field name="name"/></mm:import>
    </mm:node>

    <mm:createnode type="attachments" id="attachment" jspvar="attachment">
        <mm:setfield name="title"><mm:write referid="attachmentname"/></mm:setfield>
        <mm:setfield name="mimetype">application/pdf</mm:setfield>
        <mm:setfield name="filename"><mm:write referid="attachmentname"/>.pdf</mm:setfield>
        <% attachment.setByteValue("handle",outStream.toByteArray()); %>
        <mm:setfield name="date"><%=System.currentTimeMillis()/1000%></mm:setfield>
    </mm:createnode>

    <mm:createnode type="emails" id="emailNode" jspvar="mailNode">
         <mm:setfield name="type">0</mm:setfield>
    </mm:createnode>
    <mm:createrelation role="related" source="mailboxNode" destination="emailNode"/>
    <mm:createrelation role="related" source="attachment" destination="emailNode"/>

    <mm:treeinclude page="/email/write/write.jsp" objectlist="$includePath" referids="$referids">
        <mm:param name="id"><mm:write referid="emailNode"/></mm:param>
    </mm:treeinclude>
  </mm:compare>
</mm:present>

 <mm:notpresent referid="action">
<html>
<head>
<title>PDF output</title>
</head>
<body>
     <a href="<%= request.getContextPath() %>/pdf.db?number=<mm:write referid="number"/>">Bekijk als PDF</a> | <a href="pdf.jsp?action=mail&number=<mm:write referid="number"/>" target="_top">Mail als PDF</a>

   
</body>
</html>

</mm:notpresent>
</mm:cloud>
</mm:content>
