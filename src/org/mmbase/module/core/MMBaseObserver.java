/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * Event/changes interface for MMObjectNodes this is a callback  
 * interface thats need to be implemented when a object wants to add 
 * itself as a change listener on Builder to recieve signals if nodes change.
 *
 * @author Daniel Ockeloen
 * @version $Id: MMBaseObserver.java,v 1.6 2003-03-10 11:50:30 pierre Exp $
 */
public interface MMBaseObserver {
	public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype);
	public boolean nodeLocalChanged(String machine,String number,String builder,String ctype);
}
