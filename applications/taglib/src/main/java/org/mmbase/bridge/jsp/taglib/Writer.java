/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.JspTagException;

/**
 * Writer tag are tags which can write something to the page (or to
 * something else). To ensure a common behavior, this interface is
 * created.
 *
 * Tags implementing this interface can (should?) use {@link WriterHelper}
 * for a quick implementation.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public interface Writer {

    /**
     * Wether to write to page or not. This has to default to 'false'
     * if the tag has a body, and to 'true' if it hasn't.
     *
     */
    void setWrite(String t) throws JspTagException;


    /**
     * (Override) escape behaviour
     *
     */
    void setEscape(String e) throws JspTagException;

    /**
     * JspVar to Create, and write to
     */
    void setJspvar(String j);

    /**
     * Type of the jspvar.
     */

    void setVartype(String t) throws JspTagException;

    /**
     * Subtags of 'Writer' tag can request the value.
     */

    Object getWriterValue() throws JspTagException;

    /**
     *  To be used by child tags. If they are present, they say to the
     *  writer tag that it has body, which it can use to determine a
     *  default for the write property.
     *
     * @since MMBase-1.6
     */
    void haveBody() throws JspTagException;


}
