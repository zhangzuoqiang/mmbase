/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

/**
 * This exception gets thrown when a Syntax Error occures in a Multi Level Listing.
 *
 * @author vpro
 * @version $Id: MultiLevelParseException.java,v 1.5 2003-08-29 09:36:53 pierre Exp $
 */
public class MultiLevelParseException extends org.mmbase.module.ParseException {
	
    //javadoc is inherited
    public MultiLevelParseException() {
        super();
    }

    //javadoc is inherited
    public MultiLevelParseException(String message) {
        super("MULTILEVEL Syntax error: "+message);
    }

    //javadoc is inherited
    public MultiLevelParseException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public MultiLevelParseException(String message, Throwable cause) {
        super("MULTILEVEL Syntax error: "+message, cause);
    }
    
}
