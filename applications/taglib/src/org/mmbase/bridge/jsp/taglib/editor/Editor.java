/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.editor;

import java.io.IOException;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * You should extend this class to implement your own EditTag. 
 * Create an implememtation to serve information about the MMBase (sub)cloud 
 * and/or pages you want to access.
 * 
 * @author Andr&eacute; van Toly
 * @author Michiel Meeuwissen
 * @version $Id: Editor.java,v 1.2 2005-11-01 11:00:55 andre Exp $
 * @see org.mmbase.bridge.jsp.taglib.editor.EditTag
 * @see org.mmbase.bridge.jsp.taglib.editor.EditTagYAMMe
 */
public class Editor {
    
    private static final Logger log = Logging.getLoggerInstance(Editor.class);
    
    private List queryList = new ArrayList();       // query List
    private List nodenrList = new ArrayList();      // nodenr List
    private List fieldList = new ArrayList();       // fieldname List

    protected List parameters;
        
    /**
     * @param parameters Parameters of the EditTag.
     */
    public void setParameters(List params) {
       this.parameters = params;
    }
    
    public void setQueryList(List qlist) {
        this.queryList = qlist;
    }
    public void setNodenrList(List nrlist) {
        this.nodenrList = nrlist;
    }
    public void setFieldList(List flist) {
        this.fieldList = flist;
    }

    /**
     * Here is were the EditTag registers the lists containing the queries,
     * nodenumbers and fieldnames it received from the FieldTags with the 
     * implementing editor class.
     *
     * @param queryList     SearchQuery that delivered the field
     * @param nodenr    Nodenumber of the node the field belongs to
     * @param field     Name of the field
     */ 
    public void registerFields(List queryList, List nodenrList, List fieldList) {
        // do something usefull with this information
    }
    
    /**
     * Method that returns the results of the implementation of the EditTag.
     * For exampe a html string with an icon and an url that link to an editor 
     * that knows how to handle the information generated by the tag.
     *
     * @return A String with the generated results of the EditTag implementation
     */ 
    public String getEditorHTML() {
        String html = "Sorry. You should see an icon to click on here.";
        log.debug("returning: " + html);
        return html;
    }
    
}
