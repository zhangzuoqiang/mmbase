package org.mmbase.module.core;

import java.util.*;
import java.net.*;
import java.sql.*;


/**
 * Event/changes interface for MMObjectNodes this is a callback  
 * interface thats need to be implemented when a object wants to add 
 * itself as a change listener on Builder to recieve signals if nodes change.
 *
 * @author Daniel Ockeloen
 */
public interface MMBaseObserver {
	public boolean nodeRemoteChanged(String number,String builder,String ctype);
	public boolean nodeLocalChanged(String number,String builder,String ctype);
}
