/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.functions;

import java.io.IOException;
import java.util.*;

import javax.servlet.jsp.*;

import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.containers.FunctionContainerReferrer;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.util.logging.*;

/**
 * A function tag for functions returning a list. The result is iterated.
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.7
 * @version $Id: ListFunctionTag.java,v 1.5 2004-06-30 17:51:56 michiel Exp $
 */
public class ListFunctionTag extends AbstractFunctionTag implements ListProvider, FunctionContainerReferrer, Writer {

    private static final Logger log = Logging.getLoggerInstance(ListFunctionTag.class);

    // implementation of ListProvider

    protected List    returnList;
    protected Iterator iterator;
    protected int      currentItemIndex= -1;

    private   ContextCollector collector;
    protected Attribute  comparator = Attribute.NULL;

    public int size(){
        return returnList.size();
    }
    public int getIndex() {
        return currentItemIndex;
    }

    public int getIndexOffset() {
        return 1;
    }

    public boolean isChanged() {
        return true;
    }
    public Object getCurrent() {
        return getWriterValue();
    }

    public void remove() {
        iterator.remove();
    }


    public ContextContainer getContextContainer() {
        return collector.getContextContainer();
    }

    public int doStartTag() throws JspTagException {        

        List list = (List) getFunctionValue();;

        collector = new ContextCollector(getContextProvider());

        helper.overrideWrite(false); // default behavior is not to write to page
        
        currentItemIndex= -1;  // reset index
        
        returnList = list;
        ListSorter.sort(returnList, (String) comparator.getValue(this), pageContext);
        iterator = returnList.iterator();
        if (iterator.hasNext()) {
            return EVAL_BODY_BUFFERED;
        }
        return SKIP_BODY;
    }


    public int doAfterBody() throws JspException {
        if (getId() != null) {
            getContextProvider().getContextContainer().unRegister(getId());
        }

        helper.doAfterBody();
        collector.doAfterBody();

        if (iterator.hasNext()){
            doInitBody();
            return EVAL_BODY_AGAIN;
        } else {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (IOException ioe){
                    throw new TaglibException(ioe);
                }
            }
            return SKIP_BODY;
        }
    }
    public int doEndTag() throws JspTagException {
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), returnList, false);
        }
        // dereference for gc.
        returnList = null;
        iterator = null;
        collector = null;
        return  super.doEndTag();
    }


    public void doInitBody() throws JspTagException {
        if (iterator.hasNext()){
            currentItemIndex ++;
            helper.setValue(iterator.next());
            if (getId() != null) {
                getContextProvider().getContextContainer().register(getId(), helper.getValue());
            }

        }
    }




}
