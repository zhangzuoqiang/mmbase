/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.security.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

/**
 * This class represents a relation constraint (or contex, if you like).
 * More specifically, it represents a relation manager (itself a node manager) as it applies between bnode belonging to
 * two other node managers.
 * Some of the information here is retrieved from the NodeManager used to build the catual relation node
 * (the data as described in the xml builder config file). This NodeManager is also referred to as the parent.
 * Other data is retrieved from a special (hidden) object that decsribes what relations apply between two nodes.
 * (formerly known as the TypeRel builder).
 * This includes direction and cardinality, and the NodeManagers of nodes itself. These fields cannot be changed
 * except through the use of an administration module.
 * This interface is therefor not a real mmbase 'object' in itself - it exists of two objects joined together.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicRelationManager extends BasicNodeManager implements RelationManager {
	
    private MMObjectNode typeRelNode = null;
    private MMObjectNode relDefNode = null;
    private int snum = 0;
    private int dnum = 0;
    int roleID  = 0;
  	
  	BasicRelationManager(MMObjectNode node, Cloud cloud) {
  	    typeRelNode = node;
  	    snum=node.getIntValue("snumber");
  	    dnum=node.getIntValue("dnumber");
  	    roleID=node.getIntValue("rnumber");
  	    RelDef reldef = ((BasicCloudContext)cloud.getCloudContext()).mmb.getRelDef();
  	    relDefNode= reldef.getNode(roleID);
  	    builder=reldef.getBuilder(relDefNode);
  	    init(builder,cloud);
  	}

    /**
    * Creates a new initialized relation node.
    * The returned node will not be visible in the cloud until the commit() method is called on this node.
    * @return a node of type <code>Relation</code>
    */
    public Node createNode() {
        cloud.assert(Operation.CREATE,typeRelNode.getNumber());
        // create object as a temporary node
        int id = cloud.uniqueId();
        String currentObjectContext = BasicCloudContext.tmpObjectManager.createTmpNode(builder.getTableName(), cloud.getAccount(), ""+id);
        // if we are in a transaction, add the node to the transaction;
        if (cloud instanceof BasicTransaction) {
            ((BasicTransaction)cloud).add(currentObjectContext);
        }
        MMObjectNode node = BasicCloudContext.tmpObjectManager.getNode(cloud.getAccount(), ""+id);
        // set the owner to userName instead of account
//        node.setValue("owner",cloud.getUserName());
        node.setValue("owner","bridge");
        return new BasicRelation(node, this, id);
    }

	
	/**
	 * Retrieves the role of the source to the destination
	 * @return the role as a <code>String</code>
	 */
	public String getForwardRole() {
	    return relDefNode.getStringValue("sname");
	}

	/**
	 * Retrieves the role of the destination to the source
	 * @return the role as a <code>String</code>
	 */
	public String getReciprocalRole() {
	    return relDefNode.getStringValue("dname");
	}

 	/**
     * Retrieves the directionality for this type (the default assigned to a new relation).
     * @return one of the directionality constants
     */
    public int getDirectionality() {
	    return relDefNode.getIntValue("dir");
	}

    /**
     * Retrieves the NodeManager of node that can act as the source of a relation of this type.
     * @return the source NodeManager
     */
    public NodeManager getSourceManager() {
	    int nr=typeRelNode.getIntValue("snumber");
	    return cloud.getNodeManager(nr);
	}

	/**
     * Retrieves the type of node that can act as the destination of a relation of this type.
     * @return the destination NodeManager
     */
    public NodeManager getDestinationManager() {
	    int nr=typeRelNode.getIntValue("dnumber");
	    return cloud.getNodeManager(nr);
	}
	
	/**
     * Adds a relation from this type
     * @param sourceNode the node from which you want to relate
     * @param destinationNode the node to which you want to relate
	 * @return the added relation
     */
    public Relation createRelation(Node sourceNode, Node destinationNode) {
        //
        // checks whether all components are part of the same cloud/transaction
        // maybe should be amde more flexible?
        //
        if (sourceNode.getCloud() != cloud) {
            throw new BasicBridgeException("Relationmanager and source node are not in the same transaction or in different clouds");
        }
        if (destinationNode.getCloud() != cloud) {
            throw new BasicBridgeException("Relationmanager type and destination node are not in the same transaction or in different clouds");
        }
        if (!(cloud instanceof Transaction)  &&
             (((BasicNode)sourceNode).isNew() || ((BasicNode)destinationNode).isNew())) {
            throw new BasicBridgeException("Cannot add a relation to a new node that has not been committed.");
	    }

	    // check types of source and destination	
	    if (!sourceNode.getNodeManager().equals(getSourceManager())) {
            throw new BasicBridgeException("Source node is not of the correct type.");
	    }
	    if (!destinationNode.getNodeManager().equals(getDestinationManager())) {
            throw new BasicBridgeException("Destination node is not of the correct type.");
	    }
	
       Relation relation = (Relation)createNode();
       relation.setSource(sourceNode);
       relation.setDestination(destinationNode);
       relation.setIntValue("rnumber",roleID);
       relation.commit();
       return relation;
    }
	
    /**
    * Compares two relationmanagers, and returns true if they are equal.
    * This effectively means that both objects are relationmanagers, and they both use to the same builder type
    * @param o the object to compare it with
    */
    public boolean equals(Object o) {
        return (o instanceof RelationManager) && (o.hashCode()==hashCode());
    }

    /**
    * Returns the relationmanager's hashCode.
    * This effectively returns the object number of the typerel record
    */
    public int hashCode() {
        return  typeRelNode.getNumber();
    }
}