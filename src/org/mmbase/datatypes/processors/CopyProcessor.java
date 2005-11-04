/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
/**
 * The Processor that does nothing.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CopyProcessor.java,v 1.2 2005-11-04 23:11:52 michiel Exp $
 * @since MMBase-1.7
 */

public class CopyProcessor implements Processor {

    private static final int serialVersionUID = 1;

    private static CopyProcessor instance = new CopyProcessor();
    public static CopyProcessor getInstance() {
        return instance;
    }

    public final Object process(Node node, Field field, Object value) {
        return value;
    }

    public String toString() {
        return "COPY";
    }
}


