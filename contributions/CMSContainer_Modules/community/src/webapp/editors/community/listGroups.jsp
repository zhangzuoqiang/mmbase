<%@include file="globals.jsp" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="cmsc-community" scope="request" />
<mm:content type="text/html" encoding="UTF-8" expires="0">
<cmscedit:head title="reactions.title"></cmscedit:head>


<edit:ui-tabs>
      <edit:ui-tab key="community.search.users">
         ${pageContext.request.contextPath }/editors/community/SearchConditionalUser.do
      </edit:ui-tab>      
      <edit:ui-tab key="community.search.groups" active="true">
         ${pageContext.request.contextPath }/editors/community/searchConditionalGroupAction.do
      </edit:ui-tab>
</edit:ui-tabs>


<div class="editor">
   <div style="padding-left:10px;">
     <p><a href="${pageContext.request.contextPath }/editors/community/groupInitAction.do" style=" padding-left:20px; background: url(<cmsc:staticurl page='/editors/gfx/icons/new.png'/>) left center no-repeat">New Group</a><p>
      <html:form action="/editors/community/searchConditionalGroupAction.do" method="post">
            <table border="0">
               <tbody>
                  <tr>&nbsp;</tr>
                  <tr>
                     <td style="width:150px"><fmt:message key="community.search.groupname"/></td>
                     <td><html:text style="width: 250px" property="groupname"/></td>
                  </tr>
                   <tr>
            <td width="150px"><fmt:message key="community.search.member"/></td>
            <td><input type="text" style="width:250px" value="" name="member"></td>
             <tr>
                  <tr>&nbsp;</tr>
                  <tr>
                     <td style="width:150px">&nbsp;</td>
                     <td><input type="submit" name="submit" value="<fmt:message key="community.search.searchbatton"/>"> </td>
                  </tr>
               </tbody>
            </table>
         </html:form>
      
   </div>
</div>

<div class="editor">
   <div class="ruler_green"><div>&nbsp; result &nbsp;</div></div>
   <div class="body">
      <form action="">
        <%@ include file="grouplist_table.jspf"%> 
       </form>      
   </div>
</div>
</mm:content>