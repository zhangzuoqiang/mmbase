<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="org.mmbase.cache.Cache" %>
<%@page import="java.util.*" %>
<mm:cloud name="mmbase">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Cache Monitor, Multi Level Cache</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
</head>
<mm:import externid="cache" jspvar="cacheName" vartype="String"
required="true" />
<body class="basic" >

<table summary="applications" width="93%" cellspacing="1" cellpadding="3" border="0">
<%

 Cache cache = Cache.getCache(cacheName);
 if (cache != null) {
%>

<tr align="left">
  <th class="header" colspan="4">Cache Monitor - v1.0</th>
</tr>
<tr>
  <td class="multidata" colspan="4"><%= cache.getDescription() %> Cache - first 500 entries</td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr align="left">
  <th class="header">Position</th>
  <th class="header">Count</th>
  <th class="header">Key</th>
  <th class="header">Value</th>
</tr>
<%
   Iterator i = cache.getOrderedEntries(500).iterator();
   int j = 0;
   while(i.hasNext()) {
     Map.Entry entry = (Map.Entry) i.next();
%>
<tr>
  <td class="data"><%=++j%></td>
  <td class="data"><%=cache.getCount(entry.getKey())%></td>
  <td class="data"><%=entry.getKey()%></td>
  <td class="data"><%=entry.getValue()%></td>
</tr>
<% } %>
<tr><td>&nbsp;</td></tr>
<% } %>
<tr>
<td class="navigate"><a href="<mm:url page="../cache.jsp" />"><img src="../../images/back.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="3">Return to Cache Monitor</td>
</tr>
</table>

</body></html>
</mm:cloud>
