/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;

import org.mmbase.util.*;
import org.mmbase.module.corebuilders.FieldDefs;
/*
	$Id: TemporaryNodeManager.java,v 1.9 2000-11-08 16:11:52 vpro Exp $

	$Log: not supported by cvs2svn $
	Revision 1.8  2000/11/08 14:46:29  vpro
	Rico: added splitting into datatypes
	
	Revision 1.7  2000/11/08 14:31:23  vpro
	Rico: returns right keys
	
	Revision 1.6  2000/11/08 14:24:46  vpro
	Rico: fixed getObject
	
	Revision 1.5  2000/11/08 13:24:19  vpro
	Rico: included owner in operations
	
	Revision 1.4  2000/10/26 13:10:37  vpro
	Rico: fixed b0rken uncompilable code
	
	Revision 1.3  2000/10/13 11:41:34  vpro
	Rico: made it working
	
	Revision 1.2  2000/10/13 09:39:54  vpro
	Rico: added a method
	
	Revision 1.1  2000/08/14 19:19:06  rico
	Rico: added the temporary node and transaction support.
	      note that this is rather untested but based on previously
	      working code.
	
*/

/**
 * @author Rico Jansen
 * @version $Id: TemporaryNodeManager.java,v 1.9 2000-11-08 16:11:52 vpro Exp $
 */
public class TemporaryNodeManager implements TemporaryNodeManagerInterface {
	private String	_classname = getClass().getName();
	private boolean _debug=true;
	private void 	debug( String msg ) { System.out.println( _classname +":"+ msg ); }

	private MMBase mmbase;

	public TemporaryNodeManager(MMBase mmbase) {
		this.mmbase=mmbase;
	}

	public String createTmpNode(String type,String owner,String key) {
		if (_debug) debug("createTmpNode : type="+type+" owner="+owner+" key="+key);
		if (owner.length()>12) owner=owner.substring(0,12);
		MMObjectBuilder builder=mmbase.getMMObject(type);
		MMObjectNode node;
		if (builder!=null) {
			node=builder.getNewTmpNode(owner,getTmpKey(owner,key));
			if (_debug) debug("New tmpnode "+node);
		} else {
			debug("Can't find builder "+type);
		}
		return(key);
	}

	public String deleteTmpNode(String owner,String key) {
		MMObjectBuilder b=mmbase.getMMObject("typedef");
		b.removeTmpNode(getTmpKey(owner,key));
		if (_debug) debug("delete node "+getTmpKey(owner,key));
		return(key);
	}

	public MMObjectNode getNode(String owner,String key) {
		MMObjectBuilder bul=mmbase.getMMObject("typedef");
		MMObjectNode node;
		node=bul.getTmpNode(getTmpKey(owner,key));
		// fallback to normal nodes
		if (node==null) {
			if (_debug) debug("getNode tmp not node found "+key);
			node=bul.getNode(key);
		}
		return(node);
	}

	public String getObject(String owner,String key,String dbkey) {
		MMObjectBuilder bul=mmbase.getMMObject("typedef");
		MMObjectNode node;
		node=bul.getTmpNode(getTmpKey(owner,key));
		if (node==null) {
			if (_debug) debug("getObject not tmp node found "+key);
			node=bul.getNode(dbkey);
			if (node==null) {
				debug("Node not found in database "+dbkey);
			} else {
				bul.putTmpNode(getTmpKey(owner,key),node);
			}
		}
		if (node != null) {
			return(key);
		} else {
			return null;
		}
	}

	public String setObjectField(String owner,String key,String field,Object value) {
		MMObjectNode node;
		int i;float f;double d;long l;
		String stringValue;

		// Memo next can be done by new MMObjectNode.setValue
		node=getNode(owner,key);
		if (node!=null) {
			int type=node.getDBType(field);
			if (type>=0) {
				if (value instanceof String) {
					stringValue=(String)value;
					switch(type) {
						case FieldDefs.TYPE_STRING:
							node.setValue(field, stringValue);
							break;
						case FieldDefs.TYPE_INTEGER:
							try {
								i=Integer.parseInt(stringValue);
								node.setValue(field,i);
							} catch (NumberFormatException x) {
								debug("Value for field "+field+" is not a number "+stringValue);
							}
							break;
						case FieldDefs.TYPE_BYTE:
							debug("We don't support casts from String to Byte");
							break;
						case FieldDefs.TYPE_FLOAT:
							try {
								f=Float.parseFloat(stringValue);
								node.setValue(field,f);
							} catch (NumberFormatException x) {
								debug("Value for field "+field+" is not a number "+stringValue);
							}
							break;
						case FieldDefs.TYPE_DOUBLE:
							try {
								d=Double.parseDouble(stringValue);
								node.setValue(field,d);
							} catch (NumberFormatException x) {
								debug("Value for field "+field+" is not a number "+stringValue);
							}
							break;
						case FieldDefs.TYPE_LONG:
							try {
								l=Long.parseLong(stringValue);
								node.setValue(field,l);
							} catch (NumberFormatException x) {
								debug("Value for field "+field+" is not a number "+stringValue);
							}
							break;
						default:
							debug("Unknown type for field "+field);
							break;
					}
				} else {
					node.setValue(field,value);
				}
			} else {
				debug("Invalid type for field "+field);
			}
		} else {
			debug("setObjectField(): Can't find node : "+key);
		}
		return("");
	}


	public String getObjectFieldAsString(String owner,String key,String field) {
		String rtn;
		MMObjectNode node;
		node=getNode(owner,key);
		if (node==null) {
			debug("getObjectFieldAsString(): node "+key+" not found!");
			rtn="";
		} else {
			rtn=node.getValueAsString(field);
		} 
		return(rtn);
	}

	public Object getObjectField(String owner,String key,String field) {
		Object rtn;
		MMObjectNode node;
		node=getNode(owner,key);
		if (node==null) {
			debug("getObjectFieldAsString(): node "+key+" not found!");
			rtn="";
		} else {
			rtn=node.getValueAsString(field);
		} 
		return(rtn);
	}

	public String getTmpKey(String owner,String key) {
		return(owner+"_"+key);
	}
}
