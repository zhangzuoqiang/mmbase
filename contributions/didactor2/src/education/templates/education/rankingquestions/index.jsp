<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>

<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">

<mm:cloud loginpage="/login.jsp" jspvar="cloud">





<mm:import externid="question" required="true"/>



<%@include file="/shared/setImports.jsp" %>



<mm:node number="$question">



  <mm:field name="showtitle">
    <mm:compare value="1">
      <h2><mm:field name="title"/></h2>
    </mm:compare>
  </mm:field>

  <p/>

  <mm:field name="text" escape="none"/>

  <p/>



  <!-- TODO what is the purpose of the layout field value?! -->	

  <mm:import id="questionlayout"><mm:field name="layout"/></mm:import>





  <%-- Compute size --%>

  <mm:import id="nof_rankinganswers" jspvar="nof_rankinganswers" vartype="Integer"> <mm:relatednodescontainer type="rankinganswers"><mm:size/></mm:relatednodescontainer></mm:import>



  <mm:relatednodes type="rankinganswers" comparator="SHUFFLE">

    <mm:field id="answer" name="number" write="false"/>

    <div class="images">

      <mm:relatednodes type="images">

        <mm:field name="showtitle">
          <mm:compare value="1">
            <h3><mm:field name="title"/></h3>
          </mm:compare>
        </mm:field>

        <img src="<mm:image />" width="200" border="0" /><br/>

        <mm:field name="description" escape="none"/> 

        <mm:remove referid="hasimage"/>

        <mm:import id="hasimage"/>

      </mm:relatednodes>

    </div>

    <mm:notpresent referid="hasimage">

      <mm:field name="text" escape="none"/>

    </mm:notpresent>



    <select name="<mm:write referid="question"/>_<mm:write referid="answer"/>">

    

    <% for (int i= 1;i <= nof_rankinganswers.intValue();i++) { %>

      <option><%=i%></option>

    <% } %>

    </select><br/>

    <mm:remove referid="answer"/>

    

  </mm:relatednodes>

</mm:node>



</mm:cloud>

</mm:content>

