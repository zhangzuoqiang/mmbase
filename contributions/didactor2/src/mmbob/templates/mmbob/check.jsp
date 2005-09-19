<mm:listnodes type="posters" constraints="account='admin' AND password='admin2k'">
    <mm:import id="adminposter" reset="true"><mm:field name="number"/></mm:import>
</mm:listnodes>
<mm:import id="classforum" reset="true">-1</mm:import>
<mm:node number="$class" notfound="skip">
    <mm:relatednodes type="forums">
        <mm:import id="classforum" reset="true"><mm:field name="number"/></mm:import>
    </mm:relatednodes>
    <mm:compare referid="classforum" value="-1">
	<mm:import id="name" reset="true"><mm:write referid="class"/></mm:import>
	<mm:import id="description" reset="true"><fmt:message key="ForumForClass" /> '<mm:field name="name"/>'</mm:import>
	<mm:import id="language" reset="true"><mm:write referid="lang_code"/></mm:import>
	<mm:import id="account" reset="true">admin</mm:import>
        <mm:import id="password" reset="true">admin2k</mm:import>
	<mm:nodefunction set="mmbob" name="newForum" referids="name,language,description,account,password">
	</mm:nodefunction>
        <mm:listnodes type="forums" constraints="name='$class'">
            <mm:import id="forumid" reset="true"><mm:field name="number"/></mm:import>
            <mm:import id="classforum" reset="true"><mm:write referid="forumid"/></mm:import>
        </mm:listnodes>
	<mm:import id="name" reset="true"><mm:field name="name"/></mm:import>
        <mm:booleanfunction set="mmbob" name="changeForum" referids="forumid,name,language,description" >
	</mm:booleanfunction>
        <mm:createrelation role="related" source="class" destination="classforum" />
    </mm:compare>
</mm:node>

<mm:import id="educationforum" reset="true">-1</mm:import>
<mm:node number="$education" notfound="skip">
    <mm:relatednodes type="forums">
        <mm:import id="educationforum" reset="true"><mm:field name="number"/></mm:import>
    </mm:relatednodes>
    <mm:compare referid="educationforum" value="-1">
	<mm:import id="name" reset="true"><mm:write referid="education"/></mm:import>
	<mm:import id="description" reset="true"><fmt:message key="ForumForEducation" /> '<mm:field name="name"/>'</mm:import>
	<mm:import id="language" reset="true"><mm:write referid="lang_code"/></mm:import>
	<mm:import id="account" reset="true">admin</mm:import>
        <mm:import id="password" reset="true">admin2k</mm:import>
	<mm:nodefunction set="mmbob" name="newForum" referids="name,language,description,account,password">
	</mm:nodefunction>
        <mm:listnodes type="forums" constraints="name='$education'">
            <mm:import id="forumid" reset="true"><mm:field name="number"/></mm:import>
            <mm:import id="educationforum" reset="true"><mm:write referid="forumid"/></mm:import>
        </mm:listnodes>
	<mm:import id="name" reset="true"><mm:field name="name"/></mm:import>
        <mm:booleanfunction set="mmbob" name="changeForum" referids="forumid,name,language,description" >
	</mm:booleanfunction>
        <mm:createrelation role="related" source="education" destination="educationforum" />
    </mm:compare>
</mm:node>

