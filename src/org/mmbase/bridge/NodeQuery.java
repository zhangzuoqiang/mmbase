/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import org.mmbase.storage.search.*;
import java.util.SortedSet;

/**
 * A Query especially fit for getQuery of NodeManager.
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeQuery.java,v 1.4 2003-08-06 19:42:30 michiel Exp $
 * @since MMBase-1.7
 */
public interface NodeQuery extends Query {

    /**
     * Returns the node-manager.
     */

    NodeManager getNodeManager();

    StepField getStepField(Field field);

}
