/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;

/**
 * The TEI class for UrlTag. If 'jspvar' attribute is defined, then a
 * UrlTag will not output an url but set a variable with it. That
 * variable cannot be used in jsp:include, so it's probably not that usefull.
 *
 * @author Michiel Meeuwissen
 **/
public class UrlTEI extends TagExtraInfo {

    public VariableInfo[] getVariableInfo(TagData data){
        VariableInfo[] variableInfo =    null;
                    
        Object varObject = data.getAttribute("jspvar");

        if (varObject != null) {
            variableInfo = new VariableInfo[1];
            variableInfo[0] = new VariableInfo(
                 varObject.toString(),
                "java.lang.String",
                true,
                VariableInfo.NESTED);
        }
        return variableInfo;
    }
        
}
