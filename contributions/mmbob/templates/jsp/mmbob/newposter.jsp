<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">

<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="pathtype">poster_newposter</mm:import>
<mm:import externid="postareaid" />
<mm:import externid="feedback">none</mm:import>

<%-- login part --%>
<%@ include file="getposterid.jsp" %>
<%@ include file="thememanager/loadvars.jsp" %>
<%-- end login part --%>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
<mm:compare value="createposter" referid="action">
        <mm:import reset="true" id="account" externid="newaccount" />
        <mm:import reset="true" id="password" externid="newpassword" />
        <mm:import reset="true" id="confirmpassword" externid="newconfirmpassword" />
        <mm:import id="firstname" externid="newfirstname" />
        <mm:import id="lastname" externid="newlastname" />
        <mm:import id="email" externid="newemail" />
        <mm:import id="location" externid="newlocation" />
        <mm:import id="gender" externid="newgender" />
        <mm:import id="feedback" reset="true"><mm:function set="mmbob" name="createPoster" referids="forumid,account,password,confirmpassword,firstname,lastname,email,gender,location" /></mm:import>   
</mm:compare>
</mm:present>
<!-- end action check -->

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>

<div class="header">
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>
                                                                                              
<div class="bodypart">

<mm:include page="path.jsp?type=$pathtype" />
<mm:compare referid="feedback" value="none">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="50%">
 	<form action="<mm:url page="newposter.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        </mm:url>" method="post">
			<tr><th width="150" ><mm:write referid="mlg.Account"/></th><td>
				<input name="newaccount" value="" style="width: 100%" />
			</td></tr>
			<tr><th width="150" ><mm:write referid="mlg.Password"/></th><td>
				<input name="newpassword" style="width: 100%" type="password"/>
			</td></tr>
			<tr><th width="150" ><mm:write referid="mlg.ConfirmPassword"/></th><td>
				<input name="newconfirmpassword" style="width: 100%" type="password"/>
			</td></tr>
			<tr><th><mm:write referid="mlg.Firstname"/></th><td>
				<input name="newfirstname" value="" style="width: 100%" />
				</td></tr>
			<tr><th><mm:write referid="mlg.Lastname"/></th><td>
				<input name="newlastname" value="" style="width: 100%" />
				</td></tr>
			<tr><th><mm:write referid="mlg.Email"/></th><td>
				<input name="newemail" value="" style="width: 100%" />
				</td></tr>
			<tr><th><mm:write referid="mlg.Location"/></th><td>
				<input name="newlocation" value="" style="width: 100%" />
				</td></tr>
			<tr><th><mm:write referid="mlg.Gender"/></th><td>
				<select name="newgender">
				<option value="male"><mm:write referid="mlg.Male"/>
				<option value="female"><mm:write referid="mlg.Female"/>
				</select>
			</td></tr>
	<tr><th colspan="2">
        <input type="hidden" name="action" value="createposter">
        <center><input type="submit" value="<mm:write referid="mlg.Save"/>"></center>
	</form>
	</th></tr>
</table>
</mm:compare>

<mm:compare referid="feedback" value="inuse">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="50%">
	<tr><th colspan="2"><mm:write referid="mlg.Account_allready_in_use"/></th></tr>
 	<form action="<mm:url page="newposter.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        </mm:url>" method="post">
			<tr><th width="150" ><mm:write referid="mlg.Account"/></th><td>
				<input name="newaccount" value="" style="width: 100%" />
			</td></tr>
			<tr><th width="150" ><mm:write referid="mlg.Password"/></th><td>
				<input name="newpassword" style="width: 100%" type="password"/>
			</td></tr>
			<tr><th width="150" ><mm:write referid="mlg.ConfirmPassword"/></th><td>
				<input name="newconfirmpassword" style="width: 100%" type="password"/>
			</td></tr>
			<tr><th><mm:write referid="mlg.Firstname"/></th><td>
				<input name="newfirstname" value="" style="width: 100%" />
				</td></tr>
			<tr><th><mm:write referid="mlg.Lastname"/></th><td>
				<input name="newlastname" value="" style="width: 100%" />
				</td></tr>
			<tr><th><mm:write referid="mlg.Email"/></th><td>
				<input name="newemail" value="" style="width: 100%" />
				</td></tr>
			<tr><th><mm:write referid="mlg.Location"/></th><td>
				<input name="newlocation" value="" style="width: 100%" />
				</td></tr>
			<tr><th><mm:write referid="mlg.Gender"/></th><td>
				<select name="newgender">
				<option value="male"><mm:write referid="mlg.Male"/>
				<option value="female"><mm:write referid="mlg.Female"/>
				</select>
			</td></tr>
	<tr><th colspan="2">
        <input type="hidden" name="action" value="createposter">
        <center><input type="submit" value="<mm:write referid="mlg.Save"/>"></center>
	</form>
	</th></tr>
</table>
</mm:compare>

<mm:compare referid="feedback" value="passwordnotequal">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="50%">
	<tr><th colspan="2"><mm:write referid="mlg.Password_notequal"/></th></tr>
 	<form action="<mm:url page="newposter.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        </mm:url>" method="post">
			<tr><th width="150" ><mm:write referid="mlg.Account"/></th><td>
				<input name="newaccount" value="" style="width: 100%" />
			</td></tr>
			<tr><th width="150" ><mm:write referid="mlg.Password"/></th><td>
				<input name="newpassword" style="width: 100%" type="password"/>
			</td></tr>
			<tr><th width="150" ><mm:write referid="mlg.ConfirmPassword"/></th><td>
				<input name="newconfirmpassword" style="width: 100%" type="password"/>
			</td></tr>
			<tr><th><mm:write referid="mlg.Firstname"/></th><td>
				<input name="newfirstname" value="" style="width: 100%" />
				</td></tr>
			<tr><th><mm:write referid="mlg.Lastname"/></th><td>
				<input name="newlastname" value="" style="width: 100%" />
				</td></tr>
			<tr><th><mm:write referid="mlg.Email"/></th><td>
				<input name="newemail" value="" style="width: 100%" />
				</td></tr>
			<tr><th><mm:write referid="mlg.Location"/></th><td>
				<input name="newlocation" value="" style="width: 100%" />
				</td></tr>
			<tr><th><mm:write referid="mlg.Gender"/></th><td>
				<select name="newgender">
				<option value="male"><mm:write referid="mlg.Male"/>
				<option value="female"><mm:write referid="mlg.Female"/>
				</select>
			</td></tr>
	<tr><th colspan="2">
        <input type="hidden" name="action" value="createposter">
        <center><input type="submit" value="<mm:write referid="mlg.Save"/>"></center>
	</form>
	</th></tr>
</table>
</mm:compare>



<mm:compare referid="feedback" value="ok">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="60%">
			<tr><th><mm:write referid="mlg.Account_created"/></th></tr>
			<tr><td><mm:write referid="mlg.Your_account_is_created_you_may"/> <a href="<mm:url page="index.jsp" referids="forumid" />"><mm:write referid="mlg.login"/></a></td><tr>
</table>
</mm:compare>

</div>

<div class="footer">
    <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=footerpath%>"/>
</div>

</mm:locale>
                                                                                              
</body>
</html>

</mm:content>
</mm:cloud>
