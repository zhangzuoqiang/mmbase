<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd"><%@ page language="java" contentType="text/html;charset=utf-8" session="true"%><%@ page import="org.mmbase.bridge.*,org.mmbase.util.StringSplitter,java.util.*,java.util.regex.*" %><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %><mm:cloud rank="basic user" method="http"><html><head>	<title>+++ YAMMeditor (beta) : editor for the mm:edit tag +++</title><script src="yammeditor.js" type="text/javascript"></script><style type="text/css" media="screen">/* <![CDATA[ */@import "yammeditor.css" screen;/* ]]> */</style><%-- Breakdown:1. Toon eerst alle startnodes (bovenin)2. Toon per startnode alle paden die die node als start hebben3. Verzamel de nodes die met die paden gevonden worden4. Toon alleen die nodes en velden die in de pagina zaten5. Opties:	a. toon overige gerelateerde nodes			b. zoekopties e.d. om andere nodes te relateren#################################################################################### --%><mm:import externid="nrs"		jspvar="nrs" /><mm:import externid="fields"	jspvar="fields" /><mm:import externid="paths"		jspvar="paths" /><%-- put it all down in a yammeditor session :-P --%><mm:write referid="nrs" 	session="yammeditnrs" /><mm:write referid="fields"	session="yammeditflds" /><mm:write referid="paths"	session="yammeditpaths" /><mm:import externid="yammedithistory" id="history"	from="session" /><mm:import externid="change" /></head><body><%// startnodes: 676 (startnode)List sl = StringSplitter.split(nrs, ";");// fields: 676.number (node.field)// List fl = StringSplitter.split(fields, ";");// paths: 676.news,posrel,images (startnode.path)// let's make a map, in this map we put sets with all the pathsMap pmap = new HashMap();if (paths != null && !paths.equals("")) {  String[] str_paths = paths.split(";");			// 53.mags0,posrel,news  for (int i = 0; i < str_paths.length; i++) {	int p = str_paths[i].indexOf(".");	String key = str_paths[i].substring(0, p);		// 53	String value = str_paths[i].substring(p + 1);	// mags0,posrel,news		if (!pmap.containsKey(key)) {	  Set s = new HashSet();	// new set, f.e. set 53	  s.add(value);			// add path to set 53, f.e. mags0,posrel,news	  pmap.put(key,s);		// put the set in the map	} else {	  HashSet hs = (HashSet)pmap.remove(key);	  hs.add(value);			// add path to set	  pmap.put(key,hs);		// put the set back in the map	}  }}// map for the fieldsMap fmap = new HashMap();String[] str_fields = fields.split(";");			// 106_news.titlefor (int i = 0; i < str_fields.length; i++) {	int p = str_fields[i].indexOf("_");	String key = str_fields[i].substring(0, p);		// 106	String value = str_fields[i].substring(p + 1);	// news.title		if (!fmap.containsKey(key)) {		Set s = new HashSet();	// new set, f.e. 106		s.add(value);			// add path to set 106, f.e. news.title		fmap.put(key,s);		// put the set in the map	} else {		HashSet hs = (HashSet)fmap.remove(key);		hs.add(value);			// add path to set		fmap.put(key,hs);		// put the set back in the map	}}%><!-- header --><div id="header">  <div id="titles"><img class="yammbutton" src="/mmbase/edit/my_editors/img/mmbase-edit-40.gif" alt="edit" width="41" height="40" />  <strong>yammeditor using the edit tag</strong></div>  <div id="startnodes">startnodes: 	<%	String sn = "";	String[] str_nodes = nrs.split(";");	for (int i = 0; i < str_nodes.length; i++) {		sn = str_nodes[i];	%>		<a href="<mm:url referids="nrs,fields,paths"><mm:param name="sn" value="<%= sn %>" /><mm:param name="nr" value="<%= sn %>" /></mm:url>"><%= sn %></a> |	<% 	}	// get the first object!	sn = str_nodes[0];	%>		<mm:import id="nr" externid="nr" jspvar="nr" vartype="String"><%= sn %></mm:import>  </div></div><!-- /header --><!-- content --><div id="content">	<!-- first startnode --><mm:node number="$nr" notfound="skip">	<%-- show historic nodes --%>	<%-- wat moet 't doen? 	1. node opslaan die 'vooraan staat'	2. maar history moet weer geleegd worden als een van de startnodes 'vooraan staat'	3. nodes mogen er niet dubbel inkomen	--%>	<%	List hl = new ArrayList();	if (session.getAttribute("yammedithistory") != null) {		hl = (ArrayList)session.getAttribute("yammedithistory");		if (sl.contains(nr)) {	hl.clear(); }	// clear the list when we're back at a startnode				Iterator he = hl.iterator();		while (he.hasNext()) {			String hisnr = (String)he.next();			if (!hisnr.equals(nr)) {		%>			<mm:node number="<%= hisnr %>">				<h3><span class="buttons"><a href="<mm:url referids="nrs,fields,paths"><mm:param name="nr"><%= hisnr %></mm:param></mm:url>"><img src="/mmbase/edit/my_editors/img/mmbase-edit.gif" alt="edit" width="21" height="20" /></a></span><mm:field name="gui()" /></h3>			</mm:node>		<% 			}		} 	}	if (!hl.contains(nr)) { hl.add(nr); }	session.putValue("yammedithistory", hl);	%>		<%-- change --%>	<mm:present referid="change">		<mm:fieldlist type="edit"><mm:fieldinfo type="useinput" /></mm:fieldlist>		<div class="message"><strong><mm:field name="gui()" /></strong> is changed.</div>	</mm:present>	<form id="startnode" enctype="multipart/form-data" method="post" action="<mm:url referids="nrs,fields,paths,nr" />">	<h2>#<mm:field name="number" id="nodenr" /> (<mm:nodeinfo type="type" id="nm" />) <mm:field name="gui()" /></h2>		<mm:import jspvar="nodenr" vartype="String"><mm:write referid="nodenr" /></mm:import>		<mm:import jspvar="nm" vartype="String"><mm:write referid="nm" /></mm:import>	<%--		<mm:import id="hist"><mm:write referid="nodenr" /><mm:write referid="history"><mm:isnotempty>,<mm:write /></mm:isnotempty></mm:write></mm:import>		<mm:write referid="hist" session="yammedithistory" />		<mm:remove referid="hist" />--%>	<mm:fieldlist type="edit">		<div class="field"><strong><mm:fieldinfo type="guiname" /></strong>		<mm:fieldinfo type="name" jspvar="fieldname" vartype="String" write="true"><br />			<% 			String fieldstring = nm + "." + fieldname;			Set fs = (Set)fmap.get(sn);			// shows allways the handle of images & attachments			if (fs.contains(fieldstring) || fieldname.equals("handle")) { %><mm:fieldinfo type="input" /><% } 			else { %><div class="fieldguivalue"><mm:fieldinfo type="guivalue" />&nbsp;</div><% } %>		</mm:fieldinfo>		</div>		<mm:remove referid="fieldname" />	</mm:fieldlist>	<div class="field"><input type="submit" name="change" value="Change" class="formbutton" /></div>	</form></mm:node></div><!-- /content --><!-- sidebar --><div id="sidebar"><mm:compare referid="paths" value="" inverse="true"><h4>Related nodes</h4><mm:node number="$nr" notfound="skip"><%if (pmap.containsKey(nr)) {			// get the map entry with the paths  Set ps = (Set)pmap.get(nr);  for (Iterator i = ps.iterator(); i.hasNext();) {	String pad = (String)i.next();		// velds	String flds = "";	Set fs = (Set)fmap.get(nr);		ArrayList ntl = new ArrayList();	// list for the nodetypes		for (Iterator j = fs.iterator(); j.hasNext();) {	  String velden = (String)j.next();			// news.title	  	  int s = velden.indexOf(".");	  String nt = velden.substring(0,s);		// news	  	  if (pad.indexOf(nt) > 0) {			// when path !contains this nodetype		if(!ntl.contains(nt)) ntl.add(nt);	// add nodetype to list		if (flds.equals("")) {				// add field to fields		  flds = velden;		} else {		  flds = flds + "," + velden;		}	  }	}	// /velds	  %><div class="path"><%= pad %></div>	  	<mm:list nodes="$nr" path="<%= pad %>">	  <% 	  Iterator nti = ntl.iterator();	  int ii = 0;	  while (nti.hasNext()) {		String nm = (String)nti.next();	  %>		<mm:node element="<%= nm %>">		<div class="rel">		<% if (ii > 0) { out.println("&nbsp;&nbsp;"); }; %>		<mm:maywrite><a href="<mm:url referids="nrs,fields,paths"><mm:param name="nr"><mm:field name="number" /></mm:param></mm:url>"><img class="editbutton" src="/mmbase/edit/my_editors/img/mmbase-edit.gif" alt="edit" width="21" height="20" /></a></mm:maywrite>		<mm:function name="gui" jspvar="gui" vartype="String" write="false">			<% 			if ((gui.indexOf("<a href") < 0 || gui.indexOf("<img src") < 0) && gui.length() > 28) {			  gui = gui.substring(0,28) + "..."; 			}			out.println(gui);			ii++;			%>		</mm:function> (<%= nm %>)		</div>		</mm:node>	  <% } %>	</mm:list><%   } }%></mm:node></mm:compare><%-- /referid="paths" value="" inverse="true" --%></div><!-- /sidebar --><div id="footer"><a href="javascript:closeYAMMeditor();">close window</a></div><p>Parameters<br />nrs :  <mm:write referid="nrs" /> <br />fields : <mm:write referid="fields" /> <br />paths : <mm:write referid="paths" /> <br /></p><div><a href="#" onclick="toggle('edit_1');return false;" title="Hide or show...">Toon form!</a></div><div style="display: none;" id="edit_1"><div class="relatednode">More about this related node....</div></div><div>related nodes  in rechterkolom- je kunt related node uitklappen met javascript	<% 	out.println("<br /><br /><b>Session info</b>");	String[] names  = session.getValueNames();	for (int j = 0; j < names.length; j++) {		out.println("<br /><br />session name : " + names[j]);		out.println("<br />session value : " + session.getValue(names[j]));	}	%></div></body></html></mm:cloud>