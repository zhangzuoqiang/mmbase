<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform">
  <!-- 
  Basic parameters and settings for all xsl's of the editwizards. 

  @since  MMBase-1.6
  @author Michiel Meeuwissen
  @version $Id: base.xsl,v 1.1 2002-04-19 19:59:25 michiel Exp $
  --> 
  <xsl:output 
    method="xml"
    version="1.0"
    encoding="utf-8"
    omit-xml-declaration="no"
    standalone="no"
    doctype-public="-//W3C//DTD HTML 4.0 Transitional//"	  
    indent="no"
    />

  <xsl:param name="ew_path"></xsl:param>
  <xsl:param name="ew_context"></xsl:param>
  <xsl:param name="ew_imgdb"><xsl:value-of select="ew_context" />/img.db?</xsl:param>
  <xsl:param name="sessionid"></xsl:param>

  <xsl:param name="debug">false</xsl:param>

  <xsl:variable name="javascriptdir">../javascript/</xsl:variable>
  <xsl:variable name="mediadir">../media/</xsl:variable>  
  <xsl:variable name="imagesize">+s(128x128)</xsl:variable>


</xsl:stylesheet>

