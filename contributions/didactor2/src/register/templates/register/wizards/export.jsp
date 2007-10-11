<jsp:root
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    version="2.0">
  <mm:escaper
      id="csv" type="regexps">
    <mm:param name="patterns">
    <mm:param name='"' value='""' /></mm:param>
    <mm:param name="mode" value="ENTIRE" />
  </mm:escaper>

  <mm:content expires="0" type="application/csv"
              disposition="registered.csv"
              escaper="csv" >
    <mm:cloud rank="editor">

      <mm:import externid="person" />
      <mm:import externid="chosenclass" />
      <mm:import externid="chosenworkgroup" />

      <mm:node number="$education">
        <jsp:text>#</jsp:text>
        <mm:fieldlist nodetype="people" type="all">
          <mm:fieldinfo type="guiname" /><jsp:text>, </jsp:text>
        </mm:fieldlist>
        <jsp:text>
</jsp:text>
        <mm:relatednodes type="people" role="classrel" id="related" orderby="number" directions="down" />
        <mm:relatednodes type="people" role="related" add="related" orderby="number" directions="down">
          <mm:countrelations type="roles">
            <mm:compare value="0">
              <mm:fieldlist nodetype="people" type="all">
                <mm:fieldinfo type="value" /><jsp:text>,</jsp:text>
              </mm:fieldlist>
            </mm:compare>
            <jsp:text>
</jsp:text>
          </mm:countrelations>
        </mm:relatednodes>
      </mm:node>
    </mm:cloud>
  </mm:content>
</jsp:root>
