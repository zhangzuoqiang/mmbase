/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation;

import java.util.Collection;
import java.util.Map;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.MapNode;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


public abstract class AbstractNodeList<E extends Node> extends BasicList<E>{

    private static Logger log = Logging.getLoggerInstance(AbstractNodeList.class);

    protected Cloud cloud;
    protected NodeManager nodeManager = null;

    public AbstractNodeList() {
        super();
    }

    public AbstractNodeList(Collection c) {
        super(c);
    }

    AbstractNodeList(Collection c, Cloud cloud) {
        super(c);
        this.cloud = cloud;
    }

    AbstractNodeList(Collection c, NodeManager nodeManager) {
        super(c);
        this.nodeManager = nodeManager;
        this.cloud = nodeManager.getCloud();
    }

    protected E convert(Object o) {
        if (o == null) {
            log.debug("Null");
            return null;
        } 
        if (log.isDebugEnabled()) {
            log.debug("Converting " + o.getClass());
        }

        Node node = convertWithBridgeToNode(cloud, nodeManager, o);
        if (node == null) {
            log.debug("Could not convert with bridge");
            if (o instanceof MMObjectBuilder) { // a builder
                node = cloud.getNodeManager(((MMObjectBuilder)o).getTableName());
            } else {
                MMObjectNode coreNode = (MMObjectNode) o;
                node = convertMMObjectNodetoBridgeNode(coreNode);
            }
            //log.info("Found " + node.getClass() + " in " + getClass());
        }
        if (node == null) {
            throw new RuntimeException("Could not convert " + o.getClass() + " " + o);
        }
        
        return (E) node;
    }

    /**
     * Converts an object to a Node, using only bridge.
     * @return a Node, or <code>null</code> if o is either <code>null</code> or could not be
     * converted.
     */
    public static Node convertWithBridgeToNode(Cloud cloud, NodeManager nodeManager, Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof CharSequence) { // a string indicates a nodemanager by name, or, if numeric, a node number..
            String s = o.toString();
            if (org.mmbase.datatypes.StringDataType.NON_NEGATIVE_INTEGER_PATTERN.matcher(s).matches()) {
                return cloud.getNode(s);
            } else {
                if (cloud.hasNodeManager(s)) {
                    return cloud.getNodeManager(s);
                } else { // an alias?
                    return cloud.getNode(s);
                }
            }
        } else if (o instanceof Map) {
            if (nodeManager == null) {
                return new MapNode((Map) o, cloud);
            } else {
                return new MapNode((Map) o, nodeManager);
            }
        } else if (o instanceof Number) {
            return cloud.getNode(((Number) o).intValue());
        } else {
            return null;
        }
    }

    protected Node convertMMObjectNodetoBridgeNode(MMObjectNode coreNode) {
        Node node;
        MMObjectBuilder coreBuilder = coreNode.getBuilder();
        if (coreBuilder instanceof TypeDef) {
            String builderName = coreNode.getStringValue("name");
            if (cloud.hasNodeManager(builderName)) {
                try {
                    node = cloud.getNodeManager(builderName);
                } catch (Throwable t) {
                    node = getNode(cloud, coreNode);
                }
            } else {
                node = getNode(cloud, coreNode);
            }
        } else if (coreBuilder instanceof RelDef) {
            node = cloud.getRelationManager(coreNode.getStringValue("sname"));
        } else if (coreBuilder instanceof TypeRel) {
            int snumber = coreNode.getIntValue("snumber");
            int dnumber = coreNode.getIntValue("dnumber");
            int rnumber = coreNode.getIntValue("rnumber");
            NodeManager nm1;
            if (cloud.hasNode(snumber)) {
                nm1 = castToNodeManager(cloud.getNode(snumber));
            } else {
                log.warn("Source of typerel " + coreNode.getNumber() + " is " + (coreNode.isNull("snumber") ? "NULL" : "" + snumber));
                nm1 = cloud.getNodeManager("object");
            }
            NodeManager nm2;
            if (cloud.hasNode(dnumber)) {
                nm2 =  castToNodeManager(cloud.getNode(dnumber));
            } else {
                log.warn("Destination of typerel " + coreNode.getNumber() + " is " + (coreNode.isNull("dnumber") ? "NULL" : "" + dnumber));
                nm2 = cloud.getNodeManager("object");
            }
            Node role;
            if (cloud.hasNode(rnumber)) {
                role = cloud.getNode(rnumber);
            } else {
                log.warn("Role of typerel " + coreNode.getNumber() + " is " + (coreNode.isNull("rnumber") ? "NULL" : "" + rnumber));
                role = cloud.getNode(BasicCloudContext.mmb.getRelDef().getNumberByName("related"));
            }
            node = cloud.getRelationManager(nm1.getName(), nm2.getName(), role.getStringValue("sname"));
        } else if(coreBuilder instanceof InsRel) {
            node = getNode(cloud, coreNode);
        } else if (coreNode instanceof org.mmbase.module.core.VirtualNode) {
            MMObjectBuilder builder = coreNode.getBuilder();
            if (builder instanceof VirtualBuilder) {
                if (nodeManager != null) {
                    node = new VirtualNode(cloud, (org.mmbase.module.core.VirtualNode) coreNode, nodeManager);
                } else {
                    node = new VirtualNode((org.mmbase.module.core.VirtualNode) coreNode, cloud);
                }
            } else {
                node = new VirtualNode(cloud, (org.mmbase.module.core.VirtualNode) coreNode, cloud.getNodeManager(builder.getObjectType()));
            }
        } else {
            node =  getNode(cloud, coreNode);
        }
        return node;
    }

    /**
     * since MMBase 1.8
     */
    protected NodeManager castToNodeManager(Node n) {
        if (n instanceof NodeManager) {
            return (NodeManager) n;
        } else {
            log.error("Node " + n.getNumber() + " is not a node manager (but a " + n.getNodeManager() + "), taking it Object for now");
            return cloud.getNodeManager("object");
        }
    }

    /**
     * @since MMBase-1.8.4
     */
    protected Node getNode(Cloud c, MMObjectNode coreNode) {
        Node node;
        int n = coreNode.getNumber();
        try {
            if (n == -1) {
                String[] na  = coreNode.getStringValue("_number").split("_");
                if (na.length == 2) {
                    node = cloud.getNode(na[1]);
                } else {
                    log.error("Could not make a Node of " + coreNode);
                    node = null;
                }
            } else {
                node = cloud.getNode(n);
            }
        } catch (Exception e) {
            log.error(e);
            log.error(coreNode.getClass() + "" + coreNode.getValues());
            node = null;
        }
        return node;
    }

}
