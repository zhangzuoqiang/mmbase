/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import org.mmbase.util.*;

/**
 * interface for TransactionHandler
 *
 * @author  $Author: vpro $ 
 * @version $Revision: 1.1 $ $Date: 2000-10-13 09:39:26 $
 */
public interface TransactionHandlerInterface {
	
	public void handleTransaction(String template, sessionInfo session, scanpage sp);

}
