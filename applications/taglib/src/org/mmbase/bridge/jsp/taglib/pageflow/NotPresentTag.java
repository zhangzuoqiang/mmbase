/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import javax.servlet.jsp.JspTagException;

/**
* A very simple tag to check if a request post parameter is not present.
* Inspired by Struts taglib.
* 
* @author Michiel Meeuwissen
*/
public class NotPresentTag extends PresentTag {
              
    public int doStartTag() throws JspTagException {
        if (getKey() == null) {
            return EVAL_BODY_TAG;
        } else {
            return SKIP_BODY;
        }
    }

}
