/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * Exception thrown by the methods that process search queries. 
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 * @since MMBase-1.7
 */
public class SearchQueryException extends Exception {
    public SearchQueryException() {
        super();
    }

    //javadoc is is inherited
    public SearchQueryException(String s) {
        super(s);
    }
}
