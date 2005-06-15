<!--
  This translates mmbase XML, normally containing an objects tag. The XML related to this XSL is generated by
  org.mmbase.bridge.util.Generator, and the XSL is invoked by FormatterTag.

  @author:  Michiel Meeuwissen
  @version: $Id: mmxf2kupu.xslt,v 1.5 2005-06-15 06:44:02 michiel Exp $
  @since:   MMBase-1.6
-->
<xsl:stylesheet  
  xmlns:xsl ="http://www.w3.org/1999/XSL/Transform"
  xmlns:node ="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:o="http://www.mmbase.org/xmlns/objects"
  xmlns:mmxf="http://www.mmbase.org/xmlns/mmxf"
  xmlns:html="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="node o mmxf html"
  version = "1.1"
>
  <xsl:import href="2xhtml.xslt" />   <!-- dealing with mmxf is done there -->

  <xsl:output method="xml" 
    omit-xml-declaration="yes" /><!-- xhtml is a form of xml -->


   <!-- If objects is the entrance to this XML, then only handle the root child of it -->
  <xsl:template match="o:objects">
    <xsl:apply-templates select="o:object[1]" />
  </xsl:template>

  <xsl:template match="mmxf:h" mode="h1"><xsl:if test=". != ''"><h1><xsl:apply-templates select="node()" /></h1></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h2"><xsl:if test=". != ''"><h2><xsl:apply-templates select="node()" /></h2></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h3"><xsl:if test=". != ''"><h3><xsl:apply-templates select="node()" /></h3></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h4"><xsl:if test=". != ''"><h4><xsl:apply-templates select="node()" /></h4></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h5"><xsl:if test=". != ''"><h5><xsl:apply-templates select="node()" /></h5></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h6"><xsl:if test=". != ''"><h6><xsl:apply-templates select="node()" /></h6></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h7"><xsl:if test=". != ''"><h7><xsl:apply-templates select="node()" /></h7></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h8"><xsl:if test=". != ''"><h8><xsl:apply-templates select="node()" /></h8></xsl:if></xsl:template>


  <!-- just in case..-->
  <xsl:template match="html:html">
    <body>
      <xsl:apply-templates />
    </body>
  </xsl:template>

  <xsl:template match = "mmxf:mmxf" >
    <body>
      <xsl:apply-templates select = "mmxf:p|mmxf:table|mmxf:section|mmxf:ul|mmxf:ol|mmxf:table" />
    </body>
  </xsl:template>


</xsl:stylesheet>
