/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;

import java.util.*;
import javax.servlet.jsp.JspTagException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: NodeListConstraintTag.java,v 1.7 2003-08-01 11:03:27 michiel Exp $
 */
public class NodeListConstraintTag extends CloudReferrerTag implements NodeListContainerReferrer {

    private static Logger log = Logging.getLoggerInstance(NodeListConstraintTag.class);

    protected Attribute container  = Attribute.NULL;


    protected Attribute operator   = Attribute.NULL;

    protected Attribute field      = Attribute.NULL;
    protected Attribute value      = Attribute.NULL;


    protected Attribute field2     = Attribute.NULL; // not implemented


    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setField(String f) throws JspTagException {
        field = getAttribute(f);
    }

    public void setValue(String v) throws JspTagException {
        value = getAttribute(v);
    }

    public void setOperator(String o) throws JspTagException {
        operator = getAttribute(o);
    }

    protected int getOperator() throws JspTagException {
        String op = operator.getString(this).toUpperCase();
        if (op.equals("<") || op.equals("LESS")) {
            return FieldCompareConstraint.LESS;
        } else if (op.equals("<=") || op.equals("LESS_EQUAL")) {
            return FieldCompareConstraint.LESS_EQUAL;
        } else if (op.equals("=") || op.equals("EQUAL") || op.equals("")) {
            return FieldCompareConstraint.EQUAL;
        } else if (op.equals(">") || op.equals("GREATER")) {
            return FieldCompareConstraint.GREATER;
        } else if (op.equals(">=") || op.equals("GREATER_EQUAL")) {
            return FieldCompareConstraint.GREATER_EQUAL;
        } else if (op.equals("LIKE")) {
            return FieldCompareConstraint.LIKE;
        //} else if (op.equals("~") || op.equals("REGEXP")) {
        //  return FieldCompareConstraint.REGEXP;
        } else {
            throw new JspTagException("Unknown Field Compare Operator '" + op + "'");
        }

    }

    public static void addConstraint(Query query, String field, int operator, String stringValue) throws JspTagException {
        Object compareValue;
        if (operator < FieldCompareConstraint.LIKE) {
            try {
                compareValue = new Integer(stringValue);
            } catch (NumberFormatException e) {
                try {
                    compareValue = new Double(stringValue);
                } catch (NumberFormatException e2) {
                    throw new  JspTagException("Operator requires number value ('" + stringValue + "' is not)");
                }
            } 
        } else {
            compareValue = stringValue;
        }
        Constraint newConstraint;
        StepField stepField = query.createStepField(field);
        log.debug(stepField);
        if (stepField == null) log.warn("Could not create stepfield with '" + field + "'");
        newConstraint = query.createConstraint(stepField, operator, compareValue);
        Constraint constraint = query.getConstraint();
        if (constraint != null) {
            log.debug("compositing constraint");
            newConstraint = query.createConstraint(constraint, CompositeConstraint.LOGICAL_AND, newConstraint);
        }
        if (log.isDebugEnabled()) {
            log.debug("setting constraint " + newConstraint);
        }
        query.setConstraint(newConstraint);

    }

    public int doStartTag() throws JspTagException {        
        NodeListContainer c = (NodeListContainer) findParentTag(NodeListContainer.class, (String) container.getValue(this));

        Query query = c.getQuery();
        addConstraint(query, field.getString(this), getOperator(), value.getString(this));
                
        return SKIP_BODY;
    }

}
