<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:util="nl.didactor.xsl.Utilities"
  >
  <!--
    searchlist.xls
    @version $Id: searchlist.xsl,v 1.9 2007-11-06 12:38:30 michiel Exp $
  -->

  <xsl:import href="ew:xsl/searchlist.xsl" />


  <!--
      TODO: these overridden template are much much too complex.
      I cannot see what actually is didactor specific.
  -->

  <xsl:template match="list">
    <form>
      <div id="searchresult" class="searchresult">
        <!-- IE is too stupid to understand div.searchresult table -->
        <table class="searchresult">
          <xsl:if test="not(object)">
            <tr>
              <td>
                <xsl:call-template name="prompt_no_results" />
              </td>
            </tr>
          </xsl:if>
          <xsl:for-each select="object">
            <tr number="{@number}" id="item_{@number}">
              <xsl:choose>
                <xsl:when test="(position() mod 2) = 0">
                  <xsl:attribute name="class">even</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:attribute name="class">odd</xsl:attribute>
                </xsl:otherwise>
              </xsl:choose>
              <td style="width: 20px">
              <input
                type="checkbox"
                name="{@number}"
                did="{@number}"
              />
              </td>

              <xsl:choose>
                <xsl:when test="(@type!='images') and
                                (@type!='attachments') and
                                (@type!='audiotapes') and
                                (@type!='videotapes') and
                                (@type!='providers') and
                                (@type!='learnblocks') and
                                (@type!='urls') and
                                (@type!='competencies') and
                                (@type!='questions') and
                                (@type!='metadata') and
                                (@type!='mcanswers') and
                                (@type!='feedback') and
                                (@type!='news')">
                    <td width="30%">
                       <xsl:value-of select="util:nodeManagerGUIName($cloud,@number,1)"/>
                    </td>
                </xsl:when>
              </xsl:choose>

              <xsl:choose>
                <xsl:when test="@type='images'">
                  <td>
                    <img
                      src="{node:saxonFunction($cloud, string(@number), concat('servletpath(', $cloudkey, ',cache(', $listimagesize, '))'))}"
                      hspace="0"
                      vspace="0"
                      border="0"
                      ondblclick="javascript:window.open('{node:saxonFunction($cloud, string(@number), concat('servletpath(', $cloudkey,')'))}')"
                      title="{field[@name='description']}"
                      alt="{field[1]}" />
                    <br />
                      <b>
                      <xsl:value-of select="field[1]" />.<xsl:value-of select="field[3]" />
                      </b>
                  </td>
                  <td>
                    <xsl:value-of select="field[1]" />
                  </td>
                  <td width="30%">
                    <xsl:value-of select="field[2]"/>
                 </td>
                </xsl:when>
                <!-- audio parts -->
                <xsl:when test="@type='audioparts'">
                  <td>
                    <a href="{$ew_context}/rastreams.db?{@number}">
                      <xsl:call-template name="prompt_audio" />
                    </a>
                  </td>
                  <xsl:apply-templates select="field" />
                </xsl:when>
                <!-- video parts -->
                <xsl:when test="@type='videoparts'">
                  <td>
                    <a href="{$ew_context}/rmstreams.db?{@number}">
                      <xsl:call-template name="prompt_video" />
                    </a>
                  </td>
                  <xsl:apply-templates select="field" />
                </xsl:when>
                <!-- attachments -->
                <xsl:when test="@type='attachments'">
                  <td>
                    <xsl:value-of select="field[1]" />
                  </td>
                  <td>
                    <xsl:value-of select="field[2]" />
                  </td>
                  <td>
                    <xsl:value-of select="field[3]" />
                  </td>
                </xsl:when>

                <!-- audiotapes -->
                <xsl:when test="@type='audiotapes'">
                  <td>
                    <xsl:value-of select="field[1]" />
                  </td>
                  <td>
                    <xsl:value-of select="field[1]" />
                  </td>
                  <td>
                    <xsl:value-of select="field[2]" />
                  </td>
                </xsl:when>

                <!-- videotapes -->
                <xsl:when test="@type='videotapes'">
                  <td>
                    <xsl:value-of select="field[1]" />
                  </td>
                  <td>
                    <xsl:value-of select="field[1]" />
                  </td>
                  <td>
                    <xsl:value-of select="field[2]" />
                  </td>
                </xsl:when>

                <!-- news -->
                <xsl:when test="@type='news'">
                  <td>
                    <xsl:value-of select="field[1]" />
                  </td>
                  <td>
                    <xsl:value-of select="field[2]" />
                  </td>
                </xsl:when>

                <!-- questions -->
                <xsl:when test="@type='questions'">
                  <td>
                    <xsl:value-of select="field[1]" />
                  </td>
                  <td width="30%">
                       <xsl:value-of select="util:nodeManagerGUIName($cloud,@number,1)"/>
                    </td>
                  <td>
                    <xsl:value-of select="field[2]" />
                  </td>
                </xsl:when>

                <!-- feedback -->
                <xsl:when test="@type='feedback'">
                  <td>
                    <xsl:value-of select="field[1]" />es
                  </td>
                  <td>
                    <xsl:value-of select="field[2]" />
                  </td>
                </xsl:when>


                <xsl:otherwise>
                  <xsl:apply-templates select="field" />
                </xsl:otherwise>
              </xsl:choose>
            </tr>
          </xsl:for-each>
        </table>
      </div>

      <xsl:call-template name="searchnavigation" />
    </form>
  </xsl:template>


  <xsl:template name="searchnavigation">
    <div id="searchnavigation" class="searchnavigation">
      <table class="searchnavigation">
        <tr>
          <td colspan="2">
            <xsl:apply-templates select="pages" />

            <xsl:if test="/list/@showing">
              <xsl:text></xsl:text>
              <span class="pagenav">
                <xsl:call-template name="prompt_more_results" />
              </span>
            </xsl:if>
            <xsl:if test="not(/list/@showing)">
              <xsl:text></xsl:text>
              <span class="pagenav">
                <xsl:call-template name="prompt_result_count" />
              </span>
            </xsl:if>
          </td>
        </tr>
        <tr>
          <td>
            <input
              type="button"
              name="cancel"
              value="{$tooltip_cancel_search}"
              onclick="closeSearch();"
              class="button" />
          </td>
          <td align="right" valign="top">
            <input
              type="button"
              name="ok"
              value="{$tooltip_end_search}"
              onclick="dosubmit();"
              class="button" />
          </td>
        </tr>
      </table>
    </div>
  </xsl:template>


</xsl:stylesheet>
