<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud jspvar="cloud" method="anonymous">
<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.education.EducationMessageBundle">
<mm:import externid="number" required="true"/>
<html>
<head></head>
<body>
<mm:treeinclude page="/education/pdfpart.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="partnumber"><mm:write referid="number"/></mm:param>
    <mm:param name="level">1</mm:param>
</mm:treeinclude>
</body>
</html>
</fmt:bundle>
</mm:cloud>
</mm:content>
