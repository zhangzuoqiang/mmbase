<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page language="java" contentType="text/html; charset=utf-8"

%><mm:cloud
><%@include file="/includes/getids.jsp" 
%><%@include file="/includes/header.jsp" %>

<mm:list nodes="$page" path="pages,posrel,urls">
<td class="white" colspan="2" valign="top">
<script language="javascript">
	newwin = window.open('<mm:field name="urls.url"/>','documentation');;setTimeout('newwin.focus();',250);
</script>     
</mm:list>

<h3>The documentation will be opened in a new window.</h3>

</td>

<%@include file="/includes/footer.jsp"
%></mm:cloud>
