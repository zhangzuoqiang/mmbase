<!--
  This translates mmbase XML, normally containing an objects tag. The XML related to this XSL is generated by
  org.mmbase.bridge.util.Generator, and the XSL is invoked by FormatterTag.

  @author:  Michiel Meeuwissen
  @version: $Id: 2xhtml.xslt,v 1.12 2005-06-14 20:14:21 michiel Exp $
  @since:   MMBase-1.6
-->
<xsl:stylesheet  
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
    xmlns:o="http://www.mmbase.org/xmlns/objects"
    xmlns:mmxf="http://www.mmbase.org/xmlns/mmxf"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="node mmxf o"
    version="1.0" >

  <xsl:import href="mmxf2xhtml.xslt" />   <!-- dealing with mmxf is done there -->
  <xsl:import href="formatteddate.xslt" /><!-- dealing with dates is done there -->

  <xsl:output method="xml" omit-xml-declaration="yes" /> <!-- xhtml is a form of xml -->

  <xsl:param name="cloud">mmbase</xsl:param>


  <xsl:variable name="newstype">xmlnews</xsl:variable>
  <!-- I had an 'xmlnews' type... Can easily switch beteen them like
       this.  Perhaps you prefer 'news' itself to contain XML fields. -->

   <!-- If objects is the entrance to this XML, then only handle the root child of it -->
  <xsl:template match="o:objects">
    <div class="objects">
      <xsl:apply-templates select="o:object[1]" />
    </div>
  </xsl:template>


  <!-- how to present a node -->
  <xsl:template match="o:object">
    <xsl:apply-templates select="o:field" />
  </xsl:template>


   <!-- how to present a news node -->
   <xsl:template match="o:object[@type=$newstype and not(o:unfilledField)]">
     <xsl:apply-templates select="o:field[@name='title']"    />
     <xsl:apply-templates select="o:field[@name='subtitle']" />
     <xsl:apply-templates select="o:field[@name='intro']" />
     <xsl:apply-templates select="o:field[@name='body']" />
   </xsl:template>


  <xsl:template match="o:object[@type=$newstype]/o:field[@name='title']" >
    <h1><xsl:value-of select="." /></h1>
  </xsl:template>

  <xsl:template match="o:object[@type=$newstype]/o:field[@name='subtitle']" >
    <h2><xsl:value-of select="." /></h2>
  </xsl:template>


  <xsl:template match="o:field[@format='xml']">
    <xsl:apply-templates  />
  </xsl:template>

  <!-- 'concisely' presents a list of objects, a comma seperated list of a-tags (which is 'inline' XHTML) -->
  <xsl:template match="o:object" mode="inline">
    <xsl:choose>
      <xsl:when test="@type='images'">
	<a href="{node:function($cloud, string(./o:field[@name='number'] ), 'servletpath()')}" 
	   alt="{./o:field[@name='description']}">
	  <img src="{node:function($cloud, string(./o:field[@name='number'] ), 'servletpath(,cache(s(100x100&gt;)))')}" 
	       alt="{./o:field[@name='description']}" 
	       >
	    <xsl:if test="./o:field[@name='width']">
	      <xsl:attribute name="height"><xsl:value-of select="./o:field[@name='height']" /></xsl:attribute>
	      <xsl:attribute name="width"><xsl:value-of select="./o:field[@name='width']" /></xsl:attribute>
	    </xsl:if>
	  </img>
	</a>
	<!-- Resin's xslt-impl, does not pass 'Nodes', so we limit ourselves to strings. :-( -->
      </xsl:when>
      <xsl:when test="@type='urls'">
	<a href="{./o:field[@name='url']}">
	  <xsl:attribute name="alt">
	    <xsl:choose>
	      <xsl:when test="./o:field[@name='subtitle']" >
		<xsl:value-of select="./o:field[@name='subtitle']" />
	      </xsl:when>
	      <xsl:when test="./o:field[@name='description']" >
		<xsl:value-of select="./o:field[@name='description']" />
	      </xsl:when>
	      <xsl:otherwise>
		URL
	      </xsl:otherwise>
	    </xsl:choose>
	  </xsl:attribute>
	  <xsl:choose>
	    <xsl:when test="./o:field[@name='title'] != ''" >
	      <xsl:value-of select="./o:field[@name='title']" />
	    </xsl:when>
	    <xsl:when test="./o:field[@name='name'] != ''" >
	      <xsl:value-of select="./o:field[@name='name']" />
	    </xsl:when>
	    <xsl:when test="./o:field[@name='description'] != ''" >
	      <xsl:value-of select="./o:field[@name='description']" />
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="./o:field[@name='url']" />
	    </xsl:otherwise>
	  </xsl:choose>
	</a>

      </xsl:when>
    </xsl:choose>
    <xsl:if test="position() &lt; last()">, </xsl:if>
  </xsl:template>



  <!-- template to override mmxf tags with an 'id', we support links to it here -->
  <xsl:template match="mmxf:section|mmxf:p|mmxf:a">
    <!-- store the 'relation' nodes for convenience in $rels:-->
    <xsl:variable name="rels"   select="ancestor::o:object/o:relation[@role='idrel']" />
    
    <!-- also for conveniences: all related nodes to this node-->
    <xsl:variable name="related_to_node"   select="//o:objects/o:object[@id=$rels/@related]" />
   
    <!-- There are two type of relations, it is handy to treat them seperately: -->
    <xsl:variable name="srelations" select="//o:objects/o:object[@id=$rels[@type='source']/@object and o:field[@name='id'] = current()/@id]" />
    <xsl:variable name="drelations" select="//o:objects/o:object[@id=$rels[@type='destination']/@object and o:field[@name='id'] = current()/@id]" />


    <!-- now link the relationnodes with the nodes related to this node, the find the 'relatednodes' -->

    <xsl:variable name="relatednodes" select="$related_to_node[@id = $srelations/o:field[@name = 'dnumber']] | $related_to_node[@id = $drelations/o:field[@name='snumber']]" />
    
    <xsl:apply-templates select="." mode="sub">
      <xsl:with-param name="relatednodes" select="$relatednodes" />
    </xsl:apply-templates>
  </xsl:template>


  <!-- template to override mmxf tags with an 'id', we support links to it here -->
  <xsl:template match="mmxf:p" mode="sub">
    <xsl:param name="relatednodes" />
    <xsl:element name="{name()}">
      <xsl:copy-of select="@*" />
      <xsl:apply-templates select="$relatednodes[@type='images']" mode="inline" />
      <xsl:apply-templates />
      <xsl:if test="count($relatednodes[@type='urls'])">
	<br />
	<xsl:apply-templates select="$relatednodes[@type='urls']" mode="inline" />
      </xsl:if>
    </xsl:element>
  </xsl:template>

  <xsl:template match="mmxf:a" mode="sub">
    <xsl:param name="relatednodes" />
    <xsl:variable name="urls"   select="$relatednodes[@type='urls']" />
    <xsl:variable name="images" select="$relatednodes[@type='images']" />
    <xsl:choose>
      <xsl:when test="not($urls) and not($images)">
        <!-- no relations found, simply ignore the anchor -->
	<xsl:apply-templates />
      </xsl:when>
      <xsl:when test="count($urls) = 1">
        <!-- only one url is related, it is simple to make the body clickable -->
	<a href="{$urls[1]/o:field[@name='url']}" 
	   alt="{$urls[1]/o:field[@name='title']}">
	  <xsl:apply-templates select="$images" mode="inline" />
	  <xsl:apply-templates  />
	</a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates /> 
	<xsl:if test="count($relatednodes) &gt; 0">
	  <!-- more than one url related to this anchor, we add between parentheses a list of links -->
	  (<xsl:apply-templates select="$relatednodes" mode="inline" />)
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="mmxf:section" mode="sub">
    <xsl:param name="relatednodes" />
    <xsl:apply-templates select="mmxf:section|mmxf:h" />
    <xsl:if test="count($relatednodes[@type='images']) &gt; 0">
      <p><xsl:apply-templates select="$relatednodes[@type='images']" mode="inline" /></p>
    </xsl:if>  
    <xsl:apply-templates select="mmxf:section|mmxf:p" />
    <xsl:if test="count($relatednodes[@type='urls']) &gt; 0">
      <p><small><xsl:apply-templates select="$relatednodes[@type='urls']" mode="inline" /></small></p>
    </xsl:if>
  </xsl:template>


</xsl:stylesheet>
