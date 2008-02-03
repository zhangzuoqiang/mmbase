/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative.
 *
 * The license (Mozilla version 1.0) can be read at the MMBase site.
 * See http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;

/**
 * This is a utility class to create node event and relation event instances. the reason for it is
 * that we want to references to core classes in the NodeEvent and RelationEvent classes, to keep them bridge-friendly,
 * but we need a little help for easy instantiation.
 * @author Ernst Bunders
 * @since MMBase-1.8
 * @version $Id: NodeEventHelper.java,v 1.9 2008-02-03 17:03:40 nklasens Exp $

 */
public class NodeEventHelper {

    public static NodeEvent createNodeEventInstance(Node node, int eventType, String machineName){
        if(machineName == null)machineName = MMBase.getMMBase().getMachineName();
        MMObjectNode coreNode = MMBase.getMMBase().getBuilder(node.getNodeManager().getName()).getNode(node.getNumber());
        return createNodeEventInstance(coreNode, eventType, machineName);
    }

    /**
     * create an NodeEvent instance with an MMObjectNode
     * @param node
     * @param eventType
     * @param machineName or null to create a node event with local machine name
     * @return new instance of NodeEvent
     */
    public static NodeEvent createNodeEventInstance(MMObjectNode node, int eventType, String machineName){
        if(machineName == null) machineName = MMBase.getMMBase().getMachineName();
        Map<String, Object> oldEventValues;
        Map<String, Object> newEventValues;

        //fill the old and new values maps for the event
        switch(eventType) {
        case Event.TYPE_NEW:
            newEventValues = Collections.unmodifiableMap(node.getValues());
            oldEventValues = Collections.emptyMap();
            break;
        case Event.TYPE_CHANGE:
            oldEventValues = Collections.unmodifiableMap(node.getOldValues());
            newEventValues = new HashMap<String, Object>();
            Map<String, Object> values = node.getValues();
            for (String key : oldEventValues.keySet()) {
                    newEventValues.put(key, values.get(key));
                }
            break;
        case Event.TYPE_DELETE:
            newEventValues = Collections.emptyMap();
            oldEventValues = Collections.unmodifiableMap(node.getValues());
            break;
        default: {
            oldEventValues = Collections.emptyMap();
            newEventValues = Collections.emptyMap();
            // err.
        }
        }
        
        removeBinaryValues(oldEventValues);
        removeBinaryValues(newEventValues);

        return new NodeEvent(machineName, node.getBuilder().getTableName(), node.getNumber(), oldEventValues, newEventValues, eventType);
    }

    private static void removeBinaryValues(Map<String, Object> oldEventValues) {
        for (Map.Entry<String, Object> entry : oldEventValues.entrySet()) {
            if (entry.getValue() != null && (entry.getValue() instanceof byte[])) {
                entry.setValue(null);
            }
        }
    }

    public static RelationEvent createRelationEventInstance(Relation node, int eventType, String machineName){
        MMObjectNode coreNode = MMBase.getMMBase().getBuilder(node.getNodeManager().getName()).getNode(node.getNumber());
        return createRelationEventInstance(coreNode, eventType, machineName);
    }

    /**
     * create an RelationEvent instnce with an MMObjectNode (builder should be specialization of insrel)
     * @param node
     * @param eventType
     * @param machineName
     * @return a new RelationEvetn instance
     * @throws IllegalArgumentException when given node's builder is not a specialization of insrel
     */
    public static RelationEvent createRelationEventInstance(MMObjectNode node, int eventType, String machineName){
        if (!(node.getBuilder() instanceof InsRel)) {
            throw new IllegalArgumentException( "you can not create a relation changed event with this node");
        }
        MMBase mmbase = MMBase.getMMBase();
        if(machineName == null) machineName = mmbase.getMachineName();
        MMObjectNode reldef = node.getNodeValue("rnumber");
        
        int relationSourceNumber = node.getIntValue("snumber");
        int relationDestinationNumber = node.getIntValue("dnumber");

        String relationSourceType = mmbase.getBuilderNameForNode(relationSourceNumber);
        if (relationSourceType == null) relationSourceType = "object";
        String relationDestinationType = mmbase.getBuilderNameForNode(relationDestinationNumber);
        if (relationDestinationType == null) relationDestinationType = "object";
        NodeEvent nodeEvent = createNodeEventInstance(node, eventType, machineName);
        int role = reldef.getNumber();
        return new RelationEvent(nodeEvent, relationSourceNumber, relationDestinationNumber, relationSourceType, relationDestinationType, role);
    }
}
