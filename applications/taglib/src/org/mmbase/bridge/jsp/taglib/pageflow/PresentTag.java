/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.jsp.taglib.CloudProvider;

import javax.servlet.jsp.JspTagException;


/**
* A very simple tag to check if a request post parameter is present.
* Inspired by Struts taglib.
* 
* @author Michiel Meeuwissen
*/
public class PresentTag extends CloudReferrerTag {
           
    private String parameter;

    public void setParameter(String p) {
        parameter = p;
    }

    protected String getParameter() throws JspTagException {
        CloudProvider cp = findCloudProvider();        
        String param = (String) cp.getObject(parameter);
        if (param == null) {
            param = pageContext.getRequest().getParameter(parameter); 
        }
        return param;        
    }
    
    public int doStartTag() throws JspTagException {
        if (getParameter() != null) {
            return EVAL_BODY_TAG;
        } else {
            return SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspTagException {
        try{
            if(bodyContent != null)
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch(java.io.IOException e){
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        return EVAL_PAGE;
    }

}
